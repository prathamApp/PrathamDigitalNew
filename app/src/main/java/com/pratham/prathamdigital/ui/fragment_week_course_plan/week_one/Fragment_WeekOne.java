package com.pratham.prathamdigital.ui.fragment_week_course_plan.week_one;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.custom.view_animators.Animate;
import com.pratham.prathamdigital.custom.view_animators.Techniques;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.ui.fragment_week_course_plan.PlanningContract;
import com.pratham.prathamdigital.ui.fragment_week_course_plan.RV_CourseAdapter;
import com.pratham.prathamdigital.ui.fragment_week_course_plan.RV_SelectCourseAdapter;
import com.pratham.prathamdigital.ui.fragment_week_course_plan.WeekPlanningPresenter;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@EFragment(R.layout.fragment_week_one)
public class Fragment_WeekOne extends Fragment implements PlanningContract.weekOnePlanningView {

    @ViewById(R.id.rv_courses_enrolled)
    RecyclerView rv_courses_enrolled;
    @ViewById(R.id.rv_selectCourse)
    RecyclerView rv_selectCourse;
    @ViewById(R.id.root_card_courses)
    MaterialCardView root_card_courses;
    @ViewById(R.id.txt_week)
    TextView txt_week;
    @ViewById(R.id.rl_sel_crs)
    RelativeLayout rl_sel_crs;
    @ViewById(R.id.rl_add_new_course)
    RelativeLayout rl_add_new_course;
    @ViewById(R.id.rl_calendar_view)
    RelativeLayout rl_calendar_view;
    @ViewById(R.id.rl_enrolled_courses)
    RelativeLayout rl_enrolled_courses;
    @ViewById(R.id.course_date_picker)
    DateRangeCalendarView course_date_picker;
    @ViewById(R.id.btn_course_time_select)
    Button btn_course_time_select;
    @ViewById(R.id.ll_no_data)
    LinearLayout ll_no_data;

    @Bean(WeekPlanningPresenter.class)
    PlanningContract.weekOnePlanningPresenter planningPresenter;
    RV_CourseAdapter adapter;
    RV_SelectCourseAdapter selectCourseAdapter;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void init() {
        planningPresenter.setWeekOneView(this);
        root_card_courses.setCardBackgroundColor(PD_Utility.getRandomColorGradient());
        txt_week.setText("Week 1");
        planningPresenter.fetchEnrolledCourses("Week_1");
    }

    @UiThread
    @Override
    public void loadEnrolledCourses(List<Model_CourseEnrollment> coursesEnrolled) {
        if (coursesEnrolled != null && !coursesEnrolled.isEmpty()) {
            rl_add_new_course.setVisibility(View.GONE);
            rl_enrolled_courses.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new RV_CourseAdapter(getActivity(), Fragment_WeekOne.this);
                FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
                flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
                rv_courses_enrolled.setLayoutManager(flexboxLayoutManager);
                rv_courses_enrolled.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
        } else {
            rl_add_new_course.setVisibility(View.VISIBLE);
            rl_enrolled_courses.setVisibility(View.GONE);
        }
    }

    @UiThread
    @Override
    public void loadCourses(HashMap<String, List<Modal_ContentDetail>> courses) {
        if (courses != null && !courses.isEmpty()) {
            ll_no_data.setVisibility(View.GONE);
            rl_sel_crs.setVisibility(View.VISIBLE);
            if (selectCourseAdapter == null) {
                selectCourseAdapter = new RV_SelectCourseAdapter(getActivity(), courses, Fragment_WeekOne.this);
                FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
                flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
                rv_selectCourse.setLayoutManager(flexboxLayoutManager);
                rv_selectCourse.setAdapter(selectCourseAdapter);
            }
            selectCourseAdapter.notifyDataSetChanged();
        } else {
            ll_no_data.setVisibility(View.VISIBLE);
            rl_sel_crs.setVisibility(View.GONE);
        }
    }

    @Override
    public void selectCourse(View view, int adapterPosition) {
        rl_sel_crs.setVisibility(View.INVISIBLE);
        planningPresenter.loadCourses();
        reveal(rl_sel_crs, view);
    }

    @UiThread
    public void unreveal(View view, View end) {
        // previously visible view
        try {
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = Math.max(centerX, centerY);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, (int) end.getX(), (int) end.getY(), endRadius, startRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            anim.start();
        } catch (Exception e) {
            e.printStackTrace();
            view.setVisibility(View.GONE);
        }
    }

    @UiThread
    public void reveal(View view, View startView) {
        // previously invisible view
        try {
            int[] outLocation = new int[2];
            startView.getLocationOnScreen(outLocation);
            outLocation[0] += startView.getWidth() / 2;
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = (int) Math.hypot(centerX, centerY);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, outLocation[0], outLocation[1], startRadius, endRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            view.setVisibility(View.VISIBLE);
            anim.start();
            new Handler().postDelayed(() -> rl_add_new_course.setVisibility(View.GONE), 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btn_addNewCourse)
    public void addNewCourse(View view) {
        rl_sel_crs.setVisibility(View.INVISIBLE);
        planningPresenter.loadCourses();
        reveal(rl_sel_crs, view);
    }

    @Click(R.id.iv_close_calendar)
    public void setCloseCalendar(View view) {
        Animate.with(Techniques.SlideOutDown)
                .duration(700)
                .onEnd(animator -> rl_calendar_view.setVisibility(View.GONE))
                .playOn(rl_calendar_view);
    }

    @Override
    public void showDatePicker(Modal_ContentDetail selectedCourse, int adapterPosition) {
        Animate.with(Techniques.BounceInUp)
                .duration(700)
                .onStart(animator -> rl_calendar_view.setVisibility(View.VISIBLE))
                .playOn(rl_calendar_view);
        Calendar startSelectionDate = Calendar.getInstance();
//        startSelectionDate.add(Calendar.MONTH, -1);
        Calendar endSelectionDate = (Calendar) startSelectionDate.clone();
        endSelectionDate.add(Calendar.DATE, 4);
        course_date_picker.setSelectedDateRange(startSelectionDate, endSelectionDate);
        course_date_picker.setCalendarListener(new DateRangeCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar startDate) {

            }

            @Override
            public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
                if (endDate == null || startDate == null) {
                    Toast.makeText(getActivity(), "Please select the timeline.", Toast.LENGTH_SHORT).show();
                    return;
                }
                planningPresenter.addCourseToDb("Week_1", selectedCourse, startDate, endDate);
            }
        });
    }
}
