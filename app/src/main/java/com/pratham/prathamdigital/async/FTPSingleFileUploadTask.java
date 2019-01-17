package com.pratham.prathamdigital.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPSingleFileUploadTask extends AsyncTask {
    FTPClient client;
    Context context;
    long lenghtOfFile;
    String localFilePath;
    boolean isImage;
    boolean isProfileSharing = false;

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
                uploadSingleFile(client, localFilePath, remoteFilePath, false);
            } else {
                if (localFilePath.endsWith(".json")) {
                    //means contents are being send i.e video/pdf/games
                    remoteFilePath = "/" + new File(localFilePath).getName();
                    uploadSingleFile(client, localFilePath, remoteFilePath, false);
                } else {
                    //profiles or usages are being sent
                    File file = new File(localFilePath);
                    lenghtOfFile = PD_Utility.folderSize(file);
                    isProfileSharing = true;
                    if (file.isDirectory())
                        for (File f : file.listFiles()) {
                            remoteFilePath = "/" + f.getName();
                            uploadSingleFile(client, f.getAbsolutePath(), remoteFilePath, true);
                        }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    long total = 0;

    public void uploadSingleFile(FTPClient ftpClient, String localFilePath, String remoteFilePath, boolean isProfileSharing) {
        try {
            File localFile = new File(localFilePath);
            total += localFile.length();
            FileInputStream in = new FileInputStream(localFile);
            boolean result = ftpClient.storeFile(remoteFilePath, in);
            Log.v("upload_result:::", "" + result);
//            if (result && !isImage) localFile.delete();
            if (isProfileSharing)
                publishProgress((total * 100) / lenghtOfFile, localFile.getName());
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_SHARE_PROGRESS);
        msg.setProgress((Long) values[0]);
        msg.setFile_name((String) values[1]);
        EventBus.getDefault().post(msg);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        File path = context.getDir(PD_Constant.PRATHAM_TEMP_FILES, Context.MODE_PRIVATE);
        if (path.exists())
            PD_Utility.deleteRecursive(path);
        if (isProfileSharing) {
            EventMessage msg = new EventMessage();
            msg.setMessage(PD_Constant.FILE_SHARE_COMPLETE);
            EventBus.getDefault().post(msg);
        }
    }
}
