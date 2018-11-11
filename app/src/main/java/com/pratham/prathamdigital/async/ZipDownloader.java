package com.pratham.prathamdigital.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.ui.fragment_content.ContentPresenterImpl;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.UnzipUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
        mydir = new File(pradigiPath + "/" + FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)); //Creating an internal dir;
        if (!mydir.exists()) mydir.mkdirs();
        mydir = new File(mydir.getAbsolutePath() + "/Pratham" + foldername); //Creating an internal dir;
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
        downloadFile(url, mydir.getAbsolutePath(), filename);
    }

    public void downloadFile(String url, final String dirpath, final String f_name) {
//        wakeLock.acquire();
//        DownloadRequest request = null;
        downloadId = PRDownloader.download(url, dirpath, f_name)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        contentPresenter.fileDownloadStarted(downloadId, filename, contentDetail);
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        contentPresenter.onDownloadPaused(downloadId);
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        contentPresenter.ondownloadCancelled(downloadId);
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        contentPresenter.updateFileProgress(downloadId, f_name, (int) progressPercent);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        new UnzipAsync(dirpath + "/" + f_name, dirpath, contentPresenter, downloadId).execute();
                    }

                    @Override
                    public void onError(Error error) {
                        Log.d(TAG, "onError: content download error");
                    }
                });
    }
}




