package com.pratham.prathamdigital;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pratham.prathamdigital.custom.fluid_keyboard.FluidContentResizer;
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
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.services.BackgroundSoundService;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.services.TTSService;
import com.pratham.prathamdigital.util.ActivityManagePermission;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

import net.alhazmy13.catcho.library.Catcho;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;
import java.util.UUID;

public class BaseActivity extends ActivityManagePermission {
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
        FluidContentResizer.INSTANCE.listen(this);
        PD_Utility pd_utility = new PD_Utility(this);
        Catcho.Builder(this)
//                .activity(CatchoTransparentActivity.class)
                .recipients("your-email@domain.com")
                .build();
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
        if (language.isEmpty()) {
            FastSave.getInstance().saveString(PD_Constant.LANGUAGE, PD_Constant.HINDI);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocation();
        if (!PD_Utility.isServiceRunning(BackgroundSoundService.class, this))
            startService(new Intent(this, BackgroundSoundService.class));
        BackupDatabase.backup(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (PD_Utility.isServiceRunning(BackgroundSoundService.class, this))
            stopService(new Intent(this, BackgroundSoundService.class));
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
        if (!isPermissionsGranted(this, new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION
                , PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION
                , PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE})) {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION
                    , PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    new LocationService(BaseActivity.this).checkLocation();
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                }
            });
        } else {
            new LocationService(this).checkLocation();
        }
    }

    public void requestWritePermission() {
        if (!isPermissionGranted(this, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE)) {
            askCompactPermission(PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    BackupDatabase.backup(BaseActivity.this);
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                }
            });
        }
    }

    @Subscribe
    public void requestStoragePermission(final String permission) {
        if (permission.equalsIgnoreCase(PD_Constant.WRITE_PERMISSION)) {
            requestWritePermission();
        }
    }
}
