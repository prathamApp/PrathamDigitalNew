package com.pratham.prathamdigital.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.PD_ApiRequest;
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
            JSONObject rootJson = new JSONObject();
            Gson gson = new Gson();
            //iterate through all new sessions
            JSONArray sessionArray = new JSONArray();
            List<Modal_Session> newSessions = BaseActivity.sessionDao.getAllNewSessions();
            for (Modal_Session session : newSessions) {
                //fetch all logs
                JSONArray logArray = new JSONArray();
                List<Modal_Log> allLogs = BaseActivity.logDao.getAllLogs(session.getSessionID());
                for (Modal_Log log : allLogs)
                    logArray.put(new JSONObject(gson.toJson(log)));
                //fetch attendance
                JSONArray attendanceArray = new JSONArray();
                List<Attendance> newAttendance = BaseActivity.attendanceDao.getNewAttendances(session.getSessionID());
                for (Attendance att : newAttendance) {
                    attendanceArray.put(new JSONObject(gson.toJson(att)));
                }
                //fetch Scores & convert to Json Array
                JSONArray scoreArray = new JSONArray();
                List<Modal_Score> newScores = BaseActivity.scoreDao.getAllNewScores(session.getSessionID());
                for (Modal_Score score : newScores) {
                    scoreArray.put(new JSONObject(gson.toJson(score)));
                }
                // fetch Session Data
                JSONObject sessionJson = new JSONObject();
                sessionJson.put(PD_Constant.SESSIONID, session.getSessionID());
                sessionJson.put(PD_Constant.FROMDATE, session.getFromDate());
                sessionJson.put(PD_Constant.TODATE, session.getToDate());
                sessionJson.put(PD_Constant.SCORE, scoreArray);
                sessionJson.put(PD_Constant.ATTENDANCE, attendanceArray);
                sessionJson.put(PD_Constant.LOGS, logArray);

                sessionArray.put(sessionJson);
            }
            // send if new records found
            if (newSessions != null && newSessions.size() > 0) {
                //fetch Students & convert to Json Array
                JSONArray studentArray = new JSONArray();
                if (!PrathamApplication.isTablet) {
                    List<Modal_Student> newStudents = BaseActivity.studentDao.getAllNewStudents();
                    for (Modal_Student std : newStudents)
                        studentArray.put(new JSONObject(gson.toJson(std)));
                }
                //fetch updated status
                JSONObject metadataJson = new JSONObject();
                List<Modal_Status> metadata = BaseActivity.statusDao.getAllStatuses();
                for (Modal_Status status : metadata) {
                    metadataJson.put(status.getStatusKey(), status.getValue());
                    if (status.getStatusKey().equalsIgnoreCase("programId"))
                        programID = status.getValue();
                }
                if (!PrathamApplication.isTablet)
                    rootJson.put(PD_Constant.STUDENTS, studentArray);
                rootJson.put(PD_Constant.SESSION, sessionArray);
                rootJson.put(PD_Constant.METADATA, metadataJson);
                if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
                    new PD_ApiRequest(PrathamApplication.getInstance(), null)
                            .pushDataToRaspberry(PD_Constant.USAGEDATA, PD_Constant.URL.DATASTORE_RASPBERY_URL.toString(),
                                    rootJson.toString(), programID, PD_Constant.USAGEDATA);
                else if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork() || PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
                    if (PrathamApplication.isTablet)
                        new PD_ApiRequest(PrathamApplication.getInstance(), null)
                                .pushDataToInternet(PD_Constant.USAGEDATA, PD_Constant.URL.POST_TAB_INTERNET_URL.toString(), rootJson);
                    else
                        new PD_ApiRequest(PrathamApplication.getInstance(), null)
                                .pushDataToInternet(PD_Constant.USAGEDATA, PD_Constant.URL.POST_SMART_INTERNET_URL.toString(), rootJson);
                }
            } else {
                if (isPressed) {
                    EventMessage msg = new EventMessage();
                    msg.setMessage(PD_Constant.SUCCESSFULLYPUSHED);
                    EventBus.getDefault().post(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
