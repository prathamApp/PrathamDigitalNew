package com.pratham.prathamdigital.async;

import android.os.AsyncTask;
import android.util.Log;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Download;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.util.PD_Constant;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadingTask extends AsyncTask {
    private static final String TAG = DownloadingTask.class.getSimpleName();
    String url;
    String dir_path;
    String f_name;
    String folder_name;
    Modal_ContentDetail content;
    String downloadID;
    //    DownloadService downloadService;
    ContentContract.contentPresenter contentPresenter;
    ArrayList<Modal_ContentDetail> levelContents;

    private void initialize(Modal_Download download) {
        this.url = download.getUrl();
        this.dir_path = download.getDir_path();
        this.f_name = download.getF_name();
        this.folder_name = download.getFolder_name();
        this.content = download.getContent();
        this.downloadID = download.getContent().getNodeid();
        this.contentPresenter = download.getContentPresenter();
        this.levelContents = download.getLevelContents();
    }

    protected void afterInit() {
        Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
        modal_fileDownloading.setDownloadId(downloadID);
        modal_fileDownloading.setFilename(content.getNodetitle());
        modal_fileDownloading.setProgress(0);
        modal_fileDownloading.setContentDetail(content);
        contentPresenter.fileDownloadStarted(downloadID, modal_fileDownloading);
    }

    @Override
    protected Object doInBackground(Object... params) {
        Log.d(TAG, "doInBackground: " + url);
        Modal_Download download = (Modal_Download) params[0];
        initialize(download);
        afterInit();
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            // String root = Environment.getExternalStorageDirectory().toString();
            URL urlFormed = new URL(url);
            connection = (HttpURLConnection) urlFormed.openConnection();
            connection.setConnectTimeout(15000);
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            Log.d(TAG, "doInBackground:" + connection.getResponseCode());
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
            // getting file length
            dowloadImages();
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
                updateProgress(download_percentage_new);
            }
            // flushing output
            if (output != null)
                output.close();
            // closing streams/**/
            if (input != null)
                input.close();
            if (folder_name.equalsIgnoreCase(PD_Constant.GAME)) {
                unzipFile(dir_path + "/" + f_name, dir_path);
            }
            downloadCompleted();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void downloadCompleted() {
        Log.d(TAG, "updateFileProgress: " + downloadID);
        content.setContentType("file");
        ArrayList<Modal_ContentDetail> temp = new ArrayList<>();
        temp.addAll(levelContents);
        temp.add(content);
        for (Modal_ContentDetail d : temp) {
            if (d.getNodeimage() != null) {
                String img_name = d.getNodeimage().substring(d.getNodeimage().lastIndexOf('/') + 1);
                d.setNodeimage(img_name);
            }
            d.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            d.setDownloaded(true);
            d.setOnSDCard(false);
        }
        BaseActivity.modalContentDao.addContentList(temp);
    }

    private void updateProgress(long download_percentage_new) {
        Log.d(TAG, "updateFileProgress: " + downloadID + ":::" + f_name + ":::" + download_percentage_new);
        if (downloadID != null) {
            Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
            modal_fileDownloading.setDownloadId(downloadID);
            modal_fileDownloading.setFilename(content.getNodetitle());
            modal_fileDownloading.setProgress((int) download_percentage_new);
            modal_fileDownloading.setContentDetail(content);
            publishProgress(modal_fileDownloading);
        }
    }

    private void dowloadImages() {
        for (Modal_ContentDetail detail : levelContents) {
            if (detail.getNodeserverimage() != null) {
                String f_name = detail.getNodeserverimage()
                        .substring(detail.getNodeserverimage().lastIndexOf('/') + 1);
                PD_ApiRequest.downloadImage(detail.getNodeserverimage(), f_name);
            }
        }
        if (content.getNodeserverimage() != null) {
            String f_name = content.getNodeserverimage()
                    .substring(content.getNodeserverimage().lastIndexOf('/') + 1);
            PD_ApiRequest.downloadImage(content.getNodeserverimage(), f_name);
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
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        Modal_FileDownloading mfd = (Modal_FileDownloading) values[0];
        contentPresenter.updateFileProgress(downloadID, mfd);
    }

    @Override
    protected void onPostExecute(Object r) {
        Log.d(TAG, "onPostExecute");
        boolean result = (boolean) r;
        if (result) {
            contentPresenter.onDownloadCompleted(downloadID, content);
        } else {
            contentPresenter.ondownloadError(downloadID);
        }
    }
}
