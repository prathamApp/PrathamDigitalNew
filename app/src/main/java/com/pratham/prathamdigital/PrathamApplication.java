package com.pratham.prathamdigital;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.isupatches.wisefy.WiseFy;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.util.ConnectivityReceiver;

import java.util.UUID;

/**
 * Created by HP on 09-08-2017.
 */

public class PrathamApplication extends Application {
    private static PrathamApplication mInstance;
    public static WiseFy wiseF;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FastSave.init(getApplicationContext());
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext());
        wiseF = new WiseFy.Brains(getApplicationContext()).logging(true).getSmarts();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized PrathamApplication getInstance() {
        return mInstance;
    }
}

