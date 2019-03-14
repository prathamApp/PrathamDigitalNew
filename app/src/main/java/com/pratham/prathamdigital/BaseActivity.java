package com.pratham.prathamdigital;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.permissions.ResponsePermissionCallback;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.AttendanceDao;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.dbclasses.CRLdao;
import com.pratham.prathamdigital.dbclasses.GroupDao;
import com.pratham.prathamdigital.dbclasses.LogDao;
import com.pratham.prathamdigital.dbclasses.ModalContentDao;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.dbclasses.ScoreDao;
import com.pratham.prathamdigital.dbclasses.SessionDao;
import com.pratham.prathamdigital.dbclasses.StatusDao;
import com.pratham.prathamdigital.dbclasses.StudentDao;
import com.pratham.prathamdigital.dbclasses.VillageDao;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_PushData;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.services.TTSService;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
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
    public static LogDao logDao;
    public static String language = "";
    public static TTSService ttsService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        hideSystemUI(getWindow());   //this hides NavigationBar before showing the activity
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        super.onCreate(savedInstanceState);
//        FluidContentResizer.INSTANCE.listen(this);
        PD_Utility pd_utility = new PD_Utility(this);
//        Catcho.Builder(this)
//                .activity(CatchoTransparentActivity.class)
////                .recipients("your-email@domain.com")
//                .build();

        ttsService = new TTSService(getApplication());
        ttsService.setActivity(this);
        ttsService.setSpeechRate(0.7f);
        ttsService.setLanguage(new Locale("en", "IN"));

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
        logDao = db.getLogDao();
        language = FastSave.getInstance().getString(PD_Constant.LANGUAGE, "");
        if (language.isEmpty())
            FastSave.getInstance().saveString(PD_Constant.LANGUAGE, PD_Constant.HINDI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocation();
        BackupDatabase.backup(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackupDatabase.backup(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI(getWindow());
    }

    public static void hideSystemUI(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    public void requestLocation() {
        if (!PD_Utility.checkIfPermissionGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || !PD_Utility.checkIfPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            KotlinPermissions.with(this)
                    .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .onAccepted(new ResponsePermissionCallback() {
                        @Override
                        public void onResult(@NotNull List<String> permissionResult) {
                            new LocationService(BaseActivity.this).checkLocation();
                        }
                    })
                    .ask();
        } else {
            new LocationService(this).checkLocation();
        }
    }

    public void requestWritePermission() {
        if (!PD_Utility.checkIfPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            KotlinPermissions.with(this)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onAccepted(new ResponsePermissionCallback() {
                        @Override
                        public void onResult(@NotNull List<String> permissionResult) {
                            BackupDatabase.backup(BaseActivity.this);
                        }
                    })
                    .ask();
        }
    }

    @Subscribe
    public void requestStoragePermission(final String permission) {
        if (permission.equalsIgnoreCase(PD_Constant.WRITE_PERMISSION)) {
            requestWritePermission();
        }
    }

    @Subscribe
    public void updateFlagsWhenPushed(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.SUCCESSFULLYPUSHED)) {
                Gson gson = new Gson();
                Modal_PushData pushedData = gson.fromJson(message.getPushData(), Modal_PushData.class);
                for (Modal_PushData.Modal_PushSessionData pushed :
                        pushedData.getPushSession()) {
                    BaseActivity.sessionDao.updateFlag(pushed.getSessionId());
                    for (Modal_Score score : pushed.getScores())
                        BaseActivity.scoreDao.updateFlag(pushed.getSessionId());
                    for (Attendance att : pushed.getAttendances())
                        BaseActivity.attendanceDao.updateSentFlag(pushed.getSessionId());
                }
                for (Modal_Student student : pushedData.getStudents())
                    BaseActivity.studentDao.updateSentStudentFlags(student.getStudentId());
            }
        }
    }
}