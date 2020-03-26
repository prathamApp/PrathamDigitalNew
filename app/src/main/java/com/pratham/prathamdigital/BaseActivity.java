package com.pratham.prathamdigital;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.pratham.prathamdigital.async.CopyDbToOTG;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_PushData;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Model_ContentProgress;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.services.TTSService;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import net.alhazmy13.catcho.library.Catcho;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.contentProgressDao;
import static com.pratham.prathamdigital.PrathamApplication.courseDao;
import static com.pratham.prathamdigital.PrathamApplication.scoreDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.statusDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int UPDATE_CONNECTION = 1;
    private static final int HIDE_SYSTEM_UI = 2;
    private static final int REQUEST_WRITE_PERMISSION = 6;
    private static final int GET_LOCATION_PERMISSION = 7;
    private static final int GET_READ_PHONE_STATE = 8;
    private static final int SHOW_OTG_TRANSFER_DIALOG = 9;
    private static final int SDCARD_LOCATION_CHOOSER = 10;
    private static final int SHOW_OTG_SELECT_DIALOG = 11;
    private static final int HIDE_OTG_TRANSFER_DIALOG_SUCCESS = 12;
    private static final int HIDE_OTG_TRANSFER_DIALOG_FAILED = 13;

    @SuppressLint("StaticFieldLeak")
    public static TTSService ttsService;
    LottieAnimationView push_lottie;
    TextView txt_push_dialog_msg;
    TextView txt_push_error;
    BlurPopupWindow pushDialog;
    BlurPopupWindow sd_builder;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @SuppressLint({"MissingPermission", "SetTextI18n"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CONNECTION:
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                    EventMessage message = new EventMessage();
                    message.setMessage(PD_Constant.CONNECTION_STATUS);
                    if (wifi != null && wifi.isConnected()) {
                        message.setConnection_resource(getResources().getDrawable(R.drawable.ic_dialog_connect_wifi_item));
                        message.setConnection_name(Objects.requireNonNull(PrathamApplication.wiseF.getCurrentNetwork()).getSSID());
                    } else if (mobile != null && mobile.isConnected()) {
                        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String carrierName = manager.getNetworkOperatorName();
                        message.setConnection_resource(getResources().getDrawable(R.drawable.ic_4g_network));
                        message.setConnection_name(carrierName);
                    } else {
                        message.setConnection_resource(getResources().getDrawable(R.drawable.ic_no_wifi));
                        message.setConnection_name(PD_Constant.NO_CONNECTION);
                    }
                    EventBus.getDefault().post(message);
                    break;
                case HIDE_SYSTEM_UI:
                    getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
//                    requestWindowFeature(Window.FEATURE_NO_TITLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LOW_PROFILE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    );
                    break;
                case REQUEST_WRITE_PERMISSION:
                    KotlinPermissions.with(BaseActivity.this)
                            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .onAccepted(permissionResult -> {
                                BackupDatabase.backup(BaseActivity.this);
                            })
                            .ask();
                    break;
                case GET_LOCATION_PERMISSION:
                    KotlinPermissions.with(BaseActivity.this)
                            .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_PHONE_STATE)
                            .onAccepted(permissionResult -> {
                                new LocationService(BaseActivity.this).checkLocation();
                                mHandler.sendEmptyMessage(GET_READ_PHONE_STATE);
                            })
                            .ask();
                    break;
                case GET_READ_PHONE_STATE:
                    KotlinPermissions.with(BaseActivity.this)
                            .permissions(Manifest.permission.READ_PHONE_STATE)
                            .onAccepted(permissionResult -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    Modal_Status statusObj = new Modal_Status();
                                    statusObj.setStatusKey("SerialID");
                                    statusObj.setValue(Build.getSerial());
                                    statusDao.insert(statusObj);
                                }
                            })
                            .ask();
                    break;
                case SHOW_OTG_TRANSFER_DIALOG:
                    sd_builder = new BlurPopupWindow.Builder(BaseActivity.this)
                            .setContentView(R.layout.dialog_alert_sd_card)
                            .setGravity(Gravity.CENTER)
                            .setScaleRatio(0.2f)
                            .bindClickListener(v -> {
                                new Handler().postDelayed(() -> {
                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    startActivityForResult(intent, SDCARD_LOCATION_CHOOSER);
                                }, 1200);
                                sd_builder.dismiss();
                            }, R.id.txt_choose_sd_card)
                            .setDismissOnClickBack(true)
                            .setDismissOnTouchBackground(false)
                            .setScaleRatio(0.2f)
                            .setBlurRadius(8)
                            .setTintColor(0x30000000)
                            .build();
                    ((TextView) sd_builder.findViewById(R.id.txt_choose_sd_card)).setText("Select OTG");
                    sd_builder.show();
                    break;
                case SHOW_OTG_SELECT_DIALOG:
                    pushDialog = new BlurPopupWindow.Builder(BaseActivity.this)
                            .setContentView(R.layout.app_success_dialog)
                            .setGravity(Gravity.CENTER)
                            .setScaleRatio(0.2f)
                            .setDismissOnClickBack(true)
                            .setDismissOnTouchBackground(true)
                            .setBlurRadius(10)
                            .setTintColor(0x30000000)
                            .build();
                    push_lottie = pushDialog.findViewById(R.id.push_lottie);
                    txt_push_dialog_msg = pushDialog.findViewById(R.id.txt_push_dialog_msg);
                    txt_push_error = pushDialog.findViewById(R.id.txt_push_error);
                    pushDialog.show();
                    break;
                case HIDE_OTG_TRANSFER_DIALOG_SUCCESS:
                    push_lottie.setAnimation("success.json");
                    push_lottie.playAnimation();
                    txt_push_dialog_msg.setText("Data Copied Successfully!!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pushDialog.dismiss();
                        }
                    }, 1500);
                    break;
                case HIDE_OTG_TRANSFER_DIALOG_FAILED:
                    push_lottie.setAnimation("error_cross.json");
                    push_lottie.playAnimation();
                    txt_push_dialog_msg.setText("Data Copying Failed!! Please re-insert the OTG");
                    txt_push_error.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pushDialog.dismiss();
                        }
                    }, 1500);
                    break;
            }
        }
    };
    private final Connectable connectable = () -> mHandler.sendEmptyMessage(UPDATE_CONNECTION);
    private final Disconnectable disconnectable = () -> mHandler.sendEmptyMessage(UPDATE_CONNECTION);
    private Merlin merlin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mHandler.sendEmptyMessage(HIDE_SYSTEM_UI);
        super.onCreate(savedInstanceState);
        //Utility initialized for shuffling the color codes
        PD_Utility pd_utility = new PD_Utility(this);
        Catcho.Builder(this)
                .activity(CatchoTransparentActivity.class)
//                .recipients("abc@gm.com")
                .build();
        initializeConnectionService();
        initializeTTS();
    }

    private void initializeTTS() {
        ttsService = new TTSService(getApplication());
        ttsService.setActivity(BaseActivity.this);
        ttsService.setSpeechRate(0.7f);
        ttsService.setLanguage(new Locale("en", "IN"));
    }

    private void initializeConnectionService() {
        merlin = new Merlin.Builder().withConnectableCallbacks()
                .withDisconnectableCallbacks().build(BaseActivity.this);
        merlin.registerConnectable(connectable);
        merlin.registerDisconnectable(disconnectable);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(HIDE_SYSTEM_UI);
        merlin.bind();
        mHandler.sendEmptyMessage(GET_LOCATION_PERMISSION);
        BackupDatabase.backup(this);
    }

    @Override
    protected void onPause() {
        merlin.unbind();
        BackupDatabase.backup(this);
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        mHandler.sendEmptyMessage(HIDE_SYSTEM_UI);
    }

    @Subscribe
    public void requestStoragePermission(final String permission) {
        if (permission.equalsIgnoreCase(PD_Constant.WRITE_PERMISSION)) {
            mHandler.sendEmptyMessage(REQUEST_WRITE_PERMISSION);
        }
    }

    @Subscribe
    public void updateFlagsWhenPushed(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.SUCCESSFULLYPUSHED)) {
                Gson gson = new Gson();
                Modal_PushData pushedData = gson.fromJson(message.getPushData(), Modal_PushData.class);
                for (Modal_PushData.Modal_PushSessionData pushed : pushedData.getPushSession()) {
                    sessionDao.updateFlag(pushed.getSessionId());
                    for (Modal_Score score : pushed.getScores())
                        scoreDao.updateFlag(pushed.getSessionId());
                    for (Attendance att : pushed.getAttendances())
                        attendanceDao.updateSentFlag(pushed.getSessionId());
                }
                for (Model_CourseEnrollment enroll : pushedData.getCourse_enrolled()) {
                    courseDao.updateFlag(enroll.getCourseId(), enroll.getGroupId(), enroll.getLanguage());
                }
                for (Model_ContentProgress prog : pushedData.getCourse_progress()) {
                    contentProgressDao.updateFlag(prog.getStudentId(), prog.getResourceId());
                }
                if (pushedData.getStudents() != null)
                    for (Modal_Student student : pushedData.getStudents())
                        studentDao.updateSentStudentFlags(student.getStudentId());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.OTG_INSERTED)) {
                mHandler.sendEmptyMessage(SHOW_OTG_TRANSFER_DIALOG);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.BACKUP_DB_COPIED)) {
                mHandler.sendEmptyMessage(HIDE_OTG_TRANSFER_DIALOG_SUCCESS);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.BACKUP_DB_NOT_COPIED)) {
                mHandler.sendEmptyMessage(HIDE_OTG_TRANSFER_DIALOG_FAILED);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SDCARD_LOCATION_CHOOSER) {
            if (data != null && data.getData() != null) {
                Uri treeUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                PrathamApplication.getInstance().getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                mHandler.sendEmptyMessage(SHOW_OTG_SELECT_DIALOG);
                new CopyDbToOTG().execute(treeUri);
            }
        }
    }
}