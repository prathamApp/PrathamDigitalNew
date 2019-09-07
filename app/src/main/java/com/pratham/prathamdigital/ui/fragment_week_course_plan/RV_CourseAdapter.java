package com.pratham.prathamdigital.ui.fragment_week_course_plan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RV_CourseAdapter extends RecyclerView.Adapter {

    private static final int NORMAL_CONTENT = 1;
    private static final int FOOTER_CONTENT = 2;
    private PlanningContract.weekOnePlanningView planningView;
    private final AsyncListDiffer<Model_CourseEnrollment> mDiffer;

    public RV_CourseAdapter(Context context, PlanningContract.weekOnePlanningView planningView) {
        DiffUtil.ItemCallback<Model_CourseEnrollment> diffcallback = new DiffUtil.ItemCallback<Model_CourseEnrollment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Model_CourseEnrollment detail, @NonNull Model_CourseEnrollment t1) {
                return Objects.equals(detail.getCourseId(), t1.getCourseId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Model_CourseEnrollment detail, @NonNull Model_CourseEnrollment t1) {
                int result = detail.compareTo(t1);
                return result == 0;
            }
        };
        mDiffer = new AsyncListDiffer<>(this, diffcallback);
        Context context1 = context;
        this.planningView = planningView;
//        this.coursesEnrolled = coursesEnrolled;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDiffer.getCurrentList().get(position).getCourseId() != null)
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
                enrolledCoursesHolder.setView(enrolledCoursesHolder.getAdapterPosition(), mDiffer.getCurrentList().get(enrolledCoursesHolder.getAdapterPosition()));
                break;
            case FOOTER_CONTENT:
                FooterAddNewCourse footerAddNewCourse = (FooterAddNewCourse) viewHolder;
                footerAddNewCourse.setView(footerAddNewCourse.getAdapterPosition(), planningView);
                break;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(viewHolder, position, payloads);
        } else {
            switch (viewHolder.getItemViewType()) {
                case NORMAL_CONTENT:
                    EnrolledCoursesHolder enrolledCoursesHolder = (EnrolledCoursesHolder) viewHolder;
                    enrolledCoursesHolder.iv_delete_course.setVisibility(View.GONE);
                    enrolledCoursesHolder.iv_completed_course.setVisibility(View.GONE);
                    enrolledCoursesHolder.btn_resume.setVisibility(View.VISIBLE);
                    break;
                case FOOTER_CONTENT:
                    FooterAddNewCourse footerAddNewCourse = (FooterAddNewCourse) viewHolder;
                    footerAddNewCourse.setView(footerAddNewCourse.getAdapterPosition(), planningView);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void updateData(List<Model_CourseEnrollment> coursesEnrolled) {
        mDiffer.submitList(null);
        mDiffer.submitList(coursesEnrolled);
    }

    public void removeItem(int pos) {
        List<Model_CourseEnrollment> lst = new ArrayList<>(getData());
        lst.remove(pos);
        updateData(lst);
    }

    public List<Model_CourseEnrollment> getData() {
        return mDiffer.getCurrentList();
    }

    public class EnrolledCoursesHolder extends RecyclerView.ViewHolder {
        TextView item_course_index;
        TextView item_en_course_name;
        TextView item_en_course_detail;
        TextView item_en_course_assign;
        TextView item_en_course_dates;
        ImageView iv_delete_course;
        ImageView iv_completed_course;
        Button btn_resume;

        EnrolledCoursesHolder(@NonNull View itemView) {
            super(itemView);
            item_course_index = itemView.findViewById(R.id.item_course_index);
            item_en_course_name = itemView.findViewById(R.id.item_en_course_name);
            item_en_course_detail = itemView.findViewById(R.id.item_en_course_detail);
            item_en_course_assign = itemView.findViewById(R.id.item_en_course_assign);
            item_en_course_dates = itemView.findViewById(R.id.item_en_course_dates);
            iv_delete_course = itemView.findViewById(R.id.iv_delete_course);
            iv_completed_course = itemView.findViewById(R.id.iv_completed_course);
            btn_resume = itemView.findViewById(R.id.btn_resume);
        }

        @SuppressLint("SetTextI18n")
        public void setView(int pos, Model_CourseEnrollment c_Enrolled) {
            Modal_ContentDetail courseDetail = c_Enrolled.getCourseDetail();
            if (c_Enrolled.isCoachVerified()) {
                if (c_Enrolled.isProgressCompleted()) {
                    iv_delete_course.setVisibility(View.GONE);
                    iv_completed_course.setVisibility(View.VISIBLE);
                    btn_resume.setVisibility(View.GONE);
                } else {
                    iv_delete_course.setVisibility(View.GONE);
                    iv_completed_course.setVisibility(View.GONE);
                    btn_resume.setVisibility(View.VISIBLE);
                }
            } else {
                iv_delete_course.setVisibility(View.VISIBLE);
                iv_completed_course.setVisibility(View.GONE);
                btn_resume.setVisibility(View.GONE);
            }
            item_course_index.setText("0" + (pos + 1));
            item_en_course_name.setText(courseDetail.getNodetitle());
            item_en_course_assign.setText(courseDetail.getNodedesc());
            item_en_course_detail.setText(courseDetail.getNodedesc());
            item_en_course_dates.setText(c_Enrolled.getPlanFromDate() + " - " + c_Enrolled.getPlanToDate());
            iv_delete_course.setOnClickListener(v -> planningView.deleteCourse(pos, c_Enrolled));
            iv_completed_course.setOnClickListener(v -> planningView.courseCompleted(pos, c_Enrolled));
            btn_resume.setOnClickListener(v -> planningView.playCourse(pos, c_Enrolled));
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
