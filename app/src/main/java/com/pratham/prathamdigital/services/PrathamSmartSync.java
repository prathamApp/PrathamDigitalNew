package com.pratham.prathamdigital.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Model_ContentProgress;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.services.auto_sync.AutoSync;
import com.pratham.prathamdigital.util.PD_Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.contentProgressDao;
import static com.pratham.prathamdigital.PrathamApplication.courseDao;
import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.scoreDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.statusDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;

public class PrathamSmartSync extends AutoSync {
    private static final String TAG = PrathamSmartSync.class.getSimpleName();

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
    }

    public static void pushUsageToServer(Boolean isPressed) {
        try {
            String programID = "";
            JSONObject rootJson = new JSONObject();
            Gson gson = new Gson();
            //iterate through all new sessions
            JSONArray sessionArray = new JSONArray();
            List<Modal_Session> newSessions = sessionDao.getAllNewSessions();
            for (Modal_Session session : newSessions) {
                //fetch all logs
                JSONArray logArray = new JSONArray();
                List<Modal_Log> allLogs = logDao.getAllLogs(session.getSessionID());
                for (Modal_Log log : allLogs)
                    logArray.put(new JSONObject(gson.toJson(log)));
                //fetch attendance
                JSONArray attendanceArray = new JSONArray();
                List<Attendance> newAttendance = attendanceDao.getNewAttendances(session.getSessionID());
                for (Attendance att : newAttendance) {
                    attendanceArray.put(new JSONObject(gson.toJson(att)));
                }
                //fetch Scores & convert to Json Array
                JSONArray scoreArray = new JSONArray();
                List<Modal_Score> newScores = scoreDao.getAllNewScores(session.getSessionID());
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
            if (newSessions.size() > 0) {
                //fetch Students & convert to Json Array
                JSONArray studentArray = new JSONArray();
                if (!PrathamApplication.isTablet) {
                    List<Modal_Student> newStudents = studentDao.getAllNewStudents();
                    for (Modal_Student std : newStudents)
                        studentArray.put(new JSONObject(gson.toJson(std)));
                }
                //fetch updated status
                JSONObject metadataJson = new JSONObject();
                List<Modal_Status> metadata = statusDao.getAllStatuses();
                for (Modal_Status status : metadata) {
                    metadataJson.put(status.getStatusKey(), status.getValue());
                    if (status.getStatusKey().equalsIgnoreCase("programId"))
                        programID = status.getValue();
                }
                //fetch enrolled courses
                JSONArray courseArray = new JSONArray();
                List<Model_CourseEnrollment> coursedata = courseDao.fetchUnpushedCourses();
                for (Model_CourseEnrollment course : coursedata) {
                    courseArray.put(new JSONObject(gson.toJson(course)));
                }
                //fetch courses progress
                JSONArray progArray = new JSONArray();
                List<Model_ContentProgress> progdata = contentProgressDao.fetchProgress();
                for (Model_ContentProgress progress : progdata) {
                    progArray.put(new JSONObject(gson.toJson(progress)));
                }
                metadataJson.put(PD_Constant.SCORE_COUNT, (metadata.size() > 0) ? metadata.size() : 0);
                if (!PrathamApplication.isTablet)
                    rootJson.put(PD_Constant.STUDENTS, studentArray);
                rootJson.put(PD_Constant.SESSION, sessionArray);
                rootJson.put(PD_Constant.METADATA, metadataJson);
                rootJson.put(PD_Constant.COURSE_ENROLLED, courseArray);
                rootJson.put(PD_Constant.COURSE_PROGRESS, progArray);
                if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
                    new PD_ApiRequest(PrathamApplication.getInstance())
                            .pushDataToRaspberry(PD_Constant.URL.DATASTORE_RASPBERY_URL.toString(),
                                    rootJson.toString(), programID, PD_Constant.USAGEDATA);
                else if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork() || PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
                    if (PrathamApplication.isTablet)
                        new PD_ApiRequest(PrathamApplication.getInstance())
                                .pushDataToInternet(PD_Constant.URL.POST_TAB_INTERNET_URL.toString(), rootJson);
                    else
                        new PD_ApiRequest(PrathamApplication.getInstance())
                                .pushDataToInternet(PD_Constant.URL.POST_SMART_INTERNET_URL.toString(), rootJson);
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

    @Override
    public void onSync(Context context) {
        Log.d(TAG, "onSync: ");
        // Push Tab related Jsons
        pushUsageToServer(false);
    }
}
