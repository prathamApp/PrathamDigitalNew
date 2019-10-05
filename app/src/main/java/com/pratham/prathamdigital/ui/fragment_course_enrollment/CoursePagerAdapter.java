package com.pratham.prathamdigital.ui.fragment_course_enrollment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pratham.prathamdigital.ui.fragment_week_course_plan.week_one.Fragment_WeekOne_;

public class CoursePagerAdapter extends FragmentPagerAdapter {

    CoursePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Fragment_WeekOne_();
//            case 1:
//                return new Fragment_WeekTwo_();
//            case 2:
//                return new Fragment_WeekThree_();
//            case 3:
//                return new Fragment_WeekFour_();
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
