package com.pratham.prathamdigital.ui.show_sync_log;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Model_NewSyncLog;
import com.pratham.prathamdigital.models.Model_SyncStatusLog;
import com.pratham.prathamdigital.ui.fragment_content.ContentPresenterImpl;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.syncLogDao;

@EBean
public class ShowSyncLogPresenter implements ShowSyncLogContract.ShowSyncLogPresenter, ApiResult {//}, API_Content_Result {

    Context mContext;
    ShowSyncLogContract.ShowSyncLogView showSyncLogView;

    List<Model_NewSyncLog> dataSyncLogList;
    List<Model_NewSyncLog> dbSyncLogList;

    @Bean(PD_ApiRequest.class)
    PD_ApiRequest pd_apiRequest;

    public ShowSyncLogPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(ShowSyncLogContract.ShowSyncLogView showSyncLogView) {
        this.showSyncLogView = showSyncLogView;
        pd_apiRequest.setApiResult(ShowSyncLogPresenter.this);
    }

    @Background
    @Override
    public void getLogsData() {

        dataSyncLogList = syncLogDao.getAllSyncLogs();
        dbSyncLogList = syncLogDao.getAllDBSyncLogs();

        if ((dataSyncLogList != null && dataSyncLogList.size() > 0) || (dbSyncLogList != null && dbSyncLogList.size() > 0))
            showSyncLogView.addToAdapter(dataSyncLogList, dbSyncLogList);
        else
            showSyncLogView.showNoData();
    }

    @Override
    public void getLastSyncData() {
    }

    @Override
    public void getSyncDetails(Integer pushId) {
        String url = PD_Constant.SYNC_PUSH_DATA + pushId;
        pd_apiRequest.getContentFromInternet(PD_Constant.CHECK_SYNC_DETAILS, url, null);
    }

    //Updating the sync status in db and view after check sync
    @Override
    public void updateSyncStatus(Model_SyncStatusLog syncStatusLog) {
        syncLogDao.updateSyncStatus(Integer.parseInt(syncStatusLog.getPushId()), syncStatusLog.getUuid(), syncStatusLog.getPushStatus());
        dataSyncLogList = syncLogDao.getAllSyncLogs();
        showSyncLogView.setData(dataSyncLogList);
    }

    @Override
    public void recievedContent(String header, String response, ArrayList<Modal_ContentDetail> contentList) {
        try {
            Gson gson = new Gson();
            if (header.equalsIgnoreCase(PD_Constant.CHECK_SYNC_DETAILS)) {
                Log.e("chkSyncDetailRspons:", response);
                Log.e("chkSyncDetailRspons:", "requestType:: " + header);

                Type type = new TypeToken<Model_SyncStatusLog>() {
                }.getType();
                Model_SyncStatusLog modelSyncStatusLog = gson.fromJson(response, type);
                if(modelSyncStatusLog.getError().equalsIgnoreCase("Push Record not found.")) {
                    PD_Utility.dismissLoadingDialog();
                    Toast.makeText(mContext, modelSyncStatusLog.getError(), Toast.LENGTH_SHORT).show();
                }
                else
                    showSyncLogView.showSyncDetails(modelSyncStatusLog);
            }
        } catch (JsonSyntaxException e) {
            PD_Utility.dismissLoadingDialog();
            Toast.makeText(mContext, "JsonSyntaxException Occurred.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            PD_Utility.dismissLoadingDialog();
            Toast.makeText(mContext, "Exception Occurred while receiving data.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public void recievedError(String header, ArrayList<Modal_ContentDetail> contentList) {

    }
}