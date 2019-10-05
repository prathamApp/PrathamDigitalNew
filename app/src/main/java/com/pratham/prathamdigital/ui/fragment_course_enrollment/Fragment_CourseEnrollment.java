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
//    @ViewById(R.id.iv_next_week)
//    ImageView iv_next_week;
//    @ViewById(R.id.iv_previous_week)
//    ImageView iv_previous_week;

    @AfterViews
    public void init() {
        new Handler().postDelayed(this::initializePagerAdapter, 200);
    }

    private void initializePagerAdapter() {
        CoursePagerAdapter coursePagerAdapter = new CoursePagerAdapter(getChildFragmentManager());
//        vp_course.setClipToPadding(false);
//        vp_course.setPadding(100, 30, 100, 30);
//        vp_course.setPageMargin(30);
        vp_course.setAdapter(coursePagerAdapter);
       /* vp_course.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        iv_previous_week.setVisibility(View.GONE);
                        iv_next_week.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                    case 2:
                        iv_previous_week.setVisibility(View.VISIBLE);
                        iv_next_week.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        iv_previous_week.setVisibility(View.VISIBLE);
                        iv_next_week.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });*/
    }

    /*@Click(R.id.iv_next_week)
    public void setNext() {
        vp_course.setCurrentItem(vp_course.getCurrentItem() + 1);

    }

    @Click(R.id.iv_previous_week)
    public void setPrevious() {
        vp_course.setCurrentItem(vp_course.getCurrentItem() - 1);
    }*/
}
