package com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan;

import android.content.Context;

import com.google.gson.Gson;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.models.Model_CourseExperience;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@EBean
public class WeekPlanningPresenter implements PlanningContract.weekOnePlanningPresenter {
    private Context context;
    private PlanningContract.enrolledView planningView;
    private PlanningContract.newCoursesView newCoursesView;
    private HashMap<String, List<Model_CourseEnrollment>> coursesPerWeek = new HashMap<>();

    public WeekPlanningPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setEnrolledView(PlanningContract.enrolledView planningView) {
        this.planningView = planningView;
    }

    @Override
    public void setNewCoursesView(PlanningContract.newCoursesView newCoursesView) {
        this.newCoursesView = newCoursesView;
    }

    @Background
    @Override
    public void loadCourses() {
        HashMap<String, List<Modal_ContentDetail>> courses = new HashMap<>();
        List<Modal_ContentDetail> details = PrathamApplication.modalContentDao.
                getAllCourses(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
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
        newCoursesView.loadCourses(courses);
    }

    @Background
    @Override
    public void fetchEnrolledCourses(String week) {
        String groupId = FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group");
        List<Model_CourseEnrollment> courseEnrollments = enrolledCoursesFromDb(week, groupId);
        boolean areAllCoursesVerified = true;
        if (courseEnrollments != null) {
            for (Model_CourseEnrollment ce : courseEnrollments) {
                ce.setCourse_status(readCourseStatusFromExperience(ce));
                if (ce.getCourse_status().equalsIgnoreCase(PD_Constant.COURSE_NOT_VERIFIED))
                    areAllCoursesVerified = false;
            }
            if (!areAllCoursesVerified) {
                planningView.showVerificationButton();
                if (courseEnrollments.size() > 0)
                    courseEnrollments.add(new Model_CourseEnrollment());
            } else {
                if (courseEnrollments.size() > 0)
                    planningView.verifiedSuccessfully(courseEnrollments.get(0));
            }
        }
        planningView.loadEnrolledCourses(courseEnrollments);
    }

    private String readCourseStatusFromExperience(Model_CourseEnrollment ce) {
        Model_CourseExperience courseExperience = new Gson().fromJson(ce.getCourseExperience(), Model_CourseExperience.class);
        return courseExperience.getStatus();
    }

    private List<Model_CourseEnrollment> enrolledCoursesFromDb(String week, String groupId) {
        coursesPerWeek.remove(week);
        List<Model_CourseEnrollment> courseEnrollments = PrathamApplication.courseDao.
                fetchEnrolledCourses(groupId, week, FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        if (courseEnrollments == null) return null;
        List<Model_CourseEnrollment> temp = new ArrayList<>();
        for (Model_CourseEnrollment ce : courseEnrollments) {
            Model_CourseExperience courseExperience = new Gson().fromJson(ce.getCourseExperience(), Model_CourseExperience.class);
            if (!courseExperience.getStatus().equalsIgnoreCase(PD_Constant.FEEDBACK_GIVEN)) {
                ce.setCourseDetail(PrathamApplication.modalContentDao.getContent(ce.getCourseId(),
                        FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)));
                ce.setProgressCompleted(isCourseProgressCompleted(ce, week));
                temp.add(ce);
            }
        }
        if (temp.size() > 0)
            coursesPerWeek.put(week, temp);
        return temp;
    }

    private boolean isCourseProgressCompleted(Model_CourseEnrollment ce, String week) {
        String progress = PrathamApplication.contentProgressDao.getCourseProgress(ce.getGroupId(), ce.getCourseId(), week);
        //considering the case if the progress is not complete 100%
        if (progress != null) {
            return Integer.parseInt(progress) > 95;
        } else return false;
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
            //add experience as json object string in db
            Model_CourseExperience model_courseExperience = new Model_CourseExperience();
            model_courseExperience.setAssignments(null);
            model_courseExperience.setWords_learnt("");
            model_courseExperience.setAssignments_completed("");
            model_courseExperience.setAssignments_description("");
            model_courseExperience.setCoach_comments("");
            model_courseExperience.setCoach_verification_date("");
            model_courseExperience.setCoach_image("");
            model_courseExperience.setAssignment_submission_date(PD_Utility.getCurrentDateTime());
            model_courseExperience.setStatus(PD_Constant.COURSE_NOT_VERIFIED);

            courseEnrollment.setCourseExperience(new Gson().toJson(model_courseExperience));
            courseEnrollment.setCourseDetail(selectedCourse);
            courseEnrollment.setCourseId(selectedCourse.getNodeid());
            courseEnrollment.setGroupId(groupId);
            courseEnrollment.setPlanFromDate(week + " " + startDate.getTime().toString());
            courseEnrollment.setPlanToDate(week + " " + endDate.getTime().toString());
            courseEnrollment.setSentFlag(0);
            courseEnrollment.setLanguage(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
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
            newCoursesView.courseAdded();
        } else {
            //course is already added in that particular week
            newCoursesView.courseAlreadySelected();
        }
    }

    @Background
    @Override
    public void deleteCourse(Model_CourseEnrollment c_enrolled, String week) {
        PrathamApplication.courseDao.deleteCourse(c_enrolled.getCourseId(), c_enrolled.getGroupId(),
                week, FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
        List<Model_CourseEnrollment> enrollments = coursesPerWeek.get(week);
        for (int i = 0; i < Objects.requireNonNull(enrollments).size(); i++) {
            if (enrollments.get(i).getCourseId().equalsIgnoreCase(c_enrolled.getCourseId())) {
                Objects.requireNonNull(coursesPerWeek.get(week)).remove(i);
                break;
            }
        }
    }

    @Background
    @Override
    public void markCoursesVerified(List<Model_CourseEnrollment> enrollments, String imagePath) {
        List<Model_CourseEnrollment> temp = new ArrayList<>(enrollments);
        temp.remove(temp.size() - 1); //to remove the null item i.e footer item
        for (Model_CourseEnrollment mce : temp) {
            if (mce != null && mce.getCourseId() != null) {
                mce.setCoachVerified(true);
                mce.setCoachImage(imagePath);
                mce.setCoachVerificationDate(PD_Utility.getCurrentDateTime());
                mce.setCourseExperience(updateCourseStatusInExperience(mce));
                mce.setCourse_status(readCourseStatusFromExperience(mce));
                PrathamApplication.courseDao.updateCourse(mce);
            }
        }
        planningView.loadEnrolledCourses(temp);
    }

    private String updateCourseStatusInExperience(Model_CourseEnrollment ce) {
        Gson gson = new Gson();
        Model_CourseExperience courseExperience = gson.fromJson(ce.getCourseExperience(), Model_CourseExperience.class);
        courseExperience.setStatus(PD_Constant.COURSE_ENROLLED);
        return gson.toJson(courseExperience);
    }

    @Background
    @Override
    public void fetchCourseChilds(Model_CourseEnrollment c_enrolled) {
        Modal_ContentDetail detail = c_enrolled.getCourseDetail();
        List<Modal_ContentDetail> childs = PrathamApplication.modalContentDao.getChildsOfParent(detail.getNodeid(), detail.getAltnodeid(), detail.getContent_language());
        Collections.sort(childs, (o1, o2) -> o1.getNodetitle().compareToIgnoreCase(o2.getNodetitle()));
        planningView.showChilds(c_enrolled, childs, detail.getNodeid());
    }
}
