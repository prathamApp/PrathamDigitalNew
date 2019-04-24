package com.pratham.prathamdigital.ui.fragment_content;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.GetDownloadedContent;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.interfaces.DownloadedContents;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.models.Modal_RaspFacility;
import com.pratham.prathamdigital.models.Modal_Rasp_Content;
import com.pratham.prathamdigital.models.Modal_Rasp_Header;
import com.pratham.prathamdigital.util.FileUtils;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@EBean
public class ContentPresenterImpl implements ContentContract.contentPresenter, DownloadedContents, ApiResult {
    private static final String TAG = ContentPresenterImpl.class.getSimpleName();
    private final Context context;
    private final Map<String, Modal_FileDownloading> filesDownloading = new HashMap<>();
    private final Map<String, AsyncTask> currentDownloadTasks = new HashMap<>();
    @Bean(PD_ApiRequest.class)
    PD_ApiRequest pd_apiRequest;
    private ContentContract.contentView contentView;
    ArrayList<String> dlContentIDs = new ArrayList<>();

    @Bean(ZipDownloader.class)
    ZipDownloader zipDownloader;
    private ArrayList<Modal_ContentDetail> levelContents;

    public ContentPresenterImpl(Context context) {
        this.context = context;
    }

    @Override
    public void setView(FragmentContent context) {
        this.contentView = context;
        pd_apiRequest.setApiResult(ContentPresenterImpl.this);
    }

    @Override
    public void viewDestroyed() {
        contentView = null;
    }

    @Background
    @Override
    public void getContent(Modal_ContentDetail contentDetail) {
        //fetching content from database first
        if (contentDetail == null) {
            new GetDownloadedContent(ContentPresenterImpl.this, null).execute();
        } else {
            if (levelContents == null) levelContents = new ArrayList<>();
            boolean found = false;
            for (int i = 0; i < levelContents.size(); i++) {
                if (levelContents.get(i).getNodeid().equalsIgnoreCase(contentDetail.getNodeid())) {
                    //to prevent crash if clicked on last item i.e indexOutOfBoundException during sublist
                    if ((i + 1) == levelContents.size()) found = true;
                    else {
                        found = true;
                        levelContents.subList(i + 1, levelContents.size()).clear();
                    }
                }
            }
            if (!found) levelContents.add(contentDetail);
            if (contentView != null) {
                if (levelContents.size() == 1)
                    contentView.animateHamburger();
                contentView.displayLevel(levelContents);
            }
            new GetDownloadedContent(ContentPresenterImpl.this, contentDetail.getNodeid()).execute();
        }
    }

    @Override
    public void getContent() {
        if (levelContents != null && levelContents.size() > 0) {
            new GetDownloadedContent(ContentPresenterImpl.this,
                    levelContents.get(levelContents.size() - 1).getNodeid()).execute();
        } else {
            if (contentView != null)
                contentView.dismissDialog();
        }
    }

    @Background
    @Override
    public void checkConnectionForRaspberry() {
        if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("username", "pratham");
                    object.put("password", "pratham");
                    pd_apiRequest.getacilityIdfromRaspberry(PD_Constant.FACILITY_ID, PD_Constant.RASP_IP + "/api/session/", object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Background
    public void checkConnectivity(ArrayList<Modal_ContentDetail> contentList, String parentId) {
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
            callOnlineContentAPI(contentList, parentId);
        } else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                if (FastSave.getInstance().getString(PD_Constant.FACILITY_ID, "").isEmpty())
                    checkConnectionForRaspberry();
                callKolibriAPI(contentList, parentId);
            } else {
                callOnlineContentAPI(contentList, parentId);
            }
        } else {
            if (contentList.isEmpty()) {
                if (contentView != null)
                    contentView.showNoConnectivity();
            } else {
//                Collections.shuffle(totalContents);
                contentList.add(0, new Modal_ContentDetail());//null modal for displaying header
                if (contentView != null)
                    contentView.displayContents(contentList);
            }
        }
    }

    @Background
    public void callKolibriAPI(ArrayList<Modal_ContentDetail> contentList, String parentId) {
        if (parentId == null || parentId.equalsIgnoreCase("0") || parentId.isEmpty()) {
            pd_apiRequest.getContentFromRaspberry(PD_Constant.RASPBERRY_HEADER, PD_Constant.URL.GET_RASPBERRY_HEADER.toString(), contentList);
        } else {
            pd_apiRequest.getContentFromRaspberry(PD_Constant.BROWSE_RASPBERRY, PD_Constant.URL.BROWSE_RASPBERRY_URL.toString()
                    + parentId, contentList);
        }
    }

    @Background
    public void callOnlineContentAPI(ArrayList<Modal_ContentDetail> contentList, String parentId) {
        if (parentId == null || parentId.equalsIgnoreCase("0") || parentId.isEmpty()) {
            pd_apiRequest.getContentFromInternet(PD_Constant.INTERNET_HEADER,
                    PD_Constant.URL.GET_TOP_LEVEL_NODE
                            + FastSave.getInstance().getString(PD_Constant.LANGUAGE, "Hindi"), contentList);
        } else {
            pd_apiRequest.getContentFromInternet(PD_Constant.BROWSE_INTERNET,
                    PD_Constant.URL.BROWSE_BY_ID + parentId, contentList);
        }
    }

    @Background
    @Override
    public void downloadContent(Modal_ContentDetail contentDetail) {
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
            pd_apiRequest.getContentFromInternet(PD_Constant.INTERNET_DOWNLOAD,
                    PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid(), null);
        } else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                try {
                    String url = contentDetail.getNodekeywords();
                    String filename = URLDecoder.decode(contentDetail.getNodekeywords(), "UTF-8")
                            .substring(URLDecoder.decode(contentDetail.getNodekeywords(), "UTF-8").lastIndexOf('/') + 1);
                    String foldername = contentDetail.getResourcetype();
                    zipDownloader.initialize(ContentPresenterImpl.this
                            , url, foldername, filename, contentDetail, levelContents);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                pd_apiRequest.getContentFromInternet(PD_Constant.INTERNET_DOWNLOAD,
                        PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid(), null);
            }
        }
    }


    @Background
    @Override
    public void recievedContent(String header, String response, ArrayList<Modal_ContentDetail> contentList) {
        ArrayList<Modal_ContentDetail> displayedContents = new ArrayList<>();
        ArrayList<Modal_ContentDetail> totalContents = new ArrayList<>();
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + header);
            Gson gson = new Gson();
            if (header.equalsIgnoreCase(PD_Constant.FACILITY_ID)) {
                Modal_RaspFacility facility = gson.fromJson(response, Modal_RaspFacility.class);
                FastSave.getInstance().saveString(PD_Constant.FACILITY_ID, facility.getFacilityId());
            } else if (header.equalsIgnoreCase(PD_Constant.RASPBERRY_HEADER)) {
                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                Type listType = new TypeToken<ArrayList<Modal_Rasp_Header>>() {
                }.getType();
                List<Modal_Rasp_Header> rasp_headers = gson.fromJson(response, listType);
                for (Modal_Rasp_Header modal_rasp_header : rasp_headers) {
                    String languageSelected = FastSave.getInstance().getString(PD_Constant.LANGUAGE, "");
                    if (languageSelected.equalsIgnoreCase(PD_Utility.getLanguageKeyword(modal_rasp_header.getLang_code()))
                            || modal_rasp_header.getLang_code().equalsIgnoreCase("mul"))
                        displayedContents.add(modal_rasp_header.setContentToConfigNodeStructure(modal_rasp_header));
                }
                totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                totalContents.add(0, new Modal_ContentDetail());//null modal for displaying header
                if (contentView != null)
                    contentView.displayContents(totalContents);
            } else if (header.equalsIgnoreCase(PD_Constant.BROWSE_RASPBERRY)) {
                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                Type listType = new TypeToken<ArrayList<Modal_Rasp_Content>>() {
                }.getType();
                List<Modal_Rasp_Content> rasp_contents = gson.fromJson(response, listType);
                for (Modal_Rasp_Content modal_rasp_content : rasp_contents) {
                    String languageSelected = FastSave.getInstance().getString(PD_Constant.LANGUAGE, "");
                    if (languageSelected.equalsIgnoreCase(PD_Utility.getLanguageKeyword(modal_rasp_content.getLang().getLangCode()))
                            || modal_rasp_content.getLang().getLangCode().equalsIgnoreCase("mul")) {
                        displayedContents.add(modal_rasp_content.setContentToConfigNodeStructure(modal_rasp_content));
                    }
                }
                totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                totalContents.add(0, new Modal_ContentDetail());//null modal for displaying header
                if (contentView != null)
                    contentView.displayContents(totalContents);
            } else if ((header.equalsIgnoreCase(PD_Constant.INTERNET_HEADER))) {
                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                List<Modal_ContentDetail> tempContents = gson.fromJson(response, listType);
                for (Modal_ContentDetail detail : tempContents) {
                    if (detail.getResourcetype().equalsIgnoreCase("Game")
                            || detail.getResourcetype().equalsIgnoreCase("Video")
                            || detail.getResourcetype().equalsIgnoreCase("Pdf"))
                        detail.setContentType("file");
                    else
                        detail.setContentType("folder");
                    detail.setContent_language(BaseActivity.language);
                    displayedContents.add(detail);
                }
                totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                totalContents.add(0, new Modal_ContentDetail());//null modal for displaying header
                if (contentView != null)
                    contentView.displayContents(totalContents);
            } else if ((header.equalsIgnoreCase(PD_Constant.BROWSE_INTERNET))) {
                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                List<Modal_ContentDetail> tempContents = gson.fromJson(response, listType);
                for (Modal_ContentDetail detail : tempContents) {
                    if (detail.getResourcetype().equalsIgnoreCase("Game")
                            || detail.getResourcetype().equalsIgnoreCase("Video")
                            || detail.getResourcetype().equalsIgnoreCase("Pdf"))
                        detail.setContentType("file");
                    else
                        detail.setContentType("folder");
                    detail.setContent_language(BaseActivity.language);
                    displayedContents.add(detail);
                }
                totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                totalContents.add(0, new Modal_ContentDetail());//null modal for displaying header
                if (contentView != null)
                    contentView.displayContents(totalContents);
            } else if (header.equalsIgnoreCase(PD_Constant.INTERNET_DOWNLOAD)) {
                JSONObject jsonObject = new JSONObject(response);
                Modal_DownloadContent download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                Modal_ContentDetail contentDetail = download_content.getNodelist().get(download_content.getNodelist().size() - 1);
                String fileName = download_content.getDownloadurl()
                        .substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                zipDownloader.initialize(ContentPresenterImpl.this, download_content.getDownloadurl(),
                        download_content.getFoldername(), fileName, contentDetail, levelContents);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Modal_ContentDetail> removeDownloadedContents(final ArrayList<Modal_ContentDetail> totalContents
            , final ArrayList<Modal_ContentDetail> onlineContents) {
        if (!totalContents.isEmpty()) {
            for (Modal_ContentDetail total : totalContents) {
                boolean found = false;
                for (int i = 0; i < onlineContents.size(); i++) {
                    boolean replaced = false;
                    if (onlineContents.get(i).getNodeid().equalsIgnoreCase(total.getNodeid())) {
                        onlineContents.set(i, total);                    //content is downloaded
                        replaced = true;
                    }
                    if (replaced) {
                        found = true;                              //content not found in list, just add it
                        break;
                    }
                }
                if (!found)
                    onlineContents.add(total);
            }
            return onlineContents;
        } else
            return onlineContents;
    }

    public ArrayList<Modal_ContentDetail> removeIfCurrentlyDownloading(final ArrayList<Modal_ContentDetail> content,
                                                                       final ArrayList<Modal_FileDownloading> mfd) {
        ArrayList<Modal_ContentDetail> temp = new ArrayList<>();
        for (Modal_ContentDetail c : content) {
            boolean found = false;
            for (Modal_FileDownloading files : mfd) {
                if (c.getNodeid().equalsIgnoreCase(files.getContentDetail().getNodeid())) {
                    //content is downloading, no need to add in list
                    found = true;
                    break;
                }
            }
            if (found) continue;
            else temp.add(c);
        }
        return temp;
    }

    @Override
    public void recievedError(String header, ArrayList<Modal_ContentDetail> contentList) {
        if (contentList != null && contentList.isEmpty()) {
            if (contentView != null)
                contentView.showNoConnectivity();
        } else {
//            Collections.shuffle(totalContents);
            Objects.requireNonNull(contentList).add(0, new Modal_ContentDetail());//null modal for displaying header
            if (contentView != null)
                contentView.displayContents(contentList);
        }
    }

    @Override
    public void fileDownloadStarted(String downloadID, Modal_FileDownloading modal_fileDownloading) {
        filesDownloading.put(downloadID, modal_fileDownloading);
        postDownloadStartMessage();
    }

    @Override
    public void updateFileProgress(String downloadID, Modal_FileDownloading mfd) {
        filesDownloading.put(downloadID, mfd);
        postProgressMessage();
    }

    @Override
    public void onDownloadCompleted(String downloadID, Modal_ContentDetail content) {
        filesDownloading.remove(downloadID);
        postAllDownloadsCompletedMessage();
        postSingleFileDownloadCompleteMessage(content);
        currentDownloadTasks.remove(downloadID);
    }

    @Override
    public void ondownloadError(String downloadId) {
        Modal_ContentDetail content = Objects.requireNonNull(filesDownloading.get(downloadId)).getContentDetail();
        postSingleFileDownloadErrorMessage(content);
        filesDownloading.remove(downloadId);
        EventBus.getDefault().post(new ArrayList<>(filesDownloading.values()));
        cancelDownload(downloadId);
    }

    @Override
    public void broadcast_downloadings() {
        postProgressMessage();
    }

    @Override
    public void eventFileDownloadStarted(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_STARTED)) {
                eventFileDownloadStarted_(message);
            }
        }
    }

    @Background
    public void eventFileDownloadStarted_(EventMessage message) {
        Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
        modal_fileDownloading.setDownloadId(message.getDownloadId());
        modal_fileDownloading.setFilename(message.getFile_name());
        modal_fileDownloading.setProgress(0);
        modal_fileDownloading.setContentDetail(message.getContentDetail());
        filesDownloading.put(message.getDownloadId(), modal_fileDownloading);
        postDownloadStartMessage();
        for (Modal_ContentDetail detail : levelContents) {
            if (detail.getNodeserverimage() != null) {
                String f_name = detail.getNodeserverimage()
                        .substring(detail.getNodeserverimage().lastIndexOf('/') + 1);
                PD_ApiRequest.downloadImage(detail.getNodeserverimage(), f_name);
            }
        }
        String f_name = message.getContentDetail().getNodeserverimage()
                .substring(message.getContentDetail().getNodeserverimage().lastIndexOf('/') + 1);
        PD_ApiRequest.downloadImage(message.getContentDetail().getNodeserverimage(), f_name);
    }

    @UiThread
    public void postDownloadStartMessage() {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_DOWNLOAD_STARTED);
        msg.setDownlaodContentSize(filesDownloading.size());
        EventBus.getDefault().post(msg);
    }

    @Override
    public void eventUpdateFileProgress(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_UPDATE)) {
                eventUpdateFileProgress_(message);
            }
        }
    }

    @Background
    public void eventUpdateFileProgress_(EventMessage message) {
        String downloadId = message.getDownloadId();
        String filename = message.getFile_name();
        int progress = (int) message.getProgress();
        Log.d(TAG, "updateFileProgress: " + downloadId + ":::" + filename + ":::" + progress);
        if (filesDownloading.get(downloadId) != null /*&& filesDownloading.get(downloadId).getProgress() != progress*/) {
            Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
            modal_fileDownloading.setDownloadId(String.valueOf(downloadId));
            modal_fileDownloading.setFilename(filename);
            modal_fileDownloading.setProgress(progress);
            modal_fileDownloading.setContentDetail(Objects.requireNonNull(filesDownloading.get(downloadId)).getContentDetail());
            filesDownloading.put(String.valueOf(downloadId), modal_fileDownloading);
            postProgressMessage();
        }
    }

    @UiThread
    public void postProgressMessage() {
        EventBus.getDefault().post(new ArrayList<>(filesDownloading.values()));
    }

    @Override
    public void eventOnDownloadCompleted(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_COMPLETE)) {
                eventOnDownloadCompleted_(message);
            }
        }
    }

    @Background
    public void eventOnDownloadCompleted_(EventMessage message) {
        String downloadId = message.getDownloadId();
        Log.d(TAG, "updateFileProgress: " + downloadId);
        ArrayList<Modal_ContentDetail> temp = new ArrayList<>(levelContents);
        Modal_ContentDetail content = Objects.requireNonNull(filesDownloading.get(downloadId)).getContentDetail();
        content.setContentType("file");
        content.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
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
        filesDownloading.remove(downloadId);
        postAllDownloadsCompletedMessage();
        postSingleFileDownloadCompleteMessage(content);
    }

    @UiThread
    public void postSingleFileDownloadCompleteMessage(Modal_ContentDetail content) {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_DOWNLOAD_COMPLETE);
        msg.setDownlaodContentSize(filesDownloading.size());
        msg.setContentDetail(content);
        EventBus.getDefault().post(msg);
    }

    @UiThread
    public void postAllDownloadsCompletedMessage() {
//        if (filesDownloading.size() == 0) {
        EventBus.getDefault().post(new ArrayList<>(filesDownloading.values()));
//        }
    }

    @Background
    @Override
    public void eventOnDownloadFailed(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_FAILED)) {
                Modal_ContentDetail content = Objects.requireNonNull(filesDownloading.get(message.getDownloadId())).getContentDetail();
                postSingleFileDownloadErrorMessage(content);
                filesDownloading.remove(message.getDownloadId());
                EventBus.getDefault().post(new ArrayList<>(filesDownloading.values()));
            }
        }
    }

    @UiThread
    public void postSingleFileDownloadErrorMessage(Modal_ContentDetail content) {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.FILE_DOWNLOAD_ERROR);
        msg.setDownlaodContentSize(filesDownloading.size());
        msg.setContentDetail(content);
        EventBus.getDefault().post(msg);
    }

    @Override
    public void onDownloadPaused(int downloadId) {

    }

    @Override
    public void ondownloadCancelled(String downloadId) {
    }

    @Background
    @Override
    public void showPreviousContent() {
        if (levelContents == null || levelContents.isEmpty()) {
            if (contentView != null)
                contentView.exitApp();
//            new GetDownloadedContent(null).execute();
        } else {
            Modal_ContentDetail contentDetail = levelContents.get(levelContents.size() - 1);
            new GetDownloadedContent(ContentPresenterImpl.this, contentDetail.getParentid()).execute();
            levelContents.remove(levelContents.size() - 1);
            if (contentView != null) {
                if (levelContents.isEmpty())
                    contentView.animateHamburger();
                contentView.displayLevel(levelContents);
            }
        }
    }

    @Override
    public int getLevels() {
        if (levelContents != null)
            return levelContents.size();
        else
            return 0;
    }

    @Override
    public void downloadedContents(Object o, String parentId) {
        checkConnectivity((ArrayList<Modal_ContentDetail>) o, parentId);
    }

    @Background
    @Override
    public void parseSD_UriandPath(Intent data) {
        Uri treeUri = data.getData();
        final int takeFlags = data.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        PrathamApplication.getInstance().getContentResolver().takePersistableUriPermission(Objects.requireNonNull(treeUri), takeFlags);
        //check if folder exist on sdcard
        DocumentFile documentFile = DocumentFile.fromTreeUri(PrathamApplication.getInstance(), treeUri);
        if (Objects.requireNonNull(documentFile).findFile(PD_Constant.PRADIGI_FOLDER) != null)
            documentFile = documentFile.findFile(PD_Constant.PRADIGI_FOLDER);
        else
            documentFile = documentFile.createDirectory(PD_Constant.PRADIGI_FOLDER);
        //check for language folder
        if (Objects.requireNonNull(documentFile).findFile(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)) != null)
            documentFile = documentFile.findFile(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        else
            documentFile = documentFile.createDirectory(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));

        String path = FileUtils.getPath(PrathamApplication.getInstance(), Objects.requireNonNull(documentFile).getUri());
        FastSave.getInstance().saveString(PD_Constant.SDCARD_URI, treeUri.toString());
        FastSave.getInstance().saveString(PD_Constant.SDCARD_PATH, path);
        PrathamApplication.getInstance().setExistingSDContentPath(path);
    }

    @Background
    @Override
    public void deleteContent(Modal_ContentDetail contentItem) {
        checkAndDeleteParent(contentItem);
        if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.GAME)) {
            String foldername = contentItem.getResourcepath().split("/")[0];
            PD_Utility.deleteRecursive(new File(PrathamApplication.pradigiPath + "/PrathamGame/" + foldername));
        } else if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.VIDEO)) {
            PD_Utility.deleteRecursive(new File(PrathamApplication.pradigiPath
                    + "/PrathamVideo/" + contentItem.getResourcepath()));
        } else if (contentItem.getResourcetype().toLowerCase().equalsIgnoreCase(PD_Constant.PDF)) {
            PD_Utility.deleteRecursive(new File(PrathamApplication.pradigiPath
                    + "/PrathamPdf/" + contentItem.getResourcepath()));
        }
        //delete content thumbnail image
        PD_Utility.deleteRecursive(new File(PrathamApplication.pradigiPath
                + "/PrathamImages/" + contentItem.getNodeimage()));
    }

    @Override
    public void currentDownloadRunning(String downloadId, AsyncTask task) {
        if (!currentDownloadTasks.containsKey(downloadId)) {
            currentDownloadTasks.put(downloadId, task);
        }
    }

    @Override
    public void cancelDownload(String downloadId) {
        if (downloadId != null && !downloadId.isEmpty()) {
            if (currentDownloadTasks.containsKey(downloadId))
                Objects.requireNonNull(currentDownloadTasks.get(downloadId)).cancel(true);
            postProgressMessage();
        }
    }

    private void checkAndDeleteParent(Modal_ContentDetail contentItem) {
        String parentId = contentItem.getParentid();
        BaseActivity.modalContentDao.deleteContent(contentItem.getNodeid());
        if (parentId != null && !parentId.equalsIgnoreCase("0") && !parentId.isEmpty()) {
            int count = BaseActivity.modalContentDao.getChildCountOfParent(parentId,
                    FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            if (count == 0)
                checkAndDeleteParent(BaseActivity.modalContentDao.getContent(parentId,
                        FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)));
        }
    }

    @Background
    @Override
    public void openDeepLinkContent(String dl_content) {
        if (dl_content != null && !dl_content.isEmpty()) {
            Modal_DownloadContent download_content = new Gson().fromJson(dl_content, Modal_DownloadContent.class);
            for (Modal_ContentDetail detail : download_content.getNodelist()) {
                Modal_ContentDetail temp = BaseActivity.modalContentDao.getContent(detail.getNodeid(), BaseActivity.language);
                if (temp == null) {
                    detail.setContent_language(BaseActivity.language);
                    if (detail.getResourcetype().equalsIgnoreCase("Game")
                            || detail.getResourcetype().equalsIgnoreCase("Video")
                            || detail.getResourcetype().equalsIgnoreCase("Pdf"))
                        detail.setContentType(PD_Constant.FILE);
                    else {
                        detail.setContentType(PD_Constant.FOLDER);
                        if (levelContents == null) levelContents = new ArrayList<>();
                        levelContents.add(detail);
                    }
                } else {
                    detail = temp;
                    if (detail.getContentType().equalsIgnoreCase(PD_Constant.FOLDER)) {
                        if (levelContents == null) levelContents = new ArrayList<>();
                        levelContents.add(detail);
                    }
                }
            }
            if (contentView != null) {
                contentView.displayLevel(levelContents);
                List<Modal_ContentDetail> details = new ArrayList<>();
                details.add(0, new Modal_ContentDetail());//null modal for displaying header
                details.add(download_content.getNodelist().get(download_content.getNodelist().size() - 1));
                contentView.displayDLContents(details);
            }
        }
    }
}
