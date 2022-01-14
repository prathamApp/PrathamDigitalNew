package com.pratham.prathamdigital.ui.show_sync_log;

import com.pratham.prathamdigital.models.Modal_Log;

import java.util.List;

public interface ShowSyncLogContract {

    interface ShowSyncLogView {
        void addToAdapter(List<Modal_Log> dataLogList, List<Modal_Log> dbLogList);
        void showNoData();
        void showLastSyncData(Modal_Log lastSyncData, Modal_Log lastSyncDb);
    }

    interface ShowSyncLogPresenter {
        void setView(ShowSyncLogView ShowSyncLogView);
        void getLogsData();
        void getLastSyncData();
    }
}
