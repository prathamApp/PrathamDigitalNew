package com.pratham.prathamdigital.async;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Download;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.SpeedMonitor;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;

import static com.pratham.prathamdigital.PrathamApplication.modalContentDao;

public class DownloadingTask implements Runnable {
    private static final String TAG = DownloadingTask.class.getSimpleName();
    //    private String url;
//    private String dir_path;
//    private String f_name;
//    private String folder_name;
//    private Modal_ContentDetail content;
//    private String downloadID;
//    private ContentContract.contentPresenter contentPresenter;
//    private ArrayList<Modal_ContentDetail> levelContents;
    private Modal_Download download;

    public DownloadingTask(Modal_Download download) {
        this.download = download;
//        this.url = download.getUrl();
//        this.dir_path = download.getDir_path();
//        this.f_name = download.getF_name();
//        this.folder_name = download.getFolder_name();
//        this.content = download.getContent();
//        this.downloadID = download.getContent().getNodeid();
//        this.contentPresenter = download.getContentPresenter();
//        this.levelContents = download.getLevelContents();
    }

    public static void downloadImage(String url, String filename) {
        File dir = new File(PrathamApplication.pradigiPath + "/PrathamImages"); //Creating an internal dir;
        if (!dir.exists()) dir.mkdirs();
        AndroidNetworking.download(url, dir.getAbsolutePath(), filename)
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d("image::", "DownloadComplete");
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("image::", "Not Downloaded");
                    }
                });
    }

    @Override
    public void run() {
        downloadContentFromServer(download);
    }

    public void downloadContentFromServer(Modal_Download modal_download) {
        notifyAdapter(modal_download);
        dowloadImages(modal_download, modal_download.getLevelContents());
        AndroidNetworking.download(modal_download.getUrl(), modal_download.getDir_path(), modal_download.getF_name())
                .setTag(modal_download.getContent().getNodeid())
                .setPriority(Priority.MEDIUM)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .setDownloadProgressListener((bytesDownloaded, totalBytes) -> {
                    if (totalBytes < 0)
                        totalBytes = (modal_download.getContent().getLevel() > 0) ? modal_download.getContent().getLevel() : 1;
                    updateProgress(modal_download, totalBytes, bytesDownloaded);
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        notifyDownloadSuccess(modal_download);
                    }

                    @Override
                    public void onError(ANError anError) {
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.FAST_DOWNLOAD_ERROR);
                        msg.setDownloadId(modal_download.getContent().getNodeid());
                        EventBus.getDefault().post(msg);
                    }
                });
    }

    public void notifyAdapter(Modal_Download modal_download) {
        Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
        modal_fileDownloading.setDownloadId(modal_download.getContent().getNodeid());
        modal_fileDownloading.setFilename(modal_download.getContent().getNodetitle());
        modal_fileDownloading.setProgress(0);
        modal_fileDownloading.setContentDetail(modal_download.getContent());
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FAST_DOWNLOAD_STARTED);
        msg.setDownloadId(modal_download.getContent().getNodeid());
        msg.setModal_fileDownloading(modal_fileDownloading);
        EventBus.getDefault().post(msg);
    }

    public void dowloadImages(Modal_Download modal_download, ArrayList<Modal_ContentDetail> levelContents) {
        for (Modal_ContentDetail detail : levelContents) {
            if (detail.getNodeserverimage() != null) {
                String f_name = detail.getNodeserverimage()
                        .substring(detail.getNodeserverimage().lastIndexOf('/') + 1);
                downloadImage(detail.getNodeserverimage(), f_name);
            }
        }
        if (modal_download.getContent().getNodeserverimage() != null) {
            String f_name = modal_download.getContent().getNodeserverimage()
                    .substring(modal_download.getContent().getNodeserverimage().lastIndexOf('/') + 1);
            downloadImage(modal_download.getContent().getNodeserverimage(), f_name);
        }
    }

    public void updateProgress(Modal_Download modal_download, long totalBytes, long bytesDownloaded) {
        Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
        modal_fileDownloading.setDownloadId(Objects.requireNonNull(modal_download).getContent().getNodeid());
        modal_fileDownloading.setFilename(modal_download.getContent().getNodetitle());
        modal_fileDownloading.setProgress((int) ((100 * bytesDownloaded) / totalBytes));
        modal_fileDownloading.setContentDetail(modal_download.getContent());
        modal_fileDownloading.setRemaining_time(SpeedMonitor.compute((int) (totalBytes - bytesDownloaded)));
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(PD_Constant.FAST_DOWNLOAD_UPDATE);
        eventMessage.setDownloadId(modal_download.getContent().getNodeid());
        eventMessage.setModal_fileDownloading(modal_fileDownloading);
        EventBus.getDefault().post(eventMessage);
    }

    public void notifyDownloadSuccess(Modal_Download modal_download) {
        if (Objects.requireNonNull(modal_download).getFolder_name().equalsIgnoreCase(PD_Constant.GAME))
            unzipFile(modal_download.getDir_path() + "/" + modal_download.getF_name(), modal_download.getDir_path());
        modal_download.getContent().setContentType("file");
        ArrayList<Modal_ContentDetail> temp = new ArrayList<>(modal_download.getLevelContents());
        temp.add(modal_download.getContent());
        for (Modal_ContentDetail d : temp) {
            if (d.getNodeimage() != null) {
                String img_name = d.getNodeimage().substring(d.getNodeimage().lastIndexOf('/') + 1);
                d.setNodeimage(img_name);
            }
            d.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            d.setDownloaded(true);
            d.setOnSDCard(false);
        }
        modalContentDao.addContentList(temp);
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.FAST_DOWNLOAD_COMPLETE);
        message.setDownloadId(modal_download.getContent().getNodeid());
        message.setContentDetail(modal_download.getContent());
        EventBus.getDefault().post(message);
    }

    private void unzipFile(String source, String destination) {
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
            new File(source).delete();
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
