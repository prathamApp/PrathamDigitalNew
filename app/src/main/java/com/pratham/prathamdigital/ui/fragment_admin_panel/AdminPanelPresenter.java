package com.pratham.prathamdigital.ui.fragment_admin_panel;

import android.content.Context;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.services.PrathamSmartSync;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

/**
 * Created by PEF on 19/11/2018.
 */
@EBean
public class AdminPanelPresenter implements AdminPanelContract.AdminPanelPresenter {
    AdminPanelContract.AdminPanelView adminPanelView;
    Context context;

    public AdminPanelPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(AdminPanelFragment adminPanelFragment) {
        this.adminPanelView = (AdminPanelContract.AdminPanelView) adminPanelFragment;
    }

    @Override
    public void checkLogin(String userName, String password) {
        checkLogin_(userName, password);
    }

    @Background
    public void checkLogin_(String userName, String password) {
        // if user name and password are admin then navigate to Download activity otherWise admin activity
        if (userName.equals("pratham") && password.equals("pratham")) {
            adminPanelView.openPullDataFragment();
        } else {
            // assign push logic
            Modal_Crl loggedCrl = BaseActivity.crLdao.checkUserValidation(userName, password);
            if (loggedCrl != null) {
                adminPanelView.onLoginSuccess();
            } else {
                //userNAme and password may be wrong
                adminPanelView.onLoginFail();
            }
        }
    }

    //    @Background
    @Override
    public void clearData() {
        BaseActivity.villageDao.deleteAllVillages();
        BaseActivity.groupDao.deleteAllGroups();
        BaseActivity.studentDao.deleteAllStudents();
        BaseActivity.crLdao.deleteAllCRLs();
        adminPanelView.onDataClearToast();
    }

    @Override
    public void pushData() {
        PrathamSmartSync.pushUsageToServer(true);
    }

}
