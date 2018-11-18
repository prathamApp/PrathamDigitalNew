package com.pratham.prathamdigital.ui.fragment_content;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.models.Modal_Rasp_Content;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pratham.prathamdigital.PrathamApplication.pradigiPath;

public class ContentPresenterImpl implements ContentContract.contentPresenter {
    private static final String TAG = ContentPresenterImpl.class.getSimpleName();
    Context context;
    ContentContract.contentView contentView;
    ArrayList<Modal_ContentDetail> totalContents;
    ArrayList<Modal_ContentDetail> levelContents = new ArrayList<>();
    Map<Integer, Modal_FileDownloading> filesDownloading = new HashMap<>();

    public ContentPresenterImpl(Context context, ContentContract.contentView contentView) {
        this.context = context;
        this.contentView = contentView;
    }

    public void getContent(Modal_ContentDetail contentDetail) {
        //fetching content from database first
        if (contentDetail == null) {
            new GetDownloadedContent(null).execute();
        } else {
            if (levelContents.isEmpty()) {
                contentView.hideViews();
            }
            levelContents.add(contentDetail);
            contentView.displayHeader(contentDetail);
            new GetDownloadedContent(contentDetail.getNodeid()).execute();
        }
    }

    private void checkConnectivity(String parentId) {
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
            callOnlineContentAPI(parentId);
        } else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                callKolibriAPI(parentId);
            } else {
                callOnlineContentAPI(parentId);
            }
        } else {
            if (totalContents.isEmpty()) {
                contentView.showNoConnectivity();
            } else {
//                Collections.shuffle(totalContents);
                totalContents.add(0, new Modal_ContentDetail());//null modal for displaying header
                contentView.displayContents(totalContents);
            }
        }
    }

    private void callKolibriAPI(String parentId) {
        if (parentId == null) {
            new PD_ApiRequest(context, ContentPresenterImpl.this)
                    .getContentFromRaspberry(PD_Constant.RASPBERRY_HEADER, PD_Constant.GET_RASPBERRY_HEADER);
        } else {
            new PD_ApiRequest(context, ContentPresenterImpl.this)
                    .getContentFromRaspberry(PD_Constant.BROWSE_RASPBERRY, PD_Constant.BROWSE_RASPBERRY_URL + parentId);
        }
    }

    private void callOnlineContentAPI(String parentId) {
        if (parentId == null) {
            new PD_ApiRequest(context, ContentPresenterImpl.this)
                    .getContentFromInternet(PD_Constant.INTERNET_HEADER,
                            PD_Constant.URL.GET_TOP_LEVEL_NODE
                                    + FastSave.getInstance().getString(PD_Constant.LANGUAGE, "Hindi"));
        } else {
            new PD_ApiRequest(context, ContentPresenterImpl.this)
                    .getContentFromInternet(PD_Constant.BROWSE_INTERNET,
                            PD_Constant.URL.BROWSE_BY_ID + parentId);
        }
    }

    public void downloadContent(Modal_ContentDetail contentDetail) {
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
            new PD_ApiRequest(context, ContentPresenterImpl.this).getContentFromInternet(PD_Constant.INTERNET_DOWNLOAD,
                    PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid());
        } else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                String url = contentDetail.getResourcepath();
                String fileName = contentDetail.getResourcepath().substring(
                        contentDetail.getResourcepath().lastIndexOf('/') + 1);
                String foldername = contentDetail.getResourcetype();
                new ZipDownloader(context, ContentPresenterImpl.this, null
                        , url, foldername, fileName, pradigiPath, contentDetail);
            } else {
                new PD_ApiRequest(context, ContentPresenterImpl.this).getContentFromInternet(PD_Constant.INTERNET_DOWNLOAD,
                        PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid());
            }
        } else {
            //todo no internet connecctivity dialog
        }
    }

    @Override
    public void recievedContent(String header, String response) {
        ArrayList<Modal_ContentDetail> displayedContents = new ArrayList<>();
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + header);
            Gson gson = new Gson();
            if (header.equalsIgnoreCase(PD_Constant.RASPBERRY_HEADER)) {
                displayedContents.clear();
                Type listType = new TypeToken<ArrayList<Modal_Rasp_Content>>() {
                }.getType();
                List<Modal_Rasp_Content> rasp_contents = gson.fromJson(response, listType);
                for (Modal_Rasp_Content modal_rasp_content : rasp_contents) {
                    displayedContents.add(modal_rasp_content.setContentToConfigNodeStructure(modal_rasp_content));
                }
                totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                totalContents.add(0, new Modal_ContentDetail());//null modal for displaying header
                contentView.displayContents(totalContents);
            } else if (header.equalsIgnoreCase(PD_Constant.BROWSE_RASPBERRY)) {
                displayedContents.clear();
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
                contentView.displayContents(totalContents);
            } else if ((header.equalsIgnoreCase(PD_Constant.INTERNET_HEADER))) {
                displayedContents.clear();
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
                contentView.displayContents(totalContents);
            } else if ((header.equalsIgnoreCase(PD_Constant.BROWSE_INTERNET))) {
                displayedContents.clear();
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
                contentView.displayContents(totalContents);
            } else if (header.equalsIgnoreCase(PD_Constant.INTERNET_DOWNLOAD)) {
                JSONObject jsonObject = new JSONObject(response);
                Modal_DownloadContent download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                download_content.getNodelist().get(download_content.getNodelist().size() - 1).
                        setResourcepath(pradigiPath + "/Pratham" + download_content.getFoldername()
                                + "/" + download_content.getNodelist().get(download_content.getNodelist().size() - 1)
                                .getResourcepath());
                Modal_ContentDetail contentDetail = download_content.getNodelist().get(download_content.getNodelist().size() - 1);
                String fileName = download_content.getDownloadurl()
                        .substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                new ZipDownloader(context, ContentPresenterImpl.this, null, download_content.getDownloadurl(),
                        download_content.getFoldername(), fileName, pradigiPath, contentDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Modal_ContentDetail> removeDownloadedContents(final ArrayList<Modal_ContentDetail> totalContents
            , final ArrayList<Modal_ContentDetail> onlineContents) {
        ArrayList<Modal_ContentDetail> temp = null;
        if (!totalContents.isEmpty()) {
            temp = new ArrayList<>();
            for (Modal_ContentDetail online : onlineContents) {
                boolean found = false;
                for (Modal_ContentDetail total : totalContents) {
                    if (online.getNodeid().equalsIgnoreCase(total.getNodeid())) {
                        found = true;
                        temp.add(total);                    //content is downloaded}
                        break;
                    }
                }
                if (found) continue;
                else temp.add(online);                   //content is not downloaded
            }
            if (!filesDownloading.isEmpty()) {
                ArrayList<Modal_FileDownloading> mfd = new ArrayList<Modal_FileDownloading>(filesDownloading.values());
                temp = removeIfCurrentlyDownloading(temp, mfd);
            }
            return temp;
        } else
            return onlineContents;
    }

    private ArrayList<Modal_ContentDetail> removeIfCurrentlyDownloading(final ArrayList<Modal_ContentDetail> content,
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
    public void recievedError(String header) {
        if (totalContents.isEmpty()) {
            contentView.showNoConnectivity();
        } else {
//            Collections.shuffle(totalContents);
            totalContents.add(0, new Modal_ContentDetail());//null modal for displaying header
            contentView.displayContents(totalContents);
        }
    }

    @Override
    public void fileDownloadStarted(int downloadId, String filename, Modal_ContentDetail contentDetail) {
        Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
        modal_fileDownloading.setDownloadId(downloadId);
        modal_fileDownloading.setFilename(filename);
        modal_fileDownloading.setProgress(0);
        modal_fileDownloading.setContentDetail(contentDetail);
        filesDownloading.put(downloadId, modal_fileDownloading);
        contentView.increaseNotification(filesDownloading.size());
        for (Modal_ContentDetail detail : levelContents) {
            String f_name = detail.getNodeserverimage()
                    .substring(detail.getNodeserverimage().lastIndexOf('/') + 1);
            PD_ApiRequest.downloadImage(detail.getNodeserverimage(), f_name);
        }
    }

    @Override
    public void updateFileProgress(int downloadId, String filename, int progress) {
        Log.d(TAG, "updateFileProgress: " + downloadId + ":::" + filename + ":::" + progress);
        if (filesDownloading.get(downloadId) != null && filesDownloading.get(downloadId).getProgress() != progress) {
            Modal_FileDownloading modal_fileDownloading = new Modal_FileDownloading();
            modal_fileDownloading.setDownloadId(downloadId);
            modal_fileDownloading.setFilename(filename);
            modal_fileDownloading.setProgress(progress);
            modal_fileDownloading.setContentDetail(filesDownloading.get(downloadId).getContentDetail());
            filesDownloading.put(downloadId, modal_fileDownloading);
//        contentView.updateDownloadList(filesDownloading)
            EventBus.getDefault().post(new ArrayList<Modal_FileDownloading>(filesDownloading.values()));
        }
    }

    @Override
    public void onDownloadCompleted(final int downloadId) {
        Log.d(TAG, "updateFileProgress: " + downloadId);
        ArrayList<Modal_ContentDetail> temp = new ArrayList<>();
        temp.addAll(levelContents);
        Modal_ContentDetail content = filesDownloading.get(downloadId).getContentDetail();
        content.setContentType("file");
        content.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        content.setDownloaded(true);
        temp.add(content);
        BaseActivity.modalContentDao.addContentList(temp);
        filesDownloading.remove(downloadId);
        contentView.decreaseNotification(filesDownloading.size(), content);
        if (filesDownloading.size() == 0) {
            EventBus.getDefault().post(new ArrayList<Modal_FileDownloading>(filesDownloading.values()));
        }
    }

    @Override
    public void onDownloadPaused(int downloadId) {

    }

    @Override
    public void ondownloadCancelled(int downloadId) {

    }

    public void showPreviousContent() {
        if (levelContents.isEmpty()) {
            contentView.showViews();
            new GetDownloadedContent(null).execute();
        } else {
            Modal_ContentDetail contentDetail = levelContents.get(levelContents.size() - 1);
            contentView.displayHeader(contentDetail);
            new GetDownloadedContent(contentDetail.getParentid()).execute();
            levelContents.remove(levelContents.size() - 1);
            if (levelContents.isEmpty())
                contentView.showViews();
        }
    }

    public ArrayList<Modal_ContentDetail> getUpdatedList(Modal_ContentDetail contentDetail) {
//        ArrayList<Modal_ContentDetail> temp = new ArrayList<>();
//        temp.addAll(totalContents);
        totalContents.remove(contentDetail);
        return totalContents;
    }

    private class GetDownloadedContent extends AsyncTask {
        String parentId;

        public GetDownloadedContent(String parentId) {
            this.parentId = parentId;
            totalContents = new ArrayList<>();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if (parentId != null)
                totalContents = (ArrayList<Modal_ContentDetail>) BaseActivity.modalContentDao.getChild(parentId);
            else
                totalContents = (ArrayList<Modal_ContentDetail>) BaseActivity.modalContentDao.getParents();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            checkConnectivity(parentId);
        }
    }
}
