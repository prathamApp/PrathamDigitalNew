package com.pratham.prathamdigital.ui.fragment_admin_panel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.ui.PullData.PullDataFragment;
import com.pratham.prathamdigital.ui.assign.Activity_AssignGroups;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by PEF on 19/11/2018.
 */

public class AdminPanelFragment extends Fragment implements AdminPanelContract.AdminPanelView {
    AdminPanelContract.AdminPanelPresenter adminPanelPresenter;
    @BindView(R.id.userName)
    android.support.design.widget.TextInputEditText userNameET;

    @BindView(R.id.password)
    android.support.design.widget.TextInputEditText passwordET;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_panel_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        adminPanelPresenter = new AdminPanelPresenter(getActivity(), this);
    }

    @OnClick(R.id.btn_login)
    public void loginCheck() {
        adminPanelPresenter.checkLogin(getUserName(), getPassword());
        userNameET.getText().clear();
        passwordET.getText().clear();
    }


    @OnClick(R.id.btn_clearData)
    public void clearData() {
        adminPanelPresenter.clearData();
    }

    @OnClick(R.id.btn_push_data)
    public void pushData() {
        adminPanelPresenter.pushData();
    }

    @Override
    public String getUserName() {
        String userName = userNameET.getText().toString();
        return userName.trim();
    }

    @Override
    public String getPassword() {
        String password = passwordET.getText().toString();
        return password.trim();
    }

    @Override
    public void openPullDataFragment() {
        PD_Utility.showFragment(getActivity(), new PullDataFragment(), R.id.frame_attendance,
                null, PullDataFragment.class.getSimpleName());
    }

    @Override
    public void onLoginFail() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Invalid Credentials");
        alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                userNameET.setText("");
                passwordET.setText("");
                userNameET.requestFocus();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent(getActivity(), Activity_AssignGroups.class);
        startActivityForResult(intent, 1);
//        getActivity().startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onDataClearToast() {
        Toast.makeText(getActivity(), "Data cleared Successfully", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
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
}
