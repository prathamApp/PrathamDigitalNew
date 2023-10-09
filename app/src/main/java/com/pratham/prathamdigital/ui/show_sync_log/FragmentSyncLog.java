package com.pratham.prathamdigital.ui.show_sync_log;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Model_NewSyncLog;
import com.pratham.prathamdigital.models.Model_SyncStatusLog;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.fragment_sync_log)
public class FragmentSyncLog extends Fragment implements ShowSyncLogContract.ShowSyncLogView, CheckSyncDetailsClick {

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

    List<Model_NewSyncLog> syncDataLog;
    List<Model_NewSyncLog> syncDbLog;

    ShowSyncLogAdapter syncDataLogAdapter, syncDbLogAdapter;

    public BlurPopupWindow syncDetailDialog;

    TextView tv_syncDetail, tv_todaysDate;
    String TotalError = "", ScoreErr = "", AttendanceErr = "", StudentErr = "", SessionErr = "",
            cpErr = "", logsErr = "", CourseEnrollmentErr = "", GroupesErr = "";

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
    public void addToAdapter(List<Model_NewSyncLog> dataLogList, List<Model_NewSyncLog> dbLogList) {
        syncDataLog = dataLogList;
        syncDbLog = dbLogList;

        if (syncDataLogAdapter == null) {
            syncDataLogAdapter = new ShowSyncLogAdapter(getActivity(), syncDataLog, FragmentSyncLog.this);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            rv_dataSyncLog.setLayoutManager(mLayoutManager);
            rv_dataSyncLog.setNestedScrollingEnabled(false);
            rv_dataSyncLog.setAdapter(syncDataLogAdapter);
        } else
            syncDataLogAdapter.notifyDataSetChanged();

        if (syncDbLogAdapter == null) {
            syncDbLogAdapter = new ShowSyncLogAdapter(getActivity(), syncDbLog, FragmentSyncLog.this);
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

        if (lastSyncData != null) {
            tv_lastSyncDate.setText(lastSyncData.currentDateTime);
            tv_lastSyncType.setText(lastSyncData.exceptionMessage);
        }
        if (lastSyncDb != null) {
            tv_lastdbSyncDate.setText(lastSyncDb.currentDateTime);
            tv_lastdbSyncType.setText(lastSyncDb.exceptionMessage);
        }
    }

    //Method used to show all the details of particular push
    @Override
    public void showSyncDetails(Model_SyncStatusLog syncStatusLog) {
        presenter.updateSyncStatus(syncStatusLog);
        PD_Utility.dismissLoadingDialog();
        checkForErrorCounts(syncStatusLog);
        syncDetailDialog = new BlurPopupWindow.Builder(getActivity())
                .setContentView(R.layout.dialog_new_sync_details)
                .bindClickListener(v -> {
                    syncDetailDialog.dismiss();
                }, R.id.btn_ok)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        tv_syncDetail = syncDetailDialog.findViewById(R.id.tv_syncDetail);
        tv_todaysDate = syncDetailDialog.findViewById(R.id.tv_date);

        tv_todaysDate.setText("Today's Date : "+ PD_Utility.getCurrentDateTime());
        tv_syncDetail.setText(Html.fromHtml("PushID : <font color='#CD5B55'>" + syncStatusLog.getPushId() + "</font>"
                        + "&emsp;&emsp;" + "SyncId : <font color='#CD5B55'>" + syncStatusLog.getSyncId() + "</font>"
                        + "&emsp;&emsp;" + "Push Status : <font color='#CD5B55'>" + syncStatusLog.getPushStatus() + "</font>"
                        + "<br>" + "UUID : <font color='#CD5B55'>" + syncStatusLog.getUuid() + "</font>"
                        + "<br>" + "Push Date : <font color='#CD5B55'>" + syncStatusLog.getPushDate() + "</font>"
                        + "&emsp;&emsp;" + "Device ID : <font color='#CD5B55'>" + syncStatusLog.getDeviceId() + "</font>"
                        + "<br>" + "LastChecked : <font color='#CD5B55'>" + syncStatusLog.getLastChecked() + "</font>"
                        + "<br><br><font color='#35469E'>" + "Score Pushed : " + syncStatusLog.getScorePushed() + "</font>"
                        + "&emsp;<font color='#05AF81'>" + "Score Synced : " + syncStatusLog.getScoreSynced() + "</font>" + ScoreErr
                        + "<br><font color='#35469E'>" + "Attendance Pushed : " + syncStatusLog.getAttendancePushed() + "</font>"
                        + "&emsp;<font color='#05AF81'>" + "Attendance Synced : " + syncStatusLog.getAttendanceSynced() + "</font>" + AttendanceErr
                        + "<br><font color='#35469E'>" + "Students Pushed : " + syncStatusLog.getStudentPushed() + "</font>"
                        + "&emsp;<font color='#05AF81'>" + "Students Synced : " + syncStatusLog.getStudentSynced() + "</font>" + StudentErr
                        + "<br><font color='#35469E'>" + "Groups Pushed : " + syncStatusLog.getGroupsDataCount() + "</font>"
                        + "&emsp;<font color='#05AF81'>" + "Groups Synced : " + syncStatusLog.getGroupsDataSynced() + "</font>" + GroupesErr
                        + "<br><font color='#35469E'>" + "Sessions Pushed : " + syncStatusLog.getSessionCount() + "</font>"
                        + "&emsp;<font color='#05AF81'>" + "Sessions Synced : " + syncStatusLog.getSessionSynced() + "</font>" + SessionErr
                        + "<br><font color='#35469E'>" + "Logs Pushed : " + syncStatusLog.getLogsCount() + "</font>"
                        + "&emsp;<font color='#05AF81'>" + "Logs Synced : " + syncStatusLog.getLogsSynced() + "</font>" + logsErr
                        + "<br><font color='#35469E'>" + "Course Enrollment Pushed : " + syncStatusLog.getCourseEnrollmentCount() + "</font>"
                        + "&emsp;<font color='#05AF81'>" + "Course Enrollment Synced : " + syncStatusLog.getCourseEnrollmentSynced() + "</font>" + CourseEnrollmentErr
                        + "<br><font color='#35469E'>" + "Content Progress Pushed : " + syncStatusLog.getCpCount() + "</font>"
                        + "&emsp;<font color='#05AF81'>" + "Content Progress Synced : " + syncStatusLog.getCpSynced() + "</font>" + cpErr
        ));

        syncDetailDialog.show();
    }

    public void checkForErrorCounts(Model_SyncStatusLog syncStatusLog){

        if (!syncStatusLog.getError().equalsIgnoreCase("") && !syncStatusLog.getError().equalsIgnoreCase(" "))
            TotalError = "<font color='#FF3A41'>Error : " + syncStatusLog.getError() + "</font>";
        if (syncStatusLog.getScoreError() != 0)
            ScoreErr = "&emsp;<font color='#FF3A41'>Score Error : " + syncStatusLog.getScoreError() + "</font>";
        if (syncStatusLog.getAttendanceError() != 0)
            AttendanceErr = "&emsp;<font color='#FF3A41'>Attendance Error : " + syncStatusLog.getAttendanceError() + "</font>";
        if (syncStatusLog.getStudentError() != 0)
            StudentErr = "&emsp;<font color='#FF3A41'>Student Error : " + syncStatusLog.getStudentError() + "</font>";
        if (syncStatusLog.getSessionError() != 0)
            SessionErr = "&emsp;<font color='#FF3A41'>Session Error : " + syncStatusLog.getSessionError() + "</font>";
        if (syncStatusLog.getCpError() != 0)
            cpErr = "&emsp;<font color='#FF3A41'>Content Progress Error : " + syncStatusLog.getCpError() + "</font>";
        if (syncStatusLog.getLogsError() != 0)
            logsErr = "&emsp;<font color='#FF3A41'>Logs Error : " + syncStatusLog.getLogsError() + "</font>";
        if (syncStatusLog.getCourseEnrollmentError() != 0)
            CourseEnrollmentErr = "&emsp;<font color='#FF3A41'>Course Enrollment Error : " + syncStatusLog.getCourseEnrollmentError() + "</font>";
        if (syncStatusLog.getGroupsDataError() != 0)
            GroupesErr = "&emsp;<font color='#FF3A41'>Groups Error : " + syncStatusLog.getGroupsDataError() + "</font>";

    }

    //Used to refresh the list after check sync
    @Override
    public void setData(List<Model_NewSyncLog> syncLog) {
        syncDataLog.clear();
        syncDataLog.addAll(syncLog);
        syncDataLogAdapter.notifyDataSetChanged();
        // where this.data is the recyclerView's dataset you are
        // setting in adapter=new Adapter(this,db.getData());
    }

    @Override
    public void checkSyncDetails(Integer pushId) {
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileOrWifiNetwork()) {
            PD_Utility.showLoadingDialog(getActivity());
            presenter.getSyncDetails(pushId);
        } else {
            Toast.makeText(getActivity(), R.string.internet_connection, Toast.LENGTH_SHORT).show();
        }
    }
}