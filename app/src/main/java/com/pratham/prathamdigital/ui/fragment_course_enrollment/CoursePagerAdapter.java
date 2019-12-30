package com.pratham.prathamdigital.ui.fragment_course_enrollment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan.Fragment_EnrolledCourses_;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan.Fragment_NewCourses_;


public class CoursePagerAdapter extends FragmentPagerAdapter {

    CoursePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Fragment_EnrolledCourses_();
            case 1:
                return new Fragment_NewCourses_();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return position + "";
    }
}
