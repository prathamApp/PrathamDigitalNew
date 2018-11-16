package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CircularProgress;
import com.pratham.prathamdigital.custom.SearchView;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.socket.entity.Users;
import com.pratham.prathamdigital.socket.udp.IPMSGConst;
import com.pratham.prathamdigital.socket.udp.IPMSGProtocol;
import com.pratham.prathamdigital.socket.udp.UDPMessageListener;
import com.pratham.prathamdigital.util.FragmentManagePermission;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;
import com.pratham.prathamdigital.util.WifiUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.prathamdigital.util.PD_Utility.getPhoneModel;

public class FragmentShareRecieve extends FragmentManagePermission implements ContractShare.shareView, UDPMessageListener.OnNewMsgListener {

    private static final String TAG = FragmentShareRecieve.class.getSimpleName();
    @BindView(R.id.root_share)
    LinearLayout root_share;
    @BindView(R.id.rl_share)
    RelativeLayout rl_share;
    @BindView(R.id.rl_recieve)
    RelativeLayout rl_recieve;
    @BindView(R.id.searchCircle)
    RelativeLayout searchCircle;
    @BindView(R.id.shareCircle)
    RelativeLayout shareCircle;
    @BindView(R.id.circleProgress)
    CircularProgress circleProgress;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.rl_share_block)
    RelativeLayout rl_share_block;
    @BindView(R.id.rl_recieve_block)
    RelativeLayout rl_recieve_block;
    @BindView(R.id.rv_files)
    RecyclerView rv_files;

    private List<String> mList = new ArrayList<>();
    private UDPMessageListener mUDPListener;
    private boolean share = false;
    private Timer connectTimer;
    private String localIPaddress;
    private String serverIPaddres;
    SharePresenter sharePresenter;
    FileListAdapter fileListAdapter;
    private File selectedFile = null;
    private String currentFilePath = "";

    public static FragmentShareRecieve newInstance(int centerX, int centerY, int color) {
        Bundle args = new Bundle();
        args.putInt("cx", centerX);
        args.putInt("cy", centerY);
        args.putInt("color", color);
        FragmentShareRecieve fragment = new FragmentShareRecieve();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share, container, false);
        if (getArguments() != null) {
            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                           int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    int cx = getArguments().getInt("cx");
                    int cy = getArguments().getInt("cy");
                    int radius = (int) Math.hypot(right, bottom);
                    Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                    reveal.setInterpolator(new DecelerateInterpolator(2f));
                    reveal.setDuration(1000);
                    reveal.start();
                }
            });
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        sharePresenter = new SharePresenter(getActivity(), this);
        currentFilePath = PrathamApplication.pradigiPath;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.rl_share)
    public void setRl_share() {
        share = true;
        if (isPermissionsGranted(getActivity(),
                new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_ACCESS_FINE_LOCATION})) {
            if (new LocationService(getActivity()).checkLocationEnabled()) {
                TransitionManager.beginDelayedTransition(root_share);
                ViewGroup.LayoutParams params = rl_share.getLayoutParams();
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                rl_share.requestLayout();
                rl_recieve.setVisibility(View.GONE);
                createHotspot();
            } else {
                new LocationService(getActivity()).checkLocation();
            }
        } else {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                    PermissionUtils.Manifest_ACCESS_FINE_LOCATION}, locationPermissionResult);
        }
    }

    @OnClick(R.id.rl_recieve)
    public void setRl_recieve() {
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

    private void createHotspot() {
        rl_share_block.setVisibility(View.GONE);
        startCreateAnim();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createAP();
            }
        }, 2000);
    }

    private void startCreateAnim() {
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
//                    hideViewsandShowFolders();
                    mUDPListener = UDPMessageListener.getInstance(getActivity());
                    mUDPListener.addMsgListener(FragmentShareRecieve.this::processMessage);
                    mUDPListener.connectUDPSocket();
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
                        mUDPListener = UDPMessageListener.getInstance(getActivity());
                        mUDPListener.addMsgListener(FragmentShareRecieve.this::processMessage);
                        mUDPListener.connectUDPSocket();
                        IPMSGProtocol cmd = new IPMSGProtocol();
                        cmd.senderIP = localIPaddress;
                        cmd.targetIP = serverIPaddres;
                        cmd.commandNo = IPMSGConst.NO_CONNECT_SUCCESS;
                        cmd.packetNo = new Date().getTime() + "";
                        mUDPListener.sendUDPdata(cmd);
//                        sharePresenter.registerListener(FragmentShareRecieve.this);
                    }
                    break;
            }
        }
    };

    private void hideViewsandShowFolders() {
        shareCircle.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        rl_recieve_block.setVisibility(View.GONE);
        sharePresenter.showFolders(currentFilePath);
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
//            final boolean connFlag = WifiUtils.connectWifi(hostName, PD_Constant.WIFI_AP_PASSWORD,
//                    WifiUtils.WifiCipherType.WIFICIPHER_WPA);
            sharePresenter.connectToWify(hostName);
//            if (connFlag) {
//            }
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

    @Override
    public void onWifiConnected(String ssid) {
        circleProgress.finishAnim();
        connectTimer = new Timer();
//        connectTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
        Message.obtain(mHandler, PD_Constant.WiFiConnectSuccess, ssid).sendToTarget();
//            }
//        }, new Date(), 1000);
    }

    @Override
    public void fileItemClicked(File file, int position) {
        selectedFile = file;
        sharePresenter.showFolders(file.getAbsolutePath());
    }

    @Override
    public void sendItemClicked(File file, int position) {
//        ChatEntity chatMsg = new ChatEntity();
//        chatMsg.setContent(file.getAbsolutePath());
//        chatMsg.setIsSend(true);
//        chatMsg.setType(com.pratham.prathamdigital.socket.entity.Message.CONTENT_TYPE.TEXT);
//        chatMsg.setTime(System.currentTimeMillis());
        sharePresenter.sendMessage(file.getAbsolutePath(), com.pratham.prathamdigital.socket.entity.Message.CONTENT_TYPE.TEXT, localIPaddress, serverIPaddres);
    }

    @Override
    public void showFilesList(List<File> files) {
        if (rv_files.getVisibility() == View.GONE)
            rv_files.setVisibility(View.VISIBLE);
        if (fileListAdapter == null) {
            //initialize adapter
            fileListAdapter = new FileListAdapter(getActivity(), files, FragmentShareRecieve.this);
            rv_files.setHasFixedSize(true);
            rv_files.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rv_files.setAdapter(fileListAdapter);
        } else {
            fileListAdapter.updateList(files);
        }
    }

    @Override
    public void hotspotStarted() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
//            createAP();
        }
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

    @Subscribe
    public void onSettingsBackPressed(final String pressed) {
        Log.d(TAG, "onSettingsBackPressed:");
        if (selectedFile != null) {
            if (selectedFile.getAbsolutePath().equalsIgnoreCase(currentFilePath)) {
                selectedFile = null;
                sharePresenter.showFolders(currentFilePath);
            } else {
                selectedFile = selectedFile.getParentFile();
                sharePresenter.showFolders(selectedFile.getAbsolutePath());
            }
        }
    }

    @Override
    public void processMessage(IPMSGProtocol pMsg) {
        Message msg = Message.obtain();
        msg.what = pMsg.commandNo;
        msg.obj = pMsg;
        Toast.makeText(getActivity(), "message" + pMsg.commandNo + ":::" + pMsg, Toast.LENGTH_SHORT).show();
    }
}
