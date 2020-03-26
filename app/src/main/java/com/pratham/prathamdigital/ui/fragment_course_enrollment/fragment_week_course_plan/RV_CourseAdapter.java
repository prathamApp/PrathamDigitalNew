package com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.view_holders.EnrolledCoursesHolder;
import com.pratham.prathamdigital.view_holders.FooterAddNewCourse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RV_CourseAdapter extends RecyclerView.Adapter {

    private static final int NORMAL_CONTENT = 1;
    private static final int FOOTER_CONTENT = 2;
    private PlanningContract.enrolledView planningView;
    private final AsyncListDiffer<Model_CourseEnrollment> mDiffer;

    RV_CourseAdapter(Context context, PlanningContract.enrolledView planningView) {
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
                return new EnrolledCoursesHolder(v);
            case FOOTER_CONTENT:
                LayoutInflater inflater2 = LayoutInflater.from(parent.getContext());
                View v2 = inflater2.inflate(R.layout.item_footer_add_new_course, parent, false);
                return new FooterAddNewCourse(v2);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {
        switch (viewHolder.getItemViewType()) {
            case NORMAL_CONTENT:
                EnrolledCoursesHolder enrolledCoursesHolder = (EnrolledCoursesHolder) viewHolder;
                enrolledCoursesHolder.setView(enrolledCoursesHolder.getAdapterPosition(),
                        mDiffer.getCurrentList().get(enrolledCoursesHolder.getAdapterPosition()), planningView);
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
                    enrolledCoursesHolder.updateView();
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

    void updateData(List<Model_CourseEnrollment> coursesEnrolled) {
        mDiffer.submitList(null);
        mDiffer.submitList(coursesEnrolled);
    }

    void removeItem(int pos) {
        List<Model_CourseEnrollment> lst = new ArrayList<>(getData());
        lst.remove(pos);
        updateData(lst);
    }

    public List<Model_CourseEnrollment> getData() {
        return mDiffer.getCurrentList();
    }
}
