package com.pratham.prathamdigital.async;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener3;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Download;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.ui.content_player.course_detail.CourseDetailFragment;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.ui.fragment_content.ContentPresenterImpl;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.SpeedMonitor;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;

import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.modalContentDao;

@EBean
public class ZipDownloader {

    @Bean(PD_ApiRequest.class)
    PD_ApiRequest pd_apiRequest;
    private String filename;

    long bytes_downloaded, bytes_total;

    ZipDownloader(Context context) {
        Context context1 = context;
    }

    public void initialize(ContentContract.contentPresenter contentPresenter, String url,
                           String foldername, String f_name, Modal_ContentDetail contentDetail,
                           ArrayList<Modal_ContentDetail> levelContents, String isRaspPi) {
        this.filename = f_name;
        createFolderAndStartDownload(url, foldername, f_name, contentDetail, contentPresenter, levelContents, isRaspPi);
    }

    /**created for download functionality in course fragment*/
    public void initializeforCourse(CourseDetailFragment courseDetailFragment, String url,
                            String foldername, String f_name, Modal_ContentDetail contentDetail,
                            ArrayList<Modal_ContentDetail> levelContents, String isRaspPi) {
        this.filename = f_name;
        createFolderAndStartDownloadforCourse(url, foldername, f_name, contentDetail, courseDetailFragment, levelContents, isRaspPi);
    }

    /**Creating folder in internal.
     *That internal might be of android internal or sdcard internal (if available and writable)
     * */
    @Background
    public void createFolderAndStartDownload(String url, String foldername, String f_name,
                                             Modal_ContentDetail contentDetail,
                                             ContentContract.contentPresenter contentPresenter,
                                             ArrayList<Modal_ContentDetail> levelContents, String isRaspPi) {
        File mydir;
        mydir = new File(PrathamApplication.pradigiPath + "/Pratham" + foldername);
        if (!mydir.exists()) mydir.mkdirs();
/*        if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
            if (foldername.equalsIgnoreCase(PD_Constant.GAME)) {
                f_name = f_name.substring(0, f_name.lastIndexOf("."));
                File temp_dir = new File(mydir.getAbsolutePath() + "/" + f_name);
                if (!temp_dir.exists()) temp_dir.mkdirs();
                mydir = temp_dir;
            }
        }*/
        Log.d("internal_file", mydir.getAbsolutePath());

        Modal_Download modal_download = new Modal_Download();
        modal_download.setUrl(url);
        modal_download.setDir_path(mydir.getAbsolutePath());
        modal_download.setF_name(filename);
        modal_download.setFolder_name(foldername);
        modal_download.setContent(contentDetail);
        modal_download.setContentPresenter(contentPresenter);
        modal_download.setLevelContents(levelContents);

        //If download is over RaspPi device
        if(isRaspPi.equalsIgnoreCase(PD_Constant.RASPPI)) {
            //download Thumbnail image first
            downloadRaspImages(modal_download, modal_download.getLevelContents());
            //Todo : Check if this can be moved to PD_ApiRequest class
            AndroidNetworking.download(url, mydir.getAbsolutePath(), filename)
                    .setTag("downloadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            updateProgress(modal_download, totalBytes, bytesDownloaded);
                            Log.e("url prgrs:", String.valueOf(bytesDownloaded) + " | " + String.valueOf(totalBytes));
                        }
                    })
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            notifyDownloadSuccess(modal_download);
                            Log.e("url prgrs cmp:", "Complete");
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("url prgrs error:", "Error");
                        }
                    });
        } else {
            //download Thumbnail image first
            downloadImages(modal_download, modal_download.getLevelContents());
            DownloadTask task = new DownloadTask.Builder(url, new File(modal_download.getDir_path()))
                    .setFilename(modal_download.getF_name())
                    // the minimal interval millisecond for callback progress
                    .setMinIntervalMillisCallbackProcess(30)
                    .build();
            task.setTag(modal_download);
            task.enqueue(listener);
            contentPresenter.currentDownloadRunning(contentDetail.getNodeid(), task);
        }
    }

    /**created for download functionality in course fragment*/
    @Background
    public void createFolderAndStartDownloadforCourse(String url, String foldername, String f_name,
                                             Modal_ContentDetail contentDetail,
                                             CourseDetailFragment courseDetailFragment,
                                             ArrayList<Modal_ContentDetail> levelContents,
                                                      String isRaspPi) {
        File mydir;
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
        modal_download.setCourseDetailFragment(courseDetailFragment);
        modal_download.setLevelContents(levelContents);

        if(isRaspPi.equalsIgnoreCase(PD_Constant.RASPPI)) {
            //download Thumbnail image first
            downloadRaspImages(modal_download, modal_download.getLevelContents());
            AndroidNetworking.download(url, mydir.getAbsolutePath(), filename)
                    .setTag("downloadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            updateProgress(modal_download, totalBytes, bytesDownloaded);
                            Log.e("url prgrs:", String.valueOf(bytesDownloaded) + " | " + String.valueOf(totalBytes));
                        }
                    })
                    .startDownload(new DownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            notifyDownloadSuccess(modal_download);
                            Log.e("url prgrs cmp:", "Complete");
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("url prgrs error:", "Error");
                        }
                    });
        } else {
            //download Thumbnail image first
            downloadImages(modal_download, modal_download.getLevelContents());
            DownloadTask task = new DownloadTask.Builder(url, new File(modal_download.getDir_path()))
                    .setFilename(modal_download.getF_name())
                    // the minimal interval millisecond for callback progress
                    .setMinIntervalMillisCallbackProcess(30)
                    .build();
            task.setTag(modal_download);
            task.enqueue(listener);
            courseDetailFragment.currentDownloadRunning(contentDetail.getNodeid(), task);
        }
    }

    private com.liulishuo.okdownload.DownloadListener listener = new DownloadListener3() {
        @Override
        protected void started(@NonNull DownloadTask task) {
            notifyAdapter((Modal_Download) task.getTag());
        }

        @Override
        protected void completed(@NonNull DownloadTask task) {
            notifyDownloadSuccess((Modal_Download) task.getTag());
        }

        @Override
        protected void canceled(@NonNull DownloadTask task) {
            notifyError((Modal_Download) task.getTag());
        }

        @Override
        protected void error(@NonNull DownloadTask task, @NonNull Exception e) {
            /**From android 10 and above this library creates issue. While downloading the resource library calls this error method
             * without any issue. So the download gets cancelled. For downloading complete resource need to click download multiple
             * times.
             *
             * For resolving that issue, below condition is added. As observed below exception is thrown even the resource is 100%
             * downloaded. Currently this fix is working correctly, tested by me and jay on multiple os versions 10, 11 & 12.
            */
            Log.d("OKdwnldError:", ((Modal_Download) task.getTag()).getF_name());
            Log.d("OKdwnldErrorMsg:", (e.getMessage()));

            if(!e.getMessage().contains("The current offset on block-info isn't update correct"))
            notifyError((Modal_Download) task.getTag());
            else {
                Log.d("bypass:::", String.valueOf(bytes_downloaded+" | "+bytes_total));
                notifyDownloadSuccess((Modal_Download) task.getTag());
            }
        }

        @Override
        protected void warn(@NonNull DownloadTask task) {

        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
            Log.d("retry:::", ((Modal_Download) task.getTag()).getF_name());
            notifyError((Modal_Download) task.getTag());
        }

        @Override
        public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            bytes_downloaded = currentOffset;
            bytes_total = totalLength;
            /**
             * Here resource download sized is checked. If complete resource is downloaded then success method is called else update
             * method is called. As noticed above on os version 10 even after downloading resource 100% default complete method is not
             * called, here we call success method after checking.
             */
            if(currentOffset==totalLength){
                notifyDownloadSuccess((Modal_Download) task.getTag());
            } else {
                Modal_Download modal_download = (Modal_Download) task.getTag();
                if (totalLength <= 0)
                    totalLength = (modal_download.getContent().getLevel() > 0) ? modal_download.getContent().getLevel() : 1;
                updateProgress(modal_download, totalLength, currentOffset);
            }
        }
    };

    private void notifyError(Modal_Download modal_download) {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FAST_DOWNLOAD_ERROR);
        msg.setDownloadId(modal_download.getContent().getNodeid());
        EventBus.getDefault().post(msg);
    }

    @Background
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

    @Background
    public void notifyDownloadSuccess(Modal_Download modal_download) {
        if (Objects.requireNonNull(modal_download).getFolder_name().equalsIgnoreCase(PD_Constant.GAME))
            unzipFile(modal_download.getDir_path() + "/" + modal_download.getF_name(), modal_download.getDir_path());
        modal_download.getContent().setContentType("file");
        ArrayList<Modal_ContentDetail> temp = new ArrayList<>(modal_download.getLevelContents());
        temp.add(modal_download.getContent());
        String parentid = null;
        for (Modal_ContentDetail d : temp) {
            if (d.getContentType().equalsIgnoreCase(PD_Constant.FOLDER)) parentid = d.getNodeid();
            else {
                //for keeping the same parent id as with other childrens if downloaded with other source i.e kolibri
                if (parentid != null) d.setParentid(parentid);
            }
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

    @Background
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

    private void downloadImages(Modal_Download modal_download, ArrayList<Modal_ContentDetail> levelContents) {
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

    private void downloadRaspImages(Modal_Download modal_download, ArrayList<Modal_ContentDetail> levelContents) {
        for (Modal_ContentDetail detail : levelContents) {
            if (detail.getNodeserverimage() != null) {
                String f_name = detail.getNodeserverimage()
                        .substring(detail.getNodeserverimage().lastIndexOf('/') + 1);
                String url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_IMAGES + detail.getNodeimage();
                downloadImage(url, f_name);
            }
        }
        if (modal_download.getContent().getNodeserverimage() != null) {
            String f_name = modal_download.getContent().getNodeserverimage()
                    .substring(modal_download.getContent().getNodeserverimage().lastIndexOf('/') + 1);
            String url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_IMAGES + modal_download.getContent().getNodeimage();
            downloadImage(url, f_name);
        }
    }

    private static void downloadImage(String url, String filename) {
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
}