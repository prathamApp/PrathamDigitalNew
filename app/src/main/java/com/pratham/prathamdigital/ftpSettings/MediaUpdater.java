package com.pratham.prathamdigital.ftpSettings;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.util.Log;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


/**
 * This media rescanner runs in the background. The rescan might
 * not happen immediately.
 */
public enum MediaUpdater {
    INSTANCE;

    private final static String TAG = MediaUpdater.class.getSimpleName();

    // the system broadcast to remount the media is only done after a little while (5s)
    private static Timer sTimer = new Timer();

    private static class ScanCompletedListener implements
            MediaScannerConnection.OnScanCompletedListener {
        @Override
        public void onScanCompleted(String path, Uri uri) {
            Log.i(TAG, "Scan completed: " + path + " : " + uri);
//            EventBus.getDefault().post(new File(path));
        }
    }

    public static void notifyFileCreated(String path) {
        Log.d(TAG, "Notifying others about new file: " + path);
        Context context = PrathamApplication.getInstance();
        EventBus.getDefault().post(new File(path));
//        MediaScannerConnection.scanFile(context, new String[]{path}, null,
//                new ScanCompletedListener());
    }

    public static void notifyFileDeleted(String path) {
        Log.d(TAG, "Notifying others about deleted file: " + path);
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            // on older devices, fake a remount of the media
            // The media mounted broadcast is very taxing on the system, so
            // we only do this if for 5 seconds there was no same request,
            // otherwise we wait again.
            // the broadcast might have been requested already, cancel if so
            sTimer.cancel();
            // that timer is of no value any more, create a new one
            sTimer = new Timer();
            // and in 5s let it send the broadcast, might never hapen if
            // before
            // that time it gets canceled by this code path
            sTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "Sending ACTION_MEDIA_MOUNTED broadcast");
                    final Context context = PrathamApplication.getInstance();
                    //Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
                    Uri uri = Uri.parse("file://" + PD_Utility.getStoragePath());
                    Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, uri);
                    context.sendBroadcast(intent);
                }
            }, 5000);
        } else {
            // on newer devices, we hope that this works correctly:
            Context context = PrathamApplication.getInstance();
//            MediaScannerConnection.scanFile(context, new String[]{path}, null,
//                    new ScanCompletedListener());
        }
    }
}
