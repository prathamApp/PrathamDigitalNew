package com.pratham.prathamdigital.ui.dashboard;

import android.animation.Animator;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.NotificationBadge;
import com.pratham.prathamdigital.custom.shapes.ShapeOfView;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_FileDownloading;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent;
import com.pratham.prathamdigital.ui.fragment_language.FragmentLanguage;
import com.pratham.prathamdigital.ui.fragment_share_recieve.FragmentShareRecieve;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.prathamdigital.util.PD_Utility.getCenterPointOfView;

public class ActivityMain extends BaseActivity implements ContentContract.mainView {

    @BindView(R.id.main_root)
    RelativeLayout main_root;
    @BindView(R.id.avatar_shape)
    public ShapeOfView avatar_shape;
    @BindView(R.id.main_tab)
    public LinearLayout main_tab;
    @BindView(R.id.lottie_home)
    LottieAnimationView lottie_home;
    @BindView(R.id.lottie_language)
    LottieAnimationView lottie_language;
    @BindView(R.id.lottie_share)
    LottieAnimationView lottie_share;
    @BindView(R.id.avatar_view)
    LottieAnimationView avatar_view;
    @BindView(R.id.download_notification)
    NotificationBadge download_notification;
    @BindView(R.id.download_badge)
    RelativeLayout download_badge;
//    @BindView(R.id.rv_download)
//    RecyclerView rv_download;

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;
    private Map<Integer, Modal_FileDownloading> currentlyDownloading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        if (savedInstanceState == null && getIntent().hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                getIntent().hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            main_root.setVisibility(View.INVISIBLE);
            revealX = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
            ViewTreeObserver viewTreeObserver = main_root.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        main_root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            main_root.setVisibility(View.VISIBLE);
        }
        PD_Utility.showFragment(this, new FragmentContent(), R.id.main_frame,
                null, FragmentContent.class.getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        avatar_view.setAnimation(FastSave.getInstance().getString(PD_Constant.AVATAR, "rabbit.json"));
    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(main_root.getWidth(), main_root.getHeight()) * 1.1);
        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(main_root, x, y, 0, finalRadius);
        circularReveal.setDuration(1000);
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
        // make the view visible and start the animation
        main_root.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @OnClick(R.id.lottie_home)
    public void setLottie_home() {
        lottie_home.playAnimation();
        Point loc = getCenterPointOfView(lottie_home);
        Fragment fragment = FragmentContent.newInstance((int) loc.x, (int) loc.y, R.color.red);
        PD_Utility.showFragment(this, fragment, R.id.main_frame,
                null, FragmentContent.class.getSimpleName());
    }

    @OnClick(R.id.lottie_language)
    public void setLottie_language() {
        lottie_language.playAnimation();
        Point loc = getCenterPointOfView(lottie_language);
        Fragment fragment = FragmentLanguage.newInstance((int) loc.x, (int) loc.y, R.color.purple);
        PD_Utility.showFragment(this, fragment, R.id.main_frame,
                null, FragmentLanguage.class.getSimpleName());
    }

    @OnClick(R.id.lottie_share)
    public void setLottie_share() {
        lottie_share.playAnimation();
        Point loc = getCenterPointOfView(lottie_share);
        Fragment fragment = FragmentShareRecieve.newInstance((int) loc.x, (int) loc.y, R.color.dark_blue);
        PD_Utility.showFragment(this, fragment, R.id.main_frame,
                null, FragmentShareRecieve.class.getSimpleName());
    }

    @OnClick(R.id.download_badge)
    public void showDownloadList() {
    }

    @Override
    public void showNotificationBadge(int downloadNumber) {
        if (downloadNumber == 1) {
            ScaleAnimation animation = new ScaleAnimation(0f, 1f, 0f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setFillAfter(true);
            animation.setDuration(300);
            download_badge.setAnimation(animation);
            download_badge.setVisibility(View.VISIBLE);
            animation.start();
        }
        download_notification.setNumber(downloadNumber);
    }

    @Override
    public void hideNotificationBadge(int number) {
        if (number == 0) {
            ScaleAnimation animation = new ScaleAnimation(1f, 0f, 1f, 0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(300);
            animation.setFillAfter(false);
            download_badge.setAnimation(animation);
            animation.start();
            download_badge.setVisibility(View.GONE);
        }
        download_notification.setNumber(number);
    }

    @Override
    public void updateDownloadList(Map<Integer, Modal_FileDownloading> downloadings) {
        currentlyDownloading = downloadings;
    }
}