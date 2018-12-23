package com.pratham.prathamdigital.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPSingleFileUploadTask extends AsyncTask {
    FTPClient client;
    Context context;
    String localFilePath;
    boolean isImage;

    public FTPSingleFileUploadTask(Context context, FTPClient client, String localFilePath, boolean isImage) {
        this.context = context;
        this.client = client;
        this.localFilePath = localFilePath;
        this.isImage = isImage;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            client.enterLocalPassiveMode();
            String remoteFilePath = null;
            if (isImage) {
                client.makeDirectory("PrathamImages");
                remoteFilePath = "/PrathamImages/" + new File(localFilePath).getName();
            } else {
                remoteFilePath = "/" + new File(localFilePath).getName();
            }
            uploadSingleFile(client, localFilePath, remoteFilePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void uploadSingleFile(FTPClient ftpClient, String localFilePath, String remoteFilePath) {
        try {
            File localFile = new File(localFilePath);
            FileInputStream in = new FileInputStream(localFile);
            boolean result = ftpClient.storeFile(remoteFilePath, in);
            Log.v("upload_result:::", "" + result);
            if (result && !isImage) localFile.delete();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
