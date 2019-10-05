package com.pratham.prathamdigital.ui.fragment_admin_options;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.ReadContentDbFromSdCard;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.io.File;

import static com.pratham.prathamdigital.PrathamApplication.crLdao;
import static com.pratham.prathamdigital.PrathamApplication.groupDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;
import static com.pratham.prathamdigital.PrathamApplication.villageDao;

@EBean
public class AdminOptionsPresenter implements ContractOptions.optionPresenter {
    private Context context;
    private ContractOptions.optionView optionView;

    public AdminOptionsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(Fragment_AdminOptions fragment_adminOptions) {
        optionView = (ContractOptions.optionView) fragment_adminOptions;
    }

    @Background
    @Override
    public void clearData() {
        villageDao.deleteAllVillages();
        groupDao.deleteAllGroups();
        studentDao.deleteAllStudents();
        crLdao.deleteAllCRLs();
        optionView.onDataCleared();
    }

    @Background
    @Override
    public void updateDatabase(Uri treeUri) {
//        if (DbPresentInInternalStorage()) {
//            new UpdateDatabaseInSdCard().execute(treeUri, Environment.getExternalStorageDirectory() + "/DB/" + PrathamDatabase.DB_NAME);
//        } else {
        //copy from asset
//        }
    }

    private boolean DbPresentInInternalStorage() {
        File pradigiFolder = new File(Environment.getExternalStorageDirectory() + "/DB");
        if (!pradigiFolder.exists()) return false;
        File dbFile = new File(pradigiFolder + "/" + PrathamDatabase.DB_NAME);
        return dbFile.exists();
    }

    @Override
    public void databaseSuccessfullyUpdated() {
        new ReadContentDbFromSdCard(PrathamApplication.getInstance());
    }
}
