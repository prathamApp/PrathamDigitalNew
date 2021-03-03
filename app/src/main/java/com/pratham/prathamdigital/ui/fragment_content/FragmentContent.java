package com.pratham.prathamdigital.ui.fragment_content;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.custom.wrappedLayoutManagers.WrapContentLinearLayoutManager;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.ui.content_player.Activity_ContentPlayer_;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain;
import com.pratham.prathamdigital.ui.dashboard.ContractMenu;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;

@EFragment(R.layout.fragment_content)
public class FragmentContent extends Fragment implements ContentContract.contentView,
        ContentContract.contentClick, CircularRevelLayout.CallBacks, LevelContract {

    private static final int SDCARD_LOCATION_CHOOSER = 99;
    private static final int INITIALIZE_CONTENT_ADAPTER = 1;
    private static final int INITIALIZE_LEVEL_ADAPTER = 2;
    private static final int CLICK_DL_CONTENT = 3;
    private static final int REQUEST_CODE_COURSE_BACK = 4;
    private static boolean IS_DEEP_LINK = false;
    private Dialog dialog;
    @ViewById(R.id.frag_content_bkgd)
    RelativeLayout frag_content_bkgd;
    @ViewById(R.id.circular_content_reveal)
    CircularRevelLayout circular_content_reveal;
    @ViewById(R.id.rv_content)
    RecyclerView rv_content;
    @ViewById(R.id.rv_level)
    public RecyclerView rv_level;
    @ViewById(R.id.txt_wifi_status)
    TextView txt_wifi_status;
    @ViewById(R.id.rl_network_error)
    RelativeLayout rl_network_error;
    @ViewById(R.id.iv_wifi_status)
    ImageView iv_wifi_status;
    @ViewById(R.id.iv_updateApp)
    ImageView iv_updateApp;

    @Bean(ContentPresenterImpl.class)
    ContentContract.contentPresenter contentPresenter;

    private final Map<String, Integer> filesDownloading = new HashMap<>();
    private ContentAdapter contentAdapter;
    private RV_LevelAdapter levelAdapter;
    private int revealX;
    private int revealY;
    private BlurPopupWindow download_builder;
    private Modal_ContentDetail dl_Content;
    private int courseFlag;

    private final static int IS_COURSE = 999;
    ArrayList<Modal_ContentDetail> temp_levels;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INITIALIZE_CONTENT_ADAPTER:
                    contentAdapter = new ContentAdapter(getActivity(), FragmentContent.this);
                    FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
                    flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
//                    flexboxLayoutManager.setAlignItems(AlignItems.CENTER);
                    rv_content.setLayoutManager(flexboxLayoutManager);
                    rv_content.setAdapter(contentAdapter);
                    break;
                case INITIALIZE_LEVEL_ADAPTER:
                    levelAdapter = new RV_LevelAdapter(getActivity(), FragmentContent.this);
                    rv_level.setHasFixedSize(true);
                    rv_level.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                    rv_level.setAdapter(levelAdapter);
                    break;
                case CLICK_DL_CONTENT:
                    List<Modal_ContentDetail> details;
                    View itemview = null;
                    if (contentAdapter != null) {
                        boolean found = false;
                        details = contentAdapter.getData();
                        for (int i = 0; i < details.size(); i++) {
                            if (details.get(i).getNodeid() != null) {
                                if (dl_Content.getNodeid().equalsIgnoreCase(details.get(i).getNodeid())) {
                                    RecyclerView.ViewHolder vh = rv_content.findViewHolderForLayoutPosition(i);
                                    itemview = Objects.requireNonNull(vh).itemView;
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (found)
                            itemview.findViewById(R.id.rl_download).performClick();
                    }
                    break;
            }
        }
    };

    @AfterViews
    public void initialize() {
        contentPresenter.setView(FragmentContent.this);
        IS_DEEP_LINK = Objects.requireNonNull(getArguments()).getBoolean(PD_Constant.DEEP_LINK, false);
        frag_content_bkgd.setBackground(PD_Utility.getDrawableAccordingToMonth(getActivity()));
        circular_content_reveal.setListener(this);
        if (getArguments() != null) {
            revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            circular_content_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    circular_content_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    circular_content_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
        }
        new Handler().postDelayed(() -> {
            mHandler.sendEmptyMessage(INITIALIZE_CONTENT_ADAPTER);
            mHandler.sendEmptyMessage(INITIALIZE_LEVEL_ADAPTER);
            showDialog();
            if (levelAdapter == null) contentPresenter.getContent(null, "");
            else contentPresenter.getContent();
        }, 500);
    }

    @Click(R.id.iv_updateApp)
    public void updateApp() {
        //Toast.makeText(getContext(), "Update", Toast.LENGTH_SHORT).show();
        final String appPackageName = Objects.requireNonNull(getActivity()).getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        contentPresenter.setView(FragmentContent.this);
        if (IS_DEEP_LINK) {
            contentPresenter.openDeepLinkContent(Objects.requireNonNull(getArguments()).getString(PD_Constant.DEEP_LINK_CONTENT, null));
        }
        //When returned from the webview or other activity, latest contents are not updated. The below call is thus required.
        if (contentAdapter != null)
            if (contentPresenter.isFilesDownloading()) contentPresenter.getContent();
            else {
                displayContents(contentPresenter.getContentList());
            }
    }

    @Override
    public void onPause() {
        super.onPause();
        IS_DEEP_LINK = false;
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
    public void onDestroyView() {
        super.onDestroyView();
        contentPresenter.viewDestroyed();
    }

    @UiThread
    @Override
    public void dismissDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMainBackPressed(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CONTENT_BACK)) {
                setContent_back();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FAST_DOWNLOAD_STARTED)) {
                contentPresenter.fileDownloadStarted(message.getDownloadId(), message.getModal_fileDownloading());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FAST_DOWNLOAD_UPDATE)) {
                contentPresenter.updateFileProgress(message.getDownloadId(), message.getModal_fileDownloading());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FAST_DOWNLOAD_COMPLETE)) {
                contentPresenter.onDownloadCompleted(message.getDownloadId(), message.getContentDetail());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FAST_DOWNLOAD_ERROR)) {
                contentPresenter.ondownloadError(message.getDownloadId());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_COMPLETE)) {
                onDownloadComplete(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.CONNECTION_STATUS)) {
                updateConnectionStatus(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_ERROR)) {
                onDownloadError(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.BROADCAST_DOWNLOADINGS)) {
                contentPresenter.broadcast_downloadings();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.CANCEL_DOWNLOAD)) {
                contentPresenter.cancelDownload(message.getDownloadId());
            }
        }
    }

    @UiThread
    public void onDownloadComplete(EventMessage message) {
        if (message != null) {
            if (filesDownloading.containsKey(message.getContentDetail().getNodeid())) {
                List<Modal_ContentDetail> data = new ArrayList<>(contentAdapter.getData());
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i) != null && data.get(i).getNodeid() != null &&
                            data.get(i).getNodeid().equalsIgnoreCase(message.getContentDetail().getNodeid())) {
                        contentAdapter.notifyItemChanged(i, message.getContentDetail());
                        break;
                    }
                }
                contentAdapter.submitList(data);
            }
        }
    }

    @UiThread
    public void updateConnectionStatus(EventMessage message) {
        txt_wifi_status.setText(message.getConnection_name());
        iv_wifi_status.setImageDrawable(message.getConnection_resource());
        contentPresenter.checkConnectionForRaspberry();
    }

    @UiThread
    public void setContent_back() {
        filesDownloading.clear();
        PrathamApplication.bubble_mp.start();
        showDialog();
        contentPresenter.showPreviousContent();
    }

    @UiThread
    @Override
    public void showNoConnectivity() {
        dismissDialog();
        rv_content.setVisibility(View.GONE);
        rl_network_error.setVisibility(View.VISIBLE);
    }

    @Click(R.id.btn_retry)
    public void setRetry() {
        PrathamApplication.bubble_mp.start();
        contentPresenter.getContent(null, "");
    }

    @UiThread
    @Override
    public void displayContents(List<Modal_ContentDetail> content) {
        filesDownloading.clear();
        rl_network_error.setVisibility(View.GONE);
        dismissDialog();
        if (rv_content.getVisibility() == View.GONE)
            rv_content.setVisibility(View.VISIBLE);
        if (content != null && !content.isEmpty() && content.size() > 1) {
            if (contentAdapter == null) {
                mHandler.sendEmptyMessage(INITIALIZE_CONTENT_ADAPTER);
            }
            rv_content.smoothScrollToPosition(0);
            contentAdapter.submitList(content);
            rv_content.scheduleLayoutAnimation();
        } else {
            showNoConnectivity();
        }
    }

    @Override
    public void displayContentsInCourse(Modal_ContentDetail contentDetail, List<Modal_ContentDetail> childs) {
        if (courseFlag == IS_COURSE) {
            String courseID = contentDetail.getAltnodeid();
            String nodeTitle = contentDetail.getNodetitle();
            String nodeDesc = contentDetail.getNodedesc();
            KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                    .permissions(Manifest.permission.RECORD_AUDIO)
                    .onAccepted(permissionResult -> {
                        Intent intent = new Intent(getActivity(), Activity_ContentPlayer_.class);
                        intent.putExtra("NODE_CALL", "CALL_FROM_NODE");
                        intent.putExtra(PD_Constant.CONTENT_TYPE, PD_Constant.COURSE);
                        intent.putExtra(PD_Constant.COURSE_ID, courseID);
                        intent.putExtra(PD_Constant.WEEK, "WEEK_1");
                        intent.putExtra("NODE_TITLE", nodeTitle);
                        intent.putExtra("NODE_DESC", nodeDesc);
                        //intent.putExtra(PD_Constant.COURSE_PARENT, contentDetail);
                        intent.putParcelableArrayListExtra(PD_Constant.CONTENT, (ArrayList<? extends Parcelable>) childs);
                        intent.putParcelableArrayListExtra(PD_Constant.CONTENT_LEVEL, temp_levels);
                        Log.e("URL", String.valueOf(temp_levels.size()));
                        startActivityForResult(intent, REQUEST_CODE_COURSE_BACK);
                        getActivity().overridePendingTransition(R.anim.shrink_enter, R.anim.nothing);
                    })
                    .ask();
        }
    }

    @UiThread
    @Override
    public void levelClicked(Modal_ContentDetail detail) {
        PrathamApplication.bubble_mp.start();
        showDialog();
        contentPresenter.getContent(detail, "");
    }

    @UiThread
    @Override
    public void displayLevel(ArrayList<Modal_ContentDetail> levelContents, String isCourse) {
        temp_levels = new ArrayList<>(levelContents);
        Log.e("url displev", String.valueOf(temp_levels.size()));
        showLevels(temp_levels, isCourse);
    }

    @UiThread
    @Override
    public void onfolderClicked(int position, Modal_ContentDetail contentDetail) {
        PrathamApplication.bubble_mp.start();
        showDialog();
        contentPresenter.getContent(contentDetail, "");
    }

    @Override
    public void onCourseClicked(int position, Modal_ContentDetail contentDetail, List<Modal_ContentDetail> childs) {
        if (position == IS_COURSE) {
            PrathamApplication.bubble_mp.start();
            showDialog();
            contentPresenter.getContent(contentDetail, "isCourse");
            courseFlag = position;
        } else {
            String courseID = contentDetail.getAltnodeid();
            String nodeTitle = contentDetail.getNodetitle();
            String nodeDesc = contentDetail.getNodedesc();
            KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                    .permissions(Manifest.permission.RECORD_AUDIO)
                    .onAccepted(permissionResult -> {
                        Intent intent = new Intent(getActivity(), Activity_ContentPlayer_.class);
                        intent.putExtra("NODE_CALL", "CALL_FROM_NODE");
                        intent.putExtra(PD_Constant.CONTENT_TYPE, PD_Constant.COURSE);
                        intent.putExtra(PD_Constant.COURSE_ID, courseID);
                        intent.putExtra(PD_Constant.WEEK, "WEEK_1");
                        intent.putExtra("NODE_TITLE", nodeTitle);
                        intent.putExtra("NODE_DESC", nodeDesc);
                        //intent.putExtra(PD_Constant.COURSE_PARENT, contentDetail);
                        intent.putParcelableArrayListExtra(PD_Constant.CONTENT, (ArrayList<? extends Parcelable>) childs);
                        intent.putParcelableArrayListExtra(PD_Constant.CONTENT_LEVEL, temp_levels);
                        Log.e("URL", String.valueOf(temp_levels.size()));
                        startActivityForResult(intent, REQUEST_CODE_COURSE_BACK);
                        getActivity().overridePendingTransition(R.anim.shrink_enter, R.anim.nothing);
                    })
                    .ask();
        }
    }

    @Override
    public void onAssessmentItemClicked(Modal_ContentDetail modal_contentDetail) {
        new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                .setTitle("Confirm")
                .setMessage("You will be redirected to Assessment App!")
                .setPositiveButton("YES", (dialog, which) -> {
                    try {
                        //startActivityForResult(); //use this when actual testing
                        startAssessment(modal_contentDetail);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "App not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    public void startAssessment(Modal_ContentDetail contentDetail) {
//        if (!PrathamApplication.isTablet) {
        Intent intent = new Intent("com.pratham.assessment.ui.choose_assessment.science.ScienceAssessmentActivity_");
        Bundle mBundle = new Bundle();

        ArrayList<String> studname = new ArrayList<>();
        ArrayList<String> studID = new ArrayList<>();
        List<Attendance> newAttendance = attendanceDao.getNewAttendances(FastSave.getInstance().getString(PD_Constant.SESSIONID, "no session"));
        for (Attendance att : newAttendance) {
            List<Modal_Student> newStudent = studentDao.getAllStudent(att.getStudentID());
            for (Modal_Student stud : newStudent) {
                studname.add(stud.getFullName());
                studID.add(stud.getStudentId());
            }
        }

        if (studname.size() == 1) {
            mBundle.putString("studentId", FastSave.getInstance().getString(PD_Constant.SESSIONID, "no session"));
            mBundle.putString("appName", getResources().getString(R.string.app_name));
            mBundle.putString("studentName", FastSave.getInstance().getString(PD_Constant.PROFILE_NAME, ""));
            mBundle.putString("subjectName", contentDetail.getSubject());
            mBundle.putString("subjectLanguage", contentDetail.getContent_language());
//                mBundle.putString("subjectLevel", "1");
            mBundle.putString("examId", contentDetail.getNodekeywords());
//                mBundle.putString("subjectId", "89");
            intent.putExtras(mBundle);
            startActivity(intent);
        } else {
            final CharSequence[] charSequenceItems = studname.toArray(new CharSequence[studname.size()]);
            new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                    .setTitle("Select Student for Assessment")
                    .setItems(charSequenceItems, (dialog, which) -> {
                        // extras.putString(key, value);
                        mBundle.putString("studentId", studID.get(which));
                        mBundle.putString("appName", getResources().getString(R.string.app_name));
                        mBundle.putString("studentName", studname.get(which));
                        mBundle.putString("subjectName", contentDetail.getSubject());
                        mBundle.putString("subjectLanguage", contentDetail.getContent_language());
                        //mBundle.putString("subjectLevel", "1");
                        mBundle.putString("examId", contentDetail.getNodekeywords());
                        //mBundle.putString("subjectId", "89");
                        intent.putExtras(mBundle);
                        startActivity(intent);
                    })
                    .show();
        }
    }


    @Override
    public void displayHeader(Modal_ContentDetail contentDetail) {
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    @Override
    public void onDownloadClicked(int position, Modal_ContentDetail contentDetail, View reveal_view, View startView) {
        if (FastSave.getInstance().getBoolean(PD_Constant.STORAGE_ASKED, false)) {
            contentAdapter.reveal(reveal_view, startView);
            PrathamApplication.bubble_mp.start();
            filesDownloading.put(contentDetail.getNodeid(), position);
            contentPresenter.downloadContent(contentDetail);
        } else {
            download_builder = new BlurPopupWindow.Builder(getActivity())
                    .setContentView(R.layout.download_alert_dialog)
                    .bindClickListener(v -> {
                        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, true);
                        onDownloadClicked(position, contentDetail, reveal_view, startView);
                        download_builder.dismiss();
                    }, R.id.btn_okay)
                    .bindClickListener(v -> download_builder.dismiss(), R.id.txt_download_cancel)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnClickBack(true)
                    .setDismissOnTouchBackground(true)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(8)
                    .setTintColor(0x30000000)
                    .build();
            TextView tv = download_builder.findViewById(R.id.txt_download_alert);
            tv.append(" " + PD_Constant.STORING_IN);
            download_builder.show();
        }
    }

    @Override
    public void deleteContent(int pos, Modal_ContentDetail contentItem) {
        contentPresenter.deleteContent(contentItem);
    }

    @UiThread
    public void showSdCardDialog() {
        new BlurPopupWindow.Builder(getContext())
                .setContentView(R.layout.dialog_alert_sd_card)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(intent, SDCARD_LOCATION_CHOOSER);
                    }, 1500);
                    download_builder.dismiss();
                }, R.id.txt_choose_sd_card)
                .setDismissOnClickBack(true)
                .setDismissOnTouchBackground(true)
                .setScaleRatio(0.2f)
                .setBlurRadius(8)
                .setTintColor(0x30000000)
                .build()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SDCARD_LOCATION_CHOOSER) {
            if (data != null && data.getData() != null) {
                contentPresenter.parseSD_UriandPath(data);
            }
        } else if(requestCode == REQUEST_CODE_COURSE_BACK) {
            contentPresenter.showPreviousContent();
        }
    }

    @UiThread
    @Override
    public void hideViews() {
    }

    @UiThread
    @Override
    public void exitApp() {
        dismissDialog();
        EventMessage eventMessage = new EventMessage();
        eventMessage.setMessage(PD_Constant.EXIT_APP);
        EventBus.getDefault().post(eventMessage);
    }

    @UiThread
    @Override
    public void increaseNotification(int number) {
    }

    @UiThread
    @Override
    public void decreaseNotification(int number, Modal_ContentDetail
            contentDetail, ArrayList<String> selectedNodeIds) {
    }

    @UiThread
    @Override
    public void onDownloadError(EventMessage message) {
        if (message != null) {
            if (filesDownloading.containsKey(message.getContentDetail().getNodeid())) {
                List<Modal_ContentDetail> data = new ArrayList<>(contentAdapter.getData());
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i) != null && data.get(i).getNodeid() != null &&
                            data.get(i).getNodeid().equalsIgnoreCase(message.getContentDetail().getNodeid())) {
                        contentAdapter.notifyItemChanged(i, data.get(i));
                        break;
                    }
                }
            }
        }
    }

    @UiThread
    public void showLevels(ArrayList<Modal_ContentDetail> levelContents, String isCourse) {
        Log.e("url showlevel : ", String.valueOf(levelContents.size()));
        if (levelContents != null) {
            if (levelAdapter == null) {
                mHandler.sendEmptyMessage(INITIALIZE_LEVEL_ADAPTER);
            } else {
/*                if(isCourse.equalsIgnoreCase("IS_COURSE")){
                    Modal_ContentDetail contentDetail = levelContents.get(levelContents.size() - 1);
                    levelContents.remove(levelContents.size() - 1);
                    levelAdapter.submitList(levelContents);
                } else*/
                levelAdapter.submitList(levelContents);
            }
        }
    }

    @Override
    public void onRevealed() {
        PD_Utility.getConnectivityStatus(Objects.requireNonNull(getActivity()));
    }

    @Override
    public void onUnRevealed() {

    }

    @UiThread
    @Override
    public void openContent(int position, Modal_ContentDetail contentDetail) {
        PrathamApplication.bubble_mp.start();
        KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                .permissions(Manifest.permission.RECORD_AUDIO)
                .onAccepted(permissionResult -> {
                    Intent intent = new Intent(getActivity(), Activity_ContentPlayer_.class);
                    switch (contentDetail.getResourcetype().toLowerCase()) {
                        case PD_Constant.GAME:
                            intent.putExtra(PD_Constant.CONTENT_TYPE, PD_Constant.GAME);
                            break;
                        case PD_Constant.VIDEO:
                            intent.putExtra(PD_Constant.CONTENT_TYPE, PD_Constant.VIDEO);
                            break;
                        case PD_Constant.AUDIO:
                            intent.putExtra(PD_Constant.CONTENT_TYPE, PD_Constant.AUDIO);
                            break;
                        case PD_Constant.PDF:
                            intent.putExtra(PD_Constant.CONTENT_TYPE, PD_Constant.PDF);
                            break;
                    }
                    intent.putExtra(PD_Constant.CONTENT, contentDetail);
                    Objects.requireNonNull(getActivity()).startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.shrink_enter, R.anim.nothing);
                })
                .ask();
    }

    @Override
    public void animateHamburger() {
        ((ContractMenu) Objects.requireNonNull(getActivity())).toggleMenuIcon();
    }

    @Override
    public void displayDLContents(List<Modal_ContentDetail> details) {
        dl_Content = details.get(details.size() - 1);
        displayContents(details);
        mHandler.sendEmptyMessage(CLICK_DL_CONTENT);
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.cat_loading_dialog);
        }
        dialog.show();
    }
}
