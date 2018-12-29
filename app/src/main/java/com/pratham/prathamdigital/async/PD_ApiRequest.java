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
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;

/**
 * Created by HP on 30-12-2016.
 */

public class PD_ApiRequest {
    Context mContext;
    ApiResult apiResult;
//    OkHttpClient okHttpClient;

    public PD_ApiRequest(Context context, ApiResult apiResult) {
        this.mContext = context;
        this.apiResult = apiResult;
//        okHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build();
    }

    public void getContentFromRaspberry(final String requestType, String url, ArrayList<Modal_ContentDetail> contentList) {
        try {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (apiResult != null)
                                apiResult.recievedContent(requestType, response, contentList);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (apiResult != null)
                                apiResult.recievedError(requestType, contentList);
                            Log.d("Error::", anError.getErrorDetail());
                            Log.d("Error::", anError.getResponse().toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getContentFromInternet(final String requestType, String url, ArrayList<Modal_ContentDetail> contentList) {
        try {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (apiResult != null)
                                apiResult.recievedContent(requestType, response, contentList);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (apiResult != null)
                                apiResult.recievedError(requestType, contentList);
                            Log.d("Error:", anError.getErrorDetail());
                            Log.d("Error::", anError.getResponse().toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pushDataToRaspberry(final String requestType, String url, String data,
                                    String filter_name, String table_name) {
        AndroidNetworking.post(url)
                .addHeaders("Content-Type", "application/json")
                .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
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
                        BaseActivity.logDao.deleteLogs();
                        BackupDatabase.backup(mContext);
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

    public void pushDataToInternet(final String requestType, String url, JSONObject data) {
        AndroidNetworking.post(url)
//                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(data)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        BaseActivity.logDao.deleteLogs();
                        BackupDatabase.backup(mContext);
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

    private String getAuthHeader(String ID, String pass) {
        String encoded = Base64.encodeToString((ID + ":" + pass).getBytes(), Base64.NO_WRAP);
        String returnThis = "Basic " + encoded;
        return returnThis;
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
                .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
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
}
