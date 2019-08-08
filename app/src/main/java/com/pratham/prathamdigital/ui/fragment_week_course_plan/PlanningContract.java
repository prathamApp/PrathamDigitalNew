package com.pratham.prathamdigital.ui.fragment_week_course_plan;

import android.view.View;

import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public interface PlanningContract {
    interface weekOnePlanningView {

        void loadCourses(HashMap<String, List<Modal_ContentDetail>> courses);

        void selectCourse(View view, int adapterPosition);

        void loadEnrolledCourses(List<Model_CourseEnrollment> coursesEnrolled);

        void showDatePicker(Modal_ContentDetail modal_contentDetail, int adapterPosition);
    }

    interface weekOnePlanningPresenter {
        void setWeekOneView(weekOnePlanningView planningView);

        void loadCourses();

        void fetchEnrolledCourses(String week);

        void addCourseToDb(String week_1, Modal_ContentDetail selectedCourse, Calendar startDate, Calendar endDate);
    }
}
