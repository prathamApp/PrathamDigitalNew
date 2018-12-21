package com.pratham.prathamdigital.ui.fragment_content;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.ContentItemDecoration;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.dashboard.LevelContract;
import com.pratham.prathamdigital.ui.dashboard.RV_LevelAdapter;
import com.pratham.prathamdigital.ui.pdf_viewer.Activity_PdfViewer;
import com.pratham.prathamdigital.ui.video_player.Activity_VPlayer;
import com.pratham.prathamdigital.ui.web_view.Activity_WebView;
import com.pratham.prathamdigital.util.FragmentManagePermission;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.prathamdigital.PrathamApplication.pradigiPath;

public class FragmentContent extends FragmentManagePermission implements ContentContract.contentView,
        ContentContract.contentClick, CircularRevelLayout.CallBacks, LevelContract {

    private static final String TAG = FragmentContent.class.getSimpleName();
    @BindView(R.id.circular_content_reveal)
    CircularRevelLayout circular_content_reveal;
    //    @BindView(R.id.lottie_content_bkgd)
//    LottieAnimationView lottie_content_bkgd;
    @BindView(R.id.rv_content)
    RecyclerView rv_content;
    //    @BindView(R.id.content_back)
//    ImageView content_back;
    @BindView(R.id.rv_level)
    public RecyclerView rv_level;
    @BindView(R.id.txt_wifi_status)
    TextView txt_wifi_status;
    @BindView(R.id.rl_network_error)
    RelativeLayout rl_network_error;
    @BindView(R.id.iv_wifi_status)
    ImageView iv_wifi_status;

    ContentPresenterImpl contentPresenter;
    ContentAdapter contentAdapter;
    private RV_LevelAdapter levelAdapter;
    ContentContract.mainView mainView;
    Map<String, Integer> filesDownloading = new HashMap<>();
    private int revealX;
    private int revealY;
    BlurPopupWindow download_builder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, rootView);
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
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainView = (ContentContract.mainView) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentPresenter = new ContentPresenterImpl(getActivity(), this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onMainBackPressed(EventMessage pressed) {
        if (pressed != null) {
            if (pressed.getMessage().equalsIgnoreCase(PD_Constant.CONTENT_BACK)) {
                setContent_back();
            }
        }
    }

    @Subscribe
    public void decrease(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_COMPLETE)) {
                if (filesDownloading.containsKey(message.getContentDetail().getNodeid()))
                    contentAdapter.notifyItemChanged(filesDownloading.get(message.getContentDetail().getNodeid()), message.getContentDetail());
                mainView.hideNotificationBadge(message.getDownlaodContentSize());
            }
        }
    }

    //    @OnClick(R.id.content_back)
    public void setContent_back() {
        filesDownloading.clear();
        PrathamApplication.bubble_mp.start();
        PD_Utility.showDialog(getActivity());
        contentPresenter.showPreviousContent();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (levelAdapter == null) {
            PD_Utility.showDialog(getActivity());
            contentPresenter.getContent(null);
        }
    }

    @Override
    public void showNoConnectivity() {
        PD_Utility.dismissDialog();
        rv_content.setVisibility(View.GONE);
        rl_network_error.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.lottie_content_bkgd)
    public void setRetry() {
        PrathamApplication.bubble_mp.start();
        onResume();
    }

    @Override
    public void displayContents(ArrayList<Modal_ContentDetail> content) {
        filesDownloading.clear();
        rl_network_error.setVisibility(View.GONE);
        PD_Utility.dismissDialog();
        if (rv_content.getVisibility() == View.GONE)
            rv_content.setVisibility(View.VISIBLE);
        if (!content.isEmpty()) {
            if (contentAdapter == null) {
                contentAdapter = new ContentAdapter(getActivity(), content, FragmentContent.this);
                rv_content.setHasFixedSize(true);
                rv_content.addItemDecoration(new ContentItemDecoration(PD_Constant.CONTENT, 10));
                GridLayoutManager gridLayoutManager = (GridLayoutManager) rv_content.getLayoutManager();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int pos) {
                        switch (contentAdapter.getItemViewType(pos)) {
                            case ContentAdapter.HEADER_TYPE:
                                return gridLayoutManager.getSpanCount();
                            case ContentAdapter.FOLDER_TYPE:
                                return 1;
                            case ContentAdapter.FILE_TYPE:
                                return 1;
                            default:
                                return 1;
                        }
                    }
                });
                rv_content.setAdapter(contentAdapter);
                rv_content.scheduleLayoutAnimation();
            } else {
                contentAdapter.updateList(content);
                rv_content.scheduleLayoutAnimation();
                rv_content.smoothScrollToPosition(0);
            }
        }
        contentPresenter.getLevels();
    }

    public void showLevels(final ArrayList<Modal_ContentDetail> levelContents) {
        if (levelContents != null) {
            if (levelAdapter == null) {
                levelAdapter = new RV_LevelAdapter(getActivity(), levelContents, FragmentContent.this);
                rv_level.setHasFixedSize(true);
                rv_level.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                rv_level.setAdapter(levelAdapter);
            } else {
                levelAdapter.updateList(levelContents);
            }
        }
    }

    @Override
    public void levelClicked(Modal_ContentDetail detail) {
        Log.d(TAG, "onLevelClicked:");
        PrathamApplication.bubble_mp.start();
        PD_Utility.showDialog(getActivity());
        contentPresenter.getContent(detail);
    }

    @Override
    public void displayLevel(ArrayList<Modal_ContentDetail> levelContents) {
        showLevels(levelContents);
    }

    @Override
    public void onfolderClicked(int position, Modal_ContentDetail contentDetail) {
        PrathamApplication.bubble_mp.start();
        PD_Utility.showDialog(getActivity());
        contentPresenter.getContent(contentDetail);
    }

    @Override
    public void displayHeader(Modal_ContentDetail contentDetail) {
//        content_title.setText(contentDetail.getNodetitle());
    }

    @Override
    public void onDownloadClicked(int position, Modal_ContentDetail contentDetail, View reveal_view) {
        if (FastSave.getInstance().getBoolean(PD_Constant.STORAGE_ASKED, false)) {
            contentAdapter.reveal(reveal_view);
            PrathamApplication.bubble_mp.start();
            filesDownloading.put(contentDetail.getNodeid(), position);
            contentPresenter.downloadContent(contentDetail);
        } else {
            download_builder = new BlurPopupWindow.Builder(getActivity())
                    .setContentView(R.layout.download_alert_dialog)
                    .bindClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, true);
                            onDownloadClicked(position, contentDetail, reveal_view);
                            download_builder.dismiss();
                        }
                    }, R.id.btn_okay)
                    .bindClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            download_builder.dismiss();
                        }
                    }, R.id.btn_change)
                    .setGravity(Gravity.CENTER)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();
            TextView tv = (TextView) download_builder.findViewById(R.id.txt_download_alert);
            tv.setText(getString(R.string.content_download_alert) + " " + PD_Constant.STORING_IN);
            download_builder.show();
        }
    }

    @Override
    public void hideViews() {
    }

    @Override
    public void exitApp() {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("PraDigi")
                .setMessage("Do you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finishAffinity();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void increaseNotification(int number) {
//        ((ActivityMain) getActivity()).showNotificationBadge(number);
        mainView.showNotificationBadge(number);
    }

    @Override
    public void decreaseNotification(int number, Modal_ContentDetail contentDetail, ArrayList<String> selectedNodeIds) {
//        ((ActivityMain) getActivity()).hideNotificationBadge(number);
//        if (selectedNodeIds.contains(contentDetail.getNodeid())) {
    }

    @Override
    public void onDownloadError(String file_name, ArrayList<String> selectedNodeIds) {
        Toast.makeText(getActivity(), "Could not download " + file_name, Toast.LENGTH_SHORT).show();
//        contentAdapter.updateList(modal_contents);
    }

    @Override
    public void openContent(int position, Modal_ContentDetail contentDetail) {
        PrathamApplication.bubble_mp.start();
        if (isPermissionGranted(getActivity(), PermissionUtils.Manifest_RECORD_AUDIO)) {
            switch (contentDetail.getResourcetype().toLowerCase()) {
                case PD_Constant.GAME:
                    openGame(contentDetail);
                    break;
                case PD_Constant.VIDEO:
                    openVideo(contentDetail);
                    break;
                case PD_Constant.PDF:
                    openPdf(contentDetail);
                    break;
            }
        } else {
            askCompactPermission(PermissionUtils.Manifest_RECORD_AUDIO, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    switch (contentDetail.getResourcetype().toLowerCase()) {
                        case PD_Constant.GAME:
                            Toast.makeText(getActivity(), "Granted", Toast.LENGTH_SHORT).show();
                            openGame(contentDetail);
                            break;
                        case PD_Constant.VIDEO:
                            openVideo(contentDetail);
                            break;
                        case PD_Constant.PDF:
                            openPdf(contentDetail);
                            break;
                    }
                }

                @Override
                public void permissionDenied() {
                    Log.d(TAG, "permissionDenied:");
                }

                @Override
                public void permissionForeverDenied() {
                    Log.d(TAG, "permissionForeverDenied:");
                }
            });
        }
    }

    private void openPdf(Modal_ContentDetail contentDetail) {
        Intent intent = new Intent(getActivity(), Activity_PdfViewer.class);
        String f_path;
        if (contentDetail.isOnSDCard())
            f_path = PrathamApplication.contentSDPath + "/PrathamPdf/" + contentDetail.getResourcepath();
        else
            f_path = pradigiPath + "/PrathamPdf/" + contentDetail.getResourcepath();
        intent.putExtra("pdfPath", f_path);
        intent.putExtra("pdfTitle", contentDetail.getNodetitle());
        intent.putExtra("resId", contentDetail.getResourceid());
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.shrink_enter, R.anim.nothing);
    }

    private void openVideo(Modal_ContentDetail contentDetail) {
        Intent intent = new Intent(getActivity(), Activity_VPlayer.class);
        String f_path;
        if (contentDetail.isOnSDCard())
            f_path = PrathamApplication.contentSDPath + "/PrathamVideo/" + contentDetail.getResourcepath();
        else
            f_path = pradigiPath + "/PrathamVideo/" + contentDetail.getResourcepath();
        intent.putExtra("videoPath", f_path);
        intent.putExtra("videoTitle", contentDetail.getNodetitle());
        intent.putExtra("resId", contentDetail.getResourceid());
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.pop_in, R.anim.nothing);
    }

    private void openGame(Modal_ContentDetail contentDetail) {
        Intent intent = new Intent(getActivity(), Activity_WebView.class);
        String f_path;
        if (contentDetail.isOnSDCard())
            f_path = PrathamApplication.contentSDPath + "/PrathamGame/" + contentDetail.getResourcepath();
        else
            f_path = pradigiPath + "/PrathamGame/" + contentDetail.getResourcepath();
        intent.putExtra("index_path", f_path);
        intent.putExtra("resId", contentDetail.getResourceid());
        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.nothing);
        startActivity(intent);
    }

    @Override
    public void onRevealed() {
        PD_Utility.getConnectivityStatus(getActivity());
    }

    @Override
    public void onUnRevealed() {

    }

    @Subscribe
    public void setConnectionStatus(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CONNECTION_STATUS)) {
                txt_wifi_status.setText(message.getConnection_name());
                iv_wifi_status.setImageDrawable(message.getConnection_resource());
                contentPresenter.checkConnectionForRaspberry();
            }
        }
    }
}
