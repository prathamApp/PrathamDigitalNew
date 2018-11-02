package com.pratham.prathamdigital.ui.menu;

import android.animation.Animator;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent;
import com.pratham.prathamdigital.ui.fragment_language.FragmentLanguage;
import com.pratham.prathamdigital.ui.fragment_share_recieve.FragmentShareRecieve;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMenu extends BaseActivity {

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private int revealX;
    private int revealY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        PD_Utility.showFragment(this, new FragmentLanguage(), R.id.main_frame,
                null, FragmentLanguage.class.getSimpleName());
    }

//    @OnClick(R.id.lottie_home)
//    public void setLottie_home() {
//        lottie_home.playAnimation();
//        Point loc = PD_Utility.getCenterPointOfView(lottie_home);
//        Fragment fragment = FragmentContent.newInstance((int) loc.x, (int) loc.y, R.color.red);
//        PD_Utility.showFragment(this, fragment, R.id.main_frame,
//                null, FragmentContent.class.getSimpleName());
//    }
}
