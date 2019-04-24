package com.pratham.prathamdigital.ui.fragment_admin_panel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.ViewTreeObserver;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.ui.assign.Activity_AssignGroups_;
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

import java.util.Objects;

/**
 * Created by PEF on 19/11/2018.
 */
@EFragment(R.layout.admin_panel_login)
public class AdminPanelFragment extends Fragment implements AdminPanelContract.AdminPanelView {
    @ViewById(R.id.circular_admin_reveal)
    CircularRevelLayout circular_admin_reveal;
    @ViewById(R.id.userName)
    android.support.design.widget.TextInputEditText userNameET;
    @ViewById(R.id.password)
    android.support.design.widget.TextInputEditText passwordET;

    @Bean(AdminPanelPresenter.class)
    AdminPanelContract.AdminPanelPresenter adminPanelPresenter;

    @AfterViews
    public void setViews() {
        adminPanelPresenter.setView(AdminPanelFragment.this);
        if (getArguments() != null) {
            int revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            int revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            circular_admin_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    circular_admin_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    circular_admin_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
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

    @Click(R.id.btn_login)
    public void loginCheck() {
        adminPanelPresenter.checkLogin(getUserName(), getPassword());
        Objects.requireNonNull(userNameET.getText()).clear();
        Objects.requireNonNull(passwordET.getText()).clear();
    }

    @Click(R.id.btn_push_data)
    public void pushData() {
        adminPanelPresenter.pushData();
    }

    private String getUserName() {
        String userName = Objects.requireNonNull(userNameET.getText()).toString();
        return userName.trim();
    }

    private String getPassword() {
        String password = Objects.requireNonNull(passwordET.getText()).toString();
        return password.trim();
    }

    @UiThread
    @Override
    public void openPullDataFragment() {
        PD_Utility.showFragment(getActivity(), new PullDataFragment_(), R.id.frame_attendance,
                null, PullDataFragment.class.getSimpleName());
    }

    @UiThread
    @Override
    public void onLoginFail() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Invalid Credentials");
        alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        alertDialog.setButton("OK", (dialog, which) -> {
            userNameET.setText("");
            passwordET.setText("");
            userNameET.requestFocus();
        });
        alertDialog.show();
    }

    @UiThread
    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent(getActivity(), Activity_AssignGroups_.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void DataPushedSuccessfully(EventMessage msg) {
        if (msg != null) {
            if (msg.getMessage().equalsIgnoreCase(PD_Constant.SUCCESSFULLYPUSHED)) {
                new BlurPopupWindow.Builder(getContext())
                        .setContentView(R.layout.app_success_dialog)
                        .setGravity(Gravity.CENTER)
                        .setScaleRatio(0.2f)
                        .setDismissOnClickBack(true)
                        .setDismissOnTouchBackground(true)
                        .setBlurRadius(10)
                        .setTintColor(0x30000000)
                        .build()
                        .show();
            } else if (msg.getMessage().equalsIgnoreCase(PD_Constant.PUSHFAILED)) {
                new BlurPopupWindow.Builder(getContext())
                        .setContentView(R.layout.app_failure_dialog)
                        .setGravity(Gravity.CENTER)
                        .setScaleRatio(0.2f)
                        .setDismissOnClickBack(true)
                        .setDismissOnTouchBackground(true)
                        .setBlurRadius(10)
                        .setTintColor(0x30000000)
                        .build()
                        .show();
            }
        }
    }

    @Click(R.id.img_admin_back)
    public void setAdminBack() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
    }
}
