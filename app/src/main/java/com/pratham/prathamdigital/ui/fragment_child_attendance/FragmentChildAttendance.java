package com.pratham.prathamdigital.ui.fragment_child_attendance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Student;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentChildAttendance extends Fragment implements ContractChildAttendance.attendanceView {

    @BindView(R.id.rv_child)
    RecyclerView rv_child;

    ChildAdapter childAdapter;
    ArrayList<Modal_Student> students;
    ArrayList<String> avatars;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_child_attendance, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setChilds(ArrayList<Modal_Student> childs) {
        if (childAdapter == null) {
            childAdapter = new ChildAdapter(getActivity(), students, avatars, FragmentChildAttendance.this);
            rv_child.setHasFixedSize(true);
            rv_child.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rv_child.setAdapter(childAdapter);
        } else {
            childAdapter.updateChildItems(childs);
        }
    }

    @Override
    public void childItemClicked(Modal_Student student, int position) {
        for (Modal_Student stu : students) {
            if (stu.getStudentId().equalsIgnoreCase(student.getStudentId())) {
                stu.setChecked(true);
                break;
            }
        }
        setChilds(students);
    }
}
