package com.pratham.prathamdigital.ui.fragment_share;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.ContentItemDecoration;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.permissions.ResponsePermissionCallback;
import com.pratham.prathamdigital.custom.progress_layout.ProgressLayout;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_ReceivingFilesThroughFTP;
import com.pratham.prathamdigital.ui.fragment_share_recieve.ContractShare;
import com.pratham.prathamdigital.ui.fragment_share_recieve.FileListAdapter;
import com.pratham.prathamdigital.ui.fragment_share_recieve.SharePresenter;
import com.pratham.prathamdigital.util.ConnectionUtils;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

@EFragment(R.layout.fragment_share)
public class FragmentShare extends Fragment implements ZXingScannerView.ResultHandler, ContractShare.shareView {
    private static final int INIT_CAMERA = 1;
    public ZXingScannerView startCameraScan;
    @ViewById(R.id.rl_scan_qr)
    RelativeLayout rl_scan_qr;
    @ViewById(R.id.qr_frame)
    FrameLayout qr_frame;
    @ViewById(R.id.connecting_progress)
    LinearLayout connecting_progress;
    @ViewById(R.id.ll_share_files)
    LinearLayout ll_share_files;
    @ViewById(R.id.rv_share_files)
    RecyclerView rv_share_files;
    @Bean(SharePresenter.class)
    ContractShare.sharePresenter sharePresenter;
    HashMap<String, File_Model> filesSent = new HashMap<>();
    HashMap<String, Integer> filesSentPosition = new HashMap<>();
    FileListAdapter fileListAdapter;
    BlurPopupWindow sending_builder;
    private TextView dialog_tv;
    private ProgressLayout dialog_progressLayout;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_CAMERA:
                    initCamera();
                    break;
                case PD_Constant.WiFiConnectSuccess:
                    sharePresenter.connectFTP();
                    break;
            }
        }
    };

    @AfterViews
    public void initialize() {
        sharePresenter.setView(FragmentShare.this);
        mHandler.sendEmptyMessage(INIT_CAMERA);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (startCameraScan != null)
            startCameraScan.resumeCameraPreview(FragmentShare.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (startCameraScan != null)
            startCameraScan.resumeCameraPreview(FragmentShare.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (startCameraScan != null)
            startCameraScan.stopCamera();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void messageRecievedInShare(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_SHARE_PROGRESS)) {
                dialog_tv.setText(message.getFile_name());
                dialog_progressLayout.setCurProgress((int) message.getProgress());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_SHARE_COMPLETE)) {
                sharePresenter.startTimer();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sending_builder != null)
                            sending_builder.dismiss();
                    }
                }, 4000);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.SHARE_BACK))
                sharePresenter.traverseFolderBackward();
            else if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_FTP_SERVER))
                disconnectFTP();
        }
    }

    @Override
    public void onDestroy() {
        if (startCameraScan != null) startCameraScan.stopCamera();
        super.onDestroy();
        sharePresenter.viewDestroyed();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            PrathamApplication.getInstance().unregisterReceiver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharePresenter.viewDestroyed();
    }

    @Override
    public void handleResult(Result result) {
        try {
            startCameraScan.stopCamera();
            connecting_progress.setVisibility(View.VISIBLE);
            if (!result.getText().isEmpty()) {
                JSONObject jsonobject = new JSONObject(result.getText());
                String wifiname = jsonobject.getString(PD_Constant.FTP_HOTSPOT_SSID);
                String wifipass = jsonobject.getString(PD_Constant.FTP_HOTSPOT_PASS);
                int wifikeymgmt = jsonobject.getInt(PD_Constant.FTP_KEYMGMT);
                ConnectionUtils.getInstance(getActivity()).toggleConnection(wifiname, wifipass, wifikeymgmt, sharePresenter);
                //sharePresenter.connectToWify(wifiname, wifipass);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initCamera() {
        connecting_progress.setVisibility(View.GONE);
        KotlinPermissions.with(getActivity())
                .permissions(Manifest.permission.CAMERA)
                .onAccepted(new ResponsePermissionCallback() {
                    @Override
                    public void onResult(@NotNull List<String> permissionResult) {
                        _initCamera();
                    }
                })
                .ask();
    }

    private void _initCamera() {
        try {
            rl_scan_qr.setVisibility(View.VISIBLE);
            if (startCameraScan == null) {
                startCameraScan = new ZXingScannerView(getActivity());
                startCameraScan.setResultHandler(FragmentShare.this);
                qr_frame.addView((startCameraScan));
            } else {
                startCameraScan.stopCamera();
            }
            startCameraScan.startCamera();
            startCameraScan.resumeCameraPreview(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hotspotStarted() {

    }

    @Override
    public void onWifiConnected(String ssid) {
        Message.obtain(mHandler, PD_Constant.WiFiConnectSuccess, ssid).sendToTarget();
    }

    @Override
    public void fileItemClicked(Modal_ContentDetail detail, int position) {
        if (detail.getNodeid().equalsIgnoreCase(PD_Constant.SHARE_PROFILE)) {
            sharePresenter.sendProfiles();
            showSendingDialog();
        } else if (detail.getNodeid().equalsIgnoreCase(PD_Constant.SHARE_USAGE)) {
            sharePresenter.sendUsages();
            showSendingDialog();
        } else
            sharePresenter.showFolders(detail);
    }

    @Override
    public void sendItemChecked(File_Model model, int position) {
        filesSent.put(model.getDetail().getNodeid(), model);
        filesSentPosition.put(model.getDetail().getNodeid(), position);
        sharePresenter.sendFiles(model.getDetail());
        showSendingDialog();
    }

    public void showSendingDialog() {
        sending_builder = new BlurPopupWindow.Builder(getContext())
                .setContentView(R.layout.dialog_file_sending)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .setDismissOnClickBack(false)
                .setDismissOnTouchBackground(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(8)
                .setTintColor(0x30000000)
                .build();
        dialog_tv = (TextView) sending_builder.findViewById(R.id.dialog_file_name);
        dialog_progressLayout = (ProgressLayout) sending_builder.findViewById(R.id.dialog_progressLayout);
        dialog_tv.setText("Please Wait...");
        sending_builder.show();
    }

    @Override
    public void showFilesList(ArrayList<File_Model> contents, String parentId) {
        filesSent.clear();
        filesSentPosition.clear();
        if (rv_share_files.getVisibility() == View.GONE)
            rv_share_files.setVisibility(View.VISIBLE);
//        if (rv_share_files_receiving.getVisibility() == View.VISIBLE)
//            rv_share_files_receiving.setVisibility(View.GONE);
        if (fileListAdapter == null) {
            //initialize adapter
            fileListAdapter = new FileListAdapter(getActivity(), FragmentShare.this);
            rv_share_files.setHasFixedSize(true);
            rv_share_files.addItemDecoration(new ContentItemDecoration(PD_Constant.CONTENT, 10));
            rv_share_files.setAdapter(fileListAdapter);
            rv_share_files.scheduleLayoutAnimation();
            fileListAdapter.submitList(contents);
        } else {
            fileListAdapter.submitList(contents);
            rv_share_files.smoothScrollToPosition(0);
        }
    }

    @Override
    public void disconnectFTP() {

    }

    @Override
    public void closeFTPJoin() {

    }

    @Override
    public void showRecieving(ArrayList<Modal_ReceivingFilesThroughFTP> filesRecieving) {

    }

    @Override
    public void ftpConnected_showFolders() {
        rl_scan_qr.setVisibility(View.GONE);
        ll_share_files.setVisibility(View.VISIBLE);
        if (connecting_progress.getVisibility() == View.VISIBLE)
            connecting_progress.setVisibility(View.GONE);
        if (rl_scan_qr.getVisibility() == View.VISIBLE)
            rl_scan_qr.setVisibility(View.GONE);
        sharePresenter.showFolders(null);
    }

    @Override
    public void ftpConnectionFailed() {
    }
}
