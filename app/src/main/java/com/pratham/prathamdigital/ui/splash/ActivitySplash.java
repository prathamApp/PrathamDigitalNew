package com.pratham.prathamdigital.ui.splash;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.animators.Animate;
import com.pratham.prathamdigital.custom.animators.Techniques;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.ui.avatar.Activity_SelectAvatar;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain;
import com.pratham.prathamdigital.util.PD_Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivitySplash extends BaseActivity implements SplashContract.splashview {

    private static final int GOOGLE_SIGN_IN = 1;
    private static final String TAG = ActivitySplash.class.getSimpleName();
    @BindView(R.id.img_splash_light)
    ImageView img_splash_light;
    @BindView(R.id.iv_pradigi)
    ImageView iv_pradigi;
    @BindView(R.id.avatar_view)
    LottieAnimationView pingpong_view;

    SplashPresenterImpl splashPresenter;
    private Context mContext;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ButterKnife.bind(this);
        mContext = this;
        splashPresenter = new SplashPresenterImpl(this, this);
        startLightsAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean signedIn = FastSave.getInstance().getBoolean(PD_Constant.IS_GOOGLE_SIGNED_IN, false);
        if (mGoogleApiClient == null && !signedIn) {
            mGoogleApiClient = splashPresenter.configureSignIn();
        } else {

        }
    }

    private void startLightsAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img_splash_light, "rotation", 0f, 360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(7600);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        img_splash_light.setLayerType(View.LAYER_TYPE_NONE, null);
        objectAnimator.start();
        iv_pradigi.setVisibility(View.VISIBLE);
        Animate.with(Techniques.ZoomIn)
                .duration(700)
                .playOn(iv_pradigi);
        pingpong_view.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashPresenter.checkConnectivity();
            }
        }, 2000);
    }

    @Override
    public void showAppUpdateDialog() {
        new BlurPopupWindow.Builder(mContext)
                .setContentView(R.layout.app_update_dialog)
                .bindClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, R.id.btn_update)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build()
                .show();
    }

    @Override
    public void signInUsingGoogle() {
        if (!FastSave.getInstance().getBoolean(PD_Constant.IS_GOOGLE_SIGNED_IN, false)) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        } else {
            splashPresenter.checkStudentList();
        }
    }

    @Override
    public void redirectToDashboard() {
        Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        finishAfterTransition();
    }

    @Override
    public void redirectToAvatar() {
        Intent intent = new Intent(ActivitySplash.this, Activity_SelectAvatar.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        finishAfterTransition();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            splashPresenter.validateSignIn(data);
        } else {
            // Google Sign In failed, update UI appropriately
            Log.d(TAG, "Login Unsuccessful.");
        }
    }
}
