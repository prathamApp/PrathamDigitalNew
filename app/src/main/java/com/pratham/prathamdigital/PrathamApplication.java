package com.pratham.prathamdigital;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.androidnetworking.AndroidNetworking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.isupatches.wisefy.WiseFy;
import com.pratham.prathamdigital.async.ReadBackupDb;
import com.pratham.prathamdigital.custom.ProcessPhoenix;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.AttendanceDao;
import com.pratham.prathamdigital.dbclasses.CRLdao;
import com.pratham.prathamdigital.dbclasses.ContentProgressDao;
import com.pratham.prathamdigital.dbclasses.CourseDao;
import com.pratham.prathamdigital.dbclasses.GroupDao;
import com.pratham.prathamdigital.dbclasses.LogDao;
import com.pratham.prathamdigital.dbclasses.ModalContentDao;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.dbclasses.ScoreDao;
import com.pratham.prathamdigital.dbclasses.SessionDao;
import com.pratham.prathamdigital.dbclasses.StatusDao;
import com.pratham.prathamdigital.dbclasses.StudentDao;
import com.pratham.prathamdigital.dbclasses.VillageDao;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by HP on 09-08-2017.
 */
public class PrathamApplication extends Application {
    private static PrathamApplication mInstance;
    public static WiseFy wiseF;
    public static String pradigiPath = "";
    public static MediaPlayer bubble_mp;
    /*Also
     * Check Todo
     * Check Catcho in BaseActivity
     * Check baseUrl in PDConstant
     * Confirm the new raspberry url with Balaji
     * increase version before generating signed apk otherwise "app not installed"
     */
    public static final boolean isTablet = false;
    public static boolean useSatelliteGPS = false;
    public static boolean externalContentExists = false;
    public static String externalContentPath = "";
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
    public static CourseDao courseDao;
    public static ContentProgressDao contentProgressDao;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null) {
            mInstance = this;
        }
        //To check if your application is inside the Phoenix process to skip initialization
        if (ProcessPhoenix.isPhoenixProcess(this)) return;
        //this way the VM ignores the file URI exposure. if commented, the camera crashes on open
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//        isTablet = PD_Utility.isTablet(this);
        Fresco.initialize(this);
        FastSave.init(getApplicationContext());
        initializeDatabaseDaos();
        bubble_mp = MediaPlayer.create(this, R.raw.bubble_pop);
        setPradigiPath();
        wiseF = new WiseFy.Brains(getApplicationContext()).logging(true).getSmarts();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized PrathamApplication getInstance() {
        return mInstance;
    }

    public void setPradigiPath() {
        pradigiPath = PD_Utility.getInternalPath(this) + "/" + FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI);
        File f = new File(pradigiPath);
        if (!f.exists()) f.mkdirs();
    }

    public void setExistingSDContentPath(String path) {
        externalContentExists = true;
        externalContentPath = path;
    }

    private void initializeDatabaseDaos() {
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
        courseDao = db.getCourseDao();
        contentProgressDao = db.getContentProgressDao();
        if (!FastSave.getInstance().getBoolean(PD_Constant.BACKUP_DB_COPIED, false))
            new ReadBackupDb().execute();
    }
}

