package com.pratham.prathamdigital.ui.attendance_activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;

import androidx.fragment.app.Fragment;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
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

import static com.pratham.prathamdigital.PrathamApplication.studentDao;

@EActivity(R.layout.activity_attendance)
public class AttendanceActivity extends BaseActivity {

    private BlurPopupWindow exitDialog;

    @AfterViews
    public void initialize() {
        if (PrathamApplication.isTablet) {
            PD_Utility.showFragment(this, new FragmentSelectAgeGroup_(), R.id.frame_attendance,
                    null, FragmentSelectAgeGroup.class.getSimpleName());
        } else {
            if (getIntent().getBooleanExtra(PD_Constant.STUDENT_ADDED, false)) {
                ArrayList<Modal_Student> students = (ArrayList<Modal_Student>) studentDao.getAllStudents();
                Bundle bundle = new Bundle();
                if (getIntent().getBooleanExtra(PD_Constant.DEEP_LINK, false)) {
                    bundle.putBoolean(PD_Constant.DEEP_LINK, true);
                    bundle.putString(PD_Constant.DEEP_LINK_CONTENT, getIntent().getStringExtra(PD_Constant.DEEP_LINK_CONTENT));
                }
                bundle.putParcelableArrayList(PD_Constant.STUDENT_LIST, students);
                bundle.putInt(PD_Constant.REVEALX, 0);
                bundle.putInt(PD_Constant.REVEALY, 0);
                PD_Utility.showFragment(this, new FragmentChildAttendance_(), R.id.frame_attendance,
                        bundle, FragmentChildAttendance_.class.getSimpleName());
            } else {
                Bundle bundle = new Bundle();
                if (getIntent().getBooleanExtra(PD_Constant.DEEP_LINK, false)) {
                    bundle.putBoolean(PD_Constant.DEEP_LINK, true);
                    bundle.putString(PD_Constant.DEEP_LINK_CONTENT, getIntent().getStringExtra(PD_Constant.DEEP_LINK_CONTENT));
                }
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
            exitDialog = new BlurPopupWindow.Builder(this)
                    .setContentView(R.layout.app_exit_dialog)
                    .bindClickListener(v -> {
                        exitDialog.dismiss();
                        new Handler().postDelayed((Runnable) this::finishAffinity, 200);
                    }, R.id.dialog_btn_exit)
                    .bindClickListener(v -> exitDialog.dismiss(), R.id.btn_cancel)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnTouchBackground(true)
                    .setDismissOnClickBack(true)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(10)
                    .setTintColor(0x30000000)
                    .build();
            exitDialog.show();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
