package com.pratham.prathamdigital.services;

import android.content.Context;
import android.util.Log;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.services.auto_sync.AutoSync;
import com.pratham.prathamdigital.util.PD_Utility;

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
            pushTabletJsons();
        } else {
            // Push Smartphone related Jsons
            pushSmartphoneJsons();
        }
    }

    private void pushTabletJsons() {
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

        // get list of distinct session id
        List<String> distinctSessions = BaseActivity.attendanceDao.getAllDistinctSessions();
        JSONObject attendanceObject;
        List<Integer> presentStudents;
        JSONArray presentStudentsJsonArray;
        JSONArray attendanceData = null;
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
            statusObj.put("AndroidID", BaseActivity.statusDao.getValue("AndroidID"));
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

        // Pushing File to Server
        String programId = BaseActivity.statusDao.getValue("programId");
        String collectedData = "{ \"metadata\": " + statusObj + ", \"scoreData\": " + scoreData + ", \"attendanceData\": " + attendanceData + ", \"newStudentsData\": " + studentData + "}";
    }

    private void pushSmartphoneJsons() {

    }
}
