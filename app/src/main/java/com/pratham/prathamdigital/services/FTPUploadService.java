package com.pratham.prathamdigital.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class FTPUploadService extends Service {

    private String TAG = FTPUploadService.class.getSimpleName();

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
//            FTPFileUploadTask task = new FTPFileUploadTask(url, dir_path, f_name, folder_name, content, FTPUploadService.this);
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
    }


}
