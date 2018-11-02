package com.pratham.prathamdigital;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.pratham.prathamdigital.custom.loading_view.CatLoadingView;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.AttendanceDao;
import com.pratham.prathamdigital.dbclasses.CRLdao;
import com.pratham.prathamdigital.dbclasses.GroupDao;
import com.pratham.prathamdigital.dbclasses.ModalContentDao;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.dbclasses.ScoreDao;
import com.pratham.prathamdigital.dbclasses.SessionDao;
import com.pratham.prathamdigital.dbclasses.StatusDao;
import com.pratham.prathamdigital.dbclasses.StudentDao;
import com.pratham.prathamdigital.dbclasses.VillageDao;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.util.ActivityManagePermission;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

import java.util.UUID;

public class BaseActivity extends ActivityManagePermission {
    public static String sessionId = UUID.randomUUID().toString();
    public static AttendanceDao attendanceDao;
    public static CRLdao crLdao;
    public static GroupDao groupDao;
    public static ModalContentDao modalContentDao;
    public static ScoreDao scoreDao;
    public static SessionDao sessionDao;
    public static StatusDao statusDao;
    public static StudentDao studentDao;
    public static VillageDao villageDao;
    public static String RASP_FACILITY = "";
    public static String pradigiPath = "";
    public static String language = "";
    public static CatLoadingView catLoadingView = new CatLoadingView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PrathamDatabase db = PrathamDatabase.getDatabaseInstance(this);
        attendanceDao = db.getAttendanceDao();
        crLdao = db.getCrLdao();
        groupDao = db.getGroupDao();
        modalContentDao = db.getModalContentDao();
        scoreDao = db.getScoreDao();
        sessionDao = db.getSessionDao();
        statusDao = db.getStatusDao();
        studentDao = db.getStudentDao();
        villageDao = db.getVillageDao();
        pradigiPath = PD_Utility.getInternalPath(this);
        language = FastSave.getInstance().getString(PD_Constant.LANGUAGE, "");
        if (language.isEmpty()) {
            FastSave.getInstance().saveString(PD_Constant.LANGUAGE, PD_Constant.HINDI);
        }
    }
}
