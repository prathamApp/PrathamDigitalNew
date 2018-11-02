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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CircularProgress;
import com.pratham.prathamdigital.custom.SearchView;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.util.FragmentManagePermission;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;
import com.pratham.prathamdigital.util.WifiUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentShareRecieve extends FragmentManagePermission {

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

    private List<String> mList = new ArrayList<>();

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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.rl_share)
    public void setRl_share() {
        if (isPermissionsGranted(getActivity(),
                new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, PermissionUtils.Manifest_ACCESS_FINE_LOCATION})) {
            TransitionManager.beginDelayedTransition(root_share);
            ViewGroup.LayoutParams params = rl_share.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            rl_share.requestLayout();
            createHotspot();
        } else {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION,
                    PermissionUtils.Manifest_ACCESS_FINE_LOCATION}, locationPermissionResult);
        }
    }

    PermissionResult locationPermissionResult = new PermissionResult() {
        @Override
        public void permissionGranted() {
            setRl_share();
        }

        @Override
        public void permissionDenied() {

        }

        @Override
        public void permissionForeverDenied() {

        }
    };

    private void createHotspot() {
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
                startActivity(intent);
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
//                    mUDPListener = UDPMessageListener.getInstance(context);
//                    mUDPListener.addMsgListener(this);
//                    mUDPListener.connectUDPSocket();
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
            }
        }
    };

    @OnClick(R.id.rl_recieve)
    public void setRl_recieve() {
        TransitionManager.beginDelayedTransition(root_share);
        ViewGroup.LayoutParams params = rl_recieve.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        rl_recieve.requestLayout();
        connectHotspotAndRecieve();
    }

    private void connectHotspotAndRecieve() {
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
    }

    private void startJoinAnim() {
        shareCircle.setVisibility(View.GONE);
        searchCircle.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).
                setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        searchCircle.setVisibility(View.VISIBLE);
        searchView.startSearchAnim(1000);
    }
}
