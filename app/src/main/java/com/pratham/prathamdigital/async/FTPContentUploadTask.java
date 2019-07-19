package com.pratham.prathamdigital.async;

import android.content.Context;
import android.util.Log;

import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.EBean;
import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@EBean
public class FTPContentUploadTask {
    private FTPClient client;
    private long lenghtOfFile;
    private Context context;
    private String localPath;
    private String contentType;
    private String downloadId;
    private String remoteDirPath;
    private long total = 0;

    public FTPContentUploadTask(Context context) {
        this.context = context;
    }

    public void doInBackground(FTPClient ftpclient, String _localPath, String _contentType, String _downloadId) {
        try {
            this.client = ftpclient;
            this.localPath = _localPath;
            this.downloadId = _downloadId;
            this.contentType = "Pratham" + _contentType;
            File f = new File(localPath);
            lenghtOfFile = (f.isFile()) ? f.length() : PD_Utility.folderSize(f);
            if (_contentType.equalsIgnoreCase(PD_Constant.GAME))
                remoteDirPath = "/PrathamGame/";
            else if (_contentType.equalsIgnoreCase(PD_Constant.VIDEO))
                remoteDirPath = "/PrathamVideo/";
            else if (_contentType.equalsIgnoreCase(PD_Constant.PDF))
                remoteDirPath = "/PrathamPDF/";
            // use local passive mode to pass firewall
            client.enterLocalPassiveMode();
            client.makeDirectory(contentType);
            if (contentType.equalsIgnoreCase("PrathamGame")) {
                remoteDirPath += new File(localPath).getName();
                client.makeDirectory(remoteDirPath);
                uploadDirectory(client, remoteDirPath, localPath, "");
            } else {
                String remoteFilePath = "/" + contentType + "/" + new File(localPath).getName();
                uploadSingleFile(client, localPath, remoteFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onPostExecute();
    }

    private void uploadDirectory(FTPClient ftpClient, String remoteDirPath, String localParentDir,
                                 String remoteParentDir) {
        try {
            File localDir = new File(localParentDir);
            for (File subFile : localDir.listFiles()) {
                String remoteFilePath = remoteDirPath + "/" + remoteParentDir + "/" + subFile.getName();
                if (remoteParentDir.equals("")) {
                    remoteFilePath = remoteDirPath + "/" + subFile.getName();
                }
                if (subFile.isFile()) {
                    // upload the file
                    String localFilePath = subFile.getAbsolutePath();
                    uploadSingleFile(ftpClient, localFilePath, remoteFilePath);
                } else {
                    ftpClient.makeDirectory(remoteFilePath);
                    String parent = remoteParentDir + "/" + subFile.getName();
                    if (remoteParentDir.equals("")) {
                        parent = subFile.getName();
                    }
                    localParentDir = subFile.getAbsolutePath();
                    uploadDirectory(ftpClient, remoteDirPath, localParentDir, parent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadSingleFile(FTPClient ftpClient, String localFilePath, String remoteFilePath) {
        try {
            File localFile = new File(localFilePath);
            total += localFile.length();
            FileInputStream in = new FileInputStream(localFile);
            boolean result = ftpClient.storeFile(remoteFilePath, in);
            onProgressUpdate((total * 100) / lenghtOfFile, localFile.getName());
            Log.v("upload_result:::", "" + result);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onProgressUpdate(Object... values) {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_SHARE_PROGRESS);
        msg.setDownloadId(downloadId);
        msg.setProgress((Long) values[0]);
        msg.setFile_name((String) values[1]);
        EventBus.getDefault().post(msg);
    }

    protected void onPostExecute() {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_SHARE_COMPLETE);
        EventBus.getDefault().post(msg);
    }
}
