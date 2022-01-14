package com.pratham.prathamdigital.ui.show_sync_log;

import android.content.Context;

import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.models.Modal_Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.List;

import static com.pratham.prathamdigital.PrathamApplication.logDao;

@EBean
public class ShowSyncLogPresenter implements ShowSyncLogContract.ShowSyncLogPresenter {//}, API_Content_Result {

    Context mContext;
    ShowSyncLogContract.ShowSyncLogView showSyncLogView;

    public ShowSyncLogPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void setView(ShowSyncLogContract.ShowSyncLogView showSyncLogView) {
        this.showSyncLogView = showSyncLogView;
    }

    @Background
    @Override
    public void getLogsData() {
        List<Modal_Log> dataSyncLogList;
        List<Modal_Log> dbSyncLogList;
        dataSyncLogList = logDao.getDataSyncLogs();
        dbSyncLogList = logDao.getDbSyncLogs();

        if((dataSyncLogList!=null && dataSyncLogList.size()>0) || (dbSyncLogList!=null && dbSyncLogList.size()>0))
            showSyncLogView.addToAdapter(dataSyncLogList, dbSyncLogList);
        else
            showSyncLogView.showNoData();
    }

    @Override
    public void getLastSyncData() {
        Modal_Log lastSyncData = logDao.getLastDataSyncLog();
        Modal_Log lastSyncDB = logDao.getLastDbSyncLog();
        showSyncLogView.showLastSyncData(lastSyncData, lastSyncDB);
    }

}