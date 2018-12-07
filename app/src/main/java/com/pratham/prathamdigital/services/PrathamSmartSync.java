package com.pratham.prathamdigital.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.services.auto_sync.AutoSync;
import com.pratham.prathamdigital.util.PD_Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class PrathamSmartSync extends AutoSync {
    private static final String TAG = PrathamSmartSync.class.getSimpleName();
    private static Context context;

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
    }

    @Override
    public void onSync(Context context) throws Exception {
        this.context = context;
        Log.d(TAG, "onSync: ");
        // Push Tab related Jsons
        pushTabletJsons(false);
    }

    public static void pushTabletJsons(Boolean isPressed) {
        try {
            String programID = "";
            Gson gson = new Gson();
            // create Root Json(result)
            JSONObject rootJson = new JSONObject();
            //fetch all logs
            List<Modal_Log> allLogs = BaseActivity.logDao.getAllLogs();
            JSONArray logArray = new JSONArray();
            for (Modal_Log att : allLogs) {
                logArray.put(new JSONObject(gson.toJson(att)));
            }
            //fetch updated status
            List<Modal_Status> metadata = BaseActivity.statusDao.getAllStatuses();
            JSONObject metadataJson = new JSONObject();
            for (Modal_Status status : metadata) {
                metadataJson.put(status.getStatusKey(), status.getValue());
                if (status.getStatusKey().equalsIgnoreCase("programId"))
                    programID = status.getValue();
            }
            //fetch all data based on sessionId
            JSONObject sessionJson = new JSONObject();
            if (!FastSave.getInstance().getString(PD_Constant.SESSIONID, "").isEmpty()) {
                String s_id = FastSave.getInstance().getString(PD_Constant.SESSIONID, "");
                //fetch attendance
                List<Attendance> newAttendance = BaseActivity.attendanceDao.getNewAttendances(s_id);
                JSONArray attendanceArray = new JSONArray();
                for (Attendance att : newAttendance) {
                    attendanceArray.put(new JSONObject(gson.toJson(att)));
                }
                //fetch Scores & convert to Json Array
                List<Modal_Score> newScores = BaseActivity.scoreDao.getAllNewScores(s_id);
                JSONArray scoreArray = new JSONArray();
                for (Modal_Score score : newScores) {
                    scoreArray.put(new JSONObject(gson.toJson(score)));
                }
                JSONArray studentArray = new JSONArray();
                if (!PrathamApplication.isTablet) {
                    //fetch Students & convert to Json Array
                    List<Modal_Student> newStudents = BaseActivity.studentDao.getAllNewStudents();
                    for (Modal_Student std : newStudents) {
                        studentArray.put(new JSONObject(gson.toJson(std)));
                    }
                }
                // fetch Session Data
                Modal_Session session = BaseActivity.sessionDao.getSession(s_id);
                sessionJson.put(PD_Constant.SESSIONID, session.getSessionID());
                sessionJson.put(PD_Constant.FROMDATE, session.getFromDate());
                sessionJson.put(PD_Constant.TODATE, session.getToDate());
                sessionJson.put(PD_Constant.SCORE, scoreArray);
                sessionJson.put(PD_Constant.ATTENDANCE, attendanceArray);

                if (!PrathamApplication.isTablet)
                    rootJson.put(PD_Constant.STUDENTS, studentArray);
                rootJson.put(PD_Constant.SESSION, sessionJson);
                rootJson.put(PD_Constant.LOGS, logArray);
                rootJson.put(PD_Constant.METADATA, metadataJson);

                // send if new records found
                if (newAttendance != null && newAttendance.size() > 0) {
                    if (PrathamApplication.isTablet && PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
                        new PD_ApiRequest(PrathamApplication.getInstance(), null)
                                .pushDataToRaspberry(PD_Constant.USAGEDATA, PD_Constant.URL.DATASTORE_RASPBERY_URL.toString(),
                                        rootJson.toString(), programID, PD_Constant.USAGEDATA);
                    else if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork() || PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
                        new PD_ApiRequest(PrathamApplication.getInstance(), null)
                                .pushDataToInternet(PD_Constant.USAGEDATA, PD_Constant.URL.POST_INTERNET_URL.toString(), rootJson);
                    }

                } else {
                    if (isPressed) {
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.SUCCESSFULLYPUSHED);
                        EventBus.getDefault().post(msg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
