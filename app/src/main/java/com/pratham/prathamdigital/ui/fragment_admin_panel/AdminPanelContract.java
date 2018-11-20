package com.pratham.prathamdigital.ui.fragment_admin_panel;

/**
 * Created by PEF on 19/11/2018.
 */

public interface AdminPanelContract {
    interface AdminPanelView {
          public String  getUserName();
          public String  getPassword();
          public void openPullDataFragment();
    }

    interface AdminPanelPresenter {
        public void checkLogin(String userName,String password);
    }
}
