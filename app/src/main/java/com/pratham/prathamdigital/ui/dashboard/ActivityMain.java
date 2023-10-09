package com.pratham.prathamdigital.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.BuildConfig;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.NotificationBadge;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.custom.spotlight.SpotlightView;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.ftpSettings.FsService;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Modal_NavigationMenu;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Model_CheckSyncAPI;
import com.pratham.prathamdigital.services.PrathamSmartSync;
import com.pratham.prathamdigital.services.PrathamSmartSyncNew;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar_;
import com.pratham.prathamdigital.ui.connect_dialog.ConnectDialog;
import com.pratham.prathamdigital.ui.content_player.Activity_ContentPlayer_;
import com.pratham.prathamdigital.ui.download_list.DownloadListFragment;
import com.pratham.prathamdigital.ui.download_list.DownloadListFragment_;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent_;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.Fragment_CourseEnrollment;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.Fragment_CourseEnrollment_;
import com.pratham.prathamdigital.ui.fragment_language.FragmentLanguage;
import com.pratham.prathamdigital.ui.fragment_language.FragmentLanguage_;
import com.pratham.prathamdigital.ui.fragment_profile.Profile_Fragment;
import com.pratham.prathamdigital.ui.fragment_profile.Profile_Fragment_;
import com.pratham.prathamdigital.ui.fragment_receive.FragmentReceive_;
import com.pratham.prathamdigital.ui.fragment_share.FragmentShare_;
import com.pratham.prathamdigital.ui.fragment_share_recieve.FragmentShareRecieve;
import com.pratham.prathamdigital.ui.fragment_share_recieve.FragmentShareRecieve_;
import com.pratham.prathamdigital.ui.show_sync_log.FragmentSyncLog;
import com.pratham.prathamdigital.ui.show_sync_log.FragmentSyncLog_;
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
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.logDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;
import static com.pratham.prathamdigital.async.PD_ApiRequest.downloadAajKaSawal;
import static com.pratham.prathamdigital.dbclasses.PrathamDatabase.DB_NAME;

@EActivity(R.layout.main_activity)
public class ActivityMain extends BaseActivity implements ContentContract.mainView, ContractMenu,
        SlidingPaneLayout.PanelSlideListener, ApiResult {

    private static final String TAG = ActivityMain.class.getSimpleName();
    private static final int INITILIZE_DRAWER = 1;
    private static final int MENU_LANGUAGE = 2;
    private static final int MENU_CONNECT_WIFI = 3;
    private static final int MENU_SHARE = 4;
    private static final int MENU_SHARE_APP = 5;
    private static final int MENU_EXIT = 6;
    private static final int MENU_HOME = 7;
    private static final int SHOW_MENU = 8;
    private static final int CHECK_AAJ_KA_SAWAL = 11;
    private static final int MENU_COURSES = 12;
    private static final int SHOW_YOU_TUBE_VIDEO = 13;
    private static final int SHOW_PROFILE = 14;
    private static final int MENU_SYNC = 15;
    private static final int MENU_SYNCDB = 16;
    private static final int MENU_SYNCLOG = 17;
    private static final int MENU_DASHBOARD = 18;

    @ViewById(R.id.download_notification)
    NotificationBadge download_notification;
    @ViewById(R.id.download_badge)
    RelativeLayout download_badge;
    @ViewById(R.id.outer_area)
    View outer_area;
    @ViewById(R.id.main_hamburger)
    ImageView main_hamburger;
    @ViewById(R.id.main_sliding_drawer)
    SlidingPaneLayout main_sliding_drawer;
    @ViewById(R.id.drawer_profile_lottie)
    LottieAnimationView drawer_profile_lottie;
    @ViewById(R.id.drawer_profile_name)
    TextView drawer_profile_name;
    @ViewById(R.id.rv_drawer)
    RecyclerView rv_drawer;
    @ViewById(R.id.main_nav)
    RelativeLayout main_nav;
    @ViewById(R.id.versionNum)
    TextView versionNum;

    @Bean(PD_ApiRequest.class)
    PD_ApiRequest pd_apiRequest;

    private boolean isChecked;
    private BlurPopupWindow exitDialog;
    private BlurPopupWindow syncDataDialog;
    private DownloadListFragment_ downloadListFragment_;
    private String noti_key;
    private String noti_value;
    Modal_Log log;

    private BlurPopupWindow pushDialog;
    private LottieAnimationView push_lottie;
    private TextView txt_push_dialog_msg;
    private TextView txt_push_error;
    private TextView tv_courseCount;
    private TextView tv_scoreCount;
    private Button btn_done, btn_okay;

    public String courseCount;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_MENU:
                    Bundle bundle = new Bundle();
                    if (getIntent().getBooleanExtra(PD_Constant.DEEP_LINK, false)) {
                        bundle.putBoolean(PD_Constant.DEEP_LINK, true);
                        bundle.putString(PD_Constant.DEEP_LINK_CONTENT, getIntent().getStringExtra(PD_Constant.DEEP_LINK_CONTENT));
                    } else {
                        new Handler().postDelayed(() -> showIntro(), 2000);
                    }
                    bundle.putInt(PD_Constant.REVEALX, 0);
                    bundle.putInt(PD_Constant.REVEALY, 0);
                    PD_Utility.showFragment(ActivityMain.this, new FragmentContent_(), R.id.main_frame,
                            bundle, FragmentContent_.class.getSimpleName());
                    break;
                case INITILIZE_DRAWER:
                    initializeDrawer();
                    break;
                case MENU_HOME:
                    if (isChecked)
                        toggleToArrow();
                    Bundle homebundle = new Bundle();
                    homebundle.putInt(PD_Constant.REVEALX, 0);
                    homebundle.putInt(PD_Constant.REVEALY, 0);
                    PD_Utility.showFragment(ActivityMain.this, new FragmentContent_(), R.id.main_frame,
                            homebundle, FragmentContent_.class.getSimpleName());
                    break;
                case MENU_LANGUAGE:
                    if (isChecked)
                        toggleToArrow();
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt(PD_Constant.REVEALX, 0);
                    bundle2.putInt(PD_Constant.REVEALY, 0);
                    PD_Utility.showFragment(ActivityMain.this, new FragmentLanguage_(), R.id.main_frame,
                            bundle2, FragmentLanguage.class.getSimpleName());
                    break;
                case MENU_CONNECT_WIFI:
                    if (isChecked)
                        toggleToArrow();
                    ConnectDialog connectDialog = new ConnectDialog.Builder(ActivityMain.this, null).build();
                    connectDialog.isDismissOnTouchBackground();
                    connectDialog.isDismissOnClickBack();
                    connectDialog.setOnDismissListener(popupWindow -> {
                        Bundle bundle1 = new Bundle();
                        bundle1.putInt(PD_Constant.REVEALX, 0);
                        bundle1.putInt(PD_Constant.REVEALY, 0);
                        PD_Utility.showFragment(ActivityMain.this, new FragmentContent_(), R.id.main_frame,
                                bundle1, FragmentContent_.class.getSimpleName());
                    });
                    connectDialog.show();
                    break;
                case MENU_SHARE:
                    if (isChecked)
                        toggleToArrow();
                    Bundle bundle3 = new Bundle();
                    bundle3.putInt(PD_Constant.REVEALX, 0);
                    bundle3.putInt(PD_Constant.REVEALY, 0);
                    PD_Utility.showFragment(ActivityMain.this, new FragmentShareRecieve_(), R.id.main_frame,
                            bundle3, FragmentShareRecieve.class.getSimpleName());
                    break;

                case MENU_DASHBOARD:
                    String groupId = FastSave.getInstance().getString(PD_Constant.GROUPID_DASHBOARD, "");
                    groupId = groupId.split("_SmartPhone")[0];
                    //Toast.makeText(ActivityMain.this, groupId, Toast.LENGTH_SHORT).show();
                    String dasboardURL = "https://powerbi.pradigi.org/chhattisgarh?groupId=" + groupId;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dasboardURL));
                    startActivity(browserIntent);
                    break;

                case MENU_SHARE_APP:
                    KotlinPermissions.with(ActivityMain.this)
                            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                            .onAccepted(permissionResult -> {
                                try {
                                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                    PackageManager pm = PrathamApplication.getInstance().getPackageManager();
                                    ApplicationInfo ai = pm.getApplicationInfo(PrathamApplication.getInstance().getPackageName(), 0);
                                    File localFile = new File(ai.publicSourceDir);
                                    Uri uri = FileProvider.getUriForFile(ActivityMain.this,
                                            BuildConfig.APPLICATION_ID + ".provider", localFile);
                                    intentShareFile.setType("*/*");
                                    intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
                                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Please download apk from here...");
                                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.pratham.prathamdigital");
                                    intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(Intent.createChooser(intentShareFile, "Share through"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            })
                            .ask();
                    break;
                case MENU_EXIT:
                    exitApp();
                    break;
                case MENU_COURSES:
                    PD_Utility.showFragment(ActivityMain.this, new Fragment_CourseEnrollment_(), R.id.main_frame,
                            null, Fragment_CourseEnrollment.class.getSimpleName());
                    break;
                case CHECK_AAJ_KA_SAWAL:
                    String filename = "AajKaSawal_" + FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI) + ".json";
                    File aksFile = new File(PrathamApplication.pradigiPath + "/" + filename); //Creating an internal dir;
                    if (!aksFile.exists()) {
                        String aksUrl = PD_Constant.URL.AAJ_KA_SAWAL_URL.toString() + filename;
                        downloadAajKaSawal(aksUrl, filename);
                    }
                    break;
                case SHOW_YOU_TUBE_VIDEO:
                    Intent intent = new Intent(ActivityMain.this, Activity_ContentPlayer_.class);
                    intent.putExtra(PD_Constant.CONTENT_TYPE, noti_key);
                    intent.putExtra(PD_Constant.CONTENT, noti_value);
                    startActivity(intent);
                    overridePendingTransition(R.anim.shrink_enter, R.anim.nothing);
                    break;
                case SHOW_PROFILE:
                    if (isChecked)
                        toggleToArrow();
                    Bundle bundleProf = new Bundle();
                    bundleProf.putInt(PD_Constant.REVEALX, 0);
                    bundleProf.putInt(PD_Constant.REVEALY, 0);
                    PD_Utility.showFragment(ActivityMain.this, new Profile_Fragment_(), R.id.main_frame,
                            bundleProf, Profile_Fragment.class.getSimpleName());
                    break;

                case MENU_SYNC:
                    //TODO : Need to check RaspPi push
                    if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
                        pd_apiRequest.setApiResult(ActivityMain.this);
                        pd_apiRequest.checkSyncAPIStatus(PD_Constant.CHECK_SYNC_API, PD_Constant.CHECK_API);
/*                        showPushingDialog("Please wait...Pushing Data!");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PrathamSmartSyncNew.pushUsageToServer(true, PD_Constant.MANUAL_PUSH, ActivityMain.this);
                            }
                        }, 2500);*/
                    } else {
                        Toast.makeText(ActivityMain.this, R.string.internet_connection, Toast.LENGTH_SHORT).show();
                    }
                    break;


                case MENU_SYNCDB:
                    String uuID = "PDZ_" + PD_Utility.getUUID();
//                    File dbfilepath = new File(Environment.getExternalStorageDirectory() + "/" + PD_Constant.PRATHAM_BACKUPS + "/" + DB_NAME); // file path to save
//                    File zipFilePath = new File(Environment.getExternalStorageDirectory() + "/" + PD_Constant.PRATHAM_BACKUPS); // file path in which zip created
                    File dbfilepath = new File(PD_Utility.getStoragePath() + "/" + PD_Constant.PRATHAM_BACKUPS + "/" + DB_NAME); // file path to save
                    File zipFilePath = new File(PD_Utility.getStoragePath() + "/" + PD_Constant.PRATHAM_BACKUPS); // file path in which zip created
                    String filepathstr = zipFilePath.getAbsolutePath() + "/" + uuID;
                    //Checking all pradigi_db files in Folder and adding it to zip
                    //File dir = new File(Environment.getExternalStorageDirectory().toString() + "/PrathamBackups/");
                    File dir = new File(PD_Utility.getStoragePath().toString() + "/PrathamBackups/");
                    File[] db_files = dir.listFiles();
                    if (db_files != null) {
                        List<String> fileNameListStrings = new ArrayList<>();
                        for (int i = 0; i < db_files.length; i++)
                            if (db_files[i].exists() && db_files[i].isFile() && db_files[i].getName().contains("pradigi_db"))
                                fileNameListStrings.add(db_files[i].getAbsolutePath());
/*
                    File dbfilepath1 = new File(Environment.getExternalStorageDirectory() + "/" + PD_Constant.PRATHAM_BACKUPS +"/"+DB_NAME+"-shm"); // file path to save
                    File dbfilepath2 = new File(Environment.getExternalStorageDirectory() + "/" + PD_Constant.PRATHAM_BACKUPS +"/"+DB_NAME+"-wal"); // file path to save

                    String[] s = new String[3];
                    // Type the path of the files in here
                    s[0] = dbfilepath.getAbsolutePath();
                    s[1] = dbfilepath1.getAbsolutePath();
                    s[2] = dbfilepath2.getAbsolutePath();
*/
                        PD_Utility.zip(fileNameListStrings, filepathstr + ".zip", dbfilepath);
                        if (PrathamApplication.wiseF.isDeviceConnectedToSSID(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                            showPushingDialog("Please wait...Pushing Database!");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new PD_ApiRequest(PrathamApplication.getInstance())
                                            .pushDBToRaspberryPi(PD_Constant.URL.PUSH_DBTORASP_URL.toString(), uuID, filepathstr, PD_Constant.DB_PUSH, ActivityMain.this);
                                }
                            }, 2500);
                        } else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork() || PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
                            showPushingDialog("Please wait...Pushing Database!");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new PD_ApiRequest(PrathamApplication.getInstance())
                                            .pushDBToInternet(PD_Constant.URL.PUSH_DB_URL.toString(), uuID, filepathstr, PD_Constant.DB_PUSH, ActivityMain.this);
                                }
                            }, 2500);

                        } else {
                            Toast.makeText(ActivityMain.this, R.string.internet_connection, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

                case MENU_SYNCLOG:
                    PD_Utility.showFragment(ActivityMain.this, new FragmentSyncLog_(), R.id.main_frame,
                            null, FragmentSyncLog.class.getSimpleName());
                    break;
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void initialize() {
        mHandler.sendEmptyMessage(INITILIZE_DRAWER);
        try {
            if (Objects.requireNonNull(getIntent().getStringExtra(PD_Constant.OPEN_COURSES)).equalsIgnoreCase(PD_Constant.OPEN_COURSES)) {
                mHandler.sendEmptyMessage(MENU_COURSES);
                versionNum.setText("Version : " + PD_Utility.getCurrentVersion(this));
            }
        } catch (Exception e) {
            mHandler.sendEmptyMessage(SHOW_MENU);
            mHandler.sendEmptyMessage(CHECK_AAJ_KA_SAWAL);
            versionNum.setText("Version : " + PD_Utility.getCurrentVersion(this));
            e.printStackTrace();
        }
    }

    private void initializeDrawer() {
        main_sliding_drawer.setPanelSlideListener(this);
        if (PrathamApplication.isTablet)
            drawer_profile_lottie.setAnimation("avatars/dino_dance.json");
        else
            drawer_profile_lottie.setAnimation(FastSave.getInstance().getString(PD_Constant.AVATAR,
                    "avatars/dino_dance.json"));
        drawer_profile_name.setText(FastSave.getInstance().getString(PD_Constant.PROFILE_NAME, "No Name"));
        initializeMenu();
    }

    @Click(R.id.versionNum)
    public void showApkDate() {
        Toast.makeText(this, PD_Constant.apkDate, Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.drawer_profile_lottie)
    public void showProfile() {
        PrathamApplication.bubble_mp.start();
        //if (main_sliding_drawer.isOpen())
        main_sliding_drawer.closePane();
        mHandler.sendEmptyMessage(SHOW_PROFILE);
        //main_sliding_drawer.closePane();
/*
        Bundle bundle2 = new Bundle();
        bundle2.putInt(PD_Constant.REVEALX, 0);
        bundle2.putInt(PD_Constant.REVEALY, 0);
        PD_Utility.showFragment(ActivityMain.this, new Profile_Fragment_(), R.id.main_frame,
                bundle2, Profile_Fragment.class.getSimpleName());
*/
    }

    @Click(R.id.download_badge)
    public void showDownloadList() {
        PrathamApplication.bubble_mp.start();
        downloadListFragment_ = new DownloadListFragment_();
        downloadListFragment_.show(getSupportFragmentManager(), DownloadListFragment.class.getSimpleName());
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.BROADCAST_DOWNLOADINGS);
        EventBus.getDefault().post(message);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void showNotificationBadge(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_STARTED))
                increaseNotificationCount(message);
            else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_COMPLETE))
                decreaseNotificationCount(message);
            else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_ERROR))
                decreaseNotificationCount(message);
            else if (message.getMessage().equalsIgnoreCase(PD_Constant.EXIT_APP))
                exitApp();
            else if (message.getMessage().equalsIgnoreCase(PD_Constant.SHOW_HOME))
                mHandler.sendEmptyMessage(MENU_HOME);
        }
    }

    @UiThread
    public void increaseNotificationCount(EventMessage message) {
        download_notification.setNumber(message.getDownlaodContentSize());
        if (message.getDownlaodContentSize() == 1) {
            ScaleAnimation animation = new ScaleAnimation(0f, 1f, 0f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setFillAfter(true);
            animation.setDuration(300);
            download_badge.setAnimation(animation);
            download_badge.setVisibility(View.VISIBLE);
            animation.start();
        }
    }

    @UiThread
    public void decreaseNotificationCount(EventMessage message) {
        download_notification.setNumber(message.getDownlaodContentSize());
        if (message.getDownlaodContentSize() == 0) {
            ScaleAnimation animation = new ScaleAnimation(1f, 0f, 1f, 0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(300);
            animation.setFillAfter(false);
            download_badge.setAnimation(animation);
            animation.start();
            download_badge.setVisibility(View.GONE);
            if (downloadListFragment_ != null)
                downloadListFragment_.dismiss();
        }
    }

    @Subscribe
    public void onMessageReceived(EventMessage message) {
        if (message != null)
            if (message.getMessage().equalsIgnoreCase(PD_Constant.EDIT_SUCCESS)) {
                drawer_profile_lottie.setAnimation(FastSave.getInstance().getString(PD_Constant.AVATAR,
                        "avatars/dino_dance.json"));
                drawer_profile_name.setText(FastSave.getInstance().getString(PD_Constant.PROFILE_NAME, "No Name"));
            }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void DataPushedSuccessfully(EventMessage msg) {
        if (msg != null) {
            if (msg.getMessage().equalsIgnoreCase(PD_Constant.SUCCESSFULLYPUSHED)) {
                courseCount = msg.getCourseCount();
                Log.e("url cc1 : ", courseCount);
                tv_courseCount.setText(PD_Constant.ENROLLED_COURSE_COUNT + courseCount);
                push_lottie.setAnimation("success.json");
                push_lottie.playAnimation();
                txt_push_dialog_msg.setText(R.string.data_push_success);
                tv_courseCount.setVisibility(View.VISIBLE);
                tv_scoreCount.setVisibility(View.GONE);
                btn_done.setVisibility(View.VISIBLE);
                btn_okay.setVisibility(View.GONE);
                //new Handler().postDelayed(() -> pushDialog.dismiss(), 2500);
            } else if (msg.getMessage().equalsIgnoreCase(PD_Constant.SUCCESSFULLYPUSHEDNEW)) { // Created for new sync process
                push_lottie.setAnimation("success.json");
                push_lottie.playAnimation();
                txt_push_dialog_msg.setText(R.string.data_push_success);
                tv_courseCount.setVisibility(View.GONE);
                tv_scoreCount.setVisibility(View.GONE);
                btn_done.setVisibility(View.VISIBLE);
                btn_okay.setVisibility(View.GONE);
            } else if (msg.getMessage().equalsIgnoreCase(PD_Constant.DBSUCCESSFULLYPUSHED)) {
                tv_courseCount.setVisibility(View.GONE);
                tv_scoreCount.setVisibility(View.GONE);
                btn_done.setVisibility(View.GONE);
                btn_okay.setVisibility(View.VISIBLE);
                push_lottie.setAnimation("success.json");
                push_lottie.playAnimation();
                txt_push_dialog_msg.setText(R.string.db_push_success);
            } else if (msg.getMessage().equalsIgnoreCase(PD_Constant.PUSHFAILED)) {
                push_lottie.setAnimation("error_cross.json");
                push_lottie.playAnimation();
                txt_push_dialog_msg.setText(R.string.data_push_fail);
                txt_push_error.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> pushDialog.dismiss(), 1500);
            } else if (msg.getMessage().equalsIgnoreCase(PD_Constant.NODATATOPUSH)) {
                push_lottie.setAnimation("error_cross.json");
                push_lottie.playAnimation();
                txt_push_dialog_msg.setText(R.string.no_new_data_to_push);
                btn_done.setVisibility(View.GONE);
                btn_okay.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    public void showPushingDialog(String msg) {
        if (pushDialog == null) {
            pushDialog = new BlurPopupWindow.Builder(ActivityMain.this)
                    .setContentView(R.layout.app_success_dialog)
                    .bindClickListener(v -> {
                        pushDialog.dismiss();
                        //This is used to push the assessment data to server
                        try {
                            Intent assessmentIntent = new Intent("com.pratham.assessment.async.SyncDataActivity_");
                            startActivity(assessmentIntent);
                            tv_courseCount.setVisibility(View.GONE);
                            tv_scoreCount.setVisibility(View.GONE);
                            btn_done.setVisibility(View.GONE);
                            btn_okay.setVisibility(View.GONE);
                            push_lottie.setAnimation("success.json");
                        } catch (Exception e) {
                            Log.e("url : ", e.getMessage());
                            tv_courseCount.setVisibility(View.GONE);
                            tv_scoreCount.setVisibility(View.GONE);
                            btn_done.setVisibility(View.GONE);
                            btn_okay.setVisibility(View.GONE);
                            push_lottie.setAnimation("success.json");
                        }
                    }, R.id.btn_done)
                    .bindClickListener(v -> {
                        pushDialog.dismiss();
                        btn_done.setVisibility(View.GONE);
                        btn_okay.setVisibility(View.GONE);
                    }, R.id.btn_ok)
                    .setGravity(Gravity.CENTER)
                    .setScaleRatio(0.2f)
                    .setDismissOnClickBack(true)
                    .setDismissOnTouchBackground(false)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();
            push_lottie = pushDialog.findViewById(R.id.push_lottie);
            txt_push_dialog_msg = pushDialog.findViewById(R.id.txt_push_dialog_msg);
            txt_push_error = pushDialog.findViewById(R.id.txt_push_error);
            tv_courseCount = pushDialog.findViewById(R.id.tv_courseCount);
            tv_scoreCount = pushDialog.findViewById(R.id.tv_scoreCount);
            btn_done = pushDialog.findViewById(R.id.btn_done);
            btn_okay = pushDialog.findViewById(R.id.btn_ok);
        }
        push_lottie.setAnimation("loading.json");
        txt_push_dialog_msg.setText(msg);
        tv_courseCount.setText(R.string.course_count + FastSave.getInstance().getString(PD_Constant.COURSE_COUNT, "0"));
        pushDialog.show();
    }

    private void initializeMenu() {
        ArrayList<Modal_NavigationMenu> navigationMenus = new ArrayList<>();
        String[] menus = getResources().getStringArray(R.array.navigation_menu);
        int[] menus_img = {R.drawable.ic_education, R.drawable.ic_courses, R.drawable.ic_abc_blocks, R.drawable.ic_wifi,
                R.drawable.ic_folder, R.drawable.ic_dashboard_64, R.drawable.ic_app_sharing, R.drawable.ic_sync, R.drawable.syncdb,
                R.drawable.ic_sync_logs, R.drawable.ic_backpacker};
        for (int i = 0; i < menus.length; i++) {
            Modal_NavigationMenu nav = new Modal_NavigationMenu();
            nav.setMenu_name(menus[i]);
            nav.setMenuImage(menus_img[i]);
            nav.setIsselected(false);
            navigationMenus.add(nav);
        }
        RV_MenuAdapter rv_menuAdapter = new RV_MenuAdapter(this, navigationMenus, this);
        rv_drawer.setHasFixedSize(true);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        rv_drawer.setLayoutManager(flexboxLayoutManager);
        rv_drawer.setAdapter(rv_menuAdapter);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);
        if (fragment instanceof FragmentContent_) {
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.CONTENT_BACK);
            EventBus.getDefault().post(message);
        } else if (fragment instanceof FragmentLanguage_) {
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.LANGUAGE_BACK);
            EventBus.getDefault().post(message);
        } else if (fragment instanceof FragmentShare_ || fragment instanceof FragmentReceive_) {
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.SHARE_BACK);
            EventBus.getDefault().post(message);
        } else if (fragment instanceof Fragment_SelectAvatar_) {
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.EDIT_BACK);
            EventBus.getDefault().post(message);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /** Commented to stop adding entry in log table of AppStarted and Exited*/
        //AppStart And Exit Log
/*        String prevSessionID = FastSave.getInstance().getString("SESSION", "");
        Log.e("URL Ses : ", prevSessionID);
        String currentSessionID = FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session");
        Log.e("URL Ses1 : ", currentSessionID);

        if (!prevSessionID.equalsIgnoreCase(currentSessionID)) {
            Log.e("URL", "Start");

            //to add log for exit app, before starting new start log
            log = new Modal_Log();
            log.setCurrentDateTime(PD_Utility.getCurrentDateTime());
            log.setErrorType("AppExitLog");
            log.setExceptionMessage("App is Exited");
            log.setExceptionStackTrace("");
            log.setMethodName("NO_METHOD");
            log.setSessionId(prevSessionID);
            log.setDeviceId(PD_Utility.getDeviceSerialID());
            logDao.insertLog(log);

            //StartLog
            log = new Modal_Log();
            log.setCurrentDateTime(PD_Utility.getCurrentDateTime());
            log.setErrorType("AppStartLog");
            log.setExceptionMessage("App is Started");
            log.setExceptionStackTrace("");
            log.setMethodName("NO_METHOD");
            log.setSessionId(FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session"));
            log.setDeviceId(PD_Utility.getDeviceSerialID());
            logDao.insertLog(log);

            BackupDatabase.backup(this);
        }*/
        FastSave.getInstance().saveString("SESSION", FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session"));
    }

    @UiThread
    public void exitApp() {
        if (!FsService.isRunning()) {
            exitDialog = new BlurPopupWindow.Builder(ActivityMain.this)
                    .setContentView(R.layout.app_exit_dialog)
                    .bindClickListener(v -> {
                        exitDialog.dismiss();
                        /** Commented to stop adding entry in log table of App Exited*/
                        //Modal_Log log1 = new Modal_Log();
 /*                       log = new Modal_Log();
                        log.setCurrentDateTime(PD_Utility.getCurrentDateTime());
                        log.setErrorType("AppExitLog");
                        log.setExceptionMessage("App is Exited");
                        log.setExceptionStackTrace("");
                        log.setMethodName("NO_METHOD");
                        log.setSessionId(FastSave.getInstance().getString(PD_Constant.SESSIONID, "no_session"));
                        log.setDeviceId(PD_Utility.getDeviceSerialID());
                        logDao.insertLog(log);
                        BackupDatabase.backup(this);*/
                        FastSave.getInstance().saveString("SESSION", "no_session");
                        new Handler().postDelayed((Runnable) this::finishAffinity, 200);
                    }, R.id.dialog_btn_exit)
                    .bindClickListener(v -> exitDialog.dismiss(), R.id.btn_cancel)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnTouchBackground(true)
                    .setDismissOnClickBack(true)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();
            exitDialog.show();
        } else {
            EventMessage msg = new EventMessage();
            msg.setMessage(PD_Constant.CLOSE_FTP_SERVER);
            EventBus.getDefault().post(msg);
        }
    }

    @Click(R.id.outer_area)
    public void onRootTouch(View v) {
        if (main_sliding_drawer.isOpen())
            main_sliding_drawer.closePane();
    }

//    SpotlightListener spotlightListener = new SpotlightListener() {
//        @Override
//        public void onUserClicked(String spotlightViewId) {
//            if (top_scaling.getState() == State.EXPANDED)
//                SpotlightSequence.getInstance(ActivityMain.this, null)
//                        .addSpotlight(sheet_home, "DISPLAY CONTENT", "Click here to view contents", "sheet_home")
//                        .addSpotlight(sheet_language, "CHANGE LANGUAGE", "Click here to change Language", "sheet_language")
//                        .addSpotlight(sheet_connect, "CONNECT FTP_HOTSPOT_SSID", "Click here to connect wifi", "sheet_connect")
//                        .addSpotlight(sheet_connect, "CONNECT FTP_HOTSPOT_SSID", "Click here to connect wifi", "sheet_connect")
//                        .startSequence();
//        }
//    };

    private void showIntro() {
        if (!FastSave.getInstance().getBoolean(PD_Constant.INTRO_SHOWN, false))
            new SpotlightView.Builder(ActivityMain.this)
                    .introAnimationDuration(400)
                    .enableRevealAnimation(true)
                    .performClick(true)
                    .fadeinTextDuration(400)
                    .headingTvColor(Color.parseColor("#eb273f"))
                    .headingTvSize(32)
                    .headingTvText("Open Menu")
                    .subHeadingTvColor(Color.parseColor("#ffffff"))
                    .subHeadingTvSize(16)
                    .subHeadingTvText("Click here and Open Menu")
                    .maskColor(Color.parseColor("#dc000000"))
                    .target(main_nav)
                    .lineAnimDuration(400)
                    .lineAndArcColor(Color.parseColor("#eb273f"))
                    .dismissOnTouch(true)
                    .dismissOnBackPress(true)
                    .enableDismissAfterShown(true)
                    .setListener(spotlightViewId -> FastSave.getInstance().saveBoolean(PD_Constant.INTRO_SHOWN, true))
                    .usageId(PD_Constant.PRADIGI_ICON) //UNIQUE ID
                    .show();
        if (getIntent().getStringExtra(PD_Constant.PUSH_NOTI_KEY) != null &&
                getIntent().getStringExtra(PD_Constant.PUSH_NOTI_VALUE) != null) {
            noti_key = getIntent().getStringExtra(PD_Constant.PUSH_NOTI_KEY);
            noti_value = getIntent().getStringExtra(PD_Constant.PUSH_NOTI_VALUE);
            mHandler.sendEmptyMessage(SHOW_YOU_TUBE_VIDEO);
        }
    }

    @Override
    public void menuClicked(int position, Modal_NavigationMenu modal_navigationMenu) {
        PrathamApplication.bubble_mp.start();
        if (main_sliding_drawer.isOpen())
            main_sliding_drawer.closePane();
        if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Home"))
            mHandler.sendEmptyMessage(MENU_HOME);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Language"))
            mHandler.sendEmptyMessage(MENU_LANGUAGE);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Connect Wifi"))
            mHandler.sendEmptyMessage(MENU_CONNECT_WIFI);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Share OR Receive"))
            mHandler.sendEmptyMessage(MENU_SHARE);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Dashboard"))
            mHandler.sendEmptyMessage(MENU_DASHBOARD);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Share App"))
            mHandler.sendEmptyMessage(MENU_SHARE_APP);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Exit"))
            mHandler.sendEmptyMessage(MENU_EXIT);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Courses"))
            mHandler.sendEmptyMessage(MENU_COURSES);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Sync Data"))
            mHandler.sendEmptyMessage(MENU_SYNC);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Sync Database"))
            mHandler.sendEmptyMessage(MENU_SYNCDB);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Sync Log"))
            mHandler.sendEmptyMessage(MENU_SYNCLOG);
    }

    @Override
    public void toggleMenuIcon() {
        toggleToArrow();
    }

    @Click(R.id.main_nav)
    public void setMenuClicked() {
        if (!FsService.isRunning()) {
            if (!isChecked)
                if (main_sliding_drawer.isOpen()) main_sliding_drawer.closePane();
                else main_sliding_drawer.openPane();
            else
                onBackPressed();
        } else {
            EventMessage msg = new EventMessage();
            msg.setMessage(PD_Constant.CLOSE_FTP_SERVER);
            EventBus.getDefault().post(msg);
        }
    }

    @UiThread
    public void toggleToArrow() {
        isChecked = !isChecked;
        final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
        main_hamburger.setImageState(stateSet, true);
    }

    @Override
    public void onPanelSlide(@NonNull View view, float v) {
        outer_area.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPanelOpened(@NonNull View view) {
        Log.d(TAG, "onPanelOpened: ");
    }

    @Override
    public void onPanelClosed(@NonNull View view) {
        outer_area.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra(PD_Constant.PUSH_NOTI_KEY) != null && intent.getStringExtra(PD_Constant.PUSH_NOTI_VALUE) != null) {
            noti_key = intent.getStringExtra(PD_Constant.PUSH_NOTI_KEY);
            noti_value = intent.getStringExtra(PD_Constant.PUSH_NOTI_VALUE);
            mHandler.sendEmptyMessage(SHOW_YOU_TUBE_VIDEO);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sessionDao.UpdateToDate(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""), PD_Utility.getCurrentDateTime());
        BackupDatabase.backup(this);
        Log.d("url : ", FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
    }

    @Override
    public void recievedContent(String header, String response, ArrayList<Modal_ContentDetail> contentList) {
        //Check whether the server is running or not.
        if (header.equalsIgnoreCase(PD_Constant.CHECK_API)) {
            Log.e("NewSyncApiResponse:", response);
            Gson gson = new Gson();
            Type type = new TypeToken<Model_CheckSyncAPI>() {
            }.getType();
            Model_CheckSyncAPI model_checkSyncAPI = gson.fromJson(response, type);
            Log.e("SyncAPI Message : ", model_checkSyncAPI.getStatusCode());
            if (model_checkSyncAPI.getStatusCode().equalsIgnoreCase("0")) {
                showPushingDialog("Please wait...Pushing Data!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PrathamSmartSyncNew.pushUsageToServer(true, PD_Constant.MANUAL_PUSH, ActivityMain.this);
                    }
                }, 2500);
            } else {
                Toast.makeText(this, model_checkSyncAPI.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void recievedError(String header, ArrayList<Modal_ContentDetail> contentList) {

    }
}