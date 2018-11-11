package com.pratham.prathamdigital.ui.attendance_activity;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AttendanceActivity extends BaseActivity {
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;

    @BindView(R.id.main_attendance_root)
    RelativeLayout main_attendance_root;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ButterKnife.bind(this);
        if (savedInstanceState == null && getIntent().hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                getIntent().hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            main_attendance_root.setVisibility(View.INVISIBLE);
            revealX = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = getIntent().getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
            ViewTreeObserver viewTreeObserver = main_attendance_root.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        main_attendance_root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            main_attendance_root.setVisibility(View.VISIBLE);
        }
    }

//    @OnClick(R.id.setting_download)
//    public void setSetting_download() {
//        PrathamApplication.bubble_mp.start();
//    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(main_attendance_root.getWidth(), main_attendance_root.getHeight()) * 1.1);
        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(main_attendance_root, x, y, 0, finalRadius);
        circularReveal.setDuration(500);
        circularReveal.setInterpolator(new AccelerateDecelerateInterpolator());
        // make the view visible and start the animation
        main_attendance_root.setVisibility(View.VISIBLE);
        circularReveal.start();
    }
}
