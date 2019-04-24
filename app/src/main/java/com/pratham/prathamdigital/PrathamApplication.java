package com.pratham.prathamdigital;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.androidnetworking.AndroidNetworking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.isupatches.wisefy.WiseFy;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.ftpSettings.FsNotification;
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
     * Remove LeakCanary from oncreate
     * check version
     */
    public static boolean isTablet = true;
    public static boolean useSatelliteGPS = false;
    public static boolean contentExistOnSD = false;
    public static String contentSDPath = "";
    RequestStartStopReceiver requestStartStopReceiver;
    FsNotification fsNotification;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null) {
            mInstance = this;
        }
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//        isTablet = PD_Utility.isTablet(this);
        Fresco.initialize(this);
        FastSave.init(getApplicationContext());
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

    /*public void registerFtpReceiver() {
        //registering receivers in case of android version above Oreo
        requestStartStopReceiver = new RequestStartStopReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FsService.ACTION_START_FTPSERVER);
        intentFilter.addAction(FsService.ACTION_STOP_FTPSERVER);
        registerReceiver(requestStartStopReceiver, intentFilter);

        fsNotification = new FsNotification();
        IntentFilter fsIntentFilter = new IntentFilter();
        fsIntentFilter.addAction(FsService.ACTION_UPDATE_NOTIFICATION);
        fsIntentFilter.addAction(FsService.ACTION_STARTED);
        fsIntentFilter.addAction(FsService.ACTION_STOPPED);
        registerReceiver(fsNotification, fsIntentFilter);
    }

    public void unregisterReceiver() {
        if (requestStartStopReceiver != null)
            unregisterReceiver(requestStartStopReceiver);
        if (fsNotification != null)
            unregisterReceiver(fsNotification);
    }*/
}

