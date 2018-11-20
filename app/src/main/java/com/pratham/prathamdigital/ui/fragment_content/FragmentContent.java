package com.pratham.prathamdigital.ui.fragment_content;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.ContentItemDecoration;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain;
import com.pratham.prathamdigital.ui.pdf_viewer.Activity_PdfViewer;
import com.pratham.prathamdigital.ui.video_player.Activity_VPlayer;
import com.pratham.prathamdigital.ui.web_view.Activity_WebView;
import com.pratham.prathamdigital.util.FragmentManagePermission;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.prathamdigital.PrathamApplication.pradigiPath;

public class FragmentContent extends FragmentManagePermission implements ContentContract.contentView, ContentContract.contentClick {

    private static final String TAG = FragmentContent.class.getSimpleName();
    @BindView(R.id.content_header)
    RelativeLayout content_header;
    //    @BindView(R.id.lottie_content_bkgd)
//    LottieAnimationView lottie_content_bkgd;
    @BindView(R.id.rv_content)
    RecyclerView rv_content;
    @BindView(R.id.content_back)
    ImageView content_back;
    @BindView(R.id.content_title)
    TextView content_title;
    @BindView(R.id.rl_network_error)
    RelativeLayout rl_network_error;

    ContentPresenterImpl contentPresenter;
    ArrayList<Modal_ContentDetail> modal_contents;
    ContentAdapter contentAdapter;
    ContentContract.mainView mainView;

    public static FragmentContent newInstance(int centerX, int centerY, int color) {
        Bundle args = new Bundle();
        args.putInt("cx", centerX);
        args.putInt("cy", centerY);
        args.putInt("color", color);
        FragmentContent fragment = new FragmentContent();
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
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
//        if (getArguments() != null) {
//            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
//                                           int oldRight, int oldBottom) {
//                    v.removeOnLayoutChangeListener(this);
//                    int cx = getArguments().getInt("cx");
//                    int cy = getArguments().getInt("cy");
//                    int radius = (int) Math.hypot(right, bottom);
//                    Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
//                    reveal.setInterpolator(new DecelerateInterpolator(2f));
//                    reveal.setDuration(1000);
//                    reveal.start();
//                }
//            });
//        }
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
        ButterKnife.bind(this, view);
        contentPresenter = new ContentPresenterImpl(getActivity(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((ActivityMain) getActivity()).avatar_shape.getVisibility() == View.VISIBLE) {
            if (!BaseActivity.catLoadingView.isAdded())
                BaseActivity.catLoadingView.show(getActivity().getSupportFragmentManager(), "");
            contentPresenter.getContent(null);
        }
    }

    @Override
    public void showNoConnectivity() {
        BaseActivity.catLoadingView.dismiss();
        rv_content.setVisibility(View.GONE);
        rl_network_error.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.txt_retry)
    public void setRetry() {
        PrathamApplication.bubble_mp.start();
        onResume();
    }

    @Override
    public void displayContents(final ArrayList<Modal_ContentDetail> content) {
        rl_network_error.setVisibility(View.GONE);
        BaseActivity.catLoadingView.dismiss();
        if (rv_content.getVisibility() == View.GONE)
            rv_content.setVisibility(View.VISIBLE);
        if (!content.isEmpty()) {
            modal_contents = new ArrayList<>();
            modal_contents.addAll(content);
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
//                rv_content.setLayoutManager(gridLayoutManager);
                rv_content.setAdapter(contentAdapter);
                rv_content.scheduleLayoutAnimation();
//                new LinearSnapHelper().attachToRecyclerView(rv_content);
            } else {
                contentAdapter.updateList(content);
                rv_content.scheduleLayoutAnimation();
            }
        }
    }

    @Override
    public void onfolderClicked(int position, Modal_ContentDetail contentDetail) {
        PrathamApplication.bubble_mp.start();
        if (!BaseActivity.catLoadingView.isAdded())
            BaseActivity.catLoadingView.show(getActivity().getSupportFragmentManager(), "");
        contentPresenter.getContent(contentDetail);
    }

    @Override
    public void displayHeader(Modal_ContentDetail contentDetail) {
        content_title.setText(contentDetail.getNodetitle());
    }

    @Override
    public void onDownloadClicked(int position, Modal_ContentDetail contentDetail) {
        PrathamApplication.bubble_mp.start();
//        contentAdapter.updateList(contentPresenter.getUpdatedList(contentDetail));
        contentPresenter.downloadContent(contentDetail);
    }

    @Override
    public void hideViews() {
//        hideViewUp(((ActivityMain) getActivity()).main_tab);
        hideViewSide(((ActivityMain) getActivity()).avatar_shape);
        hideViewSide(((ActivityMain) getActivity()).search_shape);
        content_header.setVisibility(View.VISIBLE);
    }

    @Override
    public void showViews() {
//        showViewDown(((ActivityMain) getActivity()).main_tab);
        showViewSide(((ActivityMain) getActivity()).avatar_shape);
        showViewSide(((ActivityMain) getActivity()).search_shape);
        content_header.setVisibility(View.GONE);
    }

    @Override
    public void increaseNotification(int number) {
//        ((ActivityMain) getActivity()).showNotificationBadge(number);
        mainView.showNotificationBadge(number);
    }

    @Override
    public void decreaseNotification(int number, Modal_ContentDetail contentDetail) {
//        ((ActivityMain) getActivity()).hideNotificationBadge(number);
        for (int i = 0; i < modal_contents.size(); i++) {
            if (modal_contents.get(i).getNodeid() != null)
                if (modal_contents.get(i).getNodeid().equalsIgnoreCase(contentDetail.getNodeid())) {
                    modal_contents.set(i, contentDetail);
                    break;
                }
        }
        contentAdapter.updateList(modal_contents);
        mainView.hideNotificationBadge(number);
    }

    @OnClick(R.id.content_back)
    public void setContent_back() {
        PrathamApplication.bubble_mp.start();
        if (!BaseActivity.catLoadingView.isAdded())
            BaseActivity.catLoadingView.show(getActivity().getSupportFragmentManager(), "");
        contentPresenter.showPreviousContent();
    }

    public void hideViewUp(View view) {
        view.animate()
//                .translationY(0)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                        content_header.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void hideViewSide(View view) {
        view.animate()
//                .translationX(0)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public void showViewDown(View view) {
        view.animate()
//                .translationY(view.getHeight())
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void showViewSide(View view) {
        view.animate()
//                .translationX(view.getWidth())
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
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
        File directory = new File(pradigiPath + "/PrathamPdf");
        String f_path;
        if (!contentDetail.getResourcepath().contains("http://")) {
            f_path = contentDetail.getResourcepath();
        } else {
            f_path = "file:///" + directory.getAbsolutePath() + "/" + contentDetail.getNodekeywords() + ".pdf";
        }
        intent.putExtra("pdfPath", f_path);
        intent.putExtra("pdfTitle", contentDetail.getNodetitle());
        intent.putExtra("resId", contentDetail.getResourceid());
        startActivity(intent);
    }

    private void openVideo(Modal_ContentDetail contentDetail) {
        Intent intent = new Intent(getActivity(), Activity_VPlayer.class);
        File directory = new File(pradigiPath + "/PrathamVideo");
        String f_path;
        if (!contentDetail.getResourcepath().contains("http://")) {
            f_path = contentDetail.getResourcepath();
        } else {
            f_path = "file:///" + directory.getAbsolutePath() + "/" + contentDetail.getNodekeywords() + ".mp4";
        }
        intent.putExtra("videoPath", f_path);
        intent.putExtra("videoTitle", contentDetail.getNodetitle());
        intent.putExtra("resId", contentDetail.getResourceid());
        startActivity(intent);
    }

    private void openGame(Modal_ContentDetail contentDetail) {
        Intent intent = new Intent(getActivity(), Activity_WebView.class);
        File directory = new File(pradigiPath + "/PrathamGame");
        String f_path;
        String folder_path;
        if (!contentDetail.getResourcepath().contains("http://")) {
            f_path = contentDetail.getResourcepath();
            folder_path = contentDetail.getResourcepath().replace("index.html", "");
        } else {
            f_path = directory.getAbsolutePath() + "/" + contentDetail.getNodekeywords() + "/index.html";
            folder_path = directory.getAbsolutePath() + "/" +
                    new StringTokenizer(contentDetail.getNodekeywords() + "/index.html", "/").nextToken() + "/";
        }
        intent.putExtra("index_path", f_path);
        intent.putExtra("path", folder_path);
        intent.putExtra("resId", contentDetail.getResourceid());
        startActivity(intent);
    }
}
