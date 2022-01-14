package com.pratham.prathamdigital.ui.show_sync_log;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.fragment_sync_log)
public class FragmentSyncLog extends Fragment implements ShowSyncLogContract.ShowSyncLogView {

    @Bean(ShowSyncLogPresenter.class)
    ShowSyncLogContract.ShowSyncLogPresenter presenter;

    @ViewById(R.id.rv_dataSyncLog)
    RecyclerView rv_dataSyncLog;

    @ViewById(R.id.rv_dbSyncLog)
    RecyclerView rv_dbSyncLog;

    @ViewById(R.id.tv_lastSyncDate)
    TextView tv_lastSyncDate;

    @ViewById(R.id.tv_lastdbSyncDate)
    TextView tv_lastdbSyncDate;

    @ViewById(R.id.tv_lastSyncType)
    TextView tv_lastSyncType;

    @ViewById(R.id.tv_lastdbSyncType)
    TextView tv_lastdbSyncType;

    List<Modal_Log> syncDataLog;
    List<Modal_Log> syncDbLog;

    ShowSyncLogAdapter syncDataLogAdapter, syncDbLogAdapter;


    public FragmentSyncLog() {
        // Required empty public constructor
    }

    @AfterViews
    public void init() {
        presenter.setView(FragmentSyncLog.this);
        presenter.getLogsData();
        presenter.getLastSyncData();
    }

    @UiThread
    @Override
    public void addToAdapter(List<Modal_Log> dataLogList, List<Modal_Log> dbLogList) {
        syncDataLog = dataLogList;
        syncDbLog = dbLogList;

        Log.e("%%%%%%%%%%%%", String.valueOf(syncDataLog.size()));
        Log.e("%%%%%%%%%%%#", String.valueOf(syncDbLog.size()));

        if (syncDataLogAdapter == null) {
            syncDataLogAdapter = new ShowSyncLogAdapter(getActivity(), syncDataLog);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            rv_dataSyncLog.setLayoutManager(mLayoutManager);
            rv_dataSyncLog.setNestedScrollingEnabled(false);
            rv_dataSyncLog.setAdapter(syncDataLogAdapter);
        } else
            syncDataLogAdapter.notifyDataSetChanged();

        if (syncDbLogAdapter == null) {
            syncDbLogAdapter = new ShowSyncLogAdapter(getActivity(), syncDbLog);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            rv_dbSyncLog.setLayoutManager(mLayoutManager);
            rv_dbSyncLog.setNestedScrollingEnabled(false);
            rv_dbSyncLog.setAdapter(syncDbLogAdapter);
        } else
            syncDbLogAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoData() {

    }

    @Override
    public void showLastSyncData(Modal_Log lastSyncData, Modal_Log lastSyncDb) {

        if(lastSyncData!=null) {
            tv_lastSyncDate.setText(lastSyncData.currentDateTime);
            tv_lastSyncType.setText(lastSyncData.exceptionMessage);
        }
        if(lastSyncDb!=null) {
            tv_lastdbSyncDate.setText(lastSyncDb.currentDateTime);
            tv_lastdbSyncType.setText(lastSyncDb.exceptionMessage);
        }
    }
}