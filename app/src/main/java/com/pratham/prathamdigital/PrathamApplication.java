package com.pratham.prathamdigital;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.isupatches.wisefy.WiseFy;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.services.auto_sync.AutoSync;
import com.pratham.prathamdigital.socket.entity.FileState;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.HashMap;

/**
 * Created by HP on 09-08-2017.
 */

public class PrathamApplication extends Application {
    private static PrathamApplication mInstance;
    public static WiseFy wiseF;
    private static boolean isSlient = false;
    private static boolean isVIBRATE = true;
    private static SoundPool notiMediaplayer;
    private static Vibrator notiVibrator;
    public static HashMap<String, FileState> sendFileStates;
    public static HashMap<String, FileState> recieveFileStates;
    public static String IMAG_PATH;
    public static String THUMBNAIL_PATH;
    public static String VOICE_PATH;
    public static String VEDIO_PATH;
    public static String APK_PATH;
    public static String MUSIC_PATH;
    public static String FILE_PATH;
    public static String SAVE_PATH;
    public static String CAMERA_IMAGE_PATH;
    private static boolean isClient = true;
    public static String pradigiPath = "";
    public static MediaPlayer bubble_mp;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null) {
            mInstance = this;
        }
        bubble_mp = MediaPlayer.create(this, R.raw.bubble_pop);
        sendFileStates = new HashMap<String, FileState>();
        recieveFileStates = new HashMap<String, FileState>();
        pradigiPath = PD_Utility.getInternalPath(this);
        FastSave.init(getApplicationContext());
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext());
        wiseF = new WiseFy.Brains(getApplicationContext()).logging(true).getSmarts();
        AutoSync.start(mInstance);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized PrathamApplication getInstance() {
        return mInstance;
    }

    private void initNotification() {
        notiMediaplayer = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
        // notiSoundPoolID = notiMediaplayer.load(this, R.raw.crystalring, 1);
        notiVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }
}
