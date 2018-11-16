package com.pratham.prathamdigital.ui.QRLogin;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRLogin extends BaseActivity implements ZXingScannerView.ResultHandler {

    ViewGroup content_frame;
    TextView tv_stud_one;
    TextView tv_stud_two;
    TextView tv_stud_three;
    TextView tv_stud_four;
    TextView tv_stud_five;
    Button btn_Start, btn_Reset;

    public ZXingScannerView startCameraScan;
    ArrayList<Modal_Student> stdList = new ArrayList<Modal_Student>();
    Dialog dialog;
    int totalStudents = 0;
    Boolean setStud = false;
    Modal_Student std;
    //    static String programID = "";
//    StatusDBHelper statusDBHelper;
    boolean permission = false;
    List<Attendance> attendances;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrlogin);
        // Memory Allocation
        content_frame = findViewById(R.id.content_frame);
        tv_stud_one = findViewById(R.id.tv_stud_one);
        tv_stud_two = findViewById(R.id.tv_stud_two);
        tv_stud_three = findViewById(R.id.tv_stud_three);
        tv_stud_four = findViewById(R.id.tv_stud_four);
        tv_stud_five = findViewById(R.id.tv_stud_five);
        btn_Reset = findViewById(R.id.btn_Reset);
        btn_Start = findViewById(R.id.btn_Start);
        tv_stud_one.setVisibility(View.GONE);
        tv_stud_two.setVisibility(View.GONE);
        tv_stud_three.setVisibility(View.GONE);
        tv_stud_four.setVisibility(View.GONE);
        tv_stud_five.setVisibility(View.GONE);
//        statusDBHelper = new StatusDBHelper(this);
//        programID = statusDBHelper.getValue("programId");
        initCamera();

        btn_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content_frame.setVisibility(View.VISIBLE);

                stdList.clear();
                totalStudents = 0;

                tv_stud_one.setText("");
                tv_stud_two.setText("");
                tv_stud_three.setText("");
                tv_stud_four.setText("");
                tv_stud_five.setText("");

                tv_stud_one.setVisibility(View.GONE);
                tv_stud_two.setVisibility(View.GONE);
                tv_stud_three.setVisibility(View.GONE);
                tv_stud_four.setVisibility(View.GONE);
                tv_stud_five.setVisibility(View.GONE);

                scanNextQRCode();

            }
        });


        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stdList.size() > 0)
                    setValues();
                else
                    Toast.makeText(QRLogin.this, "Please Add Student !!!", Toast.LENGTH_SHORT).show();
            }
        });


        if (ContextCompat.checkSelfPermission(QRLogin.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("App requires camera permission to scan QR code");
                builder.setCancelable(false);
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(QRLogin.this, new String[]{Manifest.permission.CAMERA}, 1);
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(QRLogin.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        } else {
            initCamera();
            permission = true;
        }

    }// onCreate

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "You Need Camera permission", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                initCamera();
                permission = true;
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        startCameraScan.resumeCameraPreview(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraScan.resumeCameraPreview(this);
    }

    @Override
    public void onDestroy() {
        if (startCameraScan != null) startCameraScan.stopCamera();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startCameraScan.stopCamera();
    }

    private void setValues() {
        attendances = new ArrayList<>();

        // todo Handle Session Start Webview service to handle session tracking
//        startService(new Intent(this, WebViewService.class));

//        MultiPhotoSelectActivity.selectedGroupsScore = "";


        //todo check logic
//        MultiPhotoSelectActivity.presentStudents = new String[stdList.size()];
        try {

            if (stdList != null && stdList.size() > 0) {

                if (PD_Constant.SESSIONID == null || PD_Constant.SESSIONID.trim().equalsIgnoreCase("")) {
                    FastSave.getInstance().saveString(PD_Constant.SESSIONID, PD_Utility.getUUID().toString());
                }
                for (int i = 0; i < stdList.size(); i++) {
                    Attendance attendance = new Attendance();
                    attendance.SessionID = FastSave.getInstance().getString(PD_Constant.SESSIONID, "");
                    attendance.StudentID = stdList.get(i).getStudentId();
                    attendance.Date = PD_Utility.getCurrentDateTime();
//                    MultiPhotoSelectActivity.presentStudents[i] = stdList.get(i).getStudentID();
                    attendance.GroupID = "QR";
                    FastSave.getInstance().saveString(PD_Constant.GROUPID, attendance.GroupID);
/*
                    if (!MultiPhotoSelectActivity.selectedGroupsScore.contains(attendance.GroupID)) {
                        MultiPhotoSelectActivity.selectedGroupsScore += attendance.GroupID + ",";
                    }
*/
                    attendances.add(attendance);
                }
                BaseActivity.attendanceDao.insertAttendance(attendances);

                Modal_Session s = new Modal_Session();
                s.SessionID = FastSave.getInstance().getString(PD_Constant.SESSIONID, "");
                s.fromDate = PD_Utility.getCurrentDateTime();
                s.toDate = "NA";

                BaseActivity.sessionDao.insert(s);
//                sessionDBHelper.Add(s);
//                BackupDatabase.backup(this, PD_Constant.pradigiObbPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (startCameraScan != null) {
            startCameraScan.stopCamera();
        }

        Intent main = new Intent(QRLogin.this, ActivityMain.class);
        startActivity(main);
//        finish();

    }

    // Reading configuration Json From SDCard
    public String getProgramId() {

        String progIDString = null;
        try {
            File myJsonFile = new File(Environment.getExternalStorageDirectory() + "/.POSinternal/Json/Config.json");
            FileInputStream stream = new FileInputStream(myJsonFile);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }

            JSONObject jsonObj = new JSONObject(jsonStr);
            progIDString = jsonObj.getString("programId");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return progIDString;
    }


    public void scanNextQRCode() {
        if (startCameraScan != null) {
            startCameraScan.stopCamera();
        }
        startCameraScan.startCamera();
        startCameraScan.resumeCameraPreview(this);
    }


    @Override
    public void handleResult(Result result) {
        try {
            boolean dulicateQR = false;
            startCameraScan.stopCamera();
            Log.d("RawResult:::", "****" + result.getText());
            if (result.getText().contains("{")) {
                // New QRCode Jsons

                // Json Parsing
                JSONObject jsonobject = new JSONObject(result.getText());
                String id = jsonobject.getString("stuId");
                String name = jsonobject.getString("name");

            /*// got result in json format
            Pattern pattern = Pattern.compile("[A-Za-z0-9]+-[A-Za-z._]{2,50}");
            Matcher mat = pattern.matcher(result.getText());

            if (mat.matches()) {
*/
                if (stdList.size() <= 0)
                    qrEntryProcess(result);
                else {
                    for (int i = 0; i < stdList.size(); i++) {
                        // change
                        String[] currentIdArr = {id};
                        String currId = currentIdArr[0];
                        if (stdList.get(i).getStudentId().equalsIgnoreCase("" + currId)) {
//                            Toast.makeText(this, "Already Scaned", Toast.LENGTH_SHORT).show();
                            showQrDialog(", This QR Was Already Scaned");
                            setStud = false;
                            dulicateQR = true;
                            break;
                        }
                    }
                    if (!dulicateQR) {
                        qrEntryProcess(result);
                    }
                }
            /*} else {
                startCameraScan.startCamera();
                startCameraScan.resumeCameraPreview(this);
                BackupDatabase.backup(this);
            }*/
            } else {
                // Old Jsons
                try {
                    Log.d("RawResult:::", "****" + result.getText());

                    Pattern pattern = Pattern.compile("[A-Za-z0-9]+-[A-Za-z._]{2,50}");
                    Matcher mat = pattern.matcher(result.getText());

                    if (mat.matches()) {

                        if (stdList.size() <= 0)
                            qrEntryProcess(result);
                        else {
                            for (int i = 0; i < stdList.size(); i++) {
                                String[] currentIdArr = decodeStudentId(result.getText(), "-");
                                String currId = currentIdArr[0];
                                if (stdList.get(i).getStudentId().equalsIgnoreCase("" + currId)) {
//                            Toast.makeText(this, "Already Scaned", Toast.LENGTH_SHORT).show();
                                    showQrDialog(", This QR Was Already Scaned");
                                    setStud = false;
                                    dulicateQR = true;
                                    break;
                                }
                            }
                            if (!dulicateQR) {
                                qrEntryProcess(result);
                            }
                        }
                    } else {
                        startCameraScan.startCamera();
                        startCameraScan.resumeCameraPreview(this);
//                        BackupDatabase.backup(this, PD_Constant.pradigiObbPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Invalid QR Code !!!", Toast.LENGTH_SHORT).show();
            btn_Reset.performClick();
            e.printStackTrace();
        }
    }


    public void showQrDialog(String studentName) {

        dialog = new Dialog(QRLogin.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog_for_qrscan);
        dialog.setCanceledOnTouchOutside(false);
        TextView text = (TextView) dialog.findViewById(R.id.dialog_tv_student_name);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.dialog_iv_close);
        text.setText("Hi " + studentName);

        dialog.show();

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (totalStudents == 5) {
                    content_frame.setVisibility(View.GONE);
                    showStudentName(totalStudents);
                } else {
                    if (setStud) {
                        setStud = false;
                        showStudentName(totalStudents);
                    }
                    scanNextQRCode();
                }

            }
        });

        Button scanNextQR = (Button) dialog.findViewById(R.id.dialog_btn_scan_qr);
        scanNextQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (totalStudents == 5) {
                    content_frame.setVisibility(View.GONE);
                    showStudentName(totalStudents);
                } else {
                    if (setStud) {
                        setStud = false;
                        showStudentName(totalStudents);
                    }
                    scanNextQRCode();
                }
            }
        });

    }


    public void qrEntryProcess(Result result) {
        if (result.getText().contains("{")) {
            totalStudents++;
            String sid = "", sname = "", sscore = "", salias = "";
            std = new Modal_Student(sid, sname, "QRGroupID");
//        Toast.makeText(this, "" + totalStudents, Toast.LENGTH_SHORT).show();
            if (totalStudents < 6) {

                // todo Parse json & separate id & name
                String resultID = "", resultName = "";
                try {
                    JSONObject jsonobject = new JSONObject(result.getText());
                    resultID = jsonobject.getString("stuId");
                    resultName = jsonobject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Valid pattern
                String[] id = {resultID};

                String stdId = id[0];
                //String stdFirstName = id[1];
                String[] name = {resultName};
                String stdFirstName = name[0];
                String stdLastName = "";
                if (name.length > 1)
                    stdLastName = name[1];

                std.setStudentId(stdId);
                std.setFirstName(stdFirstName);

//            Toast.makeText(QRLogin.this, "ID" + stdId, Toast.LENGTH_LONG).show();
//            Toast.makeText(QRLogin.this, "First" + stdFirstName, Toast.LENGTH_LONG).show();
                stdList.add(std);

                //scanNextQRCode();
                setStud = true;
                showQrDialog(stdFirstName);
            }
        } else {
            totalStudents++;
            String sid = "", sname = "", sscore = "", salias = "";
            std = new Modal_Student(sid, sname, "QRGroupID");
//        Toast.makeText(this, "" + totalStudents, Toast.LENGTH_SHORT).show();
            if (totalStudents < 6) {

                //Valid pattern
                String[] id = decodeStudentId(result.getText(), "-");

                String stdId = id[0];
                //String stdFirstName = id[1];
                String[] name = decodeStudentId(id[1], "_");
                String stdFirstName = name[0];
                String stdLastName = "";
                if (name.length > 1)
                    stdLastName = name[1];

                std.setStudentId(stdId);
                std.setFirstName(stdFirstName);

//            Toast.makeText(QRLogin.this, "ID" + stdId, Toast.LENGTH_LONG).show();
//            Toast.makeText(QRLogin.this, "First" + stdFirstName, Toast.LENGTH_LONG).show();
                stdList.add(std);

                //scanNextQRCode();
                setStud = true;
                showQrDialog(stdFirstName);
            }

        }


    }

    private String[] decodeStudentId(String text, String s) {
        return text.split(s);
    }

    private void showStudentName(int totalStudents) {

        switch (totalStudents) {
            case 1:
                tv_stud_one.setVisibility(View.VISIBLE);
                tv_stud_one.setText("" + stdList.get(0).getFirstName());
                break; // break is optional
            case 2:
                tv_stud_two.setVisibility(View.VISIBLE);
                tv_stud_two.setText("" + stdList.get(1).getFirstName());
                break; // break is optional
            case 3:
                tv_stud_three.setVisibility(View.VISIBLE);
                tv_stud_three.setText("" + stdList.get(2).getFirstName());
                break; // break is optional
            case 4:
                tv_stud_four.setVisibility(View.VISIBLE);
                tv_stud_four.setText("" + stdList.get(3).getFirstName());
                break; // break is optional
            case 5:
                tv_stud_five.setVisibility(View.VISIBLE);
                tv_stud_five.setText("" + stdList.get(4).getFirstName());
                break; // break is optional
        }
    }

    public void initCamera() {
        startCameraScan = new ZXingScannerView(this);
        startCameraScan.setResultHandler(this);
        content_frame.addView((startCameraScan));
        startCameraScan.startCamera();
        //startCameraScan.resumeCameraPreview(this);
    }
}
