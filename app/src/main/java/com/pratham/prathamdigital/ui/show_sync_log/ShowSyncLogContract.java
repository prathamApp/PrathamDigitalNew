package com.pratham.prathamdigital.ui.show_sync_log;

import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Model_NewSyncLog;
import com.pratham.prathamdigital.models.Model_SyncStatusLog;

import java.util.List;

public interface ShowSyncLogContract {

    interface ShowSyncLogView {
        void addToAdapter(List<Model_NewSyncLog> dataLogList, List<Model_NewSyncLog> dbLogList);
        void showNoData();
        void showLastSyncData(Modal_Log lastSyncData, Modal_Log lastSyncDb);
        void showSyncDetails(Model_SyncStatusLog syncStatusLog);
        void setData(List<Model_NewSyncLog> syncLog);
    }

    interface ShowSyncLogPresenter {
        void setView(ShowSyncLogView ShowSyncLogView);
        void getLogsData();
        void getLastSyncData();
        void getSyncDetails(Integer pushId);
        void updateSyncStatus(Model_SyncStatusLog syncStatusLog);
    }
}
