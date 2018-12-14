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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.ui.attendance_activity.AttendanceActivity;
import com.pratham.prathamdigital.util.PD_Constant;
import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.RichPathAnimator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivitySplash extends BaseActivity implements SplashContract.splashview {

    private static final int GOOGLE_SIGN_IN = 1;
    private static final String TAG = ActivitySplash.class.getSimpleName();
    @BindView(R.id.img_splash_light)
    ImageView img_splash_light;
    //    @BindView(R.id.iv_pradigi)
//    ImageView iv_pradigi;
    @BindView(R.id.avatar_view)
    LottieAnimationView pingpong_view;
    @BindView(R.id.rich)
    RichPathView rich;

    SplashContract.splashPresenter splashPresenter;
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
        splashPresenter.populateDefaultDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!PrathamApplication.isTablet) {
//            boolean signedIn = FastSave.getInstance().getBoolean(PD_Constant.IS_GOOGLE_SIGNED_IN, false);
//            if (mGoogleApiClient == null && !signedIn) {
//                mGoogleApiClient = splashPresenter.configureSignIn();
//            }
//        }
        // Populate initial values
    }

    private void startLightsAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img_splash_light, "rotation", 0f, 360f);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(7600);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        img_splash_light.setLayerType(View.LAYER_TYPE_NONE, null);
        objectAnimator.start();
        rich.setVisibility(View.VISIBLE);
//        Animate.with(Techniques.ZoomIn)
//                .duration(700)
//                .playOn(iv_pradigi);
        final RichPath[] allPaths = rich.findAllRichPaths();
        RichPathAnimator.animate(allPaths).trimPathEnd(0, 1).interpolator(new AccelerateDecelerateInterpolator()).duration(1800).start();
        pingpong_view.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashPresenter.checkIfContentinSDCard();
//                splashPresenter.checkConnectivity();
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
//        if (!FastSave.getInstance().getBoolean(PD_Constant.IS_GOOGLE_SIGNED_IN, false)) {
//            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
//        } else {
//        splashPresenter.checkStudentList();
//        }
    }

    @Override
    public void redirectToDashboard() {
        Intent intent = new Intent(ActivitySplash.this, AttendanceActivity.class);
        intent.putExtra(PD_Constant.STUDENT_ADDED, true);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        finishAfterTransition();
    }

    @Override
    public void redirectToAvatar() {
        Intent intent = new Intent(ActivitySplash.this, AttendanceActivity.class);
        intent.putExtra(PD_Constant.STUDENT_ADDED, false);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        finishAfterTransition();
    }

    @Override
    public void redirectToAttendance() {
        Intent intent = new Intent(ActivitySplash.this, AttendanceActivity.class);
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
