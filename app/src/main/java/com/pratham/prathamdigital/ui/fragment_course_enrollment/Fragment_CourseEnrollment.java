package com.pratham.prathamdigital.ui.fragment_course_enrollment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.pratham.prathamdigital.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_course_enrollment)
public class Fragment_CourseEnrollment extends Fragment {

    @ViewById(R.id.vp_course)
    ViewPager vp_course;

    @AfterViews
    public void init() {
        new Handler().postDelayed(this::initializePagerAdapter, 200);
    }

    private void initializePagerAdapter() {
        CoursePagerAdapter coursePagerAdapter = new CoursePagerAdapter(getChildFragmentManager());
        vp_course.setClipToPadding(false);
        vp_course.setPadding(100, 30, 100, 30);
        vp_course.setPageMargin(30);
        vp_course.setOffscreenPageLimit(3);
        vp_course.setAdapter(coursePagerAdapter);
    }
}
