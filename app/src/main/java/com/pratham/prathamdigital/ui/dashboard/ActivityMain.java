package com.pratham.prathamdigital.ui.dashboard;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.NotificationBadge;
import com.pratham.prathamdigital.custom.shapes.ShapeOfView;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.services.LocationService;
import com.pratham.prathamdigital.ui.download_list.DownloadListFragment;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent;
import com.pratham.prathamdigital.ui.settings_activity.SettingsActivity;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMain extends BaseActivity implements ContentContract.mainView, LevelContract {

    private static final String TAG = ActivityMain.class.getSimpleName();
    @BindView(R.id.main_root)
    RelativeLayout main_root;
    @BindView(R.id.avatar_view)
    public LottieAnimationView avatar_view;
    @BindView(R.id.back_view)
    public LottieAnimationView back_view;
    @BindView(R.id.download_notification)
    NotificationBadge download_notification;
    @BindView(R.id.download_badge)
    RelativeLayout download_badge;
    @BindView(R.id.avatar_shape)
    public ShapeOfView avatar_shape;
    @BindView(R.id.search_shape)
    public ShapeOfView search_shape;
    @BindView(R.id.rv_level)
    public RecyclerView rv_level;

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;
    private RV_LevelAdapter levelAdapter;

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
        requestLocation();
        avatar_view.setAnimation(FastSave.getInstance().getString(PD_Constant.AVATAR, "avatars/rabbit.json"));
    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(main_root.getWidth(), main_root.getHeight()) * 1.1);
        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(main_root, x, y, 0, finalRadius);
        circularReveal.setDuration(600);
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
        // make the view visible and start the animation
        main_root.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @OnClick(R.id.download_badge)
    public void showDownloadList() {
        PrathamApplication.bubble_mp.start();
        DownloadListFragment fragment = new DownloadListFragment();
        fragment.show(getSupportFragmentManager(), DownloadListFragment.class.getSimpleName());
    }

    @OnClick(R.id.avatar_view)
    public void openSettingsActivity() {
        PrathamApplication.bubble_mp.start();
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, avatar_view, "transition");
        Point points = PD_Utility.getCenterPointOfView(avatar_view);
        int revealX = (int) points.x;
        int revealY = (int) points.y;
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_Y, revealY);
        ActivityCompat.startActivity(ActivityMain.this, intent, options.toBundle());
    }

    public void showLevels(final ArrayList<Modal_ContentDetail> levelContents) {
        if (levelContents != null) {
            if (levelAdapter == null) {
                levelAdapter = new RV_LevelAdapter(ActivityMain.this, levelContents, ActivityMain.this);
                rv_level.setHasFixedSize(true);
                rv_level.setLayoutManager(new LinearLayoutManager(ActivityMain.this, LinearLayoutManager.HORIZONTAL, false));
                rv_level.setAdapter(levelAdapter);
            } else {
                levelAdapter.updateList(levelContents);
            }
        }
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

    @OnClick(R.id.back_view)
    public void setMainBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(PD_Constant.MAIN_BACK);
    }

    public void requestLocation() {
        if (!isPermissionsGranted(ActivityMain.this, new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION
                , PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION
                , PermissionUtils.Manifest_ACCESS_FINE_LOCATION})) {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_ACCESS_COARSE_LOCATION
                    , PermissionUtils.Manifest_ACCESS_FINE_LOCATION}, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    new LocationService(ActivityMain.this).checkLocation();
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                }
            });
        } else {
            new LocationService(ActivityMain.this).checkLocation();
        }
    }

    @Override
    public void levelClicked(Modal_ContentDetail detail) {
        EventBus.getDefault().post(detail);
    }
}