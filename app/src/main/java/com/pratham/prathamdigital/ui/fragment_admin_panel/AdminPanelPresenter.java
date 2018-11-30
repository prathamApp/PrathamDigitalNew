package com.pratham.prathamdigital.ui.fragment_admin_panel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.services.PrathamSmartSync;

/**
 * Created by PEF on 19/11/2018.
 */

public class AdminPanelPresenter implements AdminPanelContract.AdminPanelPresenter {
    AdminPanelContract.AdminPanelView adminPanelView;
    Context context;

    public AdminPanelPresenter(Context context, AdminPanelContract.AdminPanelView adminPanelView) {
        this.adminPanelView = adminPanelView;
        this.context = context;
    }

    @Override
    public void checkLogin(String userName, String password) {
        // if user name and password are admin then navigate to Download activity otherWise admin activity

        if (userName.equals("admin") && password.equals("admin")) {
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

    @Override
    public void clearData() {
        AlertDialog clearDataDialog = new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Clear Data")
                .setMessage("Are you sure you want to clear everything ?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        BaseActivity.villageDao.deleteAllVillages();
                        BaseActivity.groupDao.deleteAllGroups();
                        BaseActivity.studentDao.deleteAllStudents();
                        BaseActivity.crLdao.deleteAllCRLs();
                        adminPanelView.onDataClearToast();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        clearDataDialog.show();
        clearDataDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    @Override
    public void pushData() {
        PrathamSmartSync.pushTabletJsons();
    }

}
