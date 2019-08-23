package com.pratham.prathamdigital.ui.fragment_week_course_plan;

import android.content.Context;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@EBean
public class WeekPlanningPresenter implements PlanningContract.weekOnePlanningPresenter {
    private Context context;
    private PlanningContract.weekOnePlanningView planningView;
    private HashMap<String, List<Model_CourseEnrollment>> coursesPerWeek = new HashMap<>();

    public WeekPlanningPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setWeekOneView(PlanningContract.weekOnePlanningView planningView) {
        this.planningView = planningView;
    }

    @Background
    @Override
    public void loadCourses() {
        HashMap<String, List<Modal_ContentDetail>> courses = new HashMap<>();
        List<Modal_ContentDetail> details = PrathamApplication.modalContentDao.getAllCourses();
        for (Modal_ContentDetail mcd : details) {
            List<String> parents = PrathamApplication.modalContentDao.getAllParentsOfCourses(mcd.getNodeid());
            String courseParent;
            if (parents != null) {
                if (parents.size() > 1)
                    courseParent = parents.get(1);  //assuming the static or known flow of the hierarchy
                else courseParent = parents.get(0);
                List<Modal_ContentDetail> courseChilds;
                if (courses.containsKey(courseParent)) {
                    courseChilds = courses.get(courseParent);
                    courseChilds.add(mcd);
                } else {
                    courseChilds = new ArrayList<>();
                    courseChilds.add(mcd);
                }
                courses.put(courseParent, courseChilds);
            }
        }
        planningView.loadCourses(courses);
    }

    @Background
    @Override
    public void fetchEnrolledCourses(String week) {
        coursesPerWeek.remove(week);
        String groupId = FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group");
        List<Model_CourseEnrollment> courseEnrollments = PrathamApplication.courseDao.fetchEnrolledCourses(groupId, week);
        boolean areAllCoursesVerified = true;
        for (Model_CourseEnrollment ce : courseEnrollments) {
            ce.setCourseDetail(PrathamApplication.modalContentDao.getContent(ce.getCourseId(),
                    FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)));
            if (!ce.isCoachVerified()) areAllCoursesVerified = false;
        }
        if (courseEnrollments.size() > 0)
            coursesPerWeek.put(week, new ArrayList<>(courseEnrollments));
        if (!areAllCoursesVerified) {
            planningView.showVerificationButton();
            if (courseEnrollments.size() > 0 && courseEnrollments.size() <= 5)
                courseEnrollments.add(new Model_CourseEnrollment());
        }
        planningView.loadEnrolledCourses(courseEnrollments);
    }

    @Background
    @Override
    public void addCourseToDb(String week, Modal_ContentDetail selectedCourse, Calendar startDate, Calendar endDate) {
        String groupId = FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group");
        Model_CourseEnrollment isCourseAlreadyEnrolled = PrathamApplication.courseDao.
                checkIfCourseEnrolled(selectedCourse.getNodeid(), groupId, week);
        if (isCourseAlreadyEnrolled == null) {
            Model_CourseEnrollment courseEnrollment = new Model_CourseEnrollment();
            courseEnrollment.setCoachVerificationDate("");
            courseEnrollment.setCoachVerified(false);
            courseEnrollment.setCourseExperience("");
            courseEnrollment.setCourseDetail(selectedCourse);
            courseEnrollment.setCourseId(selectedCourse.getNodeid());
            courseEnrollment.setGroupId(groupId);
            courseEnrollment.setPlanFromDate(week + " " + startDate.getTime().toString());
            courseEnrollment.setPlanToDate(week + " " + endDate.getTime().toString());
            courseEnrollment.setSentFlag(0);
            //add @courseEnrollment in hashmap and db
            List<Model_CourseEnrollment> enrollments;
            if (coursesPerWeek.containsKey(week)) {
                enrollments = new ArrayList<>(Objects.requireNonNull(coursesPerWeek.get(week)));
                enrollments.add(courseEnrollment);
            } else {
                enrollments = new ArrayList<>();
                enrollments.add(courseEnrollment);
            }
            coursesPerWeek.put(week, enrollments);
            PrathamApplication.courseDao.insertCourse(courseEnrollment);
            List<Model_CourseEnrollment> coursesEnrolled = new ArrayList<>(Objects.requireNonNull(coursesPerWeek.get(week)));
            //add footer to the list for showing add more courses button
            if (coursesEnrolled.size() > 0 && coursesEnrolled.size() <= 5)
                coursesEnrolled.add(new Model_CourseEnrollment());
            planningView.showEnrolledList(coursesEnrolled);
        } else {
            //course is already added in that particular week
            planningView.courseAlreadySelected();
        }
    }

    @Background
    @Override
    public void deleteCourse(Model_CourseEnrollment c_enrolled, String week) {
        PrathamApplication.courseDao.deleteCourse(c_enrolled.getCourseId(), c_enrolled.getGroupId(), week);
        List<Model_CourseEnrollment> enrollments = coursesPerWeek.get(week);
        for (int i = 0; i < Objects.requireNonNull(enrollments).size(); i++) {
            if (enrollments.get(i).getCourseId().equalsIgnoreCase(c_enrolled.getCourseId())) {
                Objects.requireNonNull(coursesPerWeek.get(week)).remove(i);
                break;
            }
        }
    }
}
