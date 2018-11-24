package com.pratham.prathamdigital.ui.fragment_child_attendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.pratham.prathamdigital.BaseActivity.studentDao;

public class FragmentChildAttendance extends Fragment implements ContractChildAttendance.attendanceView {

    @BindView(R.id.rv_child)
    RecyclerView rv_child;

    ChildAdapter childAdapter;
    ArrayList<Modal_Student> students;
    ArrayList<String> avatars;
    private int revealX;
    private int revealY;

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
        students = getArguments().getParcelableArrayList(PD_Constant.STUDENT_LIST);
        avatars = new ArrayList<>();
        if (PrathamApplication.isTablet) {
            for (Modal_Student stu : students) {
                avatars.add(PD_Utility.getRandomAvatar(getActivity()));
            }
        } else {
            for (Modal_Student stu : students) {
                avatars.add(stu.getAvatarName());
            }
        }
        setChilds(students);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setChilds(ArrayList<Modal_Student> childs) {
        if (childAdapter == null) {
            childAdapter = new ChildAdapter(getActivity(), childs, avatars, FragmentChildAttendance.this);
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
                if (stu.isChecked()) stu.setChecked(false);
                else stu.setChecked(true);
                break;
            }
        }
        setChilds(students);
    }

    @OnTouch(R.id.btn_attendance_next)
    public boolean setNextAvatar(View view, MotionEvent event) {
        revealX = (int) event.getRawX();
        revealY = (int) event.getY();
        return getActivity().onTouchEvent(event);
    }

    @OnClick(R.id.btn_attendance_next)
    public void setNext(View v) {
        PrathamApplication.bubble_mp.start();
        FastSave.getInstance().saveString(PD_Constant.AVATAR, "avatars/dino_dance.json");
        presentActivity(v);
    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getActivity(), view, "transition");
        Intent intent = new Intent(getActivity(), ActivityMain.class);
        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_Y, revealY);
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }

}
