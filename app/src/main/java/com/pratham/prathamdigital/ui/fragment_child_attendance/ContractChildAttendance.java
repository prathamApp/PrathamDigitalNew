package com.pratham.prathamdigital.ui.fragment_child_attendance;

import android.view.View;

import com.pratham.prathamdigital.models.Modal_Student;

public interface ContractChildAttendance {
    interface attendanceView {
        void childItemClicked(Modal_Student student, int position);

        void moveToDashboardOnChildClick(Modal_Student student, int position, View v);

        void addChild(View itemView);

        void openCamera(String stud_id);
    }
}
