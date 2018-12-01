package com.pratham.prathamdigital.services;

import android.content.Context;
import android.util.Log;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.services.auto_sync.AutoSync;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PrathamSmartSync extends AutoSync {
    private static final String TAG = PrathamSmartSync.class.getSimpleName();

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
    }

    @Override
    public void onSync(Context context) throws Exception {
        Log.d(TAG, "onSync: ");
        if (PrathamApplication.isTablet) {
            // Push Tab related Jsons
            pushTabletJsons(false);
        } else {
            // Push Smartphone related Jsons
            pushSmartphoneJsons();
        }
    }

    public static void pushTabletJsons(Boolean isPressed) {

        // Score Table
        JSONArray scoreData = new JSONArray();
        List<Modal_Score> scores = BaseActivity.scoreDao.getAllNewScores();
        if (scores != null && scores.size() > 0) {
            try {
                for (Modal_Score scoreObj : scores) {
                    JSONObject _obj = new JSONObject();
                    _obj.put("sessionId", scoreObj.getSessionID());
                    _obj.put("deviceId", scoreObj.getDeviceID());
                    _obj.put("resourceId", scoreObj.getResourceID());
                    _obj.put("questionId", scoreObj.getQuestionId());
                    _obj.put("scoredMarks", scoreObj.getScoredMarks());
                    _obj.put("totalMarks", scoreObj.getTotalMarks());
                    _obj.put("startDateTime", scoreObj.getStartDateTime());
                    _obj.put("endDateTime", scoreObj.getEndDateTime());
                    _obj.put("level", scoreObj.getLevel());
                    _obj.put("label", scoreObj.getLabel());
                    scoreData.put(_obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Session Table
        JSONArray sessionData = new JSONArray();
        List<Modal_Session> session = BaseActivity.sessionDao.getAllSessions();
        if (session != null && session.size() > 0) {
            try {
                for (Modal_Session sessionObj : session) {
                    JSONObject _obj = new JSONObject();
                    _obj.put("sessionId", sessionObj.getSessionID());
                    _obj.put("fromDate", sessionObj.getFromDate());
                    _obj.put("toDate", sessionObj.getToDate());
                    sessionData.put(_obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Attendance Table     (get list of distinct session id)
        List<String> distinctSessions = BaseActivity.attendanceDao.getAllDistinctSessions();
        JSONObject attendanceObject;
        List<Integer> presentStudents;
        JSONArray presentStudentsJsonArray;
        JSONArray attendanceData = new JSONArray();
        try {
            // get present grpid & present student ids
            for (int x = 0; x < distinctSessions.size(); x++) {
                String grpID = "";
                attendanceObject = new JSONObject();
                presentStudentsJsonArray = new JSONArray();

                grpID = BaseActivity.attendanceDao.GetGrpIDBySessionID(distinctSessions.get(x));
                presentStudents = BaseActivity.attendanceDao.GetAllPresentStdBySessionId(distinctSessions.get(x));

                presentStudentsJsonArray = new JSONArray();
                for (int i = 0; i < presentStudents.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", presentStudents.get(i));
                    presentStudentsJsonArray.put(obj);
                }

                attendanceObject.put("SessionID", distinctSessions.get(x));
                attendanceObject.put("GroupID", grpID);
                attendanceObject.put("PresentStudentIds", presentStudentsJsonArray);

                Log.d("attendance obj :::", attendanceObject.toString());
                attendanceData.put(attendanceObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Status Table
        JSONObject statusObj = new JSONObject();
        try {
            statusObj.put("ScoreCount", scores.size());
            statusObj.put("SessionCount", sessionData.length());
            statusObj.put("AttendanceCount", attendanceData.length());
            statusObj.put("CRLID", BaseActivity.statusDao.getValue("crlId"));
            statusObj.put("TransId", PD_Utility.getUUID().toString());
            statusObj.put("DeviceId", BaseActivity.statusDao.getValue("deviceId"));
            statusObj.put("ActivatedDate", BaseActivity.statusDao.getValue("ActivatedDate"));
            statusObj.put("ActivatedForGroups", BaseActivity.statusDao.getValue("ActivatedForGroups"));
            statusObj.put("Latitude", BaseActivity.statusDao.getValue("Latitude"));
            statusObj.put("Longitude", BaseActivity.statusDao.getValue("Longitude"));
            statusObj.put("GPSDateTime", BaseActivity.statusDao.getValue("GPSDateTime"));
            statusObj.put("SerialID", BaseActivity.statusDao.getValue("SerialID"));
            statusObj.put("apkVersion", BaseActivity.statusDao.getValue("apkVersion"));
            statusObj.put("appName", BaseActivity.statusDao.getValue("appName"));
            statusObj.put("gpsFixDuration", BaseActivity.statusDao.getValue("gpsFixDuration"));
            statusObj.put("wifiMAC", BaseActivity.statusDao.getValue("wifiMAC"));
            statusObj.put("apkType", BaseActivity.statusDao.getValue("apkType"));
            statusObj.put("prathamCode", BaseActivity.statusDao.getValue("prathamCode"));
            statusObj.put("programId", BaseActivity.statusDao.getValue("programId"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String programId = "";
        String collectedData = "";

        try {
            // Pushing File to Server
            programId = BaseActivity.statusDao.getValue("programId");
            collectedData = "{ \"metadata\": " + statusObj + ", \"scoreData\": " + scoreData + ", \"sessionData\": " + sessionData + ", \"attendanceData\": " + attendanceData + "}";
        } finally {
            // Send only if new Data is available
            if (scoreData.length() > 0 || sessionData.length() > 0 || attendanceData.length() > 0) {
                new PD_ApiRequest(PrathamApplication.getInstance(), null)
                        .pushDataToRaspberry("USAGEDATA", PD_Constant.RASP_IP + "/pratham/datastore/",
                                collectedData, programId, "USAGEDATA");
            } else {
                if (isPressed) {
                    EventMessage msg = new EventMessage();
                    msg.setMessage(PD_Constant.SUCCESSFULLYPUSHED);
                    EventBus.getDefault().post(msg);
                }
            }
        }

    }

    public static void pushSmartphoneJsons() {

        // Score Table
        JSONArray scoreData = new JSONArray();
        List<Modal_Score> scores = BaseActivity.scoreDao.getAllNewScores();
        if (scores != null && scores.size() > 0) {
            try {
                for (Modal_Score scoreObj : scores) {
                    JSONObject _obj = new JSONObject();
                    _obj.put("sessionId", scoreObj.getSessionID());
                    _obj.put("deviceId", scoreObj.getDeviceID());
                    _obj.put("resourceId", scoreObj.getResourceID());
                    _obj.put("questionId", scoreObj.getQuestionId());
                    _obj.put("scoredMarks", scoreObj.getScoredMarks());
                    _obj.put("totalMarks", scoreObj.getTotalMarks());
                    _obj.put("startDateTime", scoreObj.getStartDateTime());
                    _obj.put("endDateTime", scoreObj.getEndDateTime());
                    _obj.put("level", scoreObj.getLevel());
                    _obj.put("label", scoreObj.getLabel());
                    scoreData.put(_obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Session Table
        JSONArray sessionData = new JSONArray();
        List<Modal_Session> session = BaseActivity.sessionDao.getAllSessions();
        if (session != null && session.size() > 0) {
            try {
                for (Modal_Session sessionObj : session) {
                    JSONObject _obj = new JSONObject();
                    _obj.put("sessionId", sessionObj.getSessionID());
                    _obj.put("fromDate", sessionObj.getFromDate());
                    _obj.put("toDate", sessionObj.getToDate());
                    sessionData.put(_obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Attendance Table     (get list of distinct session id)
        List<String> distinctSessions = BaseActivity.attendanceDao.getAllDistinctSessions();
        JSONObject attendanceObject;
        List<Integer> presentStudents;
        JSONArray presentStudentsJsonArray;
        JSONArray attendanceData = new JSONArray();
        try {
            // get present grpid & present student ids
            for (int x = 0; x < distinctSessions.size(); x++) {
                String grpID = "";
                attendanceObject = new JSONObject();
                presentStudentsJsonArray = new JSONArray();

                grpID = BaseActivity.attendanceDao.GetGrpIDBySessionID(distinctSessions.get(x));
                presentStudents = BaseActivity.attendanceDao.GetAllPresentStdBySessionId(distinctSessions.get(x));

                presentStudentsJsonArray = null;
                for (int i = 0; i < presentStudents.size(); i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", presentStudents.get(i));
                    presentStudentsJsonArray.put(obj);
                }

                attendanceObject.put("SessionID", distinctSessions.get(x));
                attendanceObject.put("GroupID", grpID);
                attendanceObject.put("PresentStudentIds", presentStudentsJsonArray);

                Log.d("attendance obj :::", attendanceObject.toString());
                attendanceData.put(attendanceObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //For New Students data
        List<Modal_Student> studentsList = BaseActivity.studentDao.getAllNewStudents();
        JSONArray studentData = new JSONArray();
        if (studentsList != null) {
            try {
                for (Modal_Student student : studentsList) {
                    JSONObject studentObj = new JSONObject();
                    studentObj.put("StudentID", student.getStudentId());
                    studentObj.put("FirstName", student.getFirstName());
                    studentObj.put("MiddleName", student.getMiddleName());
                    studentObj.put("LastName", student.getLastName());
                    studentObj.put("Age", student.getAge());
                    studentObj.put("Class", student.getStud_Class());
                    studentObj.put("Gender", student.getGender());
                    studentObj.put("GroupID", student.getGroupId());
                    studentData.put(studentObj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Status Table
        JSONObject statusObj = new JSONObject();
        try {
            statusObj.put("ScoreCount", scores.size());
            statusObj.put("SessionCount", sessionData.length());
            statusObj.put("AttendanceCount", attendanceData.length());
            statusObj.put("CRLID", BaseActivity.statusDao.getValue("crlId"));
            statusObj.put("NewStudentsCount", studentData.length());
            statusObj.put("TransId", PD_Utility.getUUID().toString());
            statusObj.put("DeviceId", BaseActivity.statusDao.getValue("deviceId"));
            statusObj.put("ActivatedDate", BaseActivity.statusDao.getValue("ActivatedDate"));
            statusObj.put("ActivatedForGroups", BaseActivity.statusDao.getValue("ActivatedForGroups"));
            statusObj.put("Latitude", BaseActivity.statusDao.getValue("Latitude"));
            statusObj.put("Longitude", BaseActivity.statusDao.getValue("Longitude"));
            statusObj.put("GPSDateTime", BaseActivity.statusDao.getValue("GPSDateTime"));
            statusObj.put("SerialID", BaseActivity.statusDao.getValue("SerialID"));
            statusObj.put("apkVersion", BaseActivity.statusDao.getValue("apkVersion"));
            statusObj.put("appName", BaseActivity.statusDao.getValue("appName"));
            statusObj.put("gpsFixDuration", BaseActivity.statusDao.getValue("gpsFixDuration"));
            statusObj.put("wifiMAC", BaseActivity.statusDao.getValue("wifiMAC"));
            statusObj.put("apkType", BaseActivity.statusDao.getValue("apkType"));
            statusObj.put("prathamCode", BaseActivity.statusDao.getValue("prathamCode"));
            statusObj.put("programId", BaseActivity.statusDao.getValue("programId"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String programId = "";
        String collectedData = "";

        try {
            // Pushing File to Server
            programId = BaseActivity.statusDao.getValue("programId");
            collectedData = "{ \"metadata\": " + statusObj + ", \"scoreData\": " + scoreData + ", \"sessionData\": " + sessionData + ", \"attendanceData\": " + attendanceData + ", \"newStudentsData\": " + studentData + "}";
        } finally {
            // Send only if new Data is available
            if (scoreData.length() > 0 || sessionData.length() > 0 || attendanceData.length() > 0 || studentData.length() > 0) {
                new PD_ApiRequest(PrathamApplication.getInstance(), null)
                        .pushDataToRaspberry("USAGEDATA", PD_Constant.RASP_IP + "/pratham/datastore/",
                                collectedData, programId, "USAGEDATA");
            }
        }

    }
}
