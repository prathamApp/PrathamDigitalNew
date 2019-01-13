package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.CircularProgress;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.ContentItemDecoration;
import com.pratham.prathamdigital.custom.SearchView;
import com.pratham.prathamdigital.custom.progress_layout.ProgressLayout;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.ftpSettings.FsService;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_ReceivingFilesThroughFTP;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.socket.entity.Users;
import com.pratham.prathamdigital.socket.udp.IPMSGConst;
import com.pratham.prathamdigital.socket.udp.IPMSGProtocol;
import com.pratham.prathamdigital.socket.udp.UDPMessageListener;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent_;
import com.pratham.prathamdigital.util.FileUtils;
import com.pratham.prathamdigital.util.FragmentManagePermission;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;
import com.pratham.prathamdigital.util.WifiUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.pratham.prathamdigital.util.PD_Utility.getPhoneModel;

@EFragment(R.layout.fragment_share)
public class FragmentShareRecieve extends FragmentManagePermission implements ContractShare.shareView,
        UDPMessageListener.OnNewMsgListener, CircularRevelLayout.CallBacks {

    private static final String TAG = FragmentShareRecieve.class.getSimpleName();
    private static final int SDCARD_LOCATION_CHOOSER = 100;
    @ViewById(R.id.root_share)
    LinearLayout root_share;
    @ViewById(R.id.rl_share)
    RelativeLayout rl_share;
    @ViewById(R.id.rl_recieve)
    RelativeLayout rl_recieve;
    @ViewById(R.id.searchCircle)
    RelativeLayout searchCircle;
    @ViewById(R.id.shareCircle)
    RelativeLayout shareCircle;
    @ViewById(R.id.circleProgress)
    CircularProgress circleProgress;
    @ViewById(R.id.status)
    TextView status;
    @ViewById(R.id.searchView)
    SearchView searchView;
    @ViewById(R.id.rl_share_block)
    RelativeLayout rl_share_block;
    @ViewById(R.id.rl_recieve_block)
    RelativeLayout rl_recieve_block;
    @ViewById(R.id.rv_files)
    RecyclerView rv_files;
    @ViewById(R.id.rv_files_receiving)
    RecyclerView rv_files_receiving;
    @ViewById(R.id.circular_share_reveal)
    CircularRevelLayout circular_share_reveal;
    @ViewById(R.id.share_title)
    TextView share_title;
    TextView dialog_tv;
    ProgressLayout dialog_progressLayout;

    @Bean(SharePresenter.class)
    ContractShare.sharePresenter sharePresenter;

    private List<String> mList = new ArrayList<>();
    private UDPMessageListener mUDPListener;
    private boolean share = false;
    private Timer connectTimer;
    private String localIPaddress;
    private String serverIPaddres;
    FileListAdapter fileListAdapter;
    ReceivedFileListAdapter receivedFileListAdapter;
    HashMap<String, File_Model> filesSent = new HashMap<>();
    HashMap<String, Integer> filesSentPosition = new HashMap<>();
    private int revealX;
    private int revealY;
    BlurPopupWindow sd_builder;
    BlurPopupWindow sending_builder;

    @AfterViews
    public void initialize() {
        sharePresenter.setView(FragmentShareRecieve.this);
        circular_share_reveal.setListener(this);
        if (getArguments() != null) {
            revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            circular_share_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    circular_share_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    circular_share_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
        }
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

    @Click(R.id.rl_share)
    public void setRl_share() {
        share = true;
        if (isPermissionsGranted(getActivity(),
                new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_ACCESS_FINE_LOCATION})) {
            if (new LocationService(getActivity()).checkLocationEnabled()) {
                ArrayList<String> sdPath = FileUtils.getExtSdCardPaths(getActivity());
                if (sdPath.size() > 0) {
                    if (FastSave.getInstance().getString(PD_Constant.SDCARD_URI, null) == null)
                        showSdCardDialog();
                    else
                        animateHotspotCreation();
                } else
                    animateHotspotCreation();
            } else
                new LocationService(getActivity()).checkLocation();
        } else
            askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                    PermissionUtils.Manifest_ACCESS_FINE_LOCATION}, locationPermissionResult);
    }

    @UiThread
    public void animateHotspotCreation() {
        TransitionManager.beginDelayedTransition(root_share);
        ViewGroup.LayoutParams params = rl_share.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        rl_share.requestLayout();
        rl_recieve.setVisibility(View.GONE);
        rl_share.setClickable(false);
        createHotspot();
    }

    @UiThread
    public void showSdCardDialog() {
        sd_builder = new BlurPopupWindow.Builder(getContext())
                .setContentView(R.layout.dialog_alert_sd_card)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .bindClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                startActivityForResult(intent, SDCARD_LOCATION_CHOOSER);
                            }
                        }, 1500);
                        sd_builder.dismiss();
                    }
                }, R.id.txt_choose_sd_card)
                .setDismissOnClickBack(true)
                .setDismissOnTouchBackground(true)
                .setScaleRatio(0.2f)
                .setBlurRadius(8)
                .setTintColor(0x30000000)
                .build();
        sd_builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SDCARD_LOCATION_CHOOSER) {
            if (data != null && data.getData() != null) {
                Uri treeUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                PrathamApplication.getInstance().getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                FastSave.getInstance().saveString(PD_Constant.SDCARD_URI, treeUri.toString());
                //create Hotspot
                animateHotspotCreation();
            }
        }
    }

    @Click(R.id.rl_recieve)
    public void setRl_recieve() {
        WifiUtils.closeWifiAp();
        if (!PrathamApplication.wiseF.isWifiEnabled()) PrathamApplication.wiseF.enableWifi();
        share = false;
        if (isPermissionsGranted(getActivity(),
                new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_ACCESS_FINE_LOCATION})) {
            if (new LocationService(getActivity()).checkLocationEnabled()) {
                TransitionManager.beginDelayedTransition(root_share);
                ViewGroup.LayoutParams params = rl_recieve.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                rl_recieve.requestLayout();
                rl_share.setVisibility(View.GONE);
                rl_recieve.setClickable(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(getActivity())) {
                        connectHotspotAndRecieve();
                    } else {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        startActivityForResult(intent, 1);
                    }
                } else {
                    connectHotspotAndRecieve();
                }
            } else {
                new LocationService(getActivity()).checkLocation();
            }
        } else {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                    PermissionUtils.Manifest_ACCESS_FINE_LOCATION}, locationPermissionResult);
        }
    }

    PermissionResult locationPermissionResult = new PermissionResult() {
        @Override
        public void permissionGranted() {
            if (share) setRl_share();
            else setRl_recieve();
        }

        @Override
        public void permissionDenied() {

        }

        @Override
        public void permissionForeverDenied() {

        }
    };

    @UiThread
    public void createHotspot() {
        rl_share_block.setVisibility(View.GONE);
        startCreateAnim();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createAP();
            }
        }, 2000);
    }

    public void startCreateAnim() {
        searchCircle.setVisibility(View.GONE);
        shareCircle.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).
                setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        shareCircle.setVisibility(View.VISIBLE);
        circleProgress.startCircleAnim(1000);
        circleProgress.startAnim(2000);
    }

    private void createAP() {
        if (PrathamApplication.wiseF.isWifiEnabled())
            PrathamApplication.wiseF.disableWifi();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(getActivity())) {
                WifiUtils.startWifiAp(PD_Constant.WIFI_AP_HEADER + PD_Utility.getLocalHostName(), PD_Constant.WIFI_AP_PASSWORD, mHandler);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, 1);
            }
        } else {
            WifiUtils.startWifiAp(PD_Constant.WIFI_AP_HEADER + PD_Utility.getLocalHostName(), PD_Constant.WIFI_AP_PASSWORD, mHandler);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PD_Constant.ApCreateApSuccess:
                    String s = PD_Utility.getLocalHostName();
                    SpannableString ss = new SpannableString(s);
                    ss.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new RelativeSizeSpan(1.2f), 0, 4, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
                    status.setText(ss);
                    circleProgress.finishAnim();
                    getActivity().sendBroadcast(new Intent(FsService.ACTION_START_FTPSERVER));
                    break;
                case PD_Constant.LOCATION_GRANTED:
                    createAP();
                    break;
                case PD_Constant.ApScanResult:
                    for (ScanResult wifi : WifiUtils.getScanResults()) {
                        String ssid = wifi.SSID;
                        if (ssid.startsWith(PD_Constant.WIFI_AP_HEADER)) {
                            if (!mList.contains(ssid)) {
                                mList.add(wifi.SSID);
                                searchView.addApView(wifi);
                            }
                        }
                    }
                    break;
                case IPMSGConst.AN_CONNECT_SUCCESS:
                    Users user = new Users();
                    user.setDevice(getPhoneModel());
                    user.setIpaddress(serverIPaddres);
                    break;
                case IPMSGConst.NO_CONNECT_SUCCESS:
                    IPMSGProtocol command = (IPMSGProtocol) msg.obj;
                    Users user2 = new Users();
                    user2.setDevice(getPhoneModel());
                    user2.setIpaddress(command.senderIP);
                    break;
                case PD_Constant.WiFiConnectSuccess:
                    if (isValidated()) {
                        status.setText("connection succeeded...");
                        connectTimer.cancel();
                        sharePresenter.connectFTP();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                share_title.setText("Share Contents");
                                hideViewsandShowFolders();
                                sharePresenter.showFolders(null);
                            }
                        }, 2000);
                    }
                    break;
            }
        }
    };

    private void hideViewsandShowFolders() {
        root_share.setClickable(false);
        shareCircle.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        rl_recieve_block.setVisibility(View.GONE);
    }

    private void connectHotspotAndRecieve() {
        rl_recieve_block.setVisibility(View.GONE);
        startJoinAnim();
        if (!PrathamApplication.wiseF.isWifiEnabled())
            PrathamApplication.wiseF.enableWifi();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                WifiUtils.startScan();
                mHandler.sendEmptyMessage(PD_Constant.ApScanResult);
            }
        }, new Date(System.currentTimeMillis() + 2000), 2000);
        searchView.setOnAvatarClickListener(new SearchView.OnAvatarClickListener() {
            @Override
            public void onClick(View v) {
                onAvatarClick(v);
            }
        });
    }

    private void onAvatarClick(final View v) {
        startAvatarClickAnim(v);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScanResult wifi = (ScanResult) v.getTag();
                connectAp(wifi.SSID);
            }
        }, 1500);
    }

    private void connectAp(final String hostName) {
        if (hostName.startsWith(PD_Constant.WIFI_AP_HEADER)) {
            sharePresenter.connectToWify(hostName);
        }
    }

    private void startAvatarClickAnim(final View v) {
        int[] mAvatarLocation = new int[2];
        v.getLocationOnScreen(mAvatarLocation);

        int mCenterSearchLocation[] = new int[2];
        searchView.getLocationOnScreen(mCenterSearchLocation);

        int mLeft = mCenterSearchLocation[0] + searchView.getWidth() / 2 - mAvatarLocation[0] - v.getWidth() / 2;
        int mTop = mCenterSearchLocation[1] + searchView.getHeight() / 2 - mAvatarLocation[1] - v.getHeight() / 2 + PD_Utility.dp2px(getActivity(), 13);

        searchView.clearApViewButOne(v);
        searchView.hideBackground();
        v.animate().translationX(mLeft).translationY(mTop).
                setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                shareCircle.setVisibility(View.VISIBLE);
                status.setText("connecting...");
                circleProgress.startScaleAnim(0);
                circleProgress.startAnim(800);
            }
        }, 300);
        searchView.setTextColor(v, Color.BLACK);
    }

    private void startJoinAnim() {
        shareCircle.setVisibility(View.GONE);
        searchCircle.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).
                setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        searchCircle.setVisibility(View.VISIBLE);
        searchView.startSearchAnim(1000);
    }

    @UiThread
    @Override
    public void onWifiConnected(String ssid) {
        circleProgress.finishAnim();
        connectTimer = new Timer();
        Message.obtain(mHandler, PD_Constant.WiFiConnectSuccess, ssid).sendToTarget();
    }

    @Override
    public void fileItemClicked(Modal_ContentDetail detail, int position) {
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
                .setDismissOnClickBack(true)
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

    @UiThread
    @Override
    public void showFilesList(ArrayList<File_Model> contents, String parentId) {
        filesSent.clear();
        filesSentPosition.clear();
        if (rv_files.getVisibility() == View.GONE)
            rv_files.setVisibility(View.VISIBLE);
        if (rv_files_receiving.getVisibility() == View.VISIBLE)
            rv_files_receiving.setVisibility(View.GONE);
        if (fileListAdapter == null) {
            //initialize adapter
            fileListAdapter = new FileListAdapter(getActivity(), FragmentShareRecieve.this);
            rv_files.setHasFixedSize(true);
            rv_files.addItemDecoration(new ContentItemDecoration(PD_Constant.CONTENT, 10));
            rv_files.setAdapter(fileListAdapter);
            rv_files.scheduleLayoutAnimation();
            fileListAdapter.submitList(contents);
        } else {
            fileListAdapter.submitList(contents);
            rv_files.smoothScrollToPosition(0);
        }
    }

    @UiThread
    @Override
    public void showRecieving(ArrayList<Modal_ReceivingFilesThroughFTP> filesRecieving) {
        if (rv_files.getVisibility() == View.VISIBLE)
            rv_files.setVisibility(View.GONE);
        if (rv_files_receiving.getVisibility() == View.GONE)
            rv_files_receiving.setVisibility(View.VISIBLE);
        if (receivedFileListAdapter == null) {
            //initialize adapter
            receivedFileListAdapter = new ReceivedFileListAdapter(getActivity());
            rv_files_receiving.setHasFixedSize(true);
            rv_files_receiving.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rv_files_receiving.setAdapter(receivedFileListAdapter);
            receivedFileListAdapter.submitList(filesRecieving);
        } else {
            receivedFileListAdapter.submitList(filesRecieving);
        }
    }

    @Override
    public void hotspotStarted() {

    }

    public void setIPaddress() {
        if (WifiUtils.isWifiApEnabled()) {
            serverIPaddres = localIPaddress = "192.168.43.1";
        } else {
            localIPaddress = WifiUtils.getLocalIPAddress();
            serverIPaddres = WifiUtils.getServerIPAddress();
        }
        Log.d("IPAddress:::", "localIPaddress:" + localIPaddress + " serverIPaddres:" + serverIPaddres);
    }

    private boolean isValidated() {
        setIPaddress();
        String nullIP = "0.0.0.0";
        if (nullIP.equals(localIPaddress) || nullIP.equals(serverIPaddres)
                || localIPaddress == null || serverIPaddres == null) {
            return false;
        }
        return true;
    }

    @Override
    public void processMessage(IPMSGProtocol pMsg) {
        Message msg = Message.obtain();
        msg.what = pMsg.commandNo;
        msg.obj = pMsg;
        Toast.makeText(getActivity(), "message" + pMsg.commandNo + ":::" + pMsg, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void messageRecievedInShare(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_SHARE_PROGRESS)) {
                if (!filesSent.isEmpty()) {
                    File_Model model = filesSent.get(message.getDownloadId());
                    if (model != null) {
//                        model.setProgress((int) message.getProgress());
//                        fileListAdapter.notifyItemChanged(filesSentPosition.get(message.getDownloadId()), model);
                        dialog_tv.setText(message.getFile_name());
                        dialog_progressLayout.setCurProgress((int) message.getProgress());
                    }
                }
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_SHARE_COMPLETE)) {
                sharePresenter.startTimer();
                if (sending_builder != null)
                    sending_builder.dismiss();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.SHARE_BACK)) {
                sharePresenter.traverseFolderBackward();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_FTP_SERVER)) {
                disconnectFTP();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FTP_CLIENT_CONNECTED)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        share_title.setText("Files Receiving");
                        hideViewsandShowFolders();
                        Toast.makeText(getActivity(), "Client Connected", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Subscribe
    public void filesCurrentlyRecieving(File recievedFile) {
        if (recievedFile != null) {
            sharePresenter.showFilesRecieving(recievedFile);
        }
    }

    @UiThread
    @Override
    public void disconnectFTP() {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("PraDigi")
                .setMessage("Do you want to Disconnect?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WifiUtils.closeWifiAp();
                        getActivity().sendBroadcast(new Intent(FsService.ACTION_STOP_FTPSERVER));
                        circular_share_reveal.unReveal();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @UiThread
    @Override
    public void closeFTPJoin() {
        onUnRevealed();
    }

    @Override
    public void onRevealed() {

    }

    @UiThread
    @Override
    public void onUnRevealed() {
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, 0);
        bundle.putInt(PD_Constant.REVEALY, 0);
        PD_Utility.showFragment(getActivity(), new FragmentContent_(), R.id.main_frame,
                bundle, FragmentContent_.class.getSimpleName());
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
}
