package com.pratham.prathamdigital.ui.fragment_admin_panel;

/**
 * Created by PEF on 19/11/2018.
 */

public interface AdminPanelContract {
    interface AdminPanelView {
          public String  getUserName();
          public String  getPassword();
          public void openPullDataFragment();
          public void onLoginFail();
          public void onLoginSuccess();
          public void onDataClearToast();

    }

    interface AdminPanelPresenter {
        public void checkLogin(String userName,String password);
        public void clearData();
        public void pushData();
    }
}
