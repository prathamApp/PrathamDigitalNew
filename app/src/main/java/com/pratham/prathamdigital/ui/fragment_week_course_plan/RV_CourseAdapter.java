package com.pratham.prathamdigital.ui.fragment_week_course_plan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;

import java.util.List;

public class RV_CourseAdapter extends RecyclerView.Adapter {

    private static final int NORMAL_CONTENT = 1;
    private static final int FOOTER_CONTENT = 2;
    private PlanningContract.weekOnePlanningView planningView;
    private List<Model_CourseEnrollment> coursesEnrolled;

    public RV_CourseAdapter(Context context, PlanningContract.weekOnePlanningView planningView,
                            List<Model_CourseEnrollment> coursesEnrolled) {
        Context context1 = context;
        this.planningView = planningView;
        this.coursesEnrolled = coursesEnrolled;
    }

    @Override
    public int getItemViewType(int position) {
        if (position != coursesEnrolled.size() - 1)
            return NORMAL_CONTENT;
        else return FOOTER_CONTENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case NORMAL_CONTENT:
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View v = inflater.inflate(R.layout.item_course_enroll, parent, false);
                return new RV_CourseAdapter.EnrolledCoursesHolder(v);
            case FOOTER_CONTENT:
                LayoutInflater inflater2 = LayoutInflater.from(parent.getContext());
                View v2 = inflater2.inflate(R.layout.item_footer_add_new_course, parent, false);
                return new RV_CourseAdapter.FooterAddNewCourse(v2);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {
        switch (viewHolder.getItemViewType()) {
            case NORMAL_CONTENT:
                EnrolledCoursesHolder enrolledCoursesHolder = (EnrolledCoursesHolder) viewHolder;
                enrolledCoursesHolder.setView(enrolledCoursesHolder.getAdapterPosition(), coursesEnrolled.get(enrolledCoursesHolder.getAdapterPosition()));
                break;
            case FOOTER_CONTENT:
                FooterAddNewCourse footerAddNewCourse = (FooterAddNewCourse) viewHolder;
                footerAddNewCourse.setView(footerAddNewCourse.getAdapterPosition(), planningView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return coursesEnrolled.size();
    }

    public void updateData(List<Model_CourseEnrollment> coursesEnrolled) {
        this.coursesEnrolled.clear();
        this.coursesEnrolled = coursesEnrolled;
        notifyDataSetChanged();
    }

    public void removeItem(int pos) {
        coursesEnrolled.remove(pos);
        if (coursesEnrolled.size() <= 1)
            planningView.noCoursesEnrolled();
        else
            notifyItemRemoved(pos);
    }

    public void removeAddNewCourseButton() {
        int pos = coursesEnrolled.size() - 1;
        coursesEnrolled.remove(pos);
        notifyItemRemoved(pos);
    }

    public class EnrolledCoursesHolder extends RecyclerView.ViewHolder {
        TextView item_course_index;
        TextView item_en_course_name;
        TextView item_en_course_detail;
        TextView item_en_course_assign;
        TextView item_en_course_dates;
        LottieAnimationView item_course_completed_lottie;
        ImageView iv_delete_course;
        ImageView iv_completed_course;

        EnrolledCoursesHolder(@NonNull View itemView) {
            super(itemView);
            item_course_index = itemView.findViewById(R.id.item_course_index);
            item_en_course_name = itemView.findViewById(R.id.item_en_course_name);
            item_en_course_detail = itemView.findViewById(R.id.item_en_course_detail);
            item_en_course_assign = itemView.findViewById(R.id.item_en_course_assign);
            item_en_course_dates = itemView.findViewById(R.id.item_en_course_dates);
            item_course_completed_lottie = itemView.findViewById(R.id.item_course_completed_lottie);
            iv_delete_course = itemView.findViewById(R.id.iv_delete_course);
            iv_completed_course = itemView.findViewById(R.id.iv_completed_course);
        }

        @SuppressLint("SetTextI18n")
        public void setView(int pos, Model_CourseEnrollment c_Enrolled) {
            Modal_ContentDetail courseDetail = c_Enrolled.getCourseDetail();
            item_course_completed_lottie.setVisibility(View.GONE);
            item_course_index.setText("0" + (pos + 1));
            item_en_course_name.setText(courseDetail.getNodetitle());
            item_en_course_assign.setText(courseDetail.getNodedesc());
            item_en_course_detail.setText(courseDetail.getNodedesc());
            item_en_course_dates.setText(c_Enrolled.getPlanFromDate() + " - " + c_Enrolled.getPlanToDate());
            iv_delete_course.setOnClickListener(v -> {
                planningView.deleteCourse(pos, c_Enrolled);
            });
        }
    }

    public class FooterAddNewCourse extends RecyclerView.ViewHolder {
        Button footer_add_new_course;

        FooterAddNewCourse(@NonNull View itemView) {
            super(itemView);
            footer_add_new_course = itemView.findViewById(R.id.footer_add_new_course);
        }

        public void setView(int adapterPosition, PlanningContract.weekOnePlanningView planningView) {
            footer_add_new_course.setOnClickListener(v -> {
                planningView.addAnotherCourse(footer_add_new_course);
            });
        }
    }
}
