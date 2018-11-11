package com.pratham.prathamdigital.async;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.util.PD_Constant;

import org.json.JSONObject;

import java.io.File;

import static com.pratham.prathamdigital.PrathamApplication.pradigiPath;

/**
 * Created by HP on 30-12-2016.
 */

public class PD_ApiRequest {
    Context mContext;
    ContentContract.contentPresenter contentPresenter;

    public PD_ApiRequest(Context context, ContentContract.contentPresenter contentPresenter) {
        this.mContext = context;
        this.contentPresenter = contentPresenter;
    }

    public void getContentFromRaspberry(final String requestType, String url) {
        try {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("Authorization", getAuthHeader("pratham", "pratham"))
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (contentPresenter != null)
                                contentPresenter.recievedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (contentPresenter != null)
                                contentPresenter.recievedError(requestType);
                            Log.d("Error::", anError.getErrorDetail());
                            Log.d("Error::", anError.getResponse().toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getContentFromInternet(final String requestType, String url) {
        try {
            AndroidNetworking.get(url)
                    .addHeaders("Content-Type", "application/json")
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            if (contentPresenter != null)
                                contentPresenter.recievedContent(requestType, response);
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (contentPresenter != null)
                                contentPresenter.recievedError(requestType);
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
                .addBodyParameter("facility", PreferenceManager.getDefaultSharedPreferences(mContext)
                        .getString("FACILITY", ""))
                .addBodyParameter("data", data)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
//                        contentPresenter.notifySuccess(requestType, "success");
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (contentPresenter != null)
//                            contentPresenter.notifyError(requestType/*, null*/);
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
//                        contentPresenter.notifySuccess(requestType, response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (contentPresenter != null)
//                            contentPresenter.notifyError(requestType/*, null*/);
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
        File mydir = new File(pradigiPath + "/" + FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        if (!mydir.exists()) mydir.mkdirs();
        mydir = new File(mydir.getAbsolutePath() + "/PrathamImages"); //Creating an internal dir;
        if (!mydir.exists()) mydir.mkdirs();
        AndroidNetworking.download(url, mydir.getAbsolutePath(), filename)
                .setPriority(Priority.HIGH)
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
