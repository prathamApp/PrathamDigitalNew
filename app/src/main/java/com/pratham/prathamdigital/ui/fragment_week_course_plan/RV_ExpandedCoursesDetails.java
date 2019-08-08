package com.pratham.prathamdigital.ui.fragment_week_course_plan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.List;

public class RV_ExpandedCoursesDetails extends RecyclerView.Adapter<RV_ExpandedCoursesDetails.ViewHolder> {

    private List<Modal_ContentDetail> modal_contentDetails;
    private PlanningContract.weekOnePlanningView planningView;

    public RV_ExpandedCoursesDetails(Context context, List<Modal_ContentDetail> modal_contentDetails,
                                     PlanningContract.weekOnePlanningView planningView) {
        Context context1 = context;
        this.planningView = planningView;
        this.modal_contentDetails = modal_contentDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_of_expansion_select_course, parent, false);
        return new RV_ExpandedCoursesDetails.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        viewHolder.txt_course_name.setText(modal_contentDetails.get(viewHolder.getAdapterPosition()).getNodetitle());
        viewHolder.txt_course_info.setText(modal_contentDetails.get(viewHolder.getAdapterPosition()).getNodedesc());
        viewHolder.txt_assignment.setText(modal_contentDetails.get(viewHolder.getAdapterPosition()).getNodetitle());
        viewHolder.btn_course_select.setOnClickListener(v -> {
            planningView.showDatePicker(modal_contentDetails.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
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
        return modal_contentDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_course_name;
        TextView txt_course_info;
        TextView txt_assignment;
        MaterialCardView btn_course_select;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_course_name = itemView.findViewById(R.id.txt_course_name);
            txt_course_info = itemView.findViewById(R.id.txt_course_info);
            txt_assignment = itemView.findViewById(R.id.txt_assignment);
            btn_course_select = itemView.findViewById(R.id.btn_course_select);
        }
    }
}
