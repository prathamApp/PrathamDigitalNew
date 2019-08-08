package com.pratham.prathamdigital.ui.fragment_week_course_plan.week_three;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.pratham.prathamdigital.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_week_one)
public class Fragment_WeekThree extends Fragment {

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @AfterViews
    public void init() {
    }
}
