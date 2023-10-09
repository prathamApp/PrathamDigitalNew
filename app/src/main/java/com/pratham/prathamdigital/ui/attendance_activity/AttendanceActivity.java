package com.pratham.prathamdigital.ui.attendance_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;

import androidx.fragment.app.Fragment;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar_;
import com.pratham.prathamdigital.ui.fragment_age_group.FragmentSelectAgeGroup;
import com.pratham.prathamdigital.ui.fragment_age_group.FragmentSelectAgeGroup_;
import com.pratham.prathamdigital.ui.fragment_child_attendance.FragmentChildAttendance;
import com.pratham.prathamdigital.ui.fragment_child_attendance.FragmentChildAttendance_;
import com.pratham.prathamdigital.ui.fragment_select_group.FragmentSelectGroup;
import com.pratham.prathamdigital.ui.fragment_select_group.FragmentSelectGroup_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static com.pratham.prathamdigital.PrathamApplication.studentDao;

@EActivity(R.layout.activity_attendance)
public class AttendanceActivity extends BaseActivity {

    private BlurPopupWindow exitDialog;
    private String noti_key;
    private String noti_value;

    @AfterViews
    public void initialize() {
/*
        if (PrathamApplication.isTablet) {
            PD_Utility.showFragment(this, new FragmentSelectAgeGroup_(), R.id.frame_attendance,
                    null, FragmentSelectAgeGroup.class.getSimpleName());
        } else {
*/
//            if (getIntent().getBooleanExtra(PD_Constant.DEEP_LINK, false)) {
//                bundle.putBoolean(PD_Constant.DEEP_LINK, true);
//                bundle.putString(PD_Constant.DEEP_LINK_CONTENT, getIntent().getStringExtra(PD_Constant.DEEP_LINK_CONTENT));
//            }
            Bundle bundle = new Bundle();
            if (doesIntentHaveNotificationData()) {
                bundle.putString(PD_Constant.PUSH_NOTI_KEY, noti_key);
                bundle.putString(PD_Constant.PUSH_NOTI_VALUE, noti_value);
            }
            bundle.putInt(PD_Constant.REVEALX, 0);
            bundle.putInt(PD_Constant.REVEALY, 0);

            if (getIntent().getBooleanExtra(PD_Constant.STUDENT_ADDED, false)) {
                ArrayList<Modal_Student> students = (ArrayList<Modal_Student>) studentDao.getAllStudents();
                bundle.putParcelableArrayList(PD_Constant.STUDENT_LIST, students);
                bundle.putBoolean(PD_Constant.SHOW_BACK, false);
                bundle.putBoolean("ISTABMODE",false);
                PD_Utility.showFragment(this, new FragmentSelectGroup_(), R.id.frame_attendance,
                        bundle, FragmentSelectGroup.class.getSimpleName());
            } else {
                bundle.putBoolean(PD_Constant.SHOW_BACK, false);
                PD_Utility.showFragment(this, new Fragment_SelectAvatar_(), R.id.frame_attendance,
                        bundle, Fragment_SelectAvatar.class.getSimpleName());
            }
        }
    //}

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        readNotification(intent);
        updateNotificationData();
    }

    private void updateNotificationData() {
        Bundle bundle = new Bundle();
        bundle.putString(PD_Constant.PUSH_NOTI_KEY, noti_key);
        bundle.putString(PD_Constant.PUSH_NOTI_VALUE, noti_value);
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.NOTIFICATION_RECIEVED);
        message.setBundle(bundle);
        EventBus.getDefault().post(message);
    }

    private boolean doesIntentHaveNotificationData() {
        readNotification(getIntent());
        return (noti_value != null) && (noti_key != null);
    }

    private void readNotification(Intent intent) {
        if (intent.getStringExtra(PD_Constant.PUSH_NOTI_KEY) != null && intent.getStringExtra(PD_Constant.PUSH_NOTI_VALUE) != null) {
            noti_key = intent.getStringExtra(PD_Constant.PUSH_NOTI_KEY);
            noti_value = intent.getStringExtra(PD_Constant.PUSH_NOTI_VALUE);
        }
    }
}
