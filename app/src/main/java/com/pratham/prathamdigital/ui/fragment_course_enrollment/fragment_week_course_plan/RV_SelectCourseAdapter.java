package com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RV_SelectCourseAdapter extends RecyclerView.Adapter<RV_SelectCourseAdapter.ViewHolder> {

    private HashMap<String, List<Modal_ContentDetail>> courses;
    private List<String> courses_names;
    private Context context;
    private PlanningContract.newCoursesView newCoursesView;

    public RV_SelectCourseAdapter(Context context, HashMap<String, List<Modal_ContentDetail>> courses,
                                  PlanningContract.newCoursesView newCoursesView) {
        this.context = context;
        this.newCoursesView = newCoursesView;
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
        Drawable background = viewHolder.exp_header_name.getBackground();
        int color = PD_Utility.getRandomColorGradient();
        ((GradientDrawable) background).setColor(color);
        viewHolder.exp_header_name.setText(courses_names.get(viewHolder.getAdapterPosition()));
        RV_ExpandedCoursesDetails expandedCoursesDetails = new RV_ExpandedCoursesDetails(context,
                courses.get(courses_names.get(viewHolder.getAdapterPosition())), newCoursesView, color, viewHolder.getAdapterPosition());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        viewHolder.rv_exp_course_details.setLayoutManager(linearLayoutManager);
        viewHolder.rv_exp_course_details.setAdapter(expandedCoursesDetails);
        viewHolder.root_select_item.setOnClickListener(v -> {
            newCoursesView.moveToCenter(viewHolder.getAdapterPosition());
        });
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
        MaterialCardView root_select_item;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            exp_header_name = itemView.findViewById(R.id.exp_header_name);
            rv_exp_course_details = itemView.findViewById(R.id.rv_exp_course_details);
            root_select_item = itemView.findViewById(R.id.root_select_item);
        }
    }
}
