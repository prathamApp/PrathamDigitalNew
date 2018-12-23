package com.pratham.prathamdigital.ui.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.NotificationBadge;
import com.pratham.prathamdigital.custom.scaling_view.ScalingLayout;
import com.pratham.prathamdigital.custom.scaling_view.ScalingLayoutListener;
import com.pratham.prathamdigital.custom.scaling_view.State;
import com.pratham.prathamdigital.custom.spotlight.SpotlightListener;
import com.pratham.prathamdigital.custom.spotlight.SpotlightSequence;
import com.pratham.prathamdigital.custom.spotlight.SpotlightView;
import com.pratham.prathamdigital.ftpSettings.FsService;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.ui.connect_dialog.ConnectDialog;
import com.pratham.prathamdigital.ui.download_list.DownloadListFragment;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent;
import com.pratham.prathamdigital.ui.fragment_language.FragmentLanguage;
import com.pratham.prathamdigital.ui.fragment_share_recieve.FragmentShareRecieve;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMain extends BaseActivity implements ContentContract.mainView {

    private static final String TAG = ActivityMain.class.getSimpleName();
    @BindView(R.id.main_root)
    CoordinatorLayout main_root;
    @BindView(R.id.download_notification)
    NotificationBadge download_notification;
    @BindView(R.id.download_badge)
    RelativeLayout download_badge;
    @BindView(R.id.top_scaling)
    ScalingLayout top_scaling;
    @BindView(R.id.outer_area)
    View outer_area;
    @BindView(R.id.tab_card)
    MaterialCardView tab_card;
    @BindView(R.id.sliding_strip)
    View sliding_strip;
    @BindView(R.id.sheet_tab_holder)
    RelativeLayout sheet_tab_holder;
    @BindView(R.id.pradigi_icon)
    ImageView pradigi_icon;
    @BindView(R.id.sheet_connect)
    ImageView sheet_connect;
    @BindView(R.id.sheet_home)
    ImageView sheet_home;
    @BindView(R.id.sheet_language)
    ImageView sheet_language;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        top_scaling.setListener(listener);
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, 0);
        bundle.putInt(PD_Constant.REVEALY, 0);
        PD_Utility.showFragment(this, new FragmentContent(), R.id.main_frame,
                bundle, FragmentContent.class.getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showIntro();
    }

    @OnClick(R.id.download_badge)
    public void showDownloadList() {
        PrathamApplication.bubble_mp.start();
        DownloadListFragment fragment = new DownloadListFragment();
        fragment.show(getSupportFragmentManager(), DownloadListFragment.class.getSimpleName());
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

    @OnClick(R.id.pradigi_icon)
    public void setPullDown() {
        if (!FsService.isRunning()) {
            setTop_scaling();
        } else {
            EventMessage msg = new EventMessage();
            msg.setMessage(PD_Constant.CLOSE_FTP_SERVER);
            EventBus.getDefault().post(msg);
        }
    }

    @OnClick(R.id.pradigi_exit)
    public void ExitApp() {
        if (!FsService.isRunning()) {
            new AlertDialog.Builder(ActivityMain.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("PraDigi")
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            EventMessage msg = new EventMessage();
            msg.setMessage(PD_Constant.CLOSE_FTP_SERVER);
            EventBus.getDefault().post(msg);
        }
    }

    @OnClick(R.id.sheet_language)
    public void setSheetLanguage(View view) {
        animate(view);
        PrathamApplication.bubble_mp.start();
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        PD_Utility.showFragment(ActivityMain.this, new FragmentLanguage(), R.id.main_frame,
                bundle, FragmentLanguage.class.getSimpleName());
    }

    @OnClick(R.id.sheet_home)
    public void setSheetHome(View view) {
        animate(view);
        PrathamApplication.bubble_mp.start();
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        PD_Utility.showFragment(ActivityMain.this, new FragmentContent(), R.id.main_frame,
                bundle, FragmentContent.class.getSimpleName());
    }

    @OnClick(R.id.sheet_share)
    public void setSheetShare(View view) {
        animate(view);
        PrathamApplication.bubble_mp.start();
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        PD_Utility.showFragment(ActivityMain.this, new FragmentShareRecieve(), R.id.main_frame,
                bundle, FragmentShareRecieve.class.getSimpleName());
    }

    @OnClick(R.id.sheet_apk_share)
    public void shareAPK(View view) {
        animate(view);
        PrathamApplication.bubble_mp.start();
        try {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            PackageManager pm = PrathamApplication.getInstance().getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(PrathamApplication.getInstance().getPackageName(), 0);
            File localFile = new File(ai.publicSourceDir);
            intentShareFile.setType("*/*");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + localFile.getAbsolutePath()));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Please download apk from here...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.pratham.prathamdigital");
            startActivity(Intent.createChooser(intentShareFile, "Share through"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.sheet_connect)
    public void setSheetConnect(View view) {
        animate(view);
        PrathamApplication.bubble_mp.start();
        ConnectDialog connectDialog = new ConnectDialog.Builder(ActivityMain.this).build();
        connectDialog.isDismissOnTouchBackground();
        connectDialog.isDismissOnClickBack();
        connectDialog.setOnDismissListener(new BlurPopupWindow.OnDismissListener() {
            @Override
            public void onDismiss(BlurPopupWindow popupWindow) {
                Bundle bundle = new Bundle();
                bundle.putInt(PD_Constant.REVEALX, 0);
                bundle.putInt(PD_Constant.REVEALY, 0);
                PD_Utility.showFragment(ActivityMain.this, new FragmentContent(), R.id.main_frame,
                        bundle, FragmentContent.class.getSimpleName());
            }
        });
        connectDialog.show();
//        return onTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_frame);
        if (fragment instanceof FragmentContent) {
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.CONTENT_BACK);
            EventBus.getDefault().post(message);
        } else if (fragment instanceof FragmentLanguage) {
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.LANGUAGE_BACK);
            EventBus.getDefault().post(message);
        } else if (fragment instanceof FragmentShareRecieve) {
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.SHARE_BACK);
            EventBus.getDefault().post(message);
        }
    }

    ScalingLayoutListener listener = new ScalingLayoutListener() {
        @Override
        public void onCollapsed() {
//            ViewCompat.animate(pull_down_menu).alpha(1).setDuration(150).start();
            ViewCompat.animate(tab_card).alpha(0).setDuration(150).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {
//                    pull_down_menu.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(View view) {
                    outer_area.setVisibility(View.GONE);
                    tab_card.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            }).start();
        }

        @Override
        public void onExpanded() {
//            ViewCompat.animate(pull_down_menu).alpha(0).setDuration(200).start();
            ViewCompat.animate(tab_card).alpha(1).setDuration(200).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {
                    outer_area.setVisibility(View.VISIBLE);
                    tab_card.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(View view) {
//                    pull_down_menu.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            }).start();
        }

        @Override
        public void onProgress(float progress) {
            if (progress > 0) {
//                pull_down_menu.setVisibility(View.INVISIBLE);
            }

            if (progress < 1) {
                outer_area.setVisibility(View.GONE);
                tab_card.setVisibility(View.INVISIBLE);
            }
        }
    };

    //    @OnClick(R.id.top_scaling)
    public void setTop_scaling() {
        if (top_scaling.getState() == State.COLLAPSED)
            top_scaling.expand();
        else top_scaling.collapse();
    }

    private void animate(View v) {
//        float start = 0F;
//        float end = (float) Math.hypot(sheet_tab_holder.getWidth(), sheet_tab_holder.getHeight());
//        Animator animator = ViewAnimationUtils.createCircularReveal(sheet_tab_holder, (int) event.getRawX(), (int) event.getY(), start, end);
//        animator.setDuration(1150L);
//        animator.setInterpolator(new FastOutSlowInInterpolator());
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                tab_card.setCardBackgroundColor(((ColorDrawable) sheet_tab_holder.getBackground()).getColor());
////                sheet_tab_holder.setBackgroundColor(backgroundColor);
//            }
//        });
//        animator.start();
        sliding_strip.animate()
                .x(v.getX())
//                        .y(v.getY())
                .setDuration(500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        sliding_strip.setX(v.getX());
//                                sliding_strip.setY(v.getY());
                        setTop_scaling();
                    }
                }).start();
    }

    @OnClick(R.id.outer_area)
    public void onRootTouch(View v) {
        if (top_scaling.getState() == State.EXPANDED)
            top_scaling.collapse();
    }

    private void showIntro() {
        SpotlightView spotlightView = new SpotlightView.Builder(ActivityMain.this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Open Menu")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Click here and Open Menu")
                .maskColor(Color.parseColor("#dc000000"))
                .target(pradigi_icon)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(false)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .setListener(spotlightListener)
                .usageId("pradigi_icon") //UNIQUE ID
                .show();
    }

    SpotlightListener spotlightListener = new SpotlightListener() {
        @Override
        public void onUserClicked(String spotlightViewId) {
            if (top_scaling.getState() == State.EXPANDED)
                SpotlightSequence.getInstance(ActivityMain.this, null)
                        .addSpotlight(sheet_home, "DISPLAY CONTENT", "Click here to view contents", "sheet_home")
                        .addSpotlight(sheet_language, "CHANGE LANGUAGE", "Click here to change Language", "sheet_language")
                        .addSpotlight(sheet_connect, "CONNECT WIFI", "Click here to connect wifi", "sheet_connect")
                        .addSpotlight(sheet_connect, "CONNECT WIFI", "Click here to connect wifi", "sheet_connect")
                        .startSequence();
        }
    };
}