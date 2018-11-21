package com.pratham.prathamdigital.ui.settings_activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.util.PD_Constant;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.main_settings_root)
    RelativeLayout main_settings_root;
    @BindView(R.id.settings_back)
    ImageView settings_back;
    @BindView(R.id.settings_vp)
    ViewPager settings_vp;
    @BindView(R.id.settings_tab_holder)
    RelativeLayout settings_tab_holder;
    @BindView(R.id.tab_card)
    MaterialCardView tab_card;

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
        initializeTab();
    }

    private void initializeTab() {
        pagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager());
        settings_vp.setAdapter(pagerAdapter);
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
    protected void setSettings_back() {
        PrathamApplication.bubble_mp.start();
        int currentposition = settings_vp.getCurrentItem();
        switch (currentposition) {
            case 0:
            case 1:
                unRevealAndCloseActivity();
                break;
            case 2:
                EventBus.getDefault().post(PD_Constant.SETTINGS_BACK);
                break;
        }
    }

    private void unRevealAndCloseActivity() {
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

    @OnTouch(R.id.settings_language)
    public boolean setSettingLanguage(View view, MotionEvent event) {
        animate(1, event, view, getResources().getColor(R.color.red));
        return onTouchEvent(event);
    }

    @OnTouch(R.id.settings_share)
    public boolean setSettingShare(View view, MotionEvent event) {
        animate(2, event, view, getResources().getColor(R.color.green));
        return onTouchEvent(event);
    }

    @OnTouch(R.id.settings)
    public boolean setSetting(View view, MotionEvent event) {
        animate(3, event, view, getResources().getColor(R.color.blue));
        return onTouchEvent(event);
    }

    @BindView(R.id.sliding_strip)
    View sliding_strip;

    private void animate(int position, MotionEvent event, View v, int backgroundColor) {
        float start = 0F;
        float end = (float) Math.hypot(settings_tab_holder.getWidth(), settings_tab_holder.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(settings_tab_holder, (int) event.getRawX(), (int) event.getY(), start, end);
        animator.setDuration(1150L);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                tab_card.setCardBackgroundColor(((ColorDrawable) settings_tab_holder.getBackground()).getColor());
                settings_tab_holder.setBackgroundColor(backgroundColor);
                sliding_strip.animate()
                        .x(v.getX())
                        .y(v.getY())
                        .setDuration(500)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                sliding_strip.setX(v.getX());
                                sliding_strip.setY(v.getY());
                            }
                        }).start();
            }
        });
        animator.start();
    }
}
