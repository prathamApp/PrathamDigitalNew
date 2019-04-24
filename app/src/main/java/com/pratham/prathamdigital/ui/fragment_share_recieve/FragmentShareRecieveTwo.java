package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.CircularProgress;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.SearchView;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.ftpSettings.FsService;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_ReceivingFilesThroughFTP;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent_;
import com.pratham.prathamdigital.ui.fragment_receive.FragmentReceive;
import com.pratham.prathamdigital.ui.fragment_receive.FragmentReceive_;
import com.pratham.prathamdigital.ui.fragment_share.FragmentShare;
import com.pratham.prathamdigital.ui.fragment_share.FragmentShare_;
import com.pratham.prathamdigital.util.FileUtils;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.WifiUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EFragment(R.layout.fragment_share_receive)
public class FragmentShareRecieveTwo extends Fragment implements ContractShare.shareView,
        CircularRevelLayout.CallBacks {

    private static final String TAG = FragmentShareRecieveTwo.class.getSimpleName();
    private static final int SDCARD_LOCATION_CHOOSER = 100;
    private static final int CREATE_HOTSPOT = 11;
    private static final int JOIN_HOTSPOT = 12;
    private static final int ANIMATE_RECIEVE_AND_INIT_CAMERA = 15;
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
    @ViewById(R.id.circular_share_reveal)
    CircularRevelLayout circular_share_reveal;
    @ViewById(R.id.share_title)
    TextView share_title;
    @ViewById(R.id.rl_hotspot_qr)
    LinearLayout rl_hotspot_qr;
    @ViewById(R.id.img_hotspot_qr)
    ImageView img_hotspot_qr;
    @ViewById(R.id.txt_scan_qr_)
    TextView txt_scan_qr_;
    @ViewById(R.id.connecting_progress)
    LinearLayout connecting_progress;
    @ViewById(R.id.qr_frame)
    ViewGroup qr_frame;
    @ViewById(R.id.rl_scan_qr)
    RelativeLayout rl_scan_qr;

    @Bean(SharePresenter.class)
    ContractShare.sharePresenter sharePresenter;
    private final List<String> mList = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ANIMATE_RECIEVE_AND_INIT_CAMERA:
                    if (new LocationService(getActivity()).checkLocationEnabled()) {
                        TransitionManager.beginDelayedTransition(root_share);
                        ViewGroup.LayoutParams params = rl_recieve.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        rl_recieve.requestLayout();
                        rl_share.setVisibility(View.GONE);
                        rl_recieve.setClickable(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (Settings.System.canWrite(getActivity()))
                                PD_Utility.addFragment(getActivity(), new FragmentShare_(), R.id.main_frame,
                                        null, FragmentShare.class.getSimpleName());
                            else {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + Objects.requireNonNull(getActivity()).getPackageName()));
                                startActivityForResult(intent, JOIN_HOTSPOT);
                            }
                        } else
                            PD_Utility.addFragment(getActivity(), new FragmentShare_(), R.id.main_frame,
                                    null, FragmentShare.class.getSimpleName());
                    } else new LocationService(getActivity()).checkLocation();
                    break;
                case PD_Constant.ApScanResult:
                    for (ScanResult wifi : WifiUtils.getScanResults()) {
                        String ssid = wifi.SSID;
                        if (ssid.startsWith(/*PD_Constant.WIFI_AP_HEADER*/"pratham")) {
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
    private int revealX;
    private int revealY;
    private BlurPopupWindow sd_builder;

    @AfterViews
    public void initialize() {
        sharePresenter.setView(FragmentShareRecieveTwo.this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharePresenter.viewDestroyed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharePresenter.viewDestroyed();
    }

    @UiThread
    public void showSdCardDialog() {
        sd_builder = new BlurPopupWindow.Builder(getContext())
                .setContentView(R.layout.dialog_alert_sd_card)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(intent, SDCARD_LOCATION_CHOOSER);
                    }, 1500);
                    sd_builder.dismiss();
                }, R.id.txt_choose_sd_card)
                .setDismissOnClickBack(true)
                .setDismissOnTouchBackground(true)
                .setScaleRatio(0.2f)
                .setBlurRadius(8)
                .setTintColor(0x30000000)
                .build();
        sd_builder.show();
    }

    @Click(R.id.rl_share)
    public void setRl_share() {
        if (PrathamApplication.wiseF.isWifiEnabled())
            PrathamApplication.wiseF.disableWifi();
        KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .onAccepted(permissionResult -> {
                    ArrayList<String> sdPath = FileUtils.getExtSdCardPaths(getActivity());
                    if (sdPath.size() > 0) {
                        if (FastSave.getInstance().getString(PD_Constant.SDCARD_URI, null) == null)
                            showSdCardDialog();
                        else if (new LocationService(getActivity()).checkLocationEnabled()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Settings.System.canWrite(getActivity())) {
                                    animateHotspotCreation();
                                } else {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                    startActivityForResult(intent, CREATE_HOTSPOT);
                                }
                            }
                        } else
                            new LocationService(getActivity()).checkLocation();
                    }
                })
                .ask();
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
        rl_share_block.setVisibility(View.GONE);
        mHandler.postDelayed(() -> PD_Utility.addFragment(getActivity(), new FragmentReceive_(), R.id.main_frame
                , null, FragmentReceive.class.getSimpleName()), 800);
    }

    @Click(R.id.rl_recieve)
    public void setRl_recieve() {
        WifiUtils.closeWifiAp();
        if (!PrathamApplication.wiseF.isWifiEnabled()) PrathamApplication.wiseF.enableWifi();
        KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                .permissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .onAccepted(permissionResult -> {
                    mHandler.sendEmptyMessage(ANIMATE_RECIEVE_AND_INIT_CAMERA);
                })
                .ask();
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
            }
        } else if (requestCode == CREATE_HOTSPOT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(getActivity()))
                    animateHotspotCreation();
                else
                    Toast.makeText(getActivity(), "Hotspot Permission Is required", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == JOIN_HOTSPOT) {
            setRl_recieve();
        }
    }

    @Override
    public void ftpConnected_showFolders() {
    }

    @Override
    public void ftpConnectionFailed() {
    }

    @UiThread
    @Override
    public void onWifiConnected(String ssid) {
        Message.obtain(mHandler, PD_Constant.WiFiConnectSuccess, ssid).sendToTarget();
    }

    @Override
    public void fileItemClicked(Modal_ContentDetail detail, int position) {
    }

    @Override
    public void sendItemChecked(File_Model model, int position) {
    }

    @UiThread
    @Override
    public void showFilesList(ArrayList<File_Model> contents, String parentId) {
    }

    @UiThread
    @Override
    public void showRecieving(ArrayList<Modal_ReceivingFilesThroughFTP> filesRecieving) {
    }

    @Override
    public void hotspotStarted() {

    }

    @Subscribe
    public void messageRecievedInShare(EventMessage message) {
        if (message != null)
            if (message.getMessage().equalsIgnoreCase(PD_Constant.SHARE_BACK))
                sharePresenter.traverseFolderBackward();
            else if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_FTP_SERVER))
                disconnectFTP();
    }

    @UiThread
    @Override
    public void disconnectFTP() {
        new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("PraDigi")
                .setMessage("Do you want to Disconnect?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    WifiUtils.closeWifiAp();
                    getActivity().sendBroadcast(new Intent(FsService.ACTION_STOP_FTPSERVER));
                    circular_share_reveal.unReveal();
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
        Fragment fragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
}
