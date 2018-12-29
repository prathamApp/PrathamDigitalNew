package com.pratham.prathamdigital.ui.assign;

import android.content.Context;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

@EBean
public class AssignPresenterImpl implements AssignContract.assignPresenter {
    Context context;
    AssignContract.assignView assignView;

    public AssignPresenterImpl(Context context) {
        this.context = context;
        this.assignView = (AssignContract.assignView) context;
    }

    @Background
    @Override
    public void getStates() {
        List<String> states = new ArrayList<>();
//        States.add("Select State");
        states = BaseActivity.villageDao.getAllStates();
        assignView.setStates(states);
    }

    @Background
    @Override
    public void getProgramId(String header) {
        String programID = BaseActivity.statusDao.getValue("programId");
        assignView.setProgramId(header, programID);
    }

    @Background
    @Override
    public void getBlocks(String selectedState) {
        List<String> blocks = new ArrayList<>();
        blocks = BaseActivity.villageDao.GetStatewiseBlock(selectedState);
        assignView.populateBlock(blocks);
    }

    @Background
    @Override
    public void getVillages(String selectedBlock) {
        String programID = BaseActivity.statusDao.getValue("programId");
        List<Modal_Village> BlocksVillages = new ArrayList<Modal_Village>();
        if (programID.equals("1") || programID.equals("3") || programID.equals("10")) { // H Learning
            BlocksVillages = BaseActivity.villageDao.GetVillages(selectedBlock);
            assignView.populateVillage(BlocksVillages);
        } else if (programID.equals("2")) {// RI
            int vilID = BaseActivity.villageDao.GetVillageIDByBlock(selectedBlock);
            List<Modal_Groups> dbgroupList = BaseActivity.groupDao.GetGroups(vilID);
            assignView.populateGroups(dbgroupList);
        } else {
            BlocksVillages = BaseActivity.villageDao.GetVillages(selectedBlock);
            assignView.populateVillage(BlocksVillages);
        }
    }

    @Override
    public void populateGroups(int vilID) {
        String programID = BaseActivity.statusDao.getValue("programId");
        List<Modal_Groups> dbgroupList = BaseActivity.groupDao.GetGroups(vilID);
        assignView.populateGroups(dbgroupList);
    }

    @Background
    @Override
    public void assignGroups(String group1, String group2, String group3, String group4, String group5, int vilID) {
        assignView.showDialog();
        // Delete Groups where Device ID is deleted & also delete associated students & update status table
        deleteGroupsWithStudents();
        // delete deleted Students
        BaseActivity.studentDao.deleteDeletedStdRecords();
        // Update Groups in Status
        BaseActivity.statusDao.updateValue(PD_Constant.GROUPID1, group1);
        BaseActivity.statusDao.updateValue(PD_Constant.GROUPID2, group2);
        BaseActivity.statusDao.updateValue(PD_Constant.GROUPID3, group3);
        BaseActivity.statusDao.updateValue(PD_Constant.GROUPID4, group4);
        BaseActivity.statusDao.updateValue(PD_Constant.GROUPID5, group5);
        BaseActivity.statusDao.updateValue("village", Integer.toString(vilID));
        BaseActivity.statusDao.updateValue("DeviceId", PD_Utility.getDeviceID());
        BaseActivity.statusDao.updateValue("ActivatedDate", PD_Utility.getCurrentDateTime());
        BaseActivity.statusDao.updateValue("ActivatedForGroups", group1 + "," + group2 + "," + group3 + "," + group4 + "," + group5);
        assignView.groupsAssignedSuccessfully();
    }

    // Delete Groups with Students
    public void deleteGroupsWithStudents() {
        // Delete Records of Deleted Students
        List<Modal_Groups> deletedGroupsList = BaseActivity.groupDao.GetAllDeletedGroups();
        // Delete students for all deleted groups
        for (int i = 0; i < deletedGroupsList.size(); i++) {
            //Delete Group
            BaseActivity.groupDao.deleteGroupByGrpID(deletedGroupsList.get(i).GroupId);
            // Delete Student
            BaseActivity.studentDao.deleteDeletedGrpsStdRecords(deletedGroupsList.get(i).GroupId);
        }
    }
}
