package com.pratham.prathamdigital.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Model_ContentProgress;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.services.auto_sync.AutoSync;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.contentProgressDao;
import static com.pratham.prathamdigital.PrathamApplication.courseDao;
import static com.pratham.prathamdigital.PrathamApplication.groupDao;
import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.scoreDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.statusDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;

public class PrathamSmartSyncNew extends AutoSync {
    private static final String TAG = PrathamSmartSyncNew.class.getSimpleName();
    public static String courseCount = "";
    public static String scoreCount = "";

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
    }

    public static void pushUsageToServer(Boolean isPressed, String pushType, Context context) {
        try {
            String programID = "";
            JSONObject rootJson = new JSONObject();
            Gson gson = new Gson();

            // fetch Session Data
            JSONArray sessionArray = new JSONArray();
            List<Modal_Session> newSessions = sessionDao.getAllNewSessions();
            for (Modal_Session session : newSessions) {
                sessionArray.put(new JSONObject(gson.toJson(session)));
            }

            //fetch all logs
            JSONArray logArray = new JSONArray();
            List<Modal_Log> allLogs = logDao.getAllLogs();
            for (Modal_Log log : allLogs)
                logArray.put(new JSONObject(gson.toJson(log)));

            //fetch attendance
            JSONArray attendanceArray = new JSONArray();
            List<Attendance> newAttendance = attendanceDao.getNewAttendances();
            for (Attendance att : newAttendance) {
                attendanceArray.put(new JSONObject(gson.toJson(att)));
            }

            //fetch Scores & convert to Json Array
            JSONArray scoreArray = new JSONArray();
            List<Modal_Score> newScores = scoreDao.getAllNewScores();
            for (Modal_Score score : newScores) {
                scoreArray.put(new JSONObject(gson.toJson(score)));
            }
            scoreCount = String.valueOf(newScores.size());

            //fetch Students & convert to Json Array
            JSONArray studentArray = new JSONArray();
            if (!PrathamApplication.isTablet) {
                List<Modal_Student> newStudents = studentDao.getAllNewStudents();
                for (Modal_Student std : newStudents)
                    studentArray.put(new JSONObject(gson.toJson(std)));
            }

            //fetch groups & convert to Json Array
            JSONArray groupArray = new JSONArray();
            List<Modal_Groups> newgroups = groupDao.getAllGroups();
            for (Modal_Groups grp : newgroups) groupArray.put(new JSONObject(gson.toJson(grp)));

            //fetch enrolled courses
            JSONArray courseArray = new JSONArray();
            List<Model_CourseEnrollment> coursedata = courseDao.fetchUnpushedCourses();
            for (Model_CourseEnrollment course : coursedata) {
                courseArray.put(new JSONObject(gson.toJson(course)));
            }
            courseCount = String.valueOf(coursedata.size());
            Log.e("url cc : ", courseCount);

            //fetch courses progress
            JSONArray progArray = new JSONArray();
            List<Model_ContentProgress> progdata = contentProgressDao.fetchProgress();
            for (Model_ContentProgress progress : progdata) {
                progArray.put(new JSONObject(gson.toJson(progress)));
            }

            //fetch updated status
            JSONObject metadataJson = new JSONObject();
            List<Modal_Status> metadata = statusDao.getAllStatuses();
            metadataJson.put("ScoreCount", scoreArray.length());
            metadataJson.put("AttendanceCount", attendanceArray.length());
            metadataJson.put("SessionCount", sessionArray.length());
            metadataJson.put("LogsCount", logArray.length());
            metadataJson.put("StudentCount", studentArray.length());
            metadataJson.put("ContentProgressCount", progArray.length());
            metadataJson.put("CourseEnrollmentCount", courseArray.length());
            metadataJson.put("GroupsCount", groupArray.length());

            for (Modal_Status status : metadata) {
                metadataJson.put(status.getStatusKey(), status.getValue());
                if (status.getStatusKey().equalsIgnoreCase("programId"))
                    programID = status.getValue();
            }

//            metadataJson.put(PD_Constant.SCORE_COUNT, (metadata.size() > 0) ? metadata.size() : 0);

            rootJson.put(PD_Constant.STUDENTS, studentArray);
            rootJson.put(PD_Constant.GROUPS, groupArray);
            rootJson.put(PD_Constant.SESSION, sessionArray);
            rootJson.put(PD_Constant.ATTENDANCE, attendanceArray);
            rootJson.put(PD_Constant.SCORE, scoreArray);
            rootJson.put(PD_Constant.LOGS, logArray);
            rootJson.put(PD_Constant.METADATA, metadataJson);
            rootJson.put(PD_Constant.COURSE_ENROLLED, courseArray);
            rootJson.put(PD_Constant.COURSE_PROGRESS, progArray);

            Log.e("Root : ", rootJson.toString());

            pushDataToServer(rootJson, courseCount, pushType, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*} else {
                if (isPressed) {
                    EventMessage msg = new EventMessage();
                    msg.setMessage(PD_Constant.SUCCESSFULLYPUSHED);
                    EventBus.getDefault().post(msg);
                }*/
    @Override
    public void onSync(Context context) {
        Log.d(TAG, "onSync: ");
        // Push Tab related Jsons
        pushUsageToServer(false, PD_Constant.AUTO_PUSH, context);
    }

    //before pushing zipping the json and then pushing
    public static void pushDataToServer(JSONObject data, String courseCount, String pushType, Context context) {
        try {
            String uuID = "PDL_" + PD_Utility.getUUID();
            String filepathstr = PrathamApplication.pradigiPath + "/" + uuID; // file path to save
            File filepath = new File(filepathstr + ".json"); // file path to save

            if (filepath.exists())
                filepath.delete();
            FileWriter writer = new FileWriter(filepath);
            writer.write(String.valueOf(data));
            writer.flush();
            writer.close();

            String[] s = new String[1];

            // Type the path of the files in here
            s[0] = filepathstr + ".json";
            // first parameter is d files second parameter is zip file name
            zip(s, filepathstr + ".zip", filepath);

            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
                new PD_ApiRequest(PrathamApplication.getInstance())
                        .pushDataToRaspberyPI(PD_Constant.URL.DATASTORE_RASPBERY_URL.toString(), uuID, filepathstr, data, courseCount, pushType, context);
            else if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork() || PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
                new PD_ApiRequest(PrathamApplication.getInstance())
                        .pushDataToInternet(PD_Constant.URL.POST_SMART_INTERNET_URL.toString(), uuID, filepathstr, data, courseCount, pushType, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void zip(String[] _files, String zipFileName, File filepath) {
        try {
            int BUFFER = 10000;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte[] data = new byte[BUFFER];
            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
            filepath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
