package com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.expansionpanel.ExpansionLayout;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RV_SelectCourseAdapter extends RecyclerView.Adapter<RV_SelectCourseAdapter.ViewHolder> {

    private HashMap<String, List<Modal_ContentDetail>> courses;
    private List<String> courses_names;
    private Context context;
    private PlanningContract.weekOnePlanningView planningView;

    public RV_SelectCourseAdapter(Context context, HashMap<String, List<Modal_ContentDetail>> courses,
                                  PlanningContract.weekOnePlanningView planningView) {
        this.context = context;
        this.planningView = planningView;
        this.courses = courses;
        courses_names = new ArrayList<>(courses.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_select_course, parent, false);
        return new RV_SelectCourseAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        viewHolder.exp_header_name.setText(courses_names.get(viewHolder.getAdapterPosition()));
        viewHolder.expansionLayout.collapse(true);
        RV_ExpandedCoursesDetails expandedCoursesDetails = new RV_ExpandedCoursesDetails(context,
                courses.get(courses_names.get(viewHolder.getAdapterPosition())), planningView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        viewHolder.rv_exp_course_details.setLayoutManager(linearLayoutManager);
        viewHolder.rv_exp_course_details.setAdapter(expandedCoursesDetails);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView exp_header_name;
        RecyclerView rv_exp_course_details;
        ExpansionLayout expansionLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            exp_header_name = itemView.findViewById(R.id.exp_header_name);
            rv_exp_course_details = itemView.findViewById(R.id.rv_exp_course_details);
            expansionLayout = itemView.findViewById(R.id.expansionLayout);
        }
    }
}
