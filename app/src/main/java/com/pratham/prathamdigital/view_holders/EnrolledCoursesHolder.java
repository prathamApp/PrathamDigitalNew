package com.pratham.prathamdigital.view_holders;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan.PlanningContract;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

public class EnrolledCoursesHolder extends RecyclerView.ViewHolder {
    private TextView item_course_index;
    private TextView item_en_course_name;
    private TextView item_en_course_detail;
    private ImageView iv_delete_course;
    private RelativeLayout item_view_course;
    private TextView item_course_status;
    private TextView txt_course_action;
    private MaterialCardView root_course;

    public EnrolledCoursesHolder(@NonNull View itemView) {
        super(itemView);
        item_course_index = itemView.findViewById(R.id.item_course_index);
        item_en_course_name = itemView.findViewById(R.id.item_en_course_name);
        item_en_course_detail = itemView.findViewById(R.id.item_en_course_detail);
        item_view_course = itemView.findViewById(R.id.item_view_course);
        iv_delete_course = itemView.findViewById(R.id.iv_delete_course);
        item_course_status = itemView.findViewById(R.id.item_course_status);
        txt_course_action = itemView.findViewById(R.id.txt_course_action);
//        item_step_bar = itemView.findViewById(R.id.item_step_bar);
        root_course = itemView.findViewById(R.id.root_course);
    }

    @SuppressLint("SetTextI18n")
    public void setView(int pos, Model_CourseEnrollment c_Enrolled, PlanningContract.enrolledView planningView) {
        Modal_ContentDetail courseDetail = c_Enrolled.getCourseDetail();
        root_course.setCardBackgroundColor(PD_Utility.getRandomColorGradient());
        item_course_index.setText("0" + (pos + 1));
        View.OnClickListener onClickListener = v -> planningView.playCourse(pos, c_Enrolled);
        switch (c_Enrolled.getCourse_status()) {
            case PD_Constant.COURSE_NOT_VERIFIED:
                iv_delete_course.setVisibility(View.VISIBLE);
                item_view_course.setVisibility(View.GONE);
                item_course_status.setVisibility(View.GONE);
                item_view_course.setOnClickListener(null);
                break;
            case PD_Constant.COURSE_ENROLLED:
                iv_delete_course.setVisibility(View.GONE);
                item_view_course.setVisibility(View.VISIBLE);
                item_course_status.setVisibility(View.VISIBLE);
                item_course_status.setText("Enrolled");
                txt_course_action.setText("View");
                item_view_course.setOnClickListener(onClickListener);
                break;
            case PD_Constant.COURSE_COMPLETED: //used this to go to feedback directly by skipping submit assignment
                iv_delete_course.setVisibility(View.GONE);
                item_view_course.setVisibility(View.VISIBLE);
                item_course_status.setVisibility(View.VISIBLE);
                item_course_status.setText("Course Completed");
                txt_course_action.setText("Give Feedback");
                item_view_course.setOnClickListener(v -> planningView.provideFeedback(pos, c_Enrolled));
                break;
            case PD_Constant.ASSIGNMENT_SUBMITTED:
                iv_delete_course.setVisibility(View.GONE);
                item_view_course.setVisibility(View.VISIBLE);
                item_course_status.setVisibility(View.VISIBLE);
                item_course_status.setText("Assignment Submitted");
                txt_course_action.setText("Give Feedback");
                item_view_course.setOnClickListener(v -> planningView.provideFeedback(pos, c_Enrolled));
                break;
            case PD_Constant.FEEDBACK_GIVEN:
                iv_delete_course.setVisibility(View.GONE);
                item_view_course.setVisibility(View.VISIBLE);
                item_course_status.setVisibility(View.VISIBLE);
                item_course_status.setText("Feedback Submitted");
                txt_course_action.setVisibility(View.GONE);
                item_view_course.setOnClickListener(null);
                break;
        }
        if (courseDetail != null) {
            item_en_course_name.setText(courseDetail.getNodetitle());
            item_en_course_detail.setText(courseDetail.getNodedesc()
                    + ((courseDetail.getNodedesc().length() > 0) ? "\n" : "") +
                    parseDate(c_Enrolled.getPlanFromDate()) + "  -  " + parseDate(c_Enrolled.getPlanToDate()));
        }
        iv_delete_course.setOnClickListener(v -> planningView.deleteCourse(pos, c_Enrolled));
    }

    private String parseDate(String date) {
        String[] date_split = date.split(" ");
        return date_split[1] + " " + date_split[2] + " " + date_split[3] + "," + date_split[6];
    }

    public void updateView() {
        iv_delete_course.setVisibility(View.GONE);
    }
}