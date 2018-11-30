package com.pratham.prathamdigital.ui.attendance_activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar;
import com.pratham.prathamdigital.ui.fragment_age_group.FragmentSelectAgeGroup;
import com.pratham.prathamdigital.ui.fragment_child_attendance.FragmentChildAttendance;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class AttendanceActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        ButterKnife.bind(this);
        if (PrathamApplication.isTablet) {
            PD_Utility.showFragment(this, new FragmentSelectAgeGroup(), R.id.frame_attendance,
                    null, FragmentSelectAgeGroup.class.getSimpleName());
        } else {
            if (getIntent().getBooleanExtra(PD_Constant.STUDENT_ADDED, false)) {
                ArrayList<Modal_Student> students = (ArrayList<Modal_Student>) BaseActivity.studentDao.getAllStudents();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(PD_Constant.STUDENT_LIST, students);
                bundle.putInt(PD_Constant.REVEALX, 0);
                bundle.putInt(PD_Constant.REVEALY, 0);
                PD_Utility.showFragment(this, new FragmentChildAttendance(), R.id.frame_attendance,
                        bundle, FragmentChildAttendance.class.getSimpleName());
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt(PD_Constant.REVEALX, 0);
                bundle.putInt(PD_Constant.REVEALY, 0);
                PD_Utility.showFragment(this, new Fragment_SelectAvatar(), R.id.frame_attendance,
                        bundle, Fragment_SelectAvatar.class.getSimpleName());
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_attendance);
        if (f instanceof FragmentSelectAgeGroup || getSupportFragmentManager().getBackStackEntryCount() == 1)
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
        else
            getSupportFragmentManager().popBackStack();
    }
}
