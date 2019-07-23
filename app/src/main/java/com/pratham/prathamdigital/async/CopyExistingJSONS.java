package com.pratham.prathamdigital.async;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
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

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.crLdao;
import static com.pratham.prathamdigital.PrathamApplication.groupDao;
import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.modalContentDao;
import static com.pratham.prathamdigital.PrathamApplication.scoreDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;
import static com.pratham.prathamdigital.PrathamApplication.villageDao;

@EBean
public class CopyExistingJSONS {

    Context context;
    private File filePath;

    public CopyExistingJSONS(Context context) {
        this.context = context;
    }

    @Background
    public void doInBackground(File _filePath) {
        try {
            this.filePath = _filePath;
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
                            villageDao.insertAllVillages(temp);
                            break;
                        case "groups.json":
                            Type grpType = new TypeToken<List<Modal_Groups>>() {
                            }.getType();
                            List<Modal_Groups> grptemp = gson.fromJson(mResponse, grpType);
                            groupDao.insertAllGroups(grptemp);
                            break;
                        case "crls.json":
                            Type crlType = new TypeToken<List<Modal_Crl>>() {
                            }.getType();
                            List<Modal_Crl> crltemp = gson.fromJson(mResponse, crlType);
                            crLdao.insertAllCRL(crltemp);
                            break;
                        case "students.json":
                            Type stuType = new TypeToken<List<Modal_Student>>() {
                            }.getType();
                            List<Modal_Student> stutemp = gson.fromJson(mResponse, stuType);
                            studentDao.insertAllStudents(stutemp);
                            onProgressUpdate("Receiving Profiles");
                            break;
                    }
                } else if (PD_Utility.isUsages(filePath.getName())) {
                    switch (filePath.getName().toLowerCase()) {
                        case "sessions.json":
                            Type listType = new TypeToken<List<Modal_Session>>() {
                            }.getType();
                            List<Modal_Session> temp = gson.fromJson(mResponse, listType);
                            sessionDao.insertAll(temp);
                            break;
                        case "logs.json":
                            Type logType = new TypeToken<List<Modal_Log>>() {
                            }.getType();
                            List<Modal_Log> logtemp = gson.fromJson(mResponse, logType);
                            List<Modal_Log> logs = new ArrayList<>();
                            for (Modal_Log l : logtemp) {
                                Modal_Log log = new Modal_Log();
                                log.setCurrentDateTime(l.getCurrentDateTime());
                                log.setDeviceId(l.getDeviceId());
                                log.setErrorType(l.getErrorType());
                                log.setExceptionMessage(l.getExceptionMessage());
                                log.setExceptionStackTrace(l.getExceptionStackTrace());
                                log.setLogDetail(l.getLogDetail());
                                log.setMethodName(l.getMethodName());
                                log.setSentFlag(l.getSentFlag());
                                log.setSessionId(l.getSessionId());
                                logs.add(log);
                            }
                            logDao.insertAllLogs(logs);
                            break;
                        case "attendance.json":
                            Type attType = new TypeToken<List<Attendance>>() {
                            }.getType();
                            List<Attendance> atttemp = gson.fromJson(mResponse, attType);
                            List<Attendance> tmp = new ArrayList<>();
                            for (Attendance att : atttemp) {
                                Attendance attendance = new Attendance();
                                attendance.setSentFlag(att.getSentFlag());
                                attendance.setPresent(att.getPresent());
                                attendance.setDate(att.getDate());
                                attendance.setStudentID(att.getStudentID());
                                attendance.setSessionID(att.getSessionID());
                                attendance.setGroupID(att.getGroupID());
                                attendance.setVillageID(att.getVillageID());
                                tmp.add(attendance);
                            }
                            attendanceDao.insertAttendance(tmp);
                            break;
                        case "scores.json":
                            Type scoreType = new TypeToken<List<Modal_Score>>() {
                            }.getType();
                            List<Modal_Score> scoretemp = gson.fromJson(mResponse, scoreType);
                            List<Modal_Score> scores = new ArrayList<>();
                            for (Modal_Score sc : scoretemp) {
                                Modal_Score modal_score = new Modal_Score();
                                modal_score.setSentFlag(sc.getSentFlag());
                                modal_score.setLabel(sc.getLabel());
                                modal_score.setLevel(sc.getLevel());
                                modal_score.setEndDateTime(sc.getEndDateTime());
                                modal_score.setStartDateTime(sc.getStartDateTime());
                                modal_score.setTotalMarks(sc.getTotalMarks());
                                modal_score.setScoredMarks(sc.getScoredMarks());
                                modal_score.setQuestionId(sc.getQuestionId());
                                modal_score.setResourceID(sc.getResourceID());
                                modal_score.setDeviceID(sc.getDeviceID());
                                modal_score.setGroupID(sc.getGroupID());
                                modal_score.setStudentID(sc.getStudentID());
                                modal_score.setSessionID(sc.getSessionID());
                                scores.add(modal_score);
                            }
                            scoreDao.insertAll(scores);
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
                            scoreDao.insert(modalScore);
                            onProgressUpdate("Receiving Usages");
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
                    modalContentDao.addContentList(tempContents);
                    onProgressUpdate(filename);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            filePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void onProgressUpdate(String... values) {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_RECEIVE_COMPLETE);
        msg.setFile_name(values[0]);
        EventBus.getDefault().post(msg);
    }
}
