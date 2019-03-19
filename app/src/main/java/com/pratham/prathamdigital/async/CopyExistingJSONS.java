package com.pratham.prathamdigital.async;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.Interface_copying;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CopyExistingJSONS extends AsyncTask<String, String, Boolean> {

    Context context;
    File filePath;
    Interface_copying interface_copying;

    public CopyExistingJSONS(Context context, File filePath) {
        this.context = context;
        this.filePath = filePath;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            try {
                FileInputStream is = new FileInputStream(filePath);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String mResponse = new String(buffer);
                Gson gson = new Gson();
                if (PD_Utility.isProfile(filePath.getName())) {
                    switch (filePath.getName().toLowerCase()) {
                        case "villages.json":
                            Type villType = new TypeToken<List<Modal_Village>>() {
                            }.getType();
                            List<Modal_Village> temp = gson.fromJson(mResponse, villType);
                            BaseActivity.villageDao.insertAllVillages(temp);
                            break;
                        case "groups.json":
                            Type grpType = new TypeToken<List<Modal_Groups>>() {
                            }.getType();
                            List<Modal_Groups> grptemp = gson.fromJson(mResponse, grpType);
                            BaseActivity.groupDao.insertAllGroups(grptemp);
                            break;
                        case "crls.json":
                            Type crlType = new TypeToken<List<Modal_Crl>>() {
                            }.getType();
                            List<Modal_Crl> crltemp = gson.fromJson(mResponse, crlType);
                            BaseActivity.crLdao.insertAllCRL(crltemp);
                            break;
                        case "students.json":
                            Type stuType = new TypeToken<List<Modal_Student>>() {
                            }.getType();
                            List<Modal_Student> stutemp = gson.fromJson(mResponse, stuType);
                            BaseActivity.studentDao.insertAllStudents(stutemp);
                            publishProgress("Receiving Profiles");
                            break;
                    }
                } else if (PD_Utility.isUsages(filePath.getName())) {
                    switch (filePath.getName().toLowerCase()) {
                        case "sessions.json":
                            Type listType = new TypeToken<List<Modal_Session>>() {
                            }.getType();
                            List<Modal_Session> temp = gson.fromJson(mResponse, listType);
                            BaseActivity.sessionDao.insertAll(temp);
                            break;
                        case "logs.json":
                            Type logType = new TypeToken<List<Modal_Log>>() {
                            }.getType();
                            List<Modal_Log> logtemp = gson.fromJson(mResponse, logType);
                            BaseActivity.logDao.insertAllLogs(logtemp);
                            break;
                        case "attendance.json":
                            Type attType = new TypeToken<List<Attendance>>() {
                            }.getType();
                            List<Attendance> atttemp = gson.fromJson(mResponse, attType);
                            BaseActivity.attendanceDao.insertAttendance(atttemp);
                            break;
                        case "scores.json":
                            Type scoreType = new TypeToken<List<Modal_Score>>() {
                            }.getType();
                            List<Modal_Score> scoretemp = gson.fromJson(mResponse, scoreType);
                            BaseActivity.scoreDao.insertAll(scoretemp);
                            break;
                        case "status.json":
                            Modal_Score modalScore = new Modal_Score();
                            modalScore.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
                            if (PrathamApplication.isTablet)
                                modalScore.setGroupID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group"));
                            else
                                modalScore.setStudentID(FastSave.getInstance().getString(PD_Constant.STUDENTID, "no_student"));
                            modalScore.setDeviceID(PD_Utility.getDeviceID());
                            modalScore.setResourceID("received metadata");
                            modalScore.setQuestionId(0);
                            modalScore.setScoredMarks(0);
                            modalScore.setTotalMarks(0);
                            modalScore.setStartDateTime(PD_Utility.getCurrentDateTime());
                            modalScore.setEndDateTime(PD_Utility.getCurrentDateTime());
                            modalScore.setLevel(0);
                            modalScore.setLabel(mResponse);
                            modalScore.setSentFlag(0);
                            BaseActivity.scoreDao.insert(modalScore);
                            publishProgress("Receiving Usages");
                            break;
                    }
                } else if (filePath.exists()) {
                    String filename = "";
                    Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                    }.getType();
                    List<Modal_ContentDetail> tempContents = gson.fromJson(mResponse, listType);
                    for (Modal_ContentDetail detail : tempContents) {
                        if (detail.getContentType().toLowerCase().equalsIgnoreCase("file"))
                            if (detail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME))
                                filename = detail.getResourcepath().split("/")[0];
                            else if (detail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO) ||
                                    detail.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF))
                                filename = detail.getResourcepath();
                        detail.setDownloaded(true);
                        if (PrathamApplication.contentExistOnSD) detail.setOnSDCard(true);
                        else detail.setOnSDCard(false);
                    }
                    BaseActivity.modalContentDao.addContentList(tempContents);
                    publishProgress(filename);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            filePath.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_RECEIVE_COMPLETE);
        msg.setFile_name(values[0]);
        EventBus.getDefault().post(msg);
    }

    @Override
    protected void onPostExecute(Boolean copied) {
        super.onPostExecute(copied);
    }
}
