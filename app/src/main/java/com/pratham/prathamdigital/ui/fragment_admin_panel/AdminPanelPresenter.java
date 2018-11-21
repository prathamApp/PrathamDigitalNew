package com.pratham.prathamdigital.ui.fragment_admin_panel;

import android.content.Context;

import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.models.Modal_Crl;

import java.security.cert.CRL;

/**
 * Created by PEF on 19/11/2018.
 */

public class AdminPanelPresenter implements AdminPanelContract.AdminPanelPresenter {
    AdminPanelContract.AdminPanelView adminPanelView;
    Context context;
    public AdminPanelPresenter(Context context,AdminPanelContract.AdminPanelView adminPanelView) {
        this.adminPanelView = adminPanelView;
        this.context=context;
    }

    @Override
    public void checkLogin(String userName, String password) {
        // if user name and password are admin then navigate to Download activity otherWise admin activity

        if (userName.equals("admin") && password.equals("admin")) {
            adminPanelView.openPullDataFragment();
        } else {
            // assign push logic
            Modal_Crl loggedCrl= PrathamDatabase.getDatabaseInstance(context).getCrLdao().checkUserValidation(userName,password);
            if(loggedCrl!=null){
                adminPanelView.onLoginSuccess();
            }else {
                //userNAme and password may be wrong
                adminPanelView.onLoginFail();
            }
        }
    }

    @Override
    public void clearData() {
        PrathamDatabase.getDatabaseInstance(context).getVillageDao().deleteAllVillages();
        PrathamDatabase.getDatabaseInstance(context).getGroupDao().deleteAllGroups();
        PrathamDatabase.getDatabaseInstance(context).getStudentDao().deleteAllStudents();
        PrathamDatabase.getDatabaseInstance(context).getCrLdao().deleteAllCRLs();
        adminPanelView.onDataClearToast();
    }
}
