package com.pratham.prathamdigital.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPImageUploadTask extends AsyncTask {
    FTPClient client;
    Context context;
    String localFilePath;

    public FTPImageUploadTask(Context context, FTPClient client, String localFilePath) {
        this.context = context;
        this.client = client;
        this.localFilePath = localFilePath;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            // use local passive mode to pass firewall
//            client.enterLocalPassiveMode();
            client.makeDirectory("PrathamImages");
//            String remoteDirPath = "/PrathamImages";
            String remoteFilePath = "/PrathamImages/" + new File(localFilePath).getName();
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
