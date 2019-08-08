package com.pratham.prathamdigital.ui.fragment_week_course_plan;

import android.content.Context;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@EBean
public class WeekPlanningPresenter implements PlanningContract.weekOnePlanningPresenter {
    private Context context;
    private PlanningContract.weekOnePlanningView planningView;

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
        //todo fetch courses from dao, ask query from Ganesh
        planningView.loadEnrolledCourses(null);
    }

    @Override
    public void addCourseToDb(String week, Modal_ContentDetail selectedCourse, Calendar startDate, Calendar endDate) {

    }
}
