package com.pratham.prathamdigital.ui.fragment_admin_panel;

import android.content.Context;
import android.os.Handler;

import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.services.PrathamSmartSync;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import static com.pratham.prathamdigital.PrathamApplication.crLdao;
import static com.pratham.prathamdigital.PrathamApplication.statusDao;

/**
 * Created by PEF on 19/11/2018.
 */
@EBean
public class AdminPanelPresenter implements AdminPanelContract.AdminPanelPresenter {
    private AdminPanelContract.AdminPanelView adminPanelView;
    Context context;

    public AdminPanelPresenter(Context context) {
        context = this.context;
    }

    @Override
    public void setView(AdminPanelFragment adminPanelFragment) {
        this.adminPanelView = adminPanelFragment;
    }

    @Override
    public void checkLogin(String userName, String password) {
        checkLogin_(userName, password);
    }

    @Background
    public void checkLogin_(String userName, String password) {
        int crl_count = crLdao.getCRLsCount();
        // if user name and password are admin then navigate to Download activity otherWise admin activity
        if (userName.equals("pratham") && password.equals("pratham") && crl_count <= 0) {
            adminPanelView.openPullDataFragment();
        } else {
            // assign push logic
            Modal_Crl loggedCrl = crLdao.checkUserValidation(userName, password);
            if (loggedCrl != null) {
                statusDao.updateValue("CRLID", loggedCrl.getCRLId());
                adminPanelView.onLoginSuccess();
            } else {
                //userNAme and password may be wrong
                adminPanelView.onLoginFail();
            }
        }
    }

    @Override
    public void pushData() {
        adminPanelView.showPushingDialog();
        //Necessary to add some delay, as the ui will change very frequent to notice
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PrathamSmartSync.pushUsageToServer(true, PD_Constant.MANUAL_PUSH, context);
            }
        }, 1500);
    }
}
