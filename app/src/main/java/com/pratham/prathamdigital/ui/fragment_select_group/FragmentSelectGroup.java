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
        ArrayList<String> present_groups = new ArrayList<>();
        String groupId1 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID1);
        if (!groupId1.equalsIgnoreCase("0")) present_groups.add(groupId1);
        String groupId2 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID2);
        if (!groupId2.equalsIgnoreCase("0")) present_groups.add(groupId2);
        String groupId3 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID3);
        if (!groupId3.equalsIgnoreCase("0")) present_groups.add(groupId3);
        String groupId4 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID4);
        if (!groupId4.equalsIgnoreCase("0")) present_groups.add(groupId4);
        String groupId5 = BaseActivity.statusDao.getValue(PD_Constant.GROUPID5);
        if (!groupId5.equalsIgnoreCase("0")) present_groups.add(groupId5);
        if (getArguments().getBoolean(PD_Constant.GROUP_AGE_BELOW_7)) {
            get3to6Groups(present_groups);
        } else {
            get8to14Groups(present_groups);
        }
        setGroups(groups);
    }

    private void get3to6Groups(ArrayList<String> allGroups) {
        groups = new ArrayList<>();
        for (String grID : allGroups) {
            ArrayList<Modal_Student> students = (ArrayList<Modal_Student>) BaseActivity.studentDao.getGroupwiseStudents(grID);
            for (Modal_Student stu : students) {
                if (Integer.parseInt(stu.getAge()) < 7) {
                    Modal_Groups group = BaseActivity.groupDao.getGroupByGrpID(grID);
                    groups.add(group);
                    break;
                }
            }
        }
    }

    private void get8to14Groups(ArrayList<String> allGroups) {
        groups = new ArrayList<>();
        Modal_Groups group;
        for (String grID : allGroups) {
            ArrayList<Modal_Student> students = (ArrayList<Modal_Student>) BaseActivity.studentDao.getGroupwiseStudents(grID);
            for (Modal_Student stu : students) {
                if (Integer.parseInt(stu.getAge()) >= 7) {
                    group = BaseActivity.groupDao.getGroupByGrpID(grID);
                    groups.add(group);
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
            } else
                gr.setSelected(false);
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
