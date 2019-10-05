package com.pratham.prathamdigital.ui.fragment_admin_options;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_NavigationMenu;
import com.pratham.prathamdigital.services.PrathamSmartSync;
import com.pratham.prathamdigital.ui.admin_statistics.Fragment_AdminStatistics;
import com.pratham.prathamdigital.ui.admin_statistics.Fragment_AdminStatistics_;
import com.pratham.prathamdigital.ui.assign.Activity_AssignGroups_;
import com.pratham.prathamdigital.ui.fragment_admin_panel.AdminPanelFragment;
import com.pratham.prathamdigital.ui.pullData.PullDataFragment;
import com.pratham.prathamdigital.ui.pullData.PullDataFragment_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Objects;

@EFragment(R.layout.fragment_admin_options)
public class Fragment_AdminOptions extends Fragment implements ContractOptions.optionAdapterClick,
        ContractOptions.optionView {

    private static final int PUSH_DATA = 1;
    private static final int ASSIGN_GROUPS = 2;
    private static final int PULL_DATA = 3;
    private static final int UPDATE_DATABASE = 4;
    private static final int SDCARD_LOCATION_CHOOSER = 5;
    private static final int SHOW_DB_COPYING_DIALOG = 6;
    @ViewById(R.id.rv_admin_options)
    RecyclerView rv_admin_options;
    @ViewById(R.id.cir_admin_option_reveal)
    CircularRevelLayout cir_admin_option_reveal;

    @Bean(AdminOptionsPresenter.class)
    ContractOptions.optionPresenter optionPresenter;
    BlurPopupWindow pushDialog;
    LottieAnimationView push_lottie;
    TextView txt_push_dialog_msg;
    TextView txt_push_error;
    private BlurPopupWindow sd_builder;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PUSH_DATA:
                    showPushingDialog("Please wait...Pushing Data!");
                    //Necessary to add some delay or the ui will change very frequent, hard to notice
                    new Handler().postDelayed(() -> PrathamSmartSync.pushUsageToServer(true), 1500);
                    break;
                case ASSIGN_GROUPS:
                    Intent intent = new Intent(getActivity(), Activity_AssignGroups_.class);
                    startActivityForResult(intent, 1);
                    break;
                case PULL_DATA:
                    PD_Utility.showFragment(getActivity(), new PullDataFragment_(), R.id.frame_attendance,
                            null, PullDataFragment.class.getSimpleName());
                    break;
                case UPDATE_DATABASE:
                    showSdCardDialog();
                    break;
                case SHOW_DB_COPYING_DIALOG:
                    showPushingDialog("Please wait...Updating Database");
                    break;
            }
        }
    };

    @AfterViews
    public void setViews() {
        optionPresenter.setView(this);
        if (getArguments() != null) {
            int revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            int revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            cir_admin_option_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    cir_admin_option_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    cir_admin_option_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
        }
        initializeMenu();
    }

    private void initializeMenu() {
        ArrayList<Modal_NavigationMenu> navigationMenus = new ArrayList<>();
        String[] menus = getResources().getStringArray(R.array.admin_options);
        int[] menus_img = {R.drawable.ic_push_data, R.drawable.ic_assign_groups, R.drawable.ic_device_usage,
                R.drawable.ic_clear_data, R.drawable.ic_pull_data, R.drawable.ic_database};
        for (int i = 0; i < menus.length; i++) {
            Modal_NavigationMenu nav = new Modal_NavigationMenu();
            nav.setMenu_name(menus[i]);
            nav.setMenuImage(menus_img[i]);
            nav.setIsselected(false);
            navigationMenus.add(nav);
        }
        RV_OptionsAdapter rv_menuAdapter = new RV_OptionsAdapter(getActivity(), navigationMenus, this);
        rv_admin_options.setHasFixedSize(true);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        rv_admin_options.setLayoutManager(flexboxLayoutManager);
        rv_admin_options.setAdapter(rv_menuAdapter);
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void DataPushedSuccessfully(EventMessage msg) {
        if (msg != null) {
            if (msg.getMessage().equalsIgnoreCase(PD_Constant.SUCCESSFULLYPUSHED)) {
                push_lottie.setAnimation("success.json");
                push_lottie.playAnimation();
                txt_push_dialog_msg.setText("Data Pushed Successfully!!");
                new Handler().postDelayed(() -> pushDialog.dismiss(), 1500);
            } else if (msg.getMessage().equalsIgnoreCase(PD_Constant.PUSHFAILED)) {
                push_lottie.setAnimation("error_cross.json");
                push_lottie.playAnimation();
                txt_push_dialog_msg.setText("Data Pushing Failed!!");
                txt_push_error.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> pushDialog.dismiss(), 1500);
            } else if (msg.getMessage().equalsIgnoreCase(PD_Constant.DB_FILE_UPDATED)) {
                optionPresenter.databaseSuccessfullyUpdated();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void menuClicked(int position, Modal_NavigationMenu modal_navigationMenu, View view) {
        PrathamApplication.bubble_mp.start();
        if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Push Data"))
            mHandler.sendEmptyMessage(PUSH_DATA);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Assign Groups"))
            mHandler.sendEmptyMessage(ASSIGN_GROUPS);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("View Statistics")) {
            int[] outLocation = new int[2];
            view.getLocationOnScreen(outLocation);
            outLocation[0] += view.getWidth() / 2;
            Bundle bundle = new Bundle();
            bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
            bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
            PD_Utility.addFragment(getActivity(), new Fragment_AdminStatistics_(), R.id.frame_attendance,
                    null, Fragment_AdminStatistics.class.getSimpleName());
        } else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Clear Data"))
            showClearDataDialog();
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Pull Data"))
            mHandler.sendEmptyMessage(PULL_DATA);
        else if (modal_navigationMenu.getMenu_name().equalsIgnoreCase("Update Database")) {
//            mHandler.sendEmptyMessage(UPDATE_DATABASE); todo update database feature
        }
    }

    private void showClearDataDialog() {
        AlertDialog clearDataDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Clear Data")
                .setMessage("Are you sure you want to clear everything ?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Delete", (dialog, whichButton) -> {
                    optionPresenter.clearData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        clearDataDialog.show();
        clearDataDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    @UiThread
    @Override
    public void onDataCleared() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
    }

    @UiThread
    public void showPushingDialog(String msg) {
        if (pushDialog == null) {
            pushDialog = new BlurPopupWindow.Builder(getContext())
                    .setContentView(R.layout.app_success_dialog)
                    .setGravity(Gravity.CENTER)
                    .setScaleRatio(0.2f)
                    .setDismissOnClickBack(true)
                    .setDismissOnTouchBackground(true)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();
            push_lottie = pushDialog.findViewById(R.id.push_lottie);
            txt_push_dialog_msg = pushDialog.findViewById(R.id.txt_push_dialog_msg);
            txt_push_error = pushDialog.findViewById(R.id.txt_push_error);
        }
        txt_push_dialog_msg.setText(msg);
        pushDialog.show();
    }

    @Click(R.id.img_option_back)
    public void optionBack() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack(
                    AdminPanelFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (requestCode == SDCARD_LOCATION_CHOOSER) {
            if (data != null && data.getData() != null) {
                Uri treeUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                PrathamApplication.getInstance().getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                optionPresenter.updateDatabase(treeUri);
                mHandler.sendEmptyMessage(SHOW_DB_COPYING_DIALOG);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void showSdCardDialog() {
        sd_builder = new BlurPopupWindow.Builder(getActivity())
                .setContentView(R.layout.dialog_alert_sd_card)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .bindClickListener(v -> {
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        startActivityForResult(intent, SDCARD_LOCATION_CHOOSER);
                    }, 1200);
                    sd_builder.dismiss();
                }, R.id.txt_choose_sd_card)
                .setDismissOnClickBack(true)
                .setDismissOnTouchBackground(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(8)
                .setTintColor(0x30000000)
                .build();
        ((TextView) sd_builder.findViewById(R.id.txt_choose_sd_card)).setText("Select Sd-Card");
        sd_builder.show();
    }

}
