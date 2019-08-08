package com.pratham.prathamdigital.ui.fragment_week_course_plan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pratham.prathamdigital.R;

import java.util.List;

public class RV_CourseAdapter extends RecyclerView.Adapter<RV_CourseAdapter.ViewHolder> {

    private PlanningContract.weekOnePlanningView planningView;

    public RV_CourseAdapter(Context context, PlanningContract.weekOnePlanningView planningView) {
        Context context1 = context;
        this.planningView = planningView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_course_enroll, parent, false);
        return new RV_CourseAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        viewHolder.btn_selectCourse.setOnClickListener(v -> {
            planningView.selectCourse(viewHolder.btn_selectCourse, viewHolder.getAdapterPosition());
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
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_course;
        TextView txt_course_time;
        Button btn_selectCourse;
        Button btn_selectCourseTime;
//        EditText et_why_enroll;
//        ImageView iv_course_listen;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_course = itemView.findViewById(R.id.txt_course);
            txt_course_time = itemView.findViewById(R.id.txt_course_time);
            btn_selectCourse = itemView.findViewById(R.id.btn_selectCourse);
            btn_selectCourseTime = itemView.findViewById(R.id.btn_selectCourseTime);
//            et_why_enroll = itemView.findViewById(R.id.et_why_enroll);
//            iv_course_listen = itemView.findViewById(R.id.iv_course_listen);
        }
    }
}
