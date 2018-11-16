package com.pratham.prathamdigital.services;

import android.content.Context;
import android.util.Log;

import com.pratham.prathamdigital.services.auto_sync.AutoSync;

public class PrathamSmartSync extends AutoSync {
    private static final String TAG = PrathamSmartSync.class.getSimpleName();

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
    }

    @Override
    public void onSync(Context context) throws Exception {
        Log.d(TAG, "onSync: ");
        /*JSONArray scoreData = new JSONArray();
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
                    scoreData.put(_obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Attendance> attendanceData = BaseActivity.attendanceDao.getNewAttendances(0);
        if (attendanceData != null && !attendanceData.isEmpty()) {
            // get list of distinct session id
            // get present grpid & present student ids
            for (int x = 0; x < distinctSessions.size(); x++) {
                String presentStd = "", grpID = "";
                attendanceObject = new JSONObject();
                presentStudents = new JSONArray();

                presentStd = attendanceDBHelper1.GetAllPresentStudentBySessionId(distinctSessions.get(x));
                grpID = attendanceDBHelper1.GetGrpIDBySessionID(distinctSessions.get(x));
                presentStudents = attendanceDBHelper1.GetAllPresentStdBySessionId(distinctSessions.get(x));
                try {
                    attendanceObject.put("SessionID", distinctSessions.get(x));

                    attendanceObject.put("GroupID", grpID);

                    attendanceObject.put("PresentStudentIds", presentStudents);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("attendance obj :::", attendanceObject.toString());
                attendanceData.put(attendanceObject);
            }

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
//                    studentObj.put("UpdatedDate", student.UpdatedDate);
                    studentObj.put("Gender", student.getGender());
                    studentObj.put("GroupID", student.getGroupId());
//                    studentObj.put("CreatedBy", student.get);
//                    studentObj.put("newStudent", student.newStudent); // DO THE CHANGES for HANDLING NULLS
//                    studentObj.put("StudentUID", student.StudentUID == null ? "" : student.StudentUID);
//                    studentObj.put("IsSelected", student.IsSelected == null ? false : !student.IsSelected);
                    // new entries
//                    studentObj.put("sharedBy", student.sharedBy == null ? "" : student.sharedBy);
//                    studentObj.put("SharedAtDateTime", student.SharedAtDateTime == null ? "" : student.SharedAtDateTime);
//                    studentObj.put("appName", student.appName == null ? "" : student.appName);
//                    studentObj.put("appVersion", student.appVersion == null ? "" : student.appVersion);
//                    studentObj.put("CreatedOn", student.CreatedOn == null ? "" : student.CreatedOn);
                    studentData.put(studentObj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONObject statusObj = new JSONObject();
        try {
            statusObj.put("ScoreCount", scores.size());
            statusObj.put("AttendanceCount", attendanceData.length());
            statusObj.put("CRLID", statusDBHelper.getValue("crlId").equals(null) ? "admin" : statusDBHelper.getValue("crlId"));
            //obj.put("LogsCount", logs.size());
            statusObj.put("NewStudentsCount", studentData.length());
            statusObj.put("TransId", new Utility().GetUniqueID());
            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            statusObj.put("DeviceId", deviceId.equals(null) ? "0000" : deviceId);
            statusObj.put("MobileNumber", "0");
            statusObj.put("ActivatedDate", statusDBHelper.getValue("ActivatedDate"));
            statusObj.put("ActivatedForGroups", statusDBHelper.getValue("ActivatedForGroups"));

            // new status table fields
            statusObj.put("Latitude", statusDBHelper.getValue("Latitude"));
            statusObj.put("Longitude", statusDBHelper.getValue("Longitude"));
            statusObj.put("GPSDateTime", statusDBHelper.getValue("GPSDateTime"));
            statusObj.put("AndroidID", statusDBHelper.getValue("AndroidID"));
            statusObj.put("SerialID", statusDBHelper.getValue("SerialID"));
            statusObj.put("apkVersion", statusDBHelper.getValue("apkVersion"));
            statusObj.put("appName", statusDBHelper.getValue("appName"));
            statusObj.put("gpsFixDuration", statusDBHelper.getValue("gpsFixDuration"));
            statusObj.put("wifiMAC", statusDBHelper.getValue("wifiMAC"));
            statusObj.put("apkType", statusDBHelper.getValue("apkType"));
            statusObj.put("prathamCode", statusDBHelper.getValue("prathamCode"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Pushing File to Server
        String programId = statusDBHelper.getValue("programId");
        String requestString = "{ \"metadata\": " + statusObj + ", \"scoreData\": " + scoreData + ", \"attendanceData\": " + attendanceData + ", \"newStudentsData\": " + studentData + "}";
        Log.d("array:::", scoreData.toString());
*/
    }
}
