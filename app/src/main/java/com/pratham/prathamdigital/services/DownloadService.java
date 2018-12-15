package com.pratham.prathamdigital.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.greenrobot.eventbus.EventBus;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
            DownloadingTask task = new DownloadingTask(url, dir_path, f_name, folder_name, content);
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


    class DownloadingTask extends AsyncTask<String, Long, Boolean> {
        String url;
        String dir_path;
        String f_name;
        String folder_name;
        Modal_ContentDetail content;
        String downloadID;

        public DownloadingTask(String url, String dir_path, String f_name, String folder_name, Modal_ContentDetail content) {
            this.url = url;
            this.dir_path = dir_path;
            this.f_name = f_name;
            this.folder_name = folder_name;
            this.content = content;
            this.downloadID = content.getNodeid();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.DOWNLOAD_STARTED);
            message.setDownloadId(downloadID);
            message.setFile_name(f_name);
            message.setContentDetail(content);
            EventBus.getDefault().post(message);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d(TAG, "doInBackground: " + url);
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                // String root = Environment.getExternalStorageDirectory().toString();
                URL urlFormed = new URL(url);
                connection = (HttpURLConnection) urlFormed.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }
                // getting file length
                int lenghtOfFile = connection.getContentLength();
                if (lenghtOfFile < 0)
                    lenghtOfFile = (content.getLevel() > 0) ? content.getLevel() : 1;
                // input stream to read file - with 8k buffer
                input = connection.getInputStream();
                // Output stream to write file
                output = new FileOutputStream(dir_path + "/" + f_name);
                byte data[] = new byte[4096];
                long total = 0;
//                long download_percentage_old = 00;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return false;
                    }
                    total += count;
                    // writing data to file
                    output.write(data, 0, count);
                    long download_percentage_new = (100 * total) / lenghtOfFile;
                    publishProgress(download_percentage_new);
                }
                // flushing output
                if (output != null)
                    output.close();
                // closing streams/**/
                if (input != null)
                    input.close();
                if (folder_name.equalsIgnoreCase(PD_Constant.GAME)) {
                    unzipFile(dir_path + "/" + f_name, dir_path);
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private void unzipFile(String source, String destination) {
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(source);
                zipFile.extractAll(destination);
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            /*Intent intent = new Intent("download_progress");
            intent.putExtra("percentage", values[0]);
            intent.putExtra("position", values[1]);
            LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);*/
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.DOWNLOAD_UPDATE);
            message.setDownloadId(downloadID);
            message.setFile_name(f_name);
            message.setProgress(values[0]);
            EventBus.getDefault().post(message);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "onPostExecute");
            if (result) {
                EventMessage message = new EventMessage();
                message.setMessage(PD_Constant.DOWNLOAD_COMPLETE);
                message.setDownloadId(downloadID);
                EventBus.getDefault().post(message);
            }
            stopSelf();
        }
    }
}
