package com.pratham.prathamdigital.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.pratham.prathamdigital.async.DownloadingTask;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;

public class DownloadService extends Service {

    private String TAG = DownloadService.class.getSimpleName();

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String url = intent.getStringExtra(PD_Constant.DOWNLOAD_URL);
            String dir_path = intent.getStringExtra(PD_Constant.DIR_PATH);
            String f_name = intent.getStringExtra(PD_Constant.FILE_NAME);
            String folder_name = intent.getStringExtra(PD_Constant.FOLDER_NAME);
            Modal_ContentDetail content = intent.getParcelableExtra(PD_Constant.DOWNLOAD_CONTENT);
//            String[] params = new String[]{url, String.valueOf(position)};
            DownloadingTask task = new DownloadingTask(url, dir_path, f_name, folder_name, content, DownloadService.this);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
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
