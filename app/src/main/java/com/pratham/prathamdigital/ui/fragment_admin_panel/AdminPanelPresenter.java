package com.pratham.prathamdigital.ui.fragment_admin_panel;

/**
 * Created by PEF on 19/11/2018.
 */

public class AdminPanelPresenter implements AdminPanelContract.AdminPanelPresenter {
    AdminPanelContract.AdminPanelView adminPanelView;

    public AdminPanelPresenter(AdminPanelContract.AdminPanelView adminPanelView) {
        this.adminPanelView = adminPanelView;
    }

    @Override
    public void checkLogin(String userName, String password) {
        // if user name and password are admin then navigate to Download activity otherWise admin activity

        if (userName.equals("admin") && password.equals("admin")) {
            adminPanelView.openPullDataFragment();
        } else {
            // assign push logic


        }
    }
}
