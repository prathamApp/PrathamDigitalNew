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

        void showEnrolledList(List<Model_CourseEnrollment> courseEnrollments);

        void courseAlreadySelected();

        void addAnotherCourse(View view);

        void showVerificationButton();

        void deleteCourse(int pos, Model_CourseEnrollment c_enrolled);

        void noCoursesEnrolled();

        void courseCompleted(int pos, Model_CourseEnrollment c_enrolled);

        void playCourse(int pos, Model_CourseEnrollment c_enrolled);

        void showChilds(List<Modal_ContentDetail> childs, String nodeid);

        void verifiedSuccessfully(Model_CourseEnrollment model_courseEnrollment);
    }

    interface weekOnePlanningPresenter {
        void setWeekOneView(weekOnePlanningView planningView);

        void loadCourses();

        void fetchEnrolledCourses(String week);

        void addCourseToDb(String week_1, Modal_ContentDetail selectedCourse, Calendar startDate, Calendar endDate);

        void deleteCourse(Model_CourseEnrollment c_enrolled, String week_1);

        void markCoursesVerified(String week_1, String imagePath);

        void fetchCourseChilds(Model_CourseEnrollment c_enrolled);

        void checkProgress(String week, String courseId);
    }
}
