package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.CircularProgress;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.ContentItemDecoration;
import com.pratham.prathamdigital.custom.SearchView;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.permissions.ResponsePermissionCallback;
import com.pratham.prathamdigital.custom.progress_layout.ProgressLayout;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.ftpSettings.FsService;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_ReceivingFilesThroughFTP;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent_;
import com.pratham.prathamdigital.util.ConnectionUtils;
import com.pratham.prathamdigital.util.FileUtils;
import com.pratham.prathamdigital.util.HotspotUtils;
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
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.content.Context.WIFI_SERVICE;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

@EFragment(R.layout.fragment_share_receive)
public class FragmentShareRecieve extends Fragment implements ContractShare.shareView,
        CircularRevelLayout.CallBacks, ZXingScannerView.ResultHandler {

    private static final String TAG = FragmentShareRecieve.class.getSimpleName();
    private static final int SDCARD_LOCATION_CHOOSER = 100;
    private static final int CREATE_HOTSPOT = 11;
    private static final int JOIN_HOTSPOT = 12;
    private static final int CREATE_HOTSPOT_ACCORDING_TO_ANDROID_VERSION = 13;
    private static final int RECIEVED_FROM_TETHERING_ACTIVITY = 14;
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
    @ViewById(R.id.rv_files)
    RecyclerView rv_files;
    @ViewById(R.id.rv_files_receiving)
    RecyclerView rv_files_receiving;
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
    TextView dialog_tv;
    ProgressLayout dialog_progressLayout;

    @Bean(SharePresenter.class)
    ContractShare.sharePresenter sharePresenter;

    private List<String> mList = new ArrayList<>();
    private boolean isHotspotEnabled = false;
    FileListAdapter fileListAdapter;
    ReceivedFileListAdapter receivedFileListAdapter;
    HashMap<String, File_Model> filesSent = new HashMap<>();
    HashMap<String, Integer> filesSentPosition = new HashMap<>();
    private int revealX;
    private int revealY;
    BlurPopupWindow sd_builder;
    BlurPopupWindow sending_builder;
    public ZXingScannerView startCameraScan;
    private HotspotUtils hotspotUtils;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
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
                                initCamera();
                            else {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                startActivityForResult(intent, JOIN_HOTSPOT);
                            }
                        } else
                            initCamera();
                    } else new LocationService(getActivity()).checkLocation();
                    break;
                case PD_Constant.ApCreateApSuccess:
                    String s = PD_Utility.getLocalHostName();
                    SpannableString ss = new SpannableString(s);
                    ss.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new RelativeSizeSpan(1.2f), 0, 4, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
                    status.setText(ss);
                    circleProgress.finishAnim();
                    createHotspotQrCode();
                    break;
                case PD_Constant.LOCATION_GRANTED:
                    animateHotspotCreation();
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
                case PD_Constant.WiFiConnectSuccess:
                    status.setText("connection succeeded...");
                    sharePresenter.connectFTP();
                    break;
                case CREATE_HOTSPOT_ACCORDING_TO_ANDROID_VERSION:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(getActivity())) {
                            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                                try {
                                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                    final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
                                    intent.setComponent(cn);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    isHotspotEnabled = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else
                                animateHotspotCreation();
                        } else {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                            startActivityForResult(intent, CREATE_HOTSPOT);
                        }
                    } else
                        animateHotspotCreation();
                    break;
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (startCameraScan != null)
            startCameraScan.resumeCameraPreview(FragmentShareRecieve.this);
    }

    public static boolean isSharingWiFi(final WifiManager manager) {
        try {
            final Method method = manager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(manager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //
    @Override
    public void onPause() {
        super.onPause();
        if (startCameraScan != null)
            startCameraScan.stopCamera();
    }

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
    public void onResume() {
        super.onResume();
        hotspotUtils = HotspotUtils.getInstance(getActivity(), mHandler);
        if (startCameraScan != null)
            startCameraScan.resumeCameraPreview(FragmentShareRecieve.this);
        if (isHotspotEnabled) {
            isHotspotEnabled = false;
            onActivityResult(RECIEVED_FROM_TETHERING_ACTIVITY, 0, null);
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

    @Click(R.id.rl_share)
    public void setRl_share() {
        if (PrathamApplication.wiseF.isWifiEnabled())
            PrathamApplication.wiseF.disableWifi();
        KotlinPermissions.with(getActivity())
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .onAccepted(new ResponsePermissionCallback() {
                    @Override
                    public void onResult(@NotNull List<String> permissionResult) {
                        if (new LocationService(getActivity()).checkLocationEnabled()) {
                            ArrayList<String> sdPath = FileUtils.getExtSdCardPaths(getActivity());
                            if (sdPath.size() > 0) {
                                if (FastSave.getInstance().getString(PD_Constant.SDCARD_URI, null) == null)
                                    mHandler.sendEmptyMessage(CREATE_HOTSPOT_ACCORDING_TO_ANDROID_VERSION);
                            } else
                                mHandler.sendEmptyMessage(CREATE_HOTSPOT_ACCORDING_TO_ANDROID_VERSION);
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
        startCreateAnim();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hotspotUtils.enableConfigured("pratham", null);
            }
        }, 800);
    }

    @Click(R.id.rl_recieve)
    public void setRl_recieve() {
        WifiUtils.closeWifiAp();
        if (!PrathamApplication.wiseF.isWifiEnabled()) PrathamApplication.wiseF.enableWifi();
        KotlinPermissions.with(getActivity())
                .permissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .onAccepted(new ResponsePermissionCallback() {
                    @Override
                    public void onResult(@NotNull List<String> permissionResult) {
                        mHandler.sendEmptyMessage(ANIMATE_RECIEVE_AND_INIT_CAMERA);
                    }
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
                //create Hotspot
                mHandler.sendEmptyMessage(CREATE_HOTSPOT_ACCORDING_TO_ANDROID_VERSION);
            }
        } else if (requestCode == CREATE_HOTSPOT) {
            mHandler.sendEmptyMessage(CREATE_HOTSPOT_ACCORDING_TO_ANDROID_VERSION);
        } else if (requestCode == JOIN_HOTSPOT) {
            setRl_recieve();
        } else if (requestCode == RECIEVED_FROM_TETHERING_ACTIVITY) {
            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
            if (isSharingWiFi(wifiManager)) {
                WifiConfiguration wifiConfiguration = hotspotUtils.getConfiguration();
                PD_Constant.HOTSPOT_SSID = wifiConfiguration.SSID;
                PD_Constant.HOTSPOT_PASSWORD = wifiConfiguration.preSharedKey;
                PD_Constant.FTP_HOTSPOT_KEYMGMT = HotspotUtils.getAllowedKeyManagement(wifiConfiguration);
                createHotspotQrCode();
            }
        }
    }

    public void startCreateAnim() {
        searchCircle.setVisibility(View.GONE);
        shareCircle.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).
                setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
        shareCircle.setVisibility(View.VISIBLE);
        circleProgress.startCircleAnim(1000);
        circleProgress.startAnim(2000);
    }

    @UiThread
    public void createHotspot() {
    }

    @Override
    public void ftpConnected_showFolders() {
        if (connecting_progress.getVisibility() == View.VISIBLE)
            connecting_progress.setVisibility(View.GONE);
        if (rl_scan_qr.getVisibility() == View.VISIBLE)
            rl_scan_qr.setVisibility(View.GONE);
        share_title.setTextColor(BLACK);
        share_title.setText("Share Content");
        hideViewsandShowFolders();
        sharePresenter.showFolders(null);
    }

    @Override
    public void ftpConnectionFailed() {
        shareCircle.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        rl_hotspot_qr.setVisibility(View.GONE);
        rl_scan_qr.setVisibility(View.GONE);
        root_share.setVisibility(View.VISIBLE);
        rl_recieve.setVisibility(View.VISIBLE);
        TransitionManager.beginDelayedTransition(root_share);
        ViewGroup.LayoutParams params = rl_recieve.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = 0;
        rl_share.setVisibility(View.VISIBLE);
        rl_recieve.requestLayout();
        rl_recieve.setClickable(true);
    }

    //
    private void createHotspotQrCode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                PrathamApplication.getInstance().registerFtpReceiver();
            }
            startServer();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(PD_Constant.FTP_HOTSPOT_SSID, PD_Constant.HOTSPOT_SSID);
            jsonObject.put(PD_Constant.FTP_HOTSPOT_PASS, PD_Constant.HOTSPOT_PASSWORD);
            jsonObject.put(PD_Constant.FTP_KEYMGMT, PD_Constant.FTP_HOTSPOT_KEYMGMT);
            MultiFormatWriter formatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = formatWriter.encode(jsonObject.toString(), BarcodeFormat.QR_CODE, 400, 400);
            Bitmap bitmap = createBitmap(bitMatrix);
            hideViewsandShowFolders();
            rl_hotspot_qr.setVisibility(View.VISIBLE);
            rl_hotspot_qr.setClickable(true);
            img_hotspot_qr.setImageBitmap(bitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    private void startServer() {
        Intent intent = new Intent(FsService.ACTION_START_FTPSERVER);
        intent.setPackage(getActivity().getPackageName());
        getActivity().sendBroadcast(intent);
    }

    public Bitmap createBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private void hideViewsandShowFolders() {
        root_share.setVisibility(View.GONE);
        root_share.setClickable(false);
        shareCircle.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
//        rl_recieve_block.setVisibility(View.GONE);
        rl_hotspot_qr.setVisibility(View.GONE);
        rl_scan_qr.setVisibility(View.GONE);
    }

    private void connectHotspotAndRecieve() {
        rl_recieve_block.setVisibility(View.GONE);
        startJoinAnim();
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
/*
        startAvatarClickAnim(v);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScanResult wifi = (ScanResult) v.getTag();
                connectAp(wifi.SSID);
            }
        }, 1500);
     */
        initCamera();
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
                startCameraScan.setResultHandler(FragmentShareRecieve.this);
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

    /*private void startAvatarClickAnim(final View v) {
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
        searchView.setTextColor(v, BLACK);
    }*/

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
//        if (circleProgress != null)
//            circleProgress.finishAnim();
//        connectTimer = new Timer();
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

    @UiThread
    @Override
    public void showFilesList(ArrayList<File_Model> contents, String parentId) {
        filesSent.clear();
        filesSentPosition.clear();
        if (rv_files.getVisibility() == View.GONE)
            rv_files.setVisibility(View.VISIBLE);
//        if (rv_files_receiving.getVisibility() == View.VISIBLE)
//            rv_files_receiving.setVisibility(View.GONE);
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

    @Subscribe
    public void messageRecievedInShare(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_SHARE_PROGRESS)) {
//                if (!filesSent.isEmpty()) {
//                    File_Model model = filesSent.get(message.getDownloadId());
//                    if (model != null) {
//                        model.setProgress((int) message.getProgress());
//                        fileListAdapter.notifyItemChanged(filesSentPosition.get(message.getDownloadId()), model);
                dialog_tv.setText(message.getFile_name());
                dialog_progressLayout.setCurProgress((int) message.getProgress());
//                    }
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_SHARE_COMPLETE)) {
                sharePresenter.startTimer();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sending_builder != null)
                            sending_builder.dismiss();
                    }
                }, 4000);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_RECEIVE_COMPLETE)) {
                List<Modal_ReceivingFilesThroughFTP> list = receivedFileListAdapter.getList();
                Modal_ReceivingFilesThroughFTP ftpItem = null;
                int pos = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getGameName().contains(message.getFile_name())) {
                        list.get(i).setReceived(true);
                        ftpItem = list.get(i);
                        pos = i;
                    }
                }
                if (ftpItem != null)
                    receivedFileListAdapter.notifyItemChanged(pos, ftpItem);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.SHARE_BACK)) {
                sharePresenter.traverseFolderBackward();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_FTP_SERVER)) {
                disconnectFTP();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FTP_CLIENT_CONNECTED)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        share_title.setTextColor(BLACK);
                        share_title.setText("Receiving...");
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
}
