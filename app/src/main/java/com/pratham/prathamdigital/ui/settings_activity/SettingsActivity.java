package com.pratham.prathamdigital.ui.settings_activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.tab_bar.NavigationTabBar;
import com.pratham.prathamdigital.util.PD_Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.main_settings_root)
    RelativeLayout main_settings_root;
    @BindView(R.id.settings_back)
    ImageView settings_back;
    @BindView(R.id.settings_vp)
    ViewPager settings_vp;
    @BindView(R.id.setting_download)
    ImageView setting_download;
    @BindView(R.id.setting_language)
    ImageView setting_language;
    @BindView(R.id.setting_share)
    ImageView setting_share;

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;

    SettingsPagerAdapter pagerAdapter;
    final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        if (savedInstanceState == null && getIntent().hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                getIntent().hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            main_settings_root.setVisibility(View.INVISIBLE);
            revealX = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
            ViewTreeObserver viewTreeObserver = main_settings_root.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        main_settings_root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            main_settings_root.setVisibility(View.VISIBLE);
        }
        switch (getIntent().getStringExtra(PD_Constant.VIEW_TYPE)) {
            case PD_Constant.DOWNLOAD:
                initializeVP(true);
                initializeTab(true);
                break;
            case PD_Constant.SETTINGS:
                initializeVP(false);
                initializeTab(false);
                break;
        }
    }

    private void initializeTab(boolean onlyDownload) {
        if (onlyDownload) {
            setting_download.setVisibility(View.VISIBLE);
            setting_language.setVisibility(View.GONE);
            setting_share.setVisibility(View.GONE);
        } else {
            setting_download.setVisibility(View.VISIBLE);
            setting_language.setVisibility(View.VISIBLE);
            setting_share.setVisibility(View.VISIBLE);
        }
    }

    private void initializeVP(boolean onlyDownload) {
        if (onlyDownload) {
            pagerAdapter = new SettingsPagerAdapter(1, getSupportFragmentManager());
        } else {
            pagerAdapter = new SettingsPagerAdapter(3, getSupportFragmentManager());
        }
        settings_vp.setAdapter(pagerAdapter);
    }

    @OnClick(R.id.setting_download)
    public void setSetting_download() {
        settings_vp.setCurrentItem(0, true);
    }

    @OnClick(R.id.setting_language)
    public void setSetting_language() {
        settings_vp.setCurrentItem(1, true);
    }

    @OnClick(R.id.setting_share)
    public void setSetting_share() {
        settings_vp.setCurrentItem(2, true);
    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(main_settings_root.getWidth(), main_settings_root.getHeight()) * 1.1);
        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(main_settings_root, x, y, 0, finalRadius);
        circularReveal.setDuration(500);
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
        // make the view visible and start the animation
        main_settings_root.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @OnClick(R.id.settings_back)
    protected void unRevealActivity() {
        float finalRadius = (float) (Math.max(main_settings_root.getWidth(), main_settings_root.getHeight()) * 1.1);
        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(main_settings_root, revealX, revealY, finalRadius, 0);
        circularReveal.setDuration(500);
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                main_settings_root.setVisibility(View.INVISIBLE);
                finish();
            }
        });
        // make the view visible and start the animation
        circularReveal.start();
    }
}
