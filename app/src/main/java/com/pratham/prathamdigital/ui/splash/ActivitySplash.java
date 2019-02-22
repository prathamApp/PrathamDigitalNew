package com.pratham.prathamdigital.ui.splash;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.services.PrathamSmartSync;
import com.pratham.prathamdigital.ui.attendance_activity.AttendanceActivity_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.RichPathAnimator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.splash_activity)
public class ActivitySplash extends BaseActivity implements SplashContract.splashview {

    private static final int GOOGLE_SIGN_IN = 1;
    private static final String TAG = ActivitySplash.class.getSimpleName();
    private static final int LIGHT_ANIMATION = 2;
    private static final int UPDATE_DIALOG = 3;
    private static final int REDIRECT_TO_DASHBOARD = 4;
    private static final int REDIRECT_TO_AVATAR = 5;
    private static final int REDIRECT_TO_ATTENDANCE = 6;
    @ViewById(R.id.img_splash_light)
    ImageView img_splash_light;
    @ViewById(R.id.avatar_view)
    LottieAnimationView pingpong_view;
    @ViewById(R.id.rich)
    RichPathView rich;

    @Bean(SplashPresenterImpl.class)
    SplashContract.splashPresenter splashPresenter;
    private Context mContext;
    GoogleApiClient mGoogleApiClient;

    @AfterViews
    public void initializeViews() {
        mContext = this;
        splashPresenter.clearPreviousBuildData();
        startLightsAnimation();
        splashPresenter.populateDefaultDB();
        PrathamSmartSync.pushTabletJsons(false);
    }

    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LIGHT_ANIMATION:
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img_splash_light, "rotation", 0f, 360f);
                    objectAnimator.setInterpolator(new LinearInterpolator());
                    objectAnimator.setDuration(7600);
                    objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
                    img_splash_light.setLayerType(View.LAYER_TYPE_NONE, null);
                    objectAnimator.start();
                    rich.setVisibility(View.VISIBLE);
                    final RichPath[] allPaths = rich.findAllRichPaths();
                    RichPathAnimator.animate(allPaths).trimPathEnd(0, 1).interpolator(new AccelerateDecelerateInterpolator()).duration(1800).start();
                    pingpong_view.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!PrathamApplication.isTablet)
                                signInUsingGoogle();
                            else
                                splashPresenter.checkIfContentinSDCard();
                        }
                    }, 2000);
                    break;
                case UPDATE_DIALOG:
                    new BlurPopupWindow.Builder(mContext)
                            .setContentView(R.layout.app_update_dialog)
                            .bindClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.pratham.prathamdigital"));
                                    startActivity(intent);
                                }
                            }, R.id.btn_update)
                            .setGravity(Gravity.CENTER)
                            .setDismissOnTouchBackground(false)
                            .setDismissOnClickBack(false)
                            .setScaleRatio(0.2f)
                            .setBlurRadius(10)
                            .setTintColor(0x30000000)
                            .build()
                            .show();
                    break;
                case REDIRECT_TO_DASHBOARD:
                    Intent intent = new Intent(ActivitySplash.this, AttendanceActivity_.class);
                    intent.putExtra(PD_Constant.STUDENT_ADDED, true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    finishAfterTransition();
                    break;
                case REDIRECT_TO_AVATAR:
                    Intent intent2 = new Intent(ActivitySplash.this, AttendanceActivity_.class);
                    intent2.putExtra(PD_Constant.STUDENT_ADDED, false);
                    startActivity(intent2);
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    finishAfterTransition();
                    break;
                case REDIRECT_TO_ATTENDANCE:
                    Intent intent3 = new Intent(ActivitySplash.this, AttendanceActivity_.class);
                    startActivity(intent3);
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    finishAfterTransition();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!PrathamApplication.isTablet) {
            boolean signedIn = FastSave.getInstance().getBoolean(PD_Constant.IS_GOOGLE_SIGNED_IN, false);
            if (mGoogleApiClient == null && !signedIn) {
                mGoogleApiClient = splashPresenter.configureSignIn();
            }
        }
    }

    @UiThread
    public void startLightsAnimation() {
        mhandler.sendEmptyMessage(LIGHT_ANIMATION);
    }

    @UiThread
    @Override
    public void showAppUpdateDialog() {
        mhandler.sendEmptyMessage(UPDATE_DIALOG);
    }

    @Override
    public void signInUsingGoogle() {
        if (!FastSave.getInstance().getBoolean(PD_Constant.IS_GOOGLE_SIGNED_IN, false)) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        } else {
//            splashPresenter.checkStudentList();
            splashPresenter.checkIfContentinSDCard();
        }
    }

    @UiThread
    @Override
    public void redirectToDashboard() {
        mhandler.sendEmptyMessage(REDIRECT_TO_DASHBOARD);
    }

    @UiThread
    @Override
    public void redirectToAvatar() {
        mhandler.sendEmptyMessage(REDIRECT_TO_AVATAR);
    }

    @UiThread
    @Override
    public void redirectToAttendance() {
        mhandler.sendEmptyMessage(REDIRECT_TO_ATTENDANCE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            splashPresenter.validateSignIn(data);
        } else {
            // Google Sign In failed, update UI appropriately
            Log.d(TAG, "Login Unsuccessful.");
            splashPresenter.checkIfContentinSDCard();
        }
    }
}
