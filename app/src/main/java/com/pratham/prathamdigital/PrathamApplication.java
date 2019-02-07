package com.pratham.prathamdigital;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.androidnetworking.AndroidNetworking;
import com.isupatches.wisefy.WiseFy;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.ftpSettings.RequestStartStopReceiver;
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
     */
    public static final boolean isTablet = false;
    public static boolean contentExistOnSD = false;
    public static String contentSDPath = "";
    OkHttpClient okHttpClient;
    RequestStartStopReceiver requestStartStopReceiver;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null) {
            mInstance = this;
        }
        FastSave.init(getApplicationContext());
        bubble_mp = MediaPlayer.create(this, R.raw.bubble_pop);
        setPradigiPath();
        wiseF = new WiseFy.Brains(getApplicationContext()).logging(true).getSmarts();
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);
    }

//    public void toggleBackgroundMusic(boolean start) {
//        if (start) {
//            if (!PD_Utility.isServiceRunning(BackgroundSoundService.class, this))
//                startService(new Intent(this, BackgroundSoundService.class));
//        } else {
//            if (PD_Utility.isServiceRunning(BackgroundSoundService.class, this))
//                stopService(new Intent(this, BackgroundSoundService.class));
//        }
//    }

    @Override
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
        contentExistOnSD = true;
        contentSDPath = path;
    }

    public void registerFtpReceiver() {
        requestStartStopReceiver = new RequestStartStopReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.pratham.prathamdigital.ACTION_START_FTPSERVER");
        intentFilter.addAction("com.pratham.prathamdigital.ACTION_STOP_FTPSERVER");
        registerReceiver(requestStartStopReceiver, intentFilter);
    }

    public void unregisterReceiver() {
        if (requestStartStopReceiver != null) {
            unregisterReceiver(requestStartStopReceiver);
        }
    }
}

