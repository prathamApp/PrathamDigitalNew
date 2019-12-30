package com.pratham.prathamdigital.ui.fragment_course_enrollment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.tab_bar.segmentTabLayout.OnTabSelectListener;
import com.pratham.prathamdigital.custom.tab_bar.segmentTabLayout.SegmentTabLayout;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@EFragment(R.layout.fragment_course_enrollment)
public class Fragment_CourseEnrollment extends Fragment {

    @ViewById(R.id.vp_course)
    ViewPager vp_course;
    @ViewById(R.id.frag_enroll_bkgd)
    RelativeLayout frag_enroll_bkgd;
    @ViewById(R.id.tab_course)
    SegmentTabLayout tab_course;

    @AfterViews
    public void init() {
        frag_enroll_bkgd.setBackground(PD_Utility.getDrawableAccordingToMonth(getActivity()));
        initializeTabs();
        new Handler().postDelayed(this::initializePagerAdapter, 200);
    }

    private void initializeTabs() {
        String[] mTitles = {"Enrolled Courses", "New Courses"};
        tab_course.setTabData(mTitles);
        tab_course.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vp_course.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void initializePagerAdapter() {
        CoursePagerAdapter coursePagerAdapter = new CoursePagerAdapter(getChildFragmentManager());
//        vp_course.setClipToPadding(false);
//        vp_course.setPadding(100, 30, 100, 30);
//        vp_course.setPageMargin(30);
        vp_course.setAdapter(coursePagerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @UiThread
    @Subscribe
    public void msgReceived(EventMessage eventMessage) {
        if (eventMessage != null)
            if (eventMessage.getMessage().equalsIgnoreCase(PD_Constant.SHOW_NEW_COURSES)) {
                vp_course.setCurrentItem(1);
                tab_course.setCurrentTab(1);
            } else if (eventMessage.getMessage().equalsIgnoreCase(PD_Constant.NEW_COURSE_ENROLLED)) {
                vp_course.setCurrentItem(0);
                tab_course.setCurrentTab(0);
            }
    }
}
