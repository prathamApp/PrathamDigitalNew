package com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.List;

public class RV_ExpandedCoursesDetails extends RecyclerView.Adapter<RV_ExpandedCoursesDetails.ViewHolder> {

    private List<Modal_ContentDetail> modal_contentDetails;
    private PlanningContract.newCoursesView newCoursesView;
    private Context context;
    private int color;
    private int parentPos;

    public RV_ExpandedCoursesDetails(Context context, List<Modal_ContentDetail> modal_contentDetails,
                                     PlanningContract.newCoursesView newCoursesView, int color, int parentPos) {
        this.context = context;
        this.color = color;
        this.parentPos = parentPos;
        this.newCoursesView = newCoursesView;
        this.modal_contentDetails = modal_contentDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_of_expansion_select_course, parent, false);
        return new RV_ExpandedCoursesDetails.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        Drawable background = viewHolder.iv_select_course.getBackground();
        ((GradientDrawable) background).setColor(color);
        viewHolder.txt_course_name.setText(modal_contentDetails.get(viewHolder.getAdapterPosition()).getNodetitle());
        Drawable dIcon = context.getResources().getDrawable(R.drawable.ic_next);
        int margin = dIcon.getIntrinsicWidth() + 10;
        if (modal_contentDetails.get(viewHolder.getAdapterPosition()).getNodedesc() != null) {
//            String asgnmt = "";
//            if (modal_contentDetails.get(viewHolder.getAdapterPosition()).getAssignment() != null)
//                asgnmt = "\n" + modal_contentDetails.get(viewHolder.getAdapterPosition())
//                        .getAssignment().replaceAll("\\\\\n", "\n");
            String dscrptn = modal_contentDetails.get(viewHolder.getAdapterPosition()).getNodedesc();
            viewHolder.txt_course_info.setText(dscrptn /*+ asgnmt*/);
        }
        viewHolder.btn_course_select.setOnClickListener(v ->
                newCoursesView.showDatePicker(modal_contentDetails.get(viewHolder.getAdapterPosition()), parentPos));
    }

    @Override
    public int getItemCount() {
        return modal_contentDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_course_name;
        TextView txt_course_info;
        ImageView iv_select_course;
        MaterialCardView btn_course_select;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_course_name = itemView.findViewById(R.id.txt_course_name);
            txt_course_info = itemView.findViewById(R.id.txt_course_info);
            iv_select_course = itemView.findViewById(R.id.iv_select_course);
            btn_course_select = itemView.findViewById(R.id.btn_course_select);
        }
    }
}
