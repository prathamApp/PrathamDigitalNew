package com.pratham.prathamdigital.view_holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan.PlanningContract;

public class FooterAddNewCourse extends RecyclerView.ViewHolder {
    Button footer_add_new_course;

    public FooterAddNewCourse(@NonNull View itemView) {
        super(itemView);
        footer_add_new_course = itemView.findViewById(R.id.footer_add_new_course);
    }

    public void setView(int adapterPosition, PlanningContract.enrolledView planningView) {
        footer_add_new_course.setOnClickListener(v -> {
            planningView.addAnotherCourse(footer_add_new_course);
        });
    }
}
