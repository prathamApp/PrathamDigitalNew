package com.pratham.prathamdigital.ui.settings_activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;

import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;

    SettingsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
//        if (savedInstanceState == null && getIntent().hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
//                getIntent().hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
//            main_settings_root.setVisibility(View.INVISIBLE);
//            revealX = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
//            revealY = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
//            ViewTreeObserver viewTreeObserver = main_settings_root.getViewTreeObserver();
//            if (viewTreeObserver.isAlive()) {
//                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        revealActivity(revealX, revealY);
//                        main_settings_root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    }
//                });
//            }
//        } else {
//            main_settings_root.setVisibility(View.VISIBLE);
//        }
//        initializeTab();
    }

//    private void initializeTab() {
//        pagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager());
//        settings_vp.setAdapter(pagerAdapter);
//    }
//
//    protected void revealActivity(int x, int y) {
//        float finalRadius = (float) (Math.max(main_settings_root.getWidth(), main_settings_root.getHeight()) * 1.1);
//        // create the animator for this view (the start radius is zero)
//        Animator circularReveal = ViewAnimationUtils.createCircularReveal(main_settings_root, x, y, 0, finalRadius);
//        circularReveal.setDuration(500);
//        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
//        // make the view visible and start the animation
//        main_settings_root.setVisibility(View.VISIBLE);
//        circularReveal.start();
//    }
//
//    @OnClick(R.id.settings_back)
//    protected void setSettings_back() {
//        PrathamApplication.bubble_mp.start();
//        int currentposition = settings_vp.getCurrentItem();
//        switch (currentposition) {
//            case 0:
//            case 1:
//                unRevealAndCloseActivity();
//                break;
//            case 2:
//                EventBus.getDefault().post(PD_Constant.SETTINGS_BACK);
//                break;
//        }
//    }
//
//    private void unRevealAndCloseActivity() {
//        float finalRadius = (float) (Math.max(main_settings_root.getWidth(), main_settings_root.getHeight()) * 1.1);
//        // create the animator for this view (the start radius is zero)
//        Animator circularReveal = ViewAnimationUtils.createCircularReveal(main_settings_root, revealX, revealY, finalRadius, 0);
//        circularReveal.setDuration(500);
//        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
//        circularReveal.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                main_settings_root.setVisibility(View.INVISIBLE);
//                finish();
//            }
//        });
//        // make the view visible and start the animation
//        circularReveal.start();
//    }
}
