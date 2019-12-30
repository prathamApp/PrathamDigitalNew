package com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan;

import android.view.View;

import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public interface PlanningContract {
    interface enrolledView {

        void loadEnrolledCourses(List<Model_CourseEnrollment> coursesEnrolled);

        void showEnrolledList(List<Model_CourseEnrollment> courseEnrollments);

        void addAnotherCourse(View view);

        void showVerificationButton();

        void deleteCourse(int pos, Model_CourseEnrollment c_enrolled);

        void noCoursesEnrolled();

        void playCourse(int pos, Model_CourseEnrollment c_enrolled);

        void showChilds(Model_CourseEnrollment parent_course, List<Modal_ContentDetail> childs, String nodeid);

        void verifiedSuccessfully(Model_CourseEnrollment model_courseEnrollment);

        void provideFeedback(int pos, Model_CourseEnrollment c_enrolled);
    }

    interface newCoursesView {
        void loadCourses(HashMap<String, List<Modal_ContentDetail>> courses);

        void showDatePicker(Modal_ContentDetail modal_contentDetail, int adapterPosition);

        void selectCourse(View view, int adapterPosition);

        void courseAlreadySelected();

        void moveToCenter(int adapterPosition);

        void courseAdded();
    }

    interface weekOnePlanningPresenter {
        void setEnrolledView(enrolledView planningView);

        void setNewCoursesView(newCoursesView newCoursesView);

        void loadCourses();

        void fetchEnrolledCourses(String week);

        void addCourseToDb(String week_1, Modal_ContentDetail selectedCourse, Calendar startDate, Calendar endDate);

        void deleteCourse(Model_CourseEnrollment c_enrolled, String week_1);

        void markCoursesVerified(List<Model_CourseEnrollment> week_1, String imagePath);

        void fetchCourseChilds(Model_CourseEnrollment c_enrolled);
    }
}
