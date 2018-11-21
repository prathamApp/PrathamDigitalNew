package com.pratham.prathamdigital.ui.assign;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.util.PD_Utility;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_AssignGroups extends BaseActivity {

    @BindView(R.id.spinner_SelectState)
    Spinner spinner_SelectState;
    @BindView(R.id.spinner_SelectBlock)
    Spinner spinner_SelectBlock;
    @BindView(R.id.spinner_selectVillage)
    Spinner spinner_selectVillage;

    @BindView(R.id.assignGroup1)
    LinearLayout assignGroup1;
    @BindView(R.id.assignGroup2)
    LinearLayout assignGroup2;

    @BindView(R.id.allocateGroups)
    Button allocateGroups;

    List<String> Blocks;
    private List<Modal_Groups> dbgroupList;
    private int vilID, cnt = 0;
    public String checkBoxIds[], group1 = "0", group2 = "0", group3 = "0", group4 = "0", group5 = "0";
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_groups);
        ButterKnife.bind(this);
        // Hide Actionbar
        getSupportActionBar().hide();

        initializeStatesSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgramwiseSpinners();
    }

    // Show/Hide Spinners according to Program
    private void showProgramwiseSpinners() {
        // Hide Village Spinner based on HLearning / RI
        String programID = BaseActivity.statusDao.getValue("programId");

        if (programID.equals("1") || programID.equals("3") || programID.equals("10")) {
            spinner_selectVillage.setVisibility(View.VISIBLE);
        } else if (programID.equals("2")) // RI
        {
            spinner_selectVillage.setVisibility(View.GONE);
        } else {
            spinner_selectVillage.setVisibility(View.VISIBLE);
        }

    }

    // Populate States Spinner
    private void initializeStatesSpinner() {
        //Get Villages Data for States AllSpinners
        List<String> States = BaseActivity.villageDao.getAllStates();
        //Creating the ArrayAdapter instance having the Villages list
        ArrayAdapter<String> StateAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, States);
        // Hint for AllSpinners
        spinner_SelectState.setPrompt("Select State");
        spinner_SelectState.setAdapter(StateAdapter);

        spinner_SelectState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedState = spinner_SelectState.getSelectedItem().toString();
                populateBlock(selectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Populate Blocks
    public void populateBlock(String selectedState) {
        spinner_SelectBlock = (Spinner) findViewById(R.id.spinner_SelectBlock);
        //Get Villages Data for Blocks AllSpinners
        Blocks = BaseActivity.villageDao.GetStatewiseBlock(selectedState);
        //Creating the ArrayAdapter instance having the Villages list
        ArrayAdapter<String> BlockAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, Blocks);
        // Hint for AllSpinners
        spinner_SelectBlock.setPrompt("Select Block");
        spinner_SelectBlock.setAdapter(BlockAdapter);

        spinner_SelectBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBlock = spinner_SelectBlock.getSelectedItem().toString();
                String programID = BaseActivity.statusDao.getValue("programId");
                if (programID.equals("1") || programID.equals("3") || programID.equals("10")) // H Learning
                {
                    populateVillage(selectedBlock);
                } else if (programID.equals("2")) // RI
                {
                    populateRIVillage(selectedBlock);
                } else {
                    populateVillage(selectedBlock);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    // Populate Villages
    public void populateVillage(String selectedBlock) {

        //Get Villages Data for Villages filtered by block for Spinners
        List<Modal_Village> BlocksVillages = BaseActivity.villageDao.GetVillages(selectedBlock);
        //Creating the ArrayAdapter instance having the Villages list
        ArrayAdapter<Modal_Village> VillagesAdapter = new ArrayAdapter<Modal_Village>(this, R.layout.custom_spinner, BlocksVillages);
        // Hint for AllSpinners
        spinner_selectVillage.setPrompt("Select Village");
        spinner_selectVillage.setAdapter(VillagesAdapter);
        spinner_selectVillage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Modal_Village village = (Modal_Village) parent.getItemAtPosition(position);
                vilID = village.getVillageId();
                try {
                    populateGroups(vilID);  //Populate groups According to JSON & DB in Checklist instead of using spinner
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Populate RI Villages (Block Not present in Groups)
    public void populateRIVillage(String selectedBlock) {

        vilID = BaseActivity.villageDao.GetVillageIDByBlock(selectedBlock);
        try {
            populateGroups(vilID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Populate Groups
    private void populateGroups(int vilID) throws JSONException {
        String programID = BaseActivity.statusDao.getValue("programId");
        // Check Spinner Emptyness
        int VillagesSpinnerValue = spinner_selectVillage.getSelectedItemPosition();

        if (VillagesSpinnerValue > 0 || programID.equals("2")) {

            // Showing Groups from Database
            checkBoxIds = null;

            dbgroupList = BaseActivity.groupDao.GetGroups(vilID);
            List<Modal_Groups> groupList = new ArrayList<Modal_Groups>(dbgroupList);

            groupList.remove(0);

            LinearLayout my_layout = (LinearLayout) findViewById(R.id.assignGroup1);
            LinearLayout my_layout1 = (LinearLayout) findViewById(R.id.assignGroup2);

            my_layout.removeAllViews();
            my_layout1.removeAllViews();

            checkBoxIds = new String[groupList.size()];
            int half = Math.round(groupList.size() / 2);

            for (int i = 0; i < groupList.size(); i++) {

                Modal_Groups grp = groupList.get(i);
                String groupName = grp.getGroupName();
                String groupId = grp.getGroupId();

                TableRow row = new TableRow(Activity_AssignGroups.this);
                //row.setId(groupId);
                checkBoxIds[i] = groupId;

                //dynamically create checkboxes. i.e no. of students in group = no. of checkboxes
                row.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                CheckBox checkBox = new CheckBox(Activity_AssignGroups.this);

                try {
                    checkBox.setId(i);
                    checkBox.setTag(groupId);
                    checkBox.setText(groupName);
                } catch (Exception e) {

                }
                checkBox.setTextSize(20);
                checkBox.setTextColor(Color.BLACK);
                checkBox.setBackgroundColor(Color.LTGRAY);

                row.addView(checkBox);
                if (i >= half)
                    my_layout1.addView(row);
                else
                    my_layout.addView(row);
            }

            // Animation Effect on Groups populate
            LinearLayout image = (LinearLayout) findViewById(R.id.LinearLayoutGroups);
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
            image.startAnimation(animation1);

            allocateGroups.setVisibility(View.VISIBLE);
        } else {
            assignGroup1.removeAllViews();
            assignGroup2.removeAllViews();
        }

    }

    // Delete Groups with Students
    private void deleteGroupsWithStudents() {
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

    // Assign Groups
    @OnClick(R.id.allocateGroups)
    public void assignButtonClick() {
        try {
            group1 = group2 = group3 = group4 = group5 = "0";
            cnt = 0;
            for (int i = 0; i < checkBoxIds.length; i++) {
                CheckBox checkBox = (CheckBox) findViewById(i);

                if (checkBox.isChecked() && group1.equals("0")) {
                    group1 = (String) checkBox.getTag();
                    cnt++;
                } else if (checkBox.isChecked() && group2.equals("0")) {
                    cnt++;
                    group2 = (String) checkBox.getTag();
                } else if (checkBox.isChecked() && group3.equals("0")) {
                    cnt++;
                    group3 = (String) checkBox.getTag();
                } else if (checkBox.isChecked() && group4.equals("0")) {
                    cnt++;
                    group4 = (String) checkBox.getTag();
                } else if (checkBox.isChecked() && group5.equals("0")) {
                    cnt++;
                    group5 = (String) checkBox.getTag();
                } else if (checkBox.isChecked()) {
                    cnt++;
                }

            }

            if (cnt < 1) {
                Toast.makeText(Activity_AssignGroups.this, "Please Select atleast one Group !!!", Toast.LENGTH_SHORT).show();
            } else if (cnt >= 1 && cnt <= 5) {
                try {
                    //   MultiPhotoSelectActivity.dilog.showDilog(context, "Assigning Groups");
                    progress = new ProgressDialog(Activity_AssignGroups.this);
                    progress.setMessage("Please Wait...");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();

                    Thread mThread = new Thread() {
                        @Override
                        public void run() {

                            // Delete Groups where Device ID is deleted & also delete associated students & update status table
                            deleteGroupsWithStudents();

                            // delete deleted Students
                            BaseActivity.studentDao.deleteDeletedStdRecords();

                            // Update Groups in Status
                            BaseActivity.statusDao.updateValue("group1", group1);
                            BaseActivity.statusDao.updateValue("group2", group2);
                            BaseActivity.statusDao.updateValue("group3", group3);
                            BaseActivity.statusDao.updateValue("group4", group4);
                            BaseActivity.statusDao.updateValue("group5", group5);
                            BaseActivity.statusDao.updateValue("village", Integer.toString(vilID));
                            BaseActivity.statusDao.updateValue("deviceId", PD_Utility.getDeviceID());
                            BaseActivity.statusDao.updateValue("ActivatedDate", PD_Utility.getCurrentDateTime());
                            BaseActivity.statusDao.updateValue("ActivatedForGroups", group1 + "," + group2 + "," + group3 + "," + group4 + "," + group5);

                            //  MultiPhotoSelectActivity.dilog.dismissDilog();
                            Activity_AssignGroups.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Activity_AssignGroups.this, " Groups Assigned Successfully !!!", Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                }
                            });
                        }
                    };
                    mThread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(Activity_AssignGroups.this, " You can select Maximum 5 Groups !!! ", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
