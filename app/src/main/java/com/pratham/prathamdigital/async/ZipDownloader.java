package com.pratham.prathamdigital.async;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.services.DownloadService;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.io.File;

/**
 * Created by User on 16/11/15.
 */
public class ZipDownloader {

    private static final String TAG = ZipDownloader.class.getSimpleName();
    private File mydir;
    String filename;
    ProgressUpdate progressUpdate;
    Context context;
    ContentContract.contentPresenter contentPresenter;
    private File fileWithinMyDir;
    //    public PowerManager.WakeLock wakeLock;
    String foldername;
    int downloadId;
    Modal_ContentDetail contentDetail;

    public ZipDownloader(Context context, ContentContract.contentPresenter contentPresenter, ProgressUpdate progressUpdate
            , String url, String foldername, String f_name, String pradigiPath, Modal_ContentDetail contentDetail) {
        this.context = context;
        PD_Utility.DEBUG_LOG(1, "url:::", url);
        this.filename = f_name;
        this.foldername = foldername;
        this.progressUpdate = progressUpdate;
        this.contentPresenter = contentPresenter;
        this.contentDetail = contentDetail;
//        this.wakeLock = wl;
        mydir = new File(PrathamApplication.pradigiPath + "/Pratham" + foldername); //Creating an internal dir;
        if (!mydir.exists()) mydir.mkdirs();
        if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
            if (foldername.equalsIgnoreCase("Game")) {
                f_name = f_name.substring(0, f_name.lastIndexOf("."));
                File temp_dir = new File(mydir.getAbsolutePath() + "/" + f_name);
                if (!temp_dir.exists()) temp_dir.mkdirs();
                mydir = temp_dir;
            }
        }
        Log.d("internal_file", mydir.getAbsolutePath());
//        downloadFile(url, mydir.getAbsolutePath(), filename);
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(PD_Constant.DOWNLOAD_URL, url);
        intent.putExtra(PD_Constant.DIR_PATH, mydir.getAbsolutePath());
        intent.putExtra(PD_Constant.FILE_NAME, filename);
        intent.putExtra(PD_Constant.FOLDER_NAME, foldername);
        intent.putExtra(PD_Constant.DOWNLOAD_CONTENT, contentDetail);
        context.startService(intent);
    }

//    public void downloadFile(String url, final String dirpath, final String f_name) {
////        wakeLock.acquire();
////        DownloadRequest request = null;
//        downloadId = PRDownloader.download(url, dirpath, f_name)
//                .build()
//                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
//                    @Override
//                    public void onStartOrResume() {
//                        contentPresenter.fileDownloadStarted(downloadId, filename, contentDetail);
//                    }
//                })
//                .setOnPauseListener(new OnPauseListener() {
//                    @Override
//                    public void onPause() {
//                        contentPresenter.onDownloadPaused(downloadId);
//                    }
//                })
//                .setOnCancelListener(new OnCancelListener() {
//                    @Override
//                    public void onCancel() {
//                        contentPresenter.ondownloadCancelled(downloadId);
//                    }
//                })
//                .setOnProgressListener(new OnProgressListener() {
//                    long progg = 0;
//
//                    @Override
//                    public void onProgress(Progress progress) {
//                        long progressPercent = 0;
////                        if (progress.totalBytes == -1) {
////                            if (progg == 0) progg = progress.currentBytes;
////                            progressPercent = progress.currentBytes / progg;
////                        } else
//                        progressPercent = progress.currentBytes * 100 / progress.totalBytes;
//                        Log.d("onProgress::", "" + progressPercent);
//                        contentPresenter.updateFileProgress(downloadId, f_name, (int) progressPercent);
//                    }
//                })
//                .start(new OnDownloadListener() {
//                    @Override
//                    public void onDownloadComplete() {
//                        if (foldername.equalsIgnoreCase("Game")) {
////                            new UnzipAsync(dirpath + "/" + f_name, dirpath, contentPresenter, downloadId).execute();
//                        } else {
//                            contentPresenter.onDownloadCompleted(downloadId);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Error error) {
//                        Log.d(TAG, "onError: content download error");
//                        contentPresenter.ondownloadError(f_name);
//                    }
//                });
//    }
}





