package com.pratham.prathamdigital.ui.fragment_content;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.okdownload.DownloadTask;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.interfaces.DownloadedContents;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Modal_RaspFacility;
import com.pratham.prathamdigital.models.Modal_Rasp_Content;
import com.pratham.prathamdigital.models.Modal_Rasp_Header;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.RaspModel.ModalRaspContentNew;
import com.pratham.prathamdigital.models.RaspModel.Modal_RaspResult;
import com.pratham.prathamdigital.models.RaspModel.Modal_Rasp_JsonData;
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
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.modalContentDao;
import static com.pratham.prathamdigital.PrathamApplication.scoreDao;

@EBean
public class ContentPresenterImpl implements ContentContract.contentPresenter, DownloadedContents, ApiResult {
    private static final String TAG = ContentPresenterImpl.class.getSimpleName();
    private final Map<String, Modal_FileDownloading> filesDownloading = new HashMap<>();
    private final Map<String, DownloadTask> currentDownloadTasks = new HashMap<>();
    @Bean(PD_ApiRequest.class)
    PD_ApiRequest pd_apiRequest;
    private ContentContract.contentView contentView;

    @Bean(ZipDownloader.class)
    ZipDownloader zipDownloader;
    private ArrayList<Modal_ContentDetail> levelContents;
    private ArrayList<Modal_ContentDetail> tempContentList;
    private String mappedParentApi = null;
    private Modal_ContentDetail folderContentClicked;
    private String id_3_6 = "";

    private static final String IS_COURSE = "isCourse";
    String iscourse;

    ContentPresenterImpl(Context context) {
        Context context1 = context;
    }

    private String searchWord="";
    Modal_Log resource_log=null;

    @Override
    public void setView(ContentContract.contentView context) {
        this.contentView = context;
        pd_apiRequest.setApiResult(ContentPresenterImpl.this);
    }

    @Override
    public void viewDestroyed() {
        contentView = null;
    }

    @Background
    @Override
    public void getContent(Modal_ContentDetail contentDetail, String isCourse) {
        iscourse = isCourse;
        //fetching content from database first
        folderContentClicked = contentDetail;
        if (contentDetail == null) {
            modalContentDao.updateParentsFromPreviousAppVersion(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            getDownloadedContents(null, null);
//            new GetDownloadedContent(ContentPresenterImpl.this, null).execute();
        } else {
            mappedParentApi = contentDetail.getMappedApiId();
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
                if (!IS_COURSE.equalsIgnoreCase(iscourse)) {
                    contentView.displayLevel(levelContents, "");
                } else {
                    contentView.displayLevel(levelContents, "IS_COURSE");
                }
            }
            getDownloadedContents(contentDetail.getNodeid(), contentDetail.getAltnodeid());
//            new GetDownloadedContent(ContentPresenterImpl.this, contentDetail.getNodeid()).execute();
        }
    }

    @Background
    @Override
    public void getContent() {
        if (levelContents != null && levelContents.size() > 0) {
            Modal_ContentDetail detail = levelContents.get(levelContents.size() - 1);
            folderContentClicked = detail;
            getDownloadedContents(detail.getNodeid(), detail.getAltnodeid());
//            new GetDownloadedContent(ContentPresenterImpl.this,
//                    levelContents.get(levelContents.size() - 1).getNodeid()).execute();
        } else {
            folderContentClicked = null;
            if (contentView != null)
                contentView.dismissDialog();
        }
    }

    @Background
    @Override
    public void getSearchContent(String SearchText) {
        searchWord=SearchText;
        getDownloadedContents(FastSave.getInstance().getString(PD_Constant.CONTENT_PARENT,null), null);
        searchWord="";
        //FastSave.getInstance().deleteValue(PD_Constant.CONTENT_PARENT);
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
                    pd_apiRequest.getacilityIdfromRaspberry(PD_Constant.FACILITY_ID, PD_Constant.URL.RASPBERRY_FACILITY_URL.toString(), object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void checkConnectivity(ArrayList<Modal_ContentDetail> contentList, String parentId, String search) {
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
            if(search.equalsIgnoreCase("SEARCH")){ searchOnlineContentAPI(contentList, parentId, searchWord); }
            else { callOnlineContentAPI(contentList, parentId); }
        } else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                if (FastSave.getInstance().getString(PD_Constant.FACILITY_ID, "").isEmpty())
                    checkConnectionForRaspberry();
                callKolibriAPI(contentList, parentId);
            } else {
                if(search.equalsIgnoreCase("SEARCH")){
                    searchOnlineContentAPI(contentList, parentId, searchWord);
                } else {
                    callOnlineContentAPI(contentList, parentId);
                }
            }
        } else {
            if (contentList.isEmpty()) {
                if (contentView != null)
                    contentView.showNoConnectivity();
            } else {
//                Collections.shuffle(totalContents);
/*                tempContentList = getFinalListWithHeader(contentList);
                if (contentView != null)
                    contentView.displayContents(contentList);*/
                //for offline also course open in content activity
                if (!IS_COURSE.equalsIgnoreCase(iscourse)) {
                    tempContentList = getFinalListWithHeader(contentList);
                    if (contentView != null) {
                        contentView.displayContents(contentList);
                    }
                } else {
                    assert contentView != null;
                    contentView.displayContentsInCourse(folderContentClicked, contentList);
                    iscourse = "";
                }
            }
        }
    }

    private void callKolibriAPI(ArrayList<Modal_ContentDetail> contentList, String parentId) {
        if (parentId == null || parentId.equalsIgnoreCase("0") || parentId.isEmpty()) {
            pd_apiRequest.getContentFromRaspberry(PD_Constant.BROWSE_RASPBERRY, PD_Constant.URL.BROWSE_RASPBERRY_URL_NEW + FastSave.getInstance().getString(PD_Constant.LANGUAGE_CODE, "78672"), contentList);
            String url = PD_Constant.URL.BROWSE_RASPBERRY_URL_NEW.toString() + FastSave.getInstance().getString(PD_Constant.LANGUAGE_CODE, "78672");
            Log.e("url 1: ", url);
        } else {
            pd_apiRequest.getContentFromRaspberry(PD_Constant.BROWSE_RASPBERRY, PD_Constant.URL.BROWSE_RASPBERRY_URL_NEW.toString()
                    + ((mappedParentApi != null) ? mappedParentApi : parentId), contentList);
            String url = PD_Constant.URL.BROWSE_RASPBERRY_URL_NEW.toString()
                    + ((mappedParentApi != null) ? mappedParentApi : parentId);
            Log.e("url 2: ", url);
        }
    }

    private void getKolibriLanguages(ArrayList<Modal_ContentDetail> contentList, String
            parentId) {
        pd_apiRequest.getContentFromRaspberry(PD_Constant.BROWSE_RASPBERRY_LANGUAGES, PD_Constant.URL.BROWSE_RASPBERRY_URL.toString()
                + parentId, contentList);
    }

    private void getKolibriLanguagesChilds(ArrayList<Modal_ContentDetail> contentList, String parentId) {
        pd_apiRequest.getContentFromRaspberry(PD_Constant.BROWSE_RASPBERRY_LANGUAGES_CHILDS,
                PD_Constant.URL.BROWSE_RASPBERRY_URL.toString() + parentId, contentList);
    }

    private void callOnlineContentAPI(ArrayList<Modal_ContentDetail> contentList, String parentId) {
        if (parentId == null || parentId.equalsIgnoreCase("0") || parentId.isEmpty()) {
/*            pd_apiRequest.getContentFromInternet(PD_Constant.INTERNET_HEADER,
                    PD_Constant.URL.GET_TOP_LEVEL_NODE
                            + FastSave.getInstance().getString(PD_Constant.LANGUAGE, "Hindi"), contentList);
            String url = PD_Constant.URL.GET_TOP_LEVEL_NODE + FastSave.getInstance().getString(PD_Constant.LANGUAGE, "Hindi");
            Log.e("URL TOP: ", url);*/
            /*            pd_apiRequest.getContentFromInternet(PD_Constant.BROWSE_INTERNET,
                    PD_Constant.URL.BROWSE_BY_ID + "2000001" + "&deviceid=" + PD_Utility.getDeviceID(), contentList);*/

/*            String url = PD_Constant.URL.BROWSE_BY_ID + "2000001" + "&deviceid=" + PD_Utility.getDeviceID();
           Log.e("URL TOP: ", url);*/


            //new pradigi for life api top levelapi
            pd_apiRequest.getContentFromInternet(PD_Constant.BROWSE_INTERNET,
                    PD_Constant.URL.BROWSE_BY_ID + FastSave.getInstance().getString(PD_Constant.LANGUAGE_CODE, "78672") + "&deviceid=" + PD_Utility.getDeviceID(), contentList);

            String url = PD_Constant.URL.BROWSE_BY_ID + FastSave.getInstance().getString(PD_Constant.LANGUAGE_CODE, "78672") + "&deviceid=" + PD_Utility.getDeviceID();
            Log.e("URL TOP: ", url);

        } else {
            try {
                pd_apiRequest.getContentFromInternet(PD_Constant.BROWSE_INTERNET,
                        PD_Constant.URL.BROWSE_BY_ID + ((mappedParentApi != null) ? mappedParentApi : parentId) + "&deviceid=" + PD_Utility.getDeviceID(), contentList);
                String url = PD_Constant.URL.BROWSE_BY_ID + ((mappedParentApi != null) ? mappedParentApi : parentId) + "&deviceid=" + PD_Utility.getDeviceID();
                Log.e("URL BROWSE : ", url);
            } catch (Exception e) {
                Log.e("ERROR : ", e.getMessage());
            }
        }
    }

    /**Function to search entered resource online*/
    private void searchOnlineContentAPI(ArrayList<Modal_ContentDetail> contentList, String parentId, String searchText) {
        if (parentId == null || parentId.equalsIgnoreCase("0") || parentId.isEmpty()) {
            pd_apiRequest.getContentFromInternet(PD_Constant.BROWSE_INTERNET,
                    PD_Constant.URL.SEARCH_BY_NODE + searchText +PD_Constant.SERVER_NODEID+FastSave.getInstance().getString(PD_Constant.LANGUAGE_CODE, "78672") + PD_Constant.SERVER_DEVICEID + PD_Utility.getDeviceID(), contentList);
            String url = PD_Constant.URL.SEARCH_BY_NODE + searchText +PD_Constant.SERVER_NODEID+FastSave.getInstance().getString(PD_Constant.LANGUAGE_CODE, "78672") + PD_Constant.SERVER_DEVICEID + PD_Utility.getDeviceID();
            //String url = PD_Constant.URL.SEARCH_BY_NODE + ((mappedParentApi != null) ? mappedParentApi : parentId) + "&deviceid=" + PD_Utility.getDeviceID();
            Log.e("URL TOP SEARCH : ", url);
        }
        else
        {
            try {
                pd_apiRequest.getContentFromInternet(PD_Constant.BROWSE_INTERNET,
                        PD_Constant.URL.SEARCH_BY_NODE + searchText +PD_Constant.SERVER_NODEID+((mappedParentApi != null) ? mappedParentApi : parentId) + PD_Constant.SERVER_DEVICEID + PD_Utility.getDeviceID(), contentList);
                String url = PD_Constant.URL.SEARCH_BY_NODE + searchText +PD_Constant.SERVER_NODEID+ ((mappedParentApi != null) ? mappedParentApi : parentId) + PD_Constant.SERVER_DEVICEID + PD_Utility.getDeviceID();
                //String url = PD_Constant.URL.SEARCH_BY_NODE + ((mappedParentApi != null) ? mappedParentApi : parentId) + "&deviceid=" + PD_Utility.getDeviceID();
                Log.e("URL SEARCH : ", url);
            } catch (Exception e) {
                Log.e("ERROR : ", e.getMessage());
            }
        }
    }

    @Background
    @Override
    public void downloadContent(Modal_ContentDetail contentDetail) {
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
            pd_apiRequest.getContentFromInternet(PD_Constant.INTERNET_DOWNLOAD,
//                    PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid(), null);
                    PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid() + "&deviceid=" + PD_Utility.getDeviceID(), null);
            String url = PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid() + "&deviceid=" + PD_Utility.getDeviceID();
            Log.e("**URL:", url);
        } else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                try {
//                    String url = contentDetail.getResourcezip();
                    String url = "";
                    String filename = URLDecoder.decode(contentDetail.getResourcezip(), "UTF-8")
                            .substring(URLDecoder.decode(contentDetail.getResourcezip(), "UTF-8").lastIndexOf('/') + 1);
                    String foldername = contentDetail.getResourcetype();
                    String fileFormat = contentDetail.getResourcepath().substring(contentDetail.getResourcepath().lastIndexOf("."));
                    if (foldername.equalsIgnoreCase("pdf")) {
                        url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/docs/" + filename;
                    } else if (foldername.equalsIgnoreCase("game")) {
                        url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/zips/" + filename;
                    } else if (foldername.equalsIgnoreCase("video")) {
                        if (fileFormat.equalsIgnoreCase(".mp4"))
                            url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/videos/mp4/" + filename;
                        else
                            url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/videos/m4v/" + filename;
                    } else if (foldername.equalsIgnoreCase("audio")) {
                        if (fileFormat.equalsIgnoreCase(".mp3"))
                            url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/audios/mp3/" + filename;
                        else
                            url = PD_Constant.RASP_IP + PD_Constant.RASP_LOCAL_URL + "/audios/wav/" + filename;
                    }
                    Log.e("**URL:", url);
                    zipDownloader.initialize(ContentPresenterImpl.this
                            , url, foldername, filename, contentDetail, levelContents, PD_Constant.RASPPI);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                pd_apiRequest.getContentFromInternet(PD_Constant.INTERNET_DOWNLOAD,
//                        PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid(), null);
                        PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid() + "&deviceid=" + PD_Utility.getDeviceID(), null);
                String url = PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid() + "&deviceid=" + PD_Utility.getDeviceID();
                //String url = PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + contentDetail.getNodeid();
            }
        }
    }


    @Background
    @Override
    public void recievedContent(String header, String
            response, ArrayList<Modal_ContentDetail> contentList) {
        ArrayList<Modal_ContentDetail> displayedContents = new ArrayList<>();
        ArrayList<Modal_ContentDetail> totalContents = new ArrayList<>();
        try {
            Log.e("url response:::", response);
            Log.e("url response:::", "requestType:: " + header);
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
                String pradigi_id = null;
                for (Modal_Rasp_Header modal_rasp_header : rasp_headers) {
                    Modal_ContentDetail detail = modal_rasp_header.setContentToConfigNodeStructure(modal_rasp_header);
                    if (detail.getNodetitle().equalsIgnoreCase("PraDigi"))
                        pradigi_id = detail.getNodeid();
                    else displayedContents.add(detail);
                }
                if (pradigi_id != null)
                    getKolibriLanguages(displayedContents, pradigi_id);
                else {
                    totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                    tempContentList = getFinalListWithHeader(totalContents);
                    if (contentView != null)
                        contentView.displayContents(totalContents);
                }
            } else if (header.equalsIgnoreCase(PD_Constant.BROWSE_RASPBERRY_LANGUAGES)) {
                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                Type listType = new TypeToken<ArrayList<Modal_Rasp_Content>>() {
                }.getType();
                List<Modal_Rasp_Content> rasp_contents = gson.fromJson(response, listType);
                String language_id = null;
                for (Modal_Rasp_Content modal_rasp_content : rasp_contents) {
                    String languageSelected = FastSave.getInstance().getString(PD_Constant.LANGUAGE, "");
                    Modal_ContentDetail detail = modal_rasp_content.setContentToConfigNodeStructure(modal_rasp_content);
                    if (languageSelected.equalsIgnoreCase(PD_Utility.getLanguageKeyword(modal_rasp_content.getLang().getLangCode()))) {
                        language_id = detail.getNodeid();
                        break;
                    } else {
                        detail.setParentid(null);
                        displayedContents.add(detail);
                    }
                }
                if (language_id != null) {
                    getKolibriLanguagesChilds(totalContents, language_id);
                } else {
                    totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                    tempContentList = getFinalListWithHeader(totalContents);
                    if (contentView != null)
                        contentView.displayContents(totalContents);
                }
            } else if (header.equalsIgnoreCase(PD_Constant.BROWSE_RASPBERRY_LANGUAGES_CHILDS)) {
                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                Type listType = new TypeToken<ArrayList<Modal_Rasp_Content>>() {
                }.getType();
                List<Modal_Rasp_Content> rasp_contents = gson.fromJson(response, listType);
                int child_age = FastSave.getInstance().getInt(PD_Constant.STUDENT_PROFILE_AGE, 0);
                for (Modal_Rasp_Content modal_rasp_content : rasp_contents) {
                    Modal_ContentDetail detail = modal_rasp_content.setContentToConfigNodeStructure(modal_rasp_content);
                    detail.setParentid(null);
                    if (!PrathamApplication.isTablet) {
                        if (child_age > 6) {
                            if (!detail.getNodetitle().contains("3-6"))
                                displayedContents.add(detail);
                        } else {
                            if (detail.getNodetitle().contains("3-6")) {
                                id_3_6 = detail.getNodeid();
                                break;
                            }
                        }
                    } else displayedContents.add(detail);
                }
                if (!PrathamApplication.isTablet && child_age <= 6 && !id_3_6.isEmpty()) {
                    callKolibriAPI(contentList, id_3_6);
                } else {
                    totalContents = removeDownloadedContents(totalContents, displayedContents);
                    tempContentList = getFinalListWithHeader(totalContents);
                    if (contentView != null)
                        contentView.displayContents(totalContents);
                }
            } else if (header.equalsIgnoreCase(PD_Constant.BROWSE_RASPBERRY)) {
                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                ModalRaspContentNew rasp_contents = gson.fromJson(response, ModalRaspContentNew.class);
                rasp_contents.getModalRaspResults();
                Log.e("url raspResult : ", String.valueOf(rasp_contents.getModalRaspResults().size()));
                Modal_Rasp_JsonData modal_rasp_jsonData;

                if (rasp_contents.getModalRaspResults() != null) {
                    List<Modal_RaspResult> raspResults = new ArrayList<>();
                    for (int i = 0; i < rasp_contents.getModalRaspResults().size(); i++) {
                        Modal_RaspResult modalRaspResult = new Modal_RaspResult();
                        modalRaspResult.setAppId(rasp_contents.getModalRaspResults().get(i).getAppId());
                        modalRaspResult.setNodeId(rasp_contents.getModalRaspResults().get(i).getNodeId());
                        modalRaspResult.setNodeType(rasp_contents.getModalRaspResults().get(i).getNodeType());
                        modalRaspResult.setNodeTitle(rasp_contents.getModalRaspResults().get(i).getNodeTitle());
                        modalRaspResult.setParentId(rasp_contents.getModalRaspResults().get(i).getParentId());
                        modalRaspResult.setJsonData(rasp_contents.getModalRaspResults().get(i).getJsonData());
                        //raspResults.add(modalRaspResult);
                        modal_rasp_jsonData = gson.fromJson(rasp_contents.getModalRaspResults().get(i).getJsonData(), Modal_Rasp_JsonData.class);
                        Modal_ContentDetail detail = modalRaspResult.setContentToConfigNodeStructure(modalRaspResult, modal_rasp_jsonData);
                        detail.setMappedApiId(detail.getNodeid());
                        detail.setMappedParentId(mappedParentApi);
                        detail.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
                        displayedContents.add(detail);
                    }
                }

                totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                if (!IS_COURSE.equalsIgnoreCase(iscourse)) {
                    tempContentList = getFinalListWithHeader(totalContents);
                    if (contentView != null)
                        contentView.displayContents(totalContents);
                } else {
                    assert contentView != null;
                    contentView.displayContentsInCourse(folderContentClicked, totalContents);
                    iscourse = "";
                }

/*                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                Type listType = new TypeToken<ArrayList<Modal_Rasp_Content>>() {
                }.getType();
                List<Modal_Rasp_Content> rasp_contents = gson.fromJson(response, listType);
                for (Modal_Rasp_Content modal_rasp_content : rasp_contents) {
                    Modal_ContentDetail detail = modal_rasp_content.setContentToConfigNodeStructure(modal_rasp_content);
                    if (detail.getParentid().equalsIgnoreCase(id_3_6))
                        detail.setParentid("0");
                    detail.setMappedParentId(mappedParentApi);
                    displayedContents.add(detail);
                }
                totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                tempContentList = getFinalListWithHeader(totalContents);
                if (contentView != null)
                    contentView.displayContents(totalContents);*/
            } else if ((header.equalsIgnoreCase(PD_Constant.INTERNET_HEADER))) {
                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                List<Modal_ContentDetail> tempContents = gson.fromJson(response, listType);
                int child_age = FastSave.getInstance().getInt(PD_Constant.STUDENT_PROFILE_AGE, 0);
                for (Modal_ContentDetail detail : tempContents) {
                    if (!PrathamApplication.isTablet) {
                        if (child_age > 6) {
                            if (!detail.getNodeeage().contains("3-6")) {
                                if (detail.getResourcetype().equalsIgnoreCase("Game")
                                        || detail.getResourcetype().equalsIgnoreCase("Video")
                                        || detail.getResourcetype().equalsIgnoreCase("Pdf"))
                                    detail.setContentType("file");
                                else
                                    detail.setContentType("folder");
                                detail.setMappedApiId(detail.getNodeid());
                                detail.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
                                displayedContents.add(detail);
                            }
                        } else {
                            if (detail.getNodeeage().contains("3-6")) {
                                id_3_6 = detail.getNodeid();
                                break;
                            }
                        }
                    } else {
                        if (detail.getResourcetype().equalsIgnoreCase("Game")
                                || detail.getResourcetype().equalsIgnoreCase("Video")
                                || detail.getResourcetype().equalsIgnoreCase("Pdf"))
                            detail.setContentType("file");
                        else
                            detail.setContentType("folder");
                        detail.setMappedApiId(detail.getNodeid());
                        detail.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
                        displayedContents.add(detail);
                    }
                }
                if (!PrathamApplication.isTablet && child_age <= 6 && !id_3_6.isEmpty()) {
                    callOnlineContentAPI(totalContents, id_3_6);
                } else {
                    totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                    tempContentList = getFinalListWithHeader(totalContents);
                    if (contentView != null)
                        contentView.displayContents(totalContents);
                }
            } else if ((header.equalsIgnoreCase(PD_Constant.BROWSE_INTERNET))) {
                List<Modal_ContentDetail> tempUpdatedCourseContent = new ArrayList<>();
                displayedContents.clear();
                totalContents.clear();
                totalContents.addAll(contentList);
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                List<Modal_ContentDetail> tempContents = gson.fromJson(response, listType);
                for (Modal_ContentDetail detail : tempContents) {
                    if (detail.getResourcetype().equalsIgnoreCase("Game")
                            || detail.getResourcetype().equalsIgnoreCase("Video")
                            || detail.getResourcetype().equalsIgnoreCase("Youtube")
                            || detail.getResourcetype().equalsIgnoreCase("Pdf")
                            || detail.getResourcetype().equalsIgnoreCase("Audio")) {
                        detail.setContentType("file");
                        for (Modal_ContentDetail dbDetail : totalContents) {
                            if (detail.getNodeid().equalsIgnoreCase(dbDetail.getNodeid())) {
                                if (!detail.getVersion().equalsIgnoreCase(dbDetail.getVersion())) {
                                    Log.e("version no : ", detail.getNodeid());
                                    detail.setNodeUpdate(true);
                                    tempUpdatedCourseContent.add(detail);
                                    //dbDetail.setNodeUpdate(true);
                                }
//                                totalContents.add(detail);
//                                break;
                            }
                        }
                    } else
                        detail.setContentType("folder");
                    if (detail.getParentid().equalsIgnoreCase(FastSave.getInstance().getString(PD_Constant.LANGUAGE_CODE, "78672")))
                        detail.setParentid("0");
                    detail.setMappedApiId(detail.getNodeid());
                    detail.setMappedParentId(mappedParentApi);
                    detail.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
                    displayedContents.add(detail);
                }
                totalContents = removeDownloadedContents(totalContents, displayedContents);
//                Collections.shuffle(totalContents);
                if (!IS_COURSE.equalsIgnoreCase(iscourse)) {
                    tempContentList = getFinalListWithHeader(totalContents);
                    if (contentView != null)
                        contentView.displayContents(totalContents);
                } else {
                    assert contentView != null;
                    contentView.displayContentsInCourseNew(folderContentClicked, totalContents, tempUpdatedCourseContent);
                    iscourse = "";
                }
            } else if (header.equalsIgnoreCase(PD_Constant.INTERNET_DOWNLOAD)) {
                JSONObject jsonObject = new JSONObject(response);
                Modal_DownloadContent download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                Modal_ContentDetail contentDetail = download_content.getNodelist().get(download_content.getNodelist().size() - 1);
                String fileName = download_content.getDownloadurl()
                        .substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                zipDownloader.initialize(ContentPresenterImpl.this, download_content.getDownloadurl(),
                        download_content.getFoldername(), fileName, contentDetail, levelContents, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method used before Ketan solution
/*

    private ArrayList<Modal_ContentDetail> removeDownloadedContents(ArrayList<Modal_ContentDetail> dbContents
            , ArrayList<Modal_ContentDetail> onlineContents) {
        String parentid = null;
        if (!dbContents.isEmpty())
            parentid = dbContents.get(0).getParentid();
        for (int i = 0; i < onlineContents.size(); i++) {

// replaced altnodeid with nodeid

            Modal_ContentDetail content = modalContentDao.getContentFromAltNodeId(onlineContents.get(i).getNodeid(),
                    FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            if (content != null) {
                if (onlineContents.get(i).isNodeUpdate()) {
                    content.setNodeUpdate(true);
                    content.setVersion(onlineContents.get(i).getVersion());
                }

                content.setMappedApiId(onlineContents.get(i).getNodeid());
                content.setMappedParentId(mappedParentApi);
                onlineContents.set(i, content);
            } else {
                if (parentid != null) onlineContents.get(i).setParentid(parentid);
                onlineContents.get(i).setMappedParentId(mappedParentApi);
            }
            int pos = -1;
            for (int j = 0; j < dbContents.size(); j++) {
                if (dbContents.get(j).getNodeid().equalsIgnoreCase(onlineContents.get(i).getNodeid())) {
                    pos = j;
                    break;
                }
            }
            if (pos != -1) dbContents.remove(pos);
        }
        if (dbContents.size() > 0) onlineContents.addAll(dbContents);
        return onlineContents;
    }

*/

//TODO Ketan Changes for altnodeid
private ArrayList<Modal_ContentDetail> removeDownloadedContents(ArrayList<Modal_ContentDetail> dbContents
        , ArrayList<Modal_ContentDetail> onlineContents) {
    String parentid = null;
    int dwPos=0;
    ArrayList<Modal_ContentDetail> newContents = new ArrayList<>();
    boolean contentFound = false;
    for(int i=0; i<onlineContents.size(); i++){
        for(int k=0; k<dbContents.size(); k++){
            if(dbContents.get(k).getNodeid().equalsIgnoreCase(onlineContents.get(i).getNodeid())) {
                contentFound = true;
                dwPos = k;
                break;
            }
        }
        if (contentFound)
            newContents.add(dbContents.get(dwPos));
        else
            newContents.add(onlineContents.get(i));
        contentFound = false;
    }
    return newContents;
/*        if (!dbContents.isEmpty())
            parentid = dbContents.get(0).getParentid();
        for (int i = 0; i < onlineContents.size(); i++) {
            //replaced altnodeid with nodeid
            Modal_ContentDetail content = modalContentDao.getContentFromAltNodeId(onlineContents.get(i).getNodeid(),
                    FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            if (content != null) {
                if (onlineContents.get(i).isNodeUpdate()) {
                    content.setNodeUpdate(true);
                    content.setVersion(onlineContents.get(i).getVersion());
                }

                content.setMappedApiId(onlineContents.get(i).getNodeid());
                content.setMappedParentId(mappedParentApi);
                onlineContents.set(i, content);
            } else {
                if (parentid != null) onlineContents.get(i).setParentid(parentid);
                onlineContents.get(i).setMappedParentId(mappedParentApi);
            }
            int pos = -1;
            for (int j = 0; j < dbContents.size(); j++) {
                if (dbContents.get(j).getNodeid().equalsIgnoreCase(onlineContents.get(i).getNodeid())) {
                    pos = j;
                    break;
                }
            }
            if (pos != -1) dbContents.remove(pos);
        }
        if (dbContents.size() > 0) onlineContents.addAll(dbContents);
        return onlineContents;*/
}


    @Override
    public void recievedError(String header, ArrayList<Modal_ContentDetail> contentList) {
        if (contentList != null && contentList.isEmpty()) {
            if (contentView != null)
                contentView.showNoConnectivity();
        } else {
//            Collections.shuffle(totalContents);
            tempContentList = getFinalListWithHeader(contentList);
            if (contentView != null) contentView.displayContents(contentList);
        }
    }

    @Override
    public void fileDownloadStarted(String downloadID, Modal_FileDownloading
            modal_fileDownloading) {
        filesDownloading.put(downloadID, modal_fileDownloading);
        postDownloadStartMessage();
    }

    @Override
    public void updateFileProgress(String downloadID, Modal_FileDownloading mfd) {
        filesDownloading.put(downloadID, mfd);
        postProgressMessage();
    }

    @Override
    public void onDownloadCompleted(String downloadID, Modal_ContentDetail content, Context context) {
        filesDownloading.remove(downloadID);
        postAllDownloadsCompletedMessage();
        postSingleFileDownloadCompleteMessage(content);
        currentDownloadTasks.remove(downloadID);
        for (int i = 0; i < tempContentList.size(); i++) {
            if (tempContentList.get(i) != null && tempContentList.get(i).getNodeid() != null &&
                    tempContentList.get(i).getNodeid().equalsIgnoreCase(content.getNodeid())) {
                tempContentList.set(i, content);
                break;
            }
        }

        /**
         Issue : This method is called twice within seconds. So downloaded resource is entered twice in logs table.
         Fix : To avoid the duplication, entry is checked in db first, if not present then value is entered in table else not.
         As entry is made very quickly, one second delay is added before entering second entry. So the value is checked for
         duplication first and then entered.
         */
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                resource_log = PrathamDatabase.getDatabaseInstance(context).getLogDao().checkResourceLog(content.getNodetitle(), content.getNodeid());
                addToLog(content, resource_log, context);
            }
        }, 1000);
    }

    private void addToLog(Modal_ContentDetail content, Modal_Log modal_logg, Context context){
        //update data download channel
        if(modal_logg==null) {
            Modal_Log modal_log = new Modal_Log();
            modal_log.setErrorType("DOWNLOAD");
            modal_log.setExceptionMessage(content.getNodetitle());
            modal_log.setMethodName(content.getNodeid());
            modal_log.setCurrentDateTime(PD_Utility.getCurrentDateTime());
            modal_log.setSessionId(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
            modal_log.setExceptionStackTrace("APK BUILD DATE : " + PD_Constant.apkDate);
            modal_log.setDeviceId("" + PD_Utility.getDeviceID());
            modal_log.setLogDetail(content.getResourcezip());
            if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT))
                modal_log.setLogDetail("PI#" + content.getResourcezip());
            else
                modal_log.setLogDetail("INTERNET#" + content.getResourcezip());

            logDao.insertLog(modal_log);
//            BackupDatabase.backup(context);
        } else {
            Log.e("Duplicate : ", content.getNodetitle());
        }

    }

    @Override
    public void ondownloadError(String downloadId) {
        Modal_ContentDetail content = Objects.requireNonNull(filesDownloading.get(downloadId)).getContentDetail();
        filesDownloading.remove(downloadId);
        postSingleFileDownloadErrorMessage(content);
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
        try {
            EventBus.getDefault().post(new ArrayList<>(filesDownloading.values()));
        } catch (ConcurrentModificationException e){
            e.printStackTrace();
        }
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
        modalContentDao.addContentList(temp);
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

    @Background
    @Override
    public void showPreviousContent() {
        folderContentClicked = null;
        if (levelContents == null || levelContents.isEmpty()) {
            if (contentView != null)
                contentView.exitApp();
//            new GetDownloadedContent(null).execute();
        } else {
            Modal_ContentDetail contentDetail = levelContents.get(levelContents.size() - 1);
            levelContents.remove(levelContents.size() - 1);
            Log.e("URL PrevLevelContent", String.valueOf(levelContents.size()));
            if (contentView != null) {
                if (levelContents.isEmpty()) contentView.animateHamburger();
                contentView.displayLevel(levelContents, "");
            }
            mappedParentApi = contentDetail.getMappedParentId();
            getDownloadedContents(contentDetail.getParentid(), ""); //altnodeId is sent blank coz it points to its same node,
            // that's why their will be no child nodes
//            new GetDownloadedContent(ContentPresenterImpl.this, contentDetail.getParentid()).execute();

            //Used for search functionality
            FastSave.getInstance().deleteValue(PD_Constant.CONTENT_PARENT);
            FastSave.getInstance().saveString(PD_Constant.CONTENT_PARENT,mappedParentApi);

        }
    }


    @Override
    public void downloadedContents(Object o, String parentId) {
//        checkConnectivity((ArrayList<Modal_ContentDetail>) o, parentId);
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
        addDeleteEntryInScore(contentItem);
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
        getContent(levelContents.get(levelContents.size() - 1), "");
    }

    private void addDeleteEntryInScore(Modal_ContentDetail contentItem) {
        String endTime = PD_Utility.getCurrentDateTime();
        Modal_Score modalScore = new Modal_Score();
        modalScore.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        if (PrathamApplication.isTablet) {
            modalScore.setGroupID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group"));
            modalScore.setStudentID("");
        } else {
            modalScore.setGroupID("");
            modalScore.setStudentID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_student"));
        }
        modalScore.setDeviceID(PD_Utility.getDeviceID());
        modalScore.setResourceID(contentItem.getResourceid());
        modalScore.setQuestionId(0);
        modalScore.setScoredMarks(0);
        modalScore.setTotalMarks(0);
        modalScore.setStartDateTime(endTime);
        modalScore.setEndDateTime(endTime);
        modalScore.setLevel(0);
        modalScore.setLabel("Content is deleted either by crl or student");
        modalScore.setSentFlag(0);
        scoreDao.insert(modalScore);
    }

    @Override
    public void currentDownloadRunning(String downloadId, DownloadTask task) {
        if (!currentDownloadTasks.containsKey(downloadId)) {
            currentDownloadTasks.put(downloadId, task);
        }
    }

    @Override
    public void cancelDownload(String downloadId) {
        if (downloadId != null && !downloadId.isEmpty()) {
            if (currentDownloadTasks.containsKey(downloadId))
                Objects.requireNonNull(currentDownloadTasks.get(downloadId)).cancel();
            postProgressMessage();
        }
    }

    private void checkAndDeleteParent(Modal_ContentDetail contentItem) {
        String parentId = contentItem.getParentid();
        modalContentDao.deleteContent(contentItem.getNodeid());
        if (parentId != null && !parentId.equalsIgnoreCase("0") && !parentId.isEmpty()) {
            int count = modalContentDao.getChildCountOfParent(parentId,
                    FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            if (count == 0)
                checkAndDeleteParent(modalContentDao.getContent(parentId,
                        FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)));
        }
    }

    @Background
    @Override
    public void openDeepLinkContent(String dl_content) {
        if (dl_content != null && !dl_content.isEmpty()) {
            Modal_DownloadContent download_content = new Gson().fromJson(dl_content, Modal_DownloadContent.class);
            for (Modal_ContentDetail detail : download_content.getNodelist()) {
                Modal_ContentDetail temp = modalContentDao.getContent(detail.getNodeid(), FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
                if (temp == null) {
                    detail.setContent_language(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
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
                contentView.displayLevel(levelContents, "");
                List<Modal_ContentDetail> details = new ArrayList<>();
                details.add(0, new Modal_ContentDetail());//null modal for displaying header
                details.add(download_content.getNodelist().get(download_content.getNodelist().size() - 1));
                contentView.displayDLContents(details);
            }
        }
    }

    @Override
    public List<Modal_ContentDetail> getContentList() {
        return tempContentList;
    }

    @Override
    public boolean isFilesDownloading() {
        return !filesDownloading.isEmpty();
    }

    private void getDownloadedContents(String parentId, String altNodeId) {
        String lang = FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI);
        List<Modal_ContentDetail> childsOfParent;
        if (parentId != null && !parentId.equalsIgnoreCase("0") && !parentId.isEmpty()) {
            /** replaced altnodeid with nodeid*/
            childsOfParent = modalContentDao.getChildsOfParent(parentId, parentId, lang);
            try {
                Collections.sort(childsOfParent, (o1, o2) -> {
                    if (o1.seq_no == null) {
                        return (o1.getNodeid().compareToIgnoreCase(o2.getNodeid()));
                    } else {
                        int s1 = Integer.parseInt(o1.getSeq_no());
                        int s2 = Integer.parseInt(o2.getSeq_no());
                        return (Integer.compare(s1, s2));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int child_age = FastSave.getInstance().getInt(PD_Constant.STUDENT_PROFILE_AGE, 0);
            if (PrathamApplication.isTablet)
                childsOfParent = modalContentDao.getParentsHeaders(lang);
            else {
                if (child_age <= 6)
                    childsOfParent = modalContentDao.getPrimaryAgeParentsHeaders(lang);
                else
                    childsOfParent = modalContentDao.getAbovePrimaryAgeParentHeaders(lang);
            }
        }
        Log.e("URL COP Count: ", String.valueOf(childsOfParent.size()));
        if(searchWord.isEmpty())
        checkConnectivity((ArrayList<Modal_ContentDetail>) childsOfParent, parentId, "");
        else checkConnectivity((ArrayList<Modal_ContentDetail>) childsOfParent, parentId, "SEARCH");
    }

    private ArrayList<Modal_ContentDetail> getFinalListWithHeader(ArrayList<Modal_ContentDetail> contentList) {
        Modal_ContentDetail header;
        if (folderContentClicked != null && folderContentClicked.getNodetype().equalsIgnoreCase(PD_Constant.COURSE))
            header = folderContentClicked;
        else header = null;
        contentList.add(0, header);
        return contentList;
    }
}
