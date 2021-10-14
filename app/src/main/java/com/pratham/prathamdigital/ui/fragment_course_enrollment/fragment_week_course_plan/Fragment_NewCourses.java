package com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.view_animators.Animate;
import com.pratham.prathamdigital.custom.view_animators.Techniques;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@EFragment(R.layout.fragment_new_courses)
public class Fragment_NewCourses extends Fragment implements PlanningContract.newCoursesView {

    private static final int SHOW_DATE_PICKER = 1;
    private static final int HIDE_DATE_PICKER = 2;

    @ViewById(R.id.rl_sel_crs)
    RelativeLayout rl_sel_crs;
    @ViewById(R.id.rv_selectCourse)
    DiscreteScrollView rv_selectCourse;
    @ViewById(R.id.ll_no_data)
    RelativeLayout ll_no_data;
    @ViewById(R.id.rl_calendar_view)
    RelativeLayout rl_calendar_view;
    @ViewById(R.id.course_date_picker)
    DateRangeCalendarView course_date_picker;

    @Bean(WeekPlanningPresenter.class)
    PlanningContract.weekOnePlanningPresenter planningPresenter;
    RV_SelectCourseAdapter selectCourseAdapter;
    private Calendar startDate;
    private Calendar endDate;
    private Modal_ContentDetail selectedCourse;
    private int currentSubjectSelected;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DATE_PICKER:
                    Animate.with(Techniques.BounceInUp)
                            .duration(700)
                            .onStart(animator -> rl_calendar_view.setVisibility(View.VISIBLE))
                            .playOn(rl_calendar_view);
                    initializeCalendar();
                    break;
                case HIDE_DATE_PICKER:
                    Animate.with(Techniques.SlideOutDown)
                            .duration(700)
                            .onEnd(animator -> rl_calendar_view.setVisibility(View.GONE))
                            .playOn(rl_calendar_view);
                    break;
            }
        }
    };

    @AfterViews
    public void init() {
        planningPresenter.setNewCoursesView(this);
        new Handler().postDelayed(() -> planningPresenter.loadCourses(), 400);
    }

    @Override
    public void onResume() {
        super.onResume();
        planningPresenter.setNewCoursesView(this);
    }

    @UiThread
    @Override
    public void loadCourses(HashMap<String, List<Modal_ContentDetail>> courses) {
        if (courses != null && !courses.isEmpty()) {
            ll_no_data.setVisibility(View.GONE);
            if (selectCourseAdapter == null) {
                selectCourseAdapter = new RV_SelectCourseAdapter(getActivity(), courses, Fragment_NewCourses.this);
                rv_selectCourse.addScrollStateChangeListener(scrollStateChangeListener);
                rv_selectCourse.setAdapter(selectCourseAdapter);
                rv_selectCourse.setItemTransitionTimeMillis(150);
                rv_selectCourse.setItemTransformer(new ScaleTransformer.Builder()
                        .setMinScale(0.8f)
                        .setMaxScale(1.0f)
                        .build());
            }
            selectCourseAdapter.notifyDataSetChanged();
        } else {
            ll_no_data.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void selectCourse(View view, int adapterPosition) {
        planningPresenter.loadCourses();
    }

    @Override
    public void showDatePicker(Modal_ContentDetail selectedCourse, int parentPos) {
        if (currentSubjectSelected == parentPos) {
            this.selectedCourse = selectedCourse;
            mHandler.sendEmptyMessage(SHOW_DATE_PICKER);
        } else rv_selectCourse.smoothScrollToPosition(parentPos);
    }

    @Click(R.id.iv_close_calendar)
    public void setCloseCalendar() {
        mHandler.sendEmptyMessage(HIDE_DATE_PICKER);
    }

    private void initializeCalendar() {
        Calendar startSelectionDate = Calendar.getInstance();
        startSelectionDate.add(Calendar.MONTH, -1);
        Calendar endSelectionDate = (Calendar) startSelectionDate.clone();
        endSelectionDate.add(Calendar.DATE, 4);
        course_date_picker.setSelectedDateRange(startSelectionDate, endSelectionDate);
        course_date_picker.setCalendarListener(new DateRangeCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar startDate) {

            }

            @Override
            public void onDateRangeSelected(Calendar s_Date, Calendar e_Date) {
                startDate = s_Date;
                endDate = e_Date;
            }
        });
    }

    @UiThread
    @Override
    public void courseAlreadySelected() {
        Toast.makeText(getActivity(), R.string.course_already_enrolled, Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.btn_course_time_select)
    public void onCourseTimeSelected() {
        if (endDate == null || startDate == null) {
            Toast.makeText(getActivity(), R.string.select_correct_timeline, Toast.LENGTH_SHORT).show();
            return;
        }
        mHandler.sendEmptyMessage(HIDE_DATE_PICKER);
        planningPresenter.addCourseToDb("WEEK_1", selectedCourse, startDate, endDate);
    }

    DiscreteScrollView.ScrollStateChangeListener scrollStateChangeListener = new DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder>() {
        @Override
        public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

        }

        @Override
        public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
            currentSubjectSelected = adapterPosition;
        }

        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

        }
    };

    @Override
    public void moveToCenter(int adapterPosition) {
        rv_selectCourse.smoothScrollToPosition(adapterPosition);
    }

    @Override
    public void courseAdded() {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.NEW_COURSE_ENROLLED);
        EventBus.getDefault().post(message);
    }

    @Click(R.id.btn_goto_home)
    public void setGotoHome() {
        EventMessage msg = new EventMessage();
        msg.setMessage(PD_Constant.SHOW_HOME);
        EventBus.getDefault().post(msg);
    }
}
