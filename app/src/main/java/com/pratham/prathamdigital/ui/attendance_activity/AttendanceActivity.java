package com.pratham.prathamdigital.ui.attendance_activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar_;
import com.pratham.prathamdigital.ui.fragment_age_group.FragmentSelectAgeGroup;
import com.pratham.prathamdigital.ui.fragment_age_group.FragmentSelectAgeGroup_;
import com.pratham.prathamdigital.ui.fragment_child_attendance.FragmentChildAttendance_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.ArrayList;

@EActivity(R.layout.activity_attendance)
public class AttendanceActivity extends BaseActivity {

    @AfterViews
    public void initialize() {
        if (PrathamApplication.isTablet) {
            PD_Utility.showFragment(this, new FragmentSelectAgeGroup_(), R.id.frame_attendance,
                    null, FragmentSelectAgeGroup.class.getSimpleName());
        } else {
            if (getIntent().getBooleanExtra(PD_Constant.STUDENT_ADDED, false)) {
                ArrayList<Modal_Student> students = (ArrayList<Modal_Student>) BaseActivity.studentDao.getAllStudents();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(PD_Constant.STUDENT_LIST, students);
                bundle.putInt(PD_Constant.REVEALX, 0);
                bundle.putInt(PD_Constant.REVEALY, 0);
                PD_Utility.showFragment(this, new FragmentChildAttendance_(), R.id.frame_attendance,
                        bundle, FragmentChildAttendance_.class.getSimpleName());
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt(PD_Constant.REVEALX, 0);
                bundle.putInt(PD_Constant.REVEALY, 0);
                bundle.putBoolean(PD_Constant.SHOW_BACK, false);
                PD_Utility.showFragment(this, new Fragment_SelectAvatar_(), R.id.frame_attendance,
                        bundle, Fragment_SelectAvatar_.class.getSimpleName());
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_attendance);
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("PraDigi")
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
