package com.pratham.prathamdigital.ui.attendance_activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

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
import java.util.List;

import butterknife.BindView;
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
                PD_Utility.showFragment(this, new FragmentChildAttendance(), R.id.frame_attendance,
                        bundle, FragmentChildAttendance.class.getSimpleName());
            } else {
                PD_Utility.showFragment(this, new Fragment_SelectAvatar(), R.id.frame_attendance,
                        null, Fragment_SelectAvatar.class.getSimpleName());
            }
        }
    }
}
