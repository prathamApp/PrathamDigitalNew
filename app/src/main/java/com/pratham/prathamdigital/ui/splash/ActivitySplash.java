package com.pratham.prathamdigital.ui.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.video_player.CustomExoPlayerView;
import com.pratham.prathamdigital.custom.video_player.ExoPlayerCallBack;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.services.PrathamSmartSync;
import com.pratham.prathamdigital.services.PrathamSmartSyncNew;
import com.pratham.prathamdigital.ui.attendance_activity.AttendanceActivity_;
import com.pratham.prathamdigital.ui.fragment_enrollmentid.Fragment_Enrollmentid;
import com.pratham.prathamdigital.ui.fragment_enrollmentid.Fragment_Enrollmentid_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

//import com.google.android.gms.auth.api.Auth;

@EActivity(R.layout.splash_activity)
public class ActivitySplash extends BaseActivity implements SplashContract.splashview {

    private static final String TAG = ActivitySplash.class.getSimpleName();
    private static final int LIGHT_ANIMATION = 2;
    private static final int REDIRECT_TO_DASHBOARD = 4;
    private static final int REDIRECT_TO_AVATAR = 5;
    @ViewById(R.id.splash_video)
    CustomExoPlayerView splash_video;
    @ViewById(R.id.btn_createProfile)
    Button btn_createProfile;
    @ViewById(R.id.btn_enterEnrollID)
    Button btn_enterEnrollID;

    @Bean(SplashPresenterImpl.class)
    SplashContract.splashPresenter splashPresenter;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private BlurPopupWindow dialog_code;
    private BlurPopupWindow dialog_permission;
    private EditText et_qr_code;
    private String noti_key = null;
    private String noti_value = null;
    private boolean playerLoaded = false;

    private final TextWatcher codeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (et_qr_code.getText().toString().length() > 0) {
                et_qr_code.setError(null);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private final Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LIGHT_ANIMATION:
                    loadVideo();
                    break;
                case REDIRECT_TO_DASHBOARD:
                    Intent intent = new Intent(ActivitySplash.this, AttendanceActivity_.class);
                    if (getIntent().getBooleanExtra(PD_Constant.DEEP_LINK, false)) {
                        intent.putExtra(PD_Constant.DEEP_LINK, true);
                        intent.putExtra(PD_Constant.DEEP_LINK_CONTENT, getIntent().getStringExtra(PD_Constant.DEEP_LINK_CONTENT));
                    }
                    if (noti_key != null && noti_value != null) {
                        intent.putExtra(PD_Constant.PUSH_NOTI_KEY, noti_key);
                        intent.putExtra(PD_Constant.PUSH_NOTI_VALUE, noti_value);
                    }
                    intent.putExtra(PD_Constant.STUDENT_ADDED, true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    finishAfterTransition();
                    break;
                case REDIRECT_TO_AVATAR:
                    Intent intent2 = new Intent(ActivitySplash.this, AttendanceActivity_.class);
                    if (getIntent().getBooleanExtra(PD_Constant.DEEP_LINK, false)) {
                        intent2.putExtra(PD_Constant.DEEP_LINK, true);
                        intent2.putExtra(PD_Constant.DEEP_LINK_CONTENT, getIntent().getStringExtra(PD_Constant.DEEP_LINK_CONTENT));
                    }
                    if (noti_key != null && noti_value != null) {
                        intent2.putExtra(PD_Constant.PUSH_NOTI_KEY, noti_key);
                        intent2.putExtra(PD_Constant.PUSH_NOTI_VALUE, noti_value);
                    }
                    intent2.putExtra(PD_Constant.STUDENT_ADDED, false);
                    startActivity(intent2);
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    finishAfterTransition();
                    break;
            }
        }
    };

    @Click(R.id.btn_createProfile)
    public void createProfile(){
        //getExternalStoragePermission();
        //Above method is used to allow external storage permission on android 11 and above.
        //But playstore doesnot support this.
        splashPresenter.checkPrathamCode();
        //ended = true;
    }

    @Click(R.id.btn_enterEnrollID)
    public void setBtn_enterEnrollID(){
        //getExternalStoragePermission();
        checkPermissionss();
        PD_Utility.showFragment(ActivitySplash.this, new Fragment_Enrollmentid_(), R.id.splash_frame,
                null, Fragment_Enrollmentid.class.getSimpleName());
    }

    private void loadVideo() {
        splash_video.setSourceFromRawFolder(R.raw.pratham);
        splash_video.setExoPlayerCallBack(new ExoPlayerCallBack() {
            @Override
            public void onError() {
                //nothing here
            }

            @Override
            public void onStart() {
                PrathamSmartSyncNew.pushUsageToServer(false,PD_Constant.AUTO_PUSH, ActivitySplash.this); //comment while dev
                playerLoaded = true;
                //Read intent till the video completes
                if (getIntent().getExtras() != null)
                    for (String key : getIntent().getExtras().keySet())
                        if (key.equalsIgnoreCase("key"))//the key received from notification data
                            noti_key = getIntent().getExtras().getString(key);
                        else if (key.equalsIgnoreCase("value"))//the value received from notification data
                            noti_value = getIntent().getExtras().getString(key);
            }

            @Override
            public void onEnded() {
                btn_enterEnrollID.setVisibility(View.VISIBLE);
                btn_createProfile.setVisibility(View.VISIBLE);
            }
        });
    }

/*    public void getExternalStoragePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                loadSplash();
            } else { //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent,46);
            }
        } else {
            loadSplash();
        }
    }*/
    @Override
    protected void onPause() {
        super.onPause();
        splash_video.pausePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerLoaded && !splash_video.getPlayer().isPlaying())
            splash_video.setPlayWhenReady(true);
    }

    @AfterViews
    public void initializeViews() {
        mContext = this;
        splashPresenter.clearPreviousBuildData();
        splashPresenter.populateDefaultDB();
        mhandler.sendEmptyMessage(LIGHT_ANIMATION);
    }

    @Override
    public void loadSplash() {
        if (PrathamApplication.useSatelliteGPS)
            splashPresenter.startGpsTimer();
        else
            splashPresenter.checkIfContentinSDCard();
    }

    @UiThread
    @Override
    public void redirectToDashboard() {
        mhandler.sendEmptyMessage(REDIRECT_TO_DASHBOARD);
    }

    @UiThread
    @Override
    public void redirectToAvatar() {
        mhandler.sendEmptyMessage(REDIRECT_TO_AVATAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Subscribe
    public void onMessageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.LOCATION_CHANGED))
                splashPresenter.onLocationChanged(message.getLocation());
            if (message.getMessage().equalsIgnoreCase(PD_Constant.PERMISSIONS_GRANTED))
                splashPresenter.checkStudentList();
            if (message.getMessage().equalsIgnoreCase(PD_Constant.NOTIFICATION_RECIEVED)) {
                Toast.makeText(mContext, "message recieved", Toast.LENGTH_SHORT).show();
                Bundle bundle = message.getBundle();
                if (bundle.containsKey(PD_Constant.PUSH_NOTI_KEY) && bundle.containsKey(PD_Constant.PUSH_NOTI_VALUE)) {
                    Toast.makeText(mContext, "values assigned", Toast.LENGTH_SHORT).show();
                    noti_key = bundle.getString(PD_Constant.PUSH_NOTI_KEY);
                    noti_value = bundle.getString(PD_Constant.PUSH_NOTI_VALUE);
                }
            }
        }
    }

    @Override
    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || /*ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || */ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            dialog_permission = new BlurPopupWindow.Builder(ActivitySplash.this)
                    .setContentView(R.layout.permission_detail_dialog)
                    .bindClickListener(v -> {
                        EventMessage message = new EventMessage();
                        message.setMessage(PD_Constant.CHECK_PERMISSIONS);
                        EventBus.getDefault().post(message);
                        dialog_permission.dismiss();
                    }, R.id.btn_perm_okay)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnClickBack(false)
                    .setDismissOnTouchBackground(false)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(8)
                    .setTintColor(0x30000000)
                    .build();
            dialog_permission.show();
        } else {
            splashPresenter.checkStudentList();
        }
    }

    //this method created to grant permission on enrollment id click
    public void checkPermissionss() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || /*ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || */ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            dialog_permission = new BlurPopupWindow.Builder(ActivitySplash.this)
                    .setContentView(R.layout.permission_detail_dialog)
                    .bindClickListener(v -> {
                        EventMessage message = new EventMessage();
                        message.setMessage(PD_Constant.CHECK_PERMISSIONS);
                        EventBus.getDefault().post(message);
                        dialog_permission.dismiss();
                    }, R.id.btn_perm_okay)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnClickBack(false)
                    .setDismissOnTouchBackground(false)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(8)
                    .setTintColor(0x30000000)
                    .build();
            dialog_permission.show();
        }
    }
}