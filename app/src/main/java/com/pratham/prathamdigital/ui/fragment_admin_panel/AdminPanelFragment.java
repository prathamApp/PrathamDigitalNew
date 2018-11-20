package com.pratham.prathamdigital.ui.fragment_admin_panel;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.ui.PullData.PullDataFragment;
import com.pratham.prathamdigital.ui.fragment_age_group.FragmentSelectAgeGroup;
import com.pratham.prathamdigital.util.PD_Utility;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_panel_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        adminPanelPresenter=new AdminPanelPresenter(this);
    }

    @OnClick(R.id.btn_login)
    public void loginCheck() {
        adminPanelPresenter.checkLogin(getUserName(),getPassword());
    }

    @Override
    public String getUserName() {
        String userName=userNameET.getText().toString();
        return userName;
    }

    @Override
    public String getPassword() {
        String password=passwordET.getText().toString();
        return password;
    }

    @Override
    public void openPullDataFragment() {
        PD_Utility.showFragment(getActivity(), new PullDataFragment(), R.id.frame_attendance,
                null, PullDataFragment.class.getSimpleName());
    }
}
