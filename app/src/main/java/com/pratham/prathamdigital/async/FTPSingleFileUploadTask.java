package com.pratham.prathamdigital.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.EBean;
import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

@EBean
public class FTPSingleFileUploadTask {
    private FTPClient client;
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private String localFilePath;
    private boolean isImage;
    private ArrayList<Modal_ContentDetail> levels;
    private long lenghtOfFile;
    private boolean isProfileSharing = false;
    private long total = 0;

    public FTPSingleFileUploadTask(Context context) {
        this.context = context;
    }

    public void doInBackground(FTPClient _client, String _localFilePath, boolean _isImage, ArrayList<Modal_ContentDetail> _levels) {
        try {
            this.client = _client;
            this.localFilePath = _localFilePath;
            this.isImage = _isImage;
            this.levels = _levels;
            client.enterLocalPassiveMode();
            String remoteFilePath;
            if (isImage) {
                client.makeDirectory("PrathamImages");
                for (Modal_ContentDetail detail : levels) {
                    String imgDirPath = localFilePath + "/PrathamImages/" + detail.getNodeimage();
                    String remoteFPath = "/PrathamImages/" + new File(imgDirPath).getName();
                    uploadSingleFile(client, imgDirPath, remoteFPath, false);
                }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        onPostExecute();
    }

    private void uploadSingleFile(FTPClient ftpClient, String localFilePath, String remoteFilePath, boolean isProfileSharing) {
        try {
            File localFile = new File(localFilePath);
            total += localFile.length();
            FileInputStream in = new FileInputStream(localFile);
            boolean result = ftpClient.storeFile(remoteFilePath, in);
            Log.v("upload_result:::", "" + result);
//            if (result && !isImage) localFile.delete();
            if (isProfileSharing)
                onProgressUpdate((total * 100) / lenghtOfFile, localFile.getName());
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onProgressUpdate(Object... values) {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_SHARE_PROGRESS);
        msg.setProgress((Long) values[0]);
        msg.setFile_name((String) values[1]);
        EventBus.getDefault().post(msg);
    }

    protected void onPostExecute() {
        if (!isImage) {
            File path = context.getDir(PD_Constant.PRATHAM_TEMP_FILES, Context.MODE_PRIVATE);
            if (path.exists())
                PD_Utility.deleteRecursive(path);
        }
        if (isProfileSharing) {
            EventMessage msg = new EventMessage();
            msg.setMessage(PD_Constant.FILE_SHARE_COMPLETE);
            EventBus.getDefault().post(msg);
        }
    }
}
