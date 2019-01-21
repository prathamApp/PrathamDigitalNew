package com.pratham.prathamdigital.ui.fragment_content;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import com.pratham.prathamdigital.custom.wrappedLayoutManagers.WrapContentLinearLayoutManager;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.pdf_viewer.Activity_PdfViewer_;
import com.pratham.prathamdigital.ui.video_player.Activity_VPlayer_;
import com.pratham.prathamdigital.ui.web_view.Activity_WebView;
import com.pratham.prathamdigital.util.FragmentManagePermission;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

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

import static com.pratham.prathamdigital.PrathamApplication.pradigiPath;

@EFragment(R.layout.fragment_content)
public class FragmentContent extends FragmentManagePermission implements ContentContract.contentView,
        ContentContract.contentClick, CircularRevelLayout.CallBacks, LevelContract {

    private static final String TAG = FragmentContent.class.getSimpleName();
    private static final int SDCARD_LOCATION_CHOOSER = 99;
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

    @Bean(ContentPresenterImpl.class)
    ContentContract.contentPresenter contentPresenter;

    ContentAdapter contentAdapter;
    private RV_LevelAdapter levelAdapter;
    Map<String, Integer> filesDownloading = new HashMap<>();
    private int revealX;
    private int revealY;
    BlurPopupWindow download_builder;
    BlurPopupWindow deleteDialog;

    @AfterViews
    public void initialize() {
        frag_content_bkgd.setBackground(PD_Utility.getDrawableAccordingToMonth(getActivity()));
        contentPresenter.setView(FragmentContent.this);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        PD_Utility.showDialog(getActivity());
        if (levelAdapter == null) {
            contentPresenter.getContent(null);
        } else {
            contentPresenter.getContent();
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
    public void onDestroyView() {
        super.onDestroyView();
        contentPresenter.viewDestroyed();
    }

    @Override
    public void dismissDialog() {
        PD_Utility.dismissDialog();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMainBackPressed(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CONTENT_BACK)) {
                setContent_back();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_COMPLETE)) {
                onDownloadComplete(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.CONNECTION_STATUS)) {
                updateConnectionStatus(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_STARTED)) {
                contentPresenter.eventFileDownloadStarted(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_UPDATE)) {
                contentPresenter.eventUpdateFileProgress(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_COMPLETE)) {
                contentPresenter.eventOnDownloadCompleted(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.DOWNLOAD_FAILED)) {
                contentPresenter.eventOnDownloadFailed(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.FILE_DOWNLOAD_ERROR)) {
                onDownloadError(message);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.BROADCAST_DOWNLOADINGS)) {
                contentPresenter.broadcast_downloadings();
            }
        }
    }

    @UiThread
    public void onDownloadComplete(EventMessage message) {
        if (filesDownloading.containsKey(message.getContentDetail().getNodeid()))
            contentAdapter.notifyItemChanged(filesDownloading.get(message.getContentDetail().getNodeid()), message.getContentDetail());
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
        PD_Utility.showDialog(getActivity());
        contentPresenter.showPreviousContent();
    }

    @UiThread
    @Override
    public void showNoConnectivity() {
        PD_Utility.dismissDialog();
        rv_content.setVisibility(View.GONE);
        rl_network_error.setVisibility(View.VISIBLE);
    }

    @Click(R.id.btn_retry)
    public void setRetry() {
        PrathamApplication.bubble_mp.start();
        onResume();
    }

    @UiThread
    @Override
    public void displayContents(List<Modal_ContentDetail> content) {
        filesDownloading.clear();
        rl_network_error.setVisibility(View.GONE);
        PD_Utility.dismissDialog();
        if (rv_content.getVisibility() == View.GONE)
            rv_content.setVisibility(View.VISIBLE);
        if (!content.isEmpty()) {
            if (contentAdapter == null) {
                contentAdapter = new ContentAdapter(getActivity(), FragmentContent.this);
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
                contentAdapter.submitList(content);
            } else {
                contentAdapter.submitList(content);
                rv_content.smoothScrollToPosition(0);
            }
        }
    }

    @UiThread
    public void showLevels(ArrayList<Modal_ContentDetail> levelContents) {
        if (levelContents != null) {
            if (levelAdapter == null) {
                levelAdapter = new RV_LevelAdapter(getActivity(), FragmentContent.this);
                rv_level.setHasFixedSize(true);
                rv_level.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                rv_level.setAdapter(levelAdapter);
                levelAdapter.submitList(levelContents);
            } else {
                levelAdapter.submitList(levelContents);
            }
        }
    }

    @UiThread
    @Override
    public void levelClicked(Modal_ContentDetail detail) {
        PrathamApplication.bubble_mp.start();
        PD_Utility.showDialog(getActivity());
        contentPresenter.getContent(detail);
    }

    @UiThread
    @Override
    public void displayLevel(ArrayList<Modal_ContentDetail> levelContents) {
        ArrayList<Modal_ContentDetail> temp_levels = new ArrayList<>();
        temp_levels.addAll(levelContents);
        showLevels(temp_levels);
    }

    @UiThread
    @Override
    public void onfolderClicked(int position, Modal_ContentDetail contentDetail) {
        PrathamApplication.bubble_mp.start();
        PD_Utility.showDialog(getActivity());
        contentPresenter.getContent(contentDetail);
    }

    @Override
    public void displayHeader(Modal_ContentDetail contentDetail) {
    }

    @UiThread
    @Override
    public void onDownloadClicked(int position, Modal_ContentDetail contentDetail, View
            reveal_view) {
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
/*                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showSdCardDialog();
                                }
                            }, 1500);*/
                            download_builder.dismiss();
                        }
                    }, R.id.btn_change)
                    .setGravity(Gravity.CENTER)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(8)
                    .setTintColor(0x30000000)
                    .build();
            TextView tv = (TextView) download_builder.findViewById(R.id.txt_download_alert);
            tv.setText(getString(R.string.content_download_alert) + " " + PD_Constant.STORING_IN);
            download_builder.show();
        }
    }

    @Override
    public void deleteContent(int pos, Modal_ContentDetail contentItem) {
        deleteDialog = new BlurPopupWindow.Builder(getContext())
                .setContentView(R.layout.dialog_delete_content_alert)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .bindClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Modal_ContentDetail> data = new ArrayList<>();
                        data.addAll(contentAdapter.getData());
                        data.remove(pos);
                        contentAdapter.submitList(data);
                        contentPresenter.deleteContent(contentItem);
                        deleteDialog.dismiss();
                    }
                }, R.id.rl_delete_content)
                .bindClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                }, R.id.rl_stop_delete_content)
                .setDismissOnClickBack(true)
                .setDismissOnTouchBackground(true)
                .setScaleRatio(0.2f)
                .setBlurRadius(8)
                .setTintColor(0x30000000)
                .build();
        deleteDialog.show();
    }

    @UiThread
    public void showSdCardDialog() {
        new BlurPopupWindow.Builder(getContext())
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
                        download_builder.dismiss();
                    }
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
        }
    }

    @UiThread
    @Override
    public void hideViews() {
    }

    @UiThread
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

    @UiThread
    @Override
    public void increaseNotification(int number) {
//        ((ActivityMain) getActivity()).showNotificationBadge(number);
//        mainView.showNotificationBadge(number);
    }

    @UiThread
    @Override
    public void decreaseNotification(int number, Modal_ContentDetail
            contentDetail, ArrayList<String> selectedNodeIds) {
//        ((ActivityMain) getActivity()).hideNotificationBadge(number);
//        if (selectedNodeIds.contains(contentDetail.getNodeid())) {
    }

    @UiThread
    @Override
    public void onDownloadError(EventMessage message) {
//        Toast.makeText(getActivity(), "Could not download " + file_name, Toast.LENGTH_SHORT).show();
        if (filesDownloading.containsKey(message.getContentDetail().getNodeid()))
            contentAdapter.notifyItemChanged(filesDownloading.get(message.getContentDetail().getNodeid()), message.getContentDetail());
    }

    @UiThread
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

    @UiThread
    public void openPdf(Modal_ContentDetail contentDetail) {
        Intent intent = new Intent(getActivity(), Activity_PdfViewer_.class);
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

    @UiThread
    public void openVideo(Modal_ContentDetail contentDetail) {
        Intent intent = new Intent(getActivity(), Activity_VPlayer_.class);
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

    @UiThread
    public void openGame(Modal_ContentDetail contentDetail) {
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
}
