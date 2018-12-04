package com.pratham.prathamdigital.ui.dashboard;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.NotificationBadge;
import com.pratham.prathamdigital.custom.topsheet.TopSheetDialog;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.connect_dialog.ConnectDialog;
import com.pratham.prathamdigital.ui.download_list.DownloadListFragment;
import com.pratham.prathamdigital.ui.fragment_content.ContentContract;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent;
import com.pratham.prathamdigital.ui.fragment_language.FragmentLanguage;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityMain extends BaseActivity implements ContentContract.mainView, LevelContract {

    private static final String TAG = ActivityMain.class.getSimpleName();
    //    @BindView(R.id.circular_main_reveal)
//    CircularRevelLayout circular_main_reveal;
    @BindView(R.id.main_root)
    CoordinatorLayout main_root;
    //    @BindView(R.id.avatar_view)
//    public LottieAnimationView avatar_view;
//    @BindView(R.id.back_view)
//    public LottieAnimationView back_view;
    @BindView(R.id.download_notification)
    NotificationBadge download_notification;
    @BindView(R.id.download_badge)
    RelativeLayout download_badge;
    //    @BindView(R.id.avatar_shape)
//    public ShapeOfView avatar_shape;
//    @BindView(R.id.search_shape)
//    public ShapeOfView search_shape;
    @BindView(R.id.rv_level)
    public RecyclerView rv_level;
    //    @BindView(R.id.top_sheet)
//    View top_sheet;
    @BindView(R.id.pull_down_menu)
    ImageView pull_down_menu;

    private RV_LevelAdapter levelAdapter;
    TopSheetDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

//        topSheetBehavior = TopSheetBehavior.from(top_sheet);
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, 0);
        bundle.putInt(PD_Constant.REVEALY, 0);
        PD_Utility.showFragment(this, new FragmentContent(), R.id.main_frame,
                bundle, FragmentContent.class.getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        avatar_view.setAnimation(FastSave.getInstance().getString(PD_Constant.AVATAR, "avatars/rabbit.json"));
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

//    @OnClick(R.id.avatar_view)
//    public void openSettingsActivity() {
//        PrathamApplication.bubble_mp.start();
//        ActivityOptionsCompat options = ActivityOptionsCompat.
//                makeSceneTransitionAnimation(this, avatar_view, "transition");
//        Point points = PD_Utility.getCenterPointOfView(avatar_view);
//        int revealX = (int) points.x;
//        int revealY = (int) points.y;
//        Intent intent = new Intent(this, SettingsActivity.class);
//        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_X, revealX);
//        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_Y, revealY);
//        ActivityCompat.startActivity(ActivityMain.this, intent, options.toBundle());
//    }

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

    @Override
    public void levelClicked(Modal_ContentDetail detail) {
        EventBus.getDefault().post(detail);
    }

    @OnClick(R.id.pull_down_menu)
    public void setPullDown() {
        dialog = new TopSheetDialog(this);
        dialog.setContentView(R.layout.top_sheet_items_layout);
        LinearLayout top_sheet_content_home = (LinearLayout) dialog.findViewById(R.id.top_sheet_content_home);
        LinearLayout top_sheet_language = (LinearLayout) dialog.findViewById(R.id.top_sheet_language);
//        LinearLayout top_sheet_import = (LinearLayout) dialog.findViewById(R.id.top_sheet_import);
        LinearLayout top_sheet_connect_wifi = (LinearLayout) dialog.findViewById(R.id.top_sheet_connect_wifi);
        top_sheet_content_home.setOnClickListener(setTop_sheet_Content_Home);
        top_sheet_language.setOnClickListener(setTop_sheet_Language);
//        top_sheet_import.setOnClickListener(setTopSheetImport);
        top_sheet_connect_wifi.setOnClickListener(setTopSheetConnect);
        dialog.show();
    }

    View.OnClickListener setTop_sheet_Content_Home = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PrathamApplication.bubble_mp.start();
            int[] outLocation = new int[2];
            v.getLocationOnScreen(outLocation);
            outLocation[0] += v.getWidth() / 2;
            Bundle bundle = new Bundle();
            bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
            bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
            PD_Utility.showFragment(ActivityMain.this, new FragmentContent(), R.id.main_frame,
                    bundle, FragmentContent.class.getSimpleName());
            dialog.dismiss();
        }
    };
    View.OnClickListener setTop_sheet_Language = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PrathamApplication.bubble_mp.start();
            int[] outLocation = new int[2];
            v.getLocationOnScreen(outLocation);
            outLocation[0] += v.getWidth() / 2;
            Bundle bundle = new Bundle();
            bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
            bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
            PD_Utility.showFragment(ActivityMain.this, new FragmentLanguage(), R.id.main_frame,
                    bundle, FragmentLanguage.class.getSimpleName());
            dialog.dismiss();
        }
    };
    //    View.OnClickListener setTopSheetImport = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            PrathamApplication.bubble_mp.start();
//            int[] outLocation = new int[2];
//            v.getLocationOnScreen(outLocation);
//            outLocation[0] += v.getWidth() / 2;
//            Bundle bundle = new Bundle();
//            bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
//            bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
//            PD_Utility.showFragment(ActivityMain.this, new Fragment_ImportData(), R.id.main_frame,
//                    bundle, Fragment_ImportData.class.getSimpleName());
//            dialog.dismiss();
//        }
//    };
    View.OnClickListener setTopSheetConnect = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
            dialog.dismiss();
        }
    };

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
        }
    }
}