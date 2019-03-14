package com.pratham.prathamdigital.async;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Download;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.util.FileUtils;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.EBean;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 16/11/15.
 */
@EBean
public class ZipDownloader {

    private static final String TAG = ZipDownloader.class.getSimpleName();
    String filename;
    Context context;

    public ZipDownloader(Context context) {
        this.context = context;
    }

    public void initialize(ContentContract.contentPresenter contentPresenter, String url,
                           String foldername, String f_name, Modal_ContentDetail contentDetail,
                           ArrayList<Modal_ContentDetail> levelContents) {
        this.filename = f_name;
        createFolderAndStartDownload(url, foldername, f_name, contentDetail, contentPresenter, levelContents);
    }

    /*Creating folder in internal.
     *That internal might be of android internal or sdcard internal (if available and writable)
     * */
    private void createFolderAndStartDownload(String url, String foldername, String f_name,
                                              Modal_ContentDetail contentDetail,
                                              ContentContract.contentPresenter contentPresenter,
                                              ArrayList<Modal_ContentDetail> levelContents) {
        File mydir = null;
        mydir = new File(PrathamApplication.pradigiPath + "/Pratham" + foldername);
        if (!mydir.exists()) mydir.mkdirs();
        if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
            if (foldername.equalsIgnoreCase(PD_Constant.GAME)) {
                f_name = f_name.substring(0, f_name.lastIndexOf("."));
                File temp_dir = new File(mydir.getAbsolutePath() + "/" + f_name);
                if (!temp_dir.exists()) temp_dir.mkdirs();
                mydir = temp_dir;
            }
        }
        Log.d("internal_file", mydir.getAbsolutePath());

        Modal_Download modal_download = new Modal_Download();
        modal_download.setUrl(url);
        modal_download.setDir_path(mydir.getAbsolutePath());
        modal_download.setF_name(filename);
        modal_download.setFolder_name(foldername);
        modal_download.setContent(contentDetail);
        modal_download.setContentPresenter(contentPresenter);
        modal_download.setLevelContents(levelContents);
        AsyncTask task = new DownloadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, modal_download);
        contentPresenter.currentDownloadRunning(contentDetail.getNodeid(), task);
    }


    private void createOverSdCardAndStartDownload(String url, String foldername, String f_name,
                                                  Modal_ContentDetail contentDetail,
                                                  ContentContract.contentPresenter contentPresenter,
                                                  ArrayList<Modal_ContentDetail> levelContents) {
        String path = FastSave.getInstance().getString(PD_Constant.SDCARD_PATH, "");
        if (path.isEmpty())
            return;
        DocumentFile documentFile = DocumentFile.fromFile(new File(path));
        if (documentFile.findFile("/Pratham" + foldername) != null)
            documentFile = documentFile.findFile("/Pratham" + foldername);
        else
            documentFile = documentFile.createDirectory("/Pratham" + foldername);
        if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
            if (foldername.equalsIgnoreCase(PD_Constant.GAME)) {
                f_name = f_name.substring(0, f_name.lastIndexOf("."));
                if (documentFile.findFile(f_name) != null)
                    documentFile = documentFile.findFile(f_name);
                else
                    documentFile = documentFile.createDirectory(f_name);
            }
        }
        Modal_Download modal_download = new Modal_Download();
        modal_download.setUrl(url);
        modal_download.setDir_path(FileUtils.getPath(PrathamApplication.getInstance(), documentFile.getUri()));
        modal_download.setF_name(filename);
        modal_download.setFolder_name(foldername);
        modal_download.setContent(contentDetail);
        modal_download.setContentPresenter(contentPresenter);
        modal_download.setLevelContents(levelContents);
        new DownloadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, modal_download);
    }

}





