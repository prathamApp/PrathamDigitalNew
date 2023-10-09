package com.pratham.prathamdigital.async;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Download;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Model_CheckSyncAPI;
import com.pratham.prathamdigital.models.Model_NewSyncLog;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.SpeedMonitor;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.contentProgressDao;
import static com.pratham.prathamdigital.PrathamApplication.courseDao;
import static com.pratham.prathamdigital.PrathamApplication.groupDao;
import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.modalContentDao;
import static com.pratham.prathamdigital.PrathamApplication.scoreDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;
import static com.pratham.prathamdigital.PrathamApplication.syncLogDao;

/**
 * Created by HP on 30-12-2016.
 */

@EBean
public class PD_ApiRequest {
    private final Context mContext;
    private ApiResult apiResult = null;
    private ApiResult.languageResult languageResult = null;
//    OkHttpClient okHttpClient;

    public PD_ApiRequest(Context mContext) {
        this.mContext = mContext;
    }

    public void setApiResult(ApiResult result) {
        this.apiResult = result;
    }

    public void setLangApiResult(ApiResult.languageResult result) {this.languageResult=result;}

    //Used to get the content from rasp_pi device in json format
    public void getContentFromRaspberry(final String requestType, String url, ArrayList<Modal_ContentDetail> contentList) {
        try {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    //.addHeaders("Authorization", getAuthHeader())
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("url rasp respon : ",response);
                            if (apiResult != null)
                                apiResult.recievedContent(requestType, response, contentList);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (apiResult != null)
                                apiResult.recievedError(requestType, contentList);
                            Log.e("Error::", anError.getErrorDetail());
                            Log.e("Error::", anError.getResponse().toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Used to get the content over server device in json format
    public void getContentFromInternet(final String requestType, String url, ArrayList<Modal_ContentDetail> contentList) {
        try {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (apiResult != null) apiResult.recievedContent(requestType, response, contentList);
                            Log.e("url api response : ", response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (apiResult != null)
                                apiResult.recievedError(requestType, contentList);
                            Log.d("Error:", anError.getErrorDetail());
                            Log.d("ErrorCode:", String.valueOf(anError.getErrorCode()));
                            Log.d("Error::", anError.getResponse().toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Used to get the language over server and Rasp Pi device in json format
    public void getLanguageFromInternetAndRaspPi(final String requestType, String url) {
        try {
            Log.e("url : ",url);
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (languageResult != null)
                                languageResult.recievedLang(requestType, response);
//                                Log.e("url api response : ", response);
                         }

                        @Override
                        public void onError(ANError anError) {
                            if (languageResult != null)
                                languageResult.recievedLangError(requestType);
                            Log.d("Error:", anError.getErrorDetail());
                            Log.d("ErrorCodeLng:", String.valueOf(anError.getErrorCode()));
                            Log.d("Error::", anError.getErrorBody());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void pushDataToRaspberry(/*final String requestType, */String url, String data,
                                                                  String filter_name, String table_name) {
        AndroidNetworking.post(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", getAuthHeader())
                .addBodyParameter("filter_name", filter_name)
                .addBodyParameter("table_name", table_name)
                .addBodyParameter("facility", FastSave.getInstance().getString(PD_Constant.FACILITY_ID, ""))
                .addBodyParameter("data", data)
                .setExecutor(Executors.newSingleThreadExecutor())
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
//                        apiResult.notifySuccess(requestType, "success");
                        logDao.deleteLogs();
                        BackupDatabase.backup(PrathamApplication.getInstance());
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.SUCCESSFULLYPUSHED);
                        msg.setPushData(data);
                        EventBus.getDefault().post(msg);
                    }

                    @Override
                    public void onError(ANError anError) {
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.PUSHFAILED);
                        EventBus.getDefault().post(msg);
                        Log.d("Error::", anError.getErrorDetail());
                        Log.d("Error::", anError.getMessage());
                        Log.d("Error::", anError.getResponse().toString());
                    }
                });
    }

/*
    public void pushDataToInternet(*/
/*final String requestType, *//*
String url, JSONObject data) {
        AndroidNetworking.post(url)
//                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(data)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //todo comented deleted logs to check appStart and end logs
                        //logDao.deleteLogs();
                        BackupDatabase.backup(PrathamApplication.getInstance());
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.SUCCESSFULLYPUSHED);
                        msg.setPushData(data.toString());
                        EventBus.getDefault().post(msg);
                    }

                    @Override
                    public void onError(ANError anError) {
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.PUSHFAILED);
                        EventBus.getDefault().post(msg);
                        Log.d("Error::", anError.getErrorDetail());
                        Log.d("Error::", anError.getMessage());
                        Log.d("Error::", anError.getResponse().toString());
                    }
                });
    }
*/

    /** Used to check whether the server is running or not.*/
    public void checkSyncAPIStatus(String url, String requestType) {
        try {
            Log.e("CheckSyncStatusAPI : ",url);
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (apiResult != null)
                                apiResult.recievedContent(requestType, response,null);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (apiResult != null)
                                apiResult.recievedError(requestType, null);
                            Log.e("Check API Error Code::", String.valueOf(anError.getErrorCode()));
                            Log.e("Check API Error:", anError.getErrorDetail());
                            Log.e("Check API ErrorMsg:", anError.getMessage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Used to push data to server in zip format(zip contains json file)
    public void pushDataToInternet(String url, String uuID, String filepathstr, JSONObject data, String courseCount, String pushType, Context context) {
        AndroidNetworking.upload(url)
                .addHeaders("Content-Type", "file/zip")
                .addMultipartFile(""+uuID, new File(filepathstr + ".zip"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("PushData", "DATA PUSH "+response);
                        new File(filepathstr + ".zip").delete();

                        enterSyncLog(pushType,PD_Constant.SUCCESSFULLYPUSHED,context,courseCount);

                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.SUCCESSFULLYPUSHED);
                        msg.setCourseCount(courseCount);
                        msg.setPushData(data.toString());
                        EventBus.getDefault().post(msg);
                    }

                    @Override
                    public void onError(ANError anError) {
                        //Fail - Show dialog with failure message.
                        enterSyncLog(pushType,PD_Constant.PUSHFAILED,context,courseCount);
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.PUSHFAILED);
                        EventBus.getDefault().post(msg);
                        Log.e("Error::", anError.getErrorDetail());
                        Log.e("Error::", anError.getMessage());
                        Log.e("Error::", anError.getResponse().toString());
                    }
                });
    }

    /** Used to push data to server in zip format(zip contains json file)
     * for new API given by afzal.*/
    public void pushDataToInternetNewSync(String url, String uuID, String filepathstr, JSONObject data, String courseCount, String pushType, Context context) {
        AndroidNetworking.upload(url)
                .addHeaders("Content-Type", "file/zip")
                .addMultipartFile("fileJson", new File(filepathstr + ".zip"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("PushDataResponse", "DATA PUSH "+response);
                        new File(filepathstr + ".zip").delete();

                        enterNewSyncLog(pushType,PD_Constant.SUCCESSFULLYPUSHEDNEW,context,response);
                        setPushFlag();

                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.SUCCESSFULLYPUSHEDNEW);
                        EventBus.getDefault().post(msg);
                    }

                    @Override
                    public void onError(ANError anError) {
                        //Fail - Show dialog with failure message.
                        enterNewSyncLog(pushType,PD_Constant.FAILED,context,anError.getErrorDetail());
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.PUSHFAILED);
                        EventBus.getDefault().post(msg);
                        Log.e("New Sync Error Code::", String.valueOf(anError.getErrorCode()));
                        Log.e("New Sync Error::", anError.getErrorDetail());
                        Log.e("New Sync Error::", anError.getMessage());
                    }
                });
    }

    /** Update all sent flags to 1 once zip is successfully pushed to server.*/
    public void setPushFlag(){
        studentDao.updateSentFlag();
        groupDao.updateSentFlag();
        sessionDao.updateSentFlag();
        attendanceDao.updateSentFlag();
        scoreDao.updateSentFlag();
        logDao.updateSentFlag();
        courseDao.updateSentFlag();
        contentProgressDao.updateSentFlag();
    }


    //Used to push data to rasp_Pi device in zip format(zip contains json file)
    public void pushDataToRaspberyPI(String url, String uuID, String filepathstr, JSONObject data, String courseCount, String pushType, Context context) {
        Log.e("url :",url);
        AndroidNetworking.upload(url)
                .addHeaders("Content-Type", "application/json")
                .addMultipartFile("uploaded_file", new File(filepathstr + ".zip"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("PushData", "DATA PUSH "+response);
                        new File(filepathstr + ".zip").delete();

                        enterSyncLog(pushType,PD_Constant.SUCCESSFULLYPUSHED,context,courseCount);

                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.SUCCESSFULLYPUSHED);
                        msg.setCourseCount(courseCount);
                        msg.setPushData(data.toString());
                        EventBus.getDefault().post(msg);
                    }

                    @Override
                    public void onError(ANError anError) {
                        //Fail - Show dialog with failure message.
                        enterSyncLog(pushType,PD_Constant.PUSHFAILED,context,courseCount);
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.PUSHFAILED);
                        EventBus.getDefault().post(msg);
                        Log.e("Error::", anError.getErrorDetail());
                        Log.e("Error::", anError.getMessage());
                        Log.e("Error::", anError.getResponse().toString());
                    }
                });
    }

    //Used to push database to server in zip format(zip contains database file)
    public void pushDBToInternet(String url, String uuID, String filepathstr, String pushType, Context context) {
        AndroidNetworking.upload(url)
                .addHeaders("Content-Type", "file/zip")
                .addMultipartFile(""+uuID, new File(filepathstr + ".zip"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("PushDataBase", "DATABASE PUSH "+response);
                        new File(filepathstr + ".zip").delete();
                        enterDBSyncLog(pushType,PD_Constant.SUCCESS,context,uuID);
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.DBSUCCESSFULLYPUSHED);
                        EventBus.getDefault().post(msg);
                    }

                    @Override
                    public void onError(ANError anError) {
                        //Fail - Show dialog with failure message.
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.PUSHFAILED);
                        enterDBSyncLog(pushType,PD_Constant.FAILED,context,uuID);
                        EventBus.getDefault().post(msg);
                        Log.e("Error 1::", anError.getErrorDetail());
                        Log.e("Error 2::", anError.getMessage());
                        Log.e("Error 3::", anError.getResponse().toString());
                    }
                });
    }

    //Used to push database to raspdevice in zip format(zip contains database file)
    public void pushDBToRaspberryPi(String url, String uuID, String filepathstr, String pushType, Context context) {
        AndroidNetworking.upload(url)
                .addHeaders("Content-Type", "file/zip")
                .addMultipartFile("uploaded_file", new File(filepathstr + ".zip"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("PushDataBase", "DATABASE PUSH "+response);
                        new File(filepathstr + ".zip").delete();
                        enterSyncLog(pushType,PD_Constant.DBSUCCESSFULLYPUSHED,context,"");
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.DBSUCCESSFULLYPUSHED);
                        EventBus.getDefault().post(msg);
                    }

                    @Override
                    public void onError(ANError anError) {
                        //Fail - Show dialog with failure message.
                        EventMessage msg = new EventMessage();
                        msg.setMessage(PD_Constant.PUSHFAILED);
                        EventBus.getDefault().post(msg);
                        enterSyncLog(pushType,PD_Constant.PUSHFAILED,context,"");
                        Log.e("Error1::", anError.getErrorDetail());
                        Log.e("Error2::", anError.getMessage());
                        Log.e("Error3::", anError.getResponse().toString());
                    }
                });
    }

    public void getacilityIdfromRaspberry(final String requestType, String url, JSONObject data) {
        AndroidNetworking.post(url)
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(data)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        apiResult.recievedContent(requestType, response, null);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (apiResult != null)
//                            apiResult.notifyError(requestType/*, null*/);
                            Log.d("Error::", anError.getErrorDetail());
                        Log.d("Error::", anError.getMessage());
                        Log.d("Error::", anError.getResponse().toString());
                    }
                });
    }

    private String getAuthHeader() {
        String encoded = Base64.encodeToString(("pratham" + ":" + "pratham").getBytes(), Base64.NO_WRAP);
        return "Basic " + encoded;
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

    public void pullFromKolibri(String header, String url) {
        AndroidNetworking.get(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", getAuthHeader())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (apiResult != null)
                            apiResult.recievedContent(header, response.toString(), null);
                    }

                    @Override
                    public void onError(ANError error) {
                        if (apiResult != null)
                            apiResult.recievedError(header, null);
                    }
                });
    }

    public void pullFromInternet(String header, String url) {
        AndroidNetworking.get(url)
                .addHeaders("Content-Type", "application/json")
//                .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (apiResult != null)
                            apiResult.recievedContent(header, response.toString(), null);
                    }

                    @Override
                    public void onError(ANError error) {
                        if (apiResult != null)
                            apiResult.recievedError(header, null);
                    }
                });
    }

    public static void downloadAajKaSawal(String url, String filename) {
        AndroidNetworking.get(url)
                .addHeaders("Content-Type", "application/json")
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            File aksFile = new File(PrathamApplication.pradigiPath + "/" + filename); //Creating an internal dir;
                            if (!aksFile.exists()) aksFile.createNewFile();
                            Writer output = new BufferedWriter(new FileWriter(aksFile));
                            output.write(response.toString());
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
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

    @Background
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

    @Background
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

    //Method used to enter sync status in Log table
    public void enterSyncLog(String pushType, String pushStatus, Context context, String courseCount){
        try {
            String currentDateTime = PD_Utility.getCurrentDateTime();
            Modal_Log log = new Modal_Log();
            log.setErrorType(" ");
            if (pushType.equalsIgnoreCase(PD_Constant.AUTO_PUSH))
                log.setExceptionMessage("Auto_Sync");
            else if(pushType.equalsIgnoreCase(PD_Constant.DB_PUSH))
                log.setExceptionMessage("DB_Sync");
            else
                log.setExceptionMessage("Manual_Sync");
//            log.setMethodName(""+FastSave.getInstance().getString(PD_Utility.getUUID().toString(), "na"));
            log.setMethodName("na");
            log.setSessionId("" + FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
            log.setExceptionStackTrace("APK BUILD DATE : " + PD_Constant.apkDate);
            log.setDeviceId("" + PD_Utility.getDeviceID());
            log.setCurrentDateTime(currentDateTime);
            log.setErrorType(pushStatus);

            JSONObject pushStatusJson = null;
            pushStatusJson = new JSONObject();
            pushStatusJson.put("syncTime", currentDateTime);
            pushStatusJson.put("coursesCount", courseCount);
            log.setLogDetail(pushStatusJson.toString());

            Log.d("PushData", "pushStatusJson JSON : " + pushStatusJson.toString());

            logDao.insertLog(log);
            BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Method used to enter data sync status in newly created SyncLog table.*/
    public void enterNewSyncLog(String pushType, String pushStatus, Context context, String response){
        try {
            Model_NewSyncLog modelNewSyncLog;
            if(pushStatus.equalsIgnoreCase(PD_Constant.FAILED)){
                modelNewSyncLog = new Model_NewSyncLog();
                modelNewSyncLog.setUuid("-");
                modelNewSyncLog.setError(response);
                modelNewSyncLog.setPushType(pushType);
                modelNewSyncLog.setSentFlag(0);
                syncLogDao.insertLog(modelNewSyncLog);
            } else {
                Gson gson = new Gson();
                Type type = new TypeToken<Model_NewSyncLog>() {
                }.getType();
                modelNewSyncLog = gson.fromJson(response, type);
                modelNewSyncLog.setPushType(pushType);
                modelNewSyncLog.setSentFlag(0);
                syncLogDao.insertLog(modelNewSyncLog);
            }
                BackupDatabase.backup(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Method used to enter all db sync status in newly created SyncLog table.*/
    public void enterDBSyncLog(String pushType, String pushStatus, Context context, String uuID){
        Model_NewSyncLog model_newSyncLog = new Model_NewSyncLog();
        model_newSyncLog.setPushType(pushType);
        model_newSyncLog.setPushId(0);
        model_newSyncLog.setPushDate(PD_Utility.getCurrentDateTime());
        model_newSyncLog.setUuid(uuID);
        model_newSyncLog.setError("");
        model_newSyncLog.setStatus(pushStatus);
        model_newSyncLog.setSentFlag(0);
        syncLogDao.insertLog(model_newSyncLog);
        BackupDatabase.backup(context);
    }
}
