package com.pratham.prathamdigital.ui.fragment_select_group;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.ui.fragment_child_attendance.FragmentChildAttendance;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentSelectGroup extends Fragment implements ContractGroup {

    @BindView(R.id.rv_group)
    RecyclerView rv_group;

    GroupAdapter groupAdapter;
    ArrayList<Modal_Groups> groups;
    Modal_Groups groupSelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_group, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if (getArguments().getBoolean(PD_Constant.GROUP_AGE_BELOW_7)) {
            get3to6Groups(BaseActivity.groupDao.getAllGroups(), BaseActivity.studentDao.getAllStudents());
        } else {
            get8to14Groups(BaseActivity.groupDao.getAllGroups(), BaseActivity.studentDao.getAllStudents());
        }
        setGroups(groups);
    }

    private void get3to6Groups(List<Modal_Groups> allGroups, List<Modal_Student> allStudents) {
        for (Modal_Groups gr : allGroups) {
            for (Modal_Student stu : allStudents) {
                if (Integer.parseInt(stu.getAge()) < 7) {
                    if (!groups.contains(gr))
                        groups.add(gr);
                    break;
                }
            }
        }
    }

    private void get8to14Groups(List<Modal_Groups> allGroups, List<Modal_Student> allStudents) {
        for (Modal_Groups gr : allGroups) {
            for (Modal_Student stu : allStudents) {
                if (Integer.parseInt(stu.getAge()) >= 7) {
                    if (!groups.contains(gr))
                        groups.add(gr);
                    break;
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setGroups(ArrayList<Modal_Groups> groups) {
        if (groupAdapter == null) {
            groupAdapter = new GroupAdapter(getActivity(), groups, FragmentSelectGroup.this);
            rv_group.setHasFixedSize(true);
            rv_group.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rv_group.setAdapter(groupAdapter);
        } else {
            groupAdapter.updateGroupItems(groups);
        }
    }

//    @OnTouch(R.id.btn_attendance_next)
//    public boolean setNextAvatar(View view, MotionEvent event) {
//        revealX = (int) event.getRawX();
//        revealY = (int) event.getY();
//        return getActivity().onTouchEvent(event);
//    }

    @OnClick(R.id.btn_group_next)
    public void setNext(View v) {
        PrathamApplication.bubble_mp.start();
        ArrayList<Modal_Student> students = new ArrayList<>();
        students.addAll(BaseActivity.studentDao.getGroupwiseStudents(groupSelected.getGroupId()));
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PD_Constant.STUDENT_LIST, students);
        PD_Utility.showFragment(getActivity(), new FragmentChildAttendance(), R.id.frame_attendance,
                bundle, FragmentChildAttendance.class.getSimpleName());
    }

    @Override
    public void groupItemClicked(Modal_Groups modalGroup, int position) {
        groupSelected = modalGroup;
        for (Modal_Groups gr : groups) {
            if (gr.getGroupId().equalsIgnoreCase(modalGroup.getGroupId())) {
                gr.setSelected(true);
                break;
            }
        }
        setGroups(groups);
    }

//    public void presentActivity(View view) {
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getActivity(), view, "transition");
//        Intent intent = new Intent(getActivity(), ActivityMain.class);
//        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_X, revealX);
//        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_Y, revealY);
//        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//    }

}
