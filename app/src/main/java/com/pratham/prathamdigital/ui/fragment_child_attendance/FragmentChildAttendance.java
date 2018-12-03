package com.pratham.prathamdigital.ui.fragment_child_attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class FragmentChildAttendance extends Fragment implements ContractChildAttendance.attendanceView, CircularRevelLayout.CallBacks {

    @BindView(R.id.chid_attendance_reveal)
    CircularRevelLayout chid_attendance_reveal;
    @BindView(R.id.rv_child)
    RecyclerView rv_child;
    @BindView(R.id.btn_attendance_next)
    Button btn_attendance_next;
    @BindView(R.id.add_child)
    RelativeLayout add_child;

    ChildAdapter childAdapter;
    ArrayList<Modal_Student> students;
    ArrayList<String> avatars;
    private int revealX;
    private int revealY;
    private String groupID = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_child_attendance, container, false);
        ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            chid_attendance_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    chid_attendance_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    chid_attendance_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        students = getArguments().getParcelableArrayList(PD_Constant.STUDENT_LIST);
        avatars = new ArrayList<>();
        if (PrathamApplication.isTablet) {
            btn_attendance_next.setVisibility(View.VISIBLE);
            add_child.setVisibility(View.GONE);
            groupID = getArguments().getString(PD_Constant.GROUPID);
            for (Modal_Student stu : students)
                avatars.add(PD_Utility.getRandomAvatar(getActivity()));
        } else {
            btn_attendance_next.setVisibility(View.GONE);
            add_child.setVisibility(View.VISIBLE);
            groupID = "SmartPhone";
            for (Modal_Student stu : students)
                avatars.add(stu.getAvatarName());
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
            childAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void childItemClicked(Modal_Student student, int position) {
        PrathamApplication.bubble_mp.start();
        for (Modal_Student stu : students) {
            if (stu.getStudentId().equalsIgnoreCase(student.getStudentId())) {
                if (stu.isChecked()) stu.setChecked(false);
                else stu.setChecked(true);
                break;
            }
        }
        setChilds(students);
    }

    @Override
    public void moveToDashboardOnChildClick(Modal_Student student, int position, View v) {
        PrathamApplication.bubble_mp.start();
        FastSave.getInstance().saveString(PD_Constant.AVATAR, student.getAvatarName());
        ArrayList<Modal_Student> s = new ArrayList<>();
        s.add(student);
        markAttendance(s);
        presentActivity(v);
    }

    @OnTouch(R.id.btn_attendance_next)
    public boolean setNextAvatar(View view, MotionEvent event) {
        revealX = (int) event.getRawX();
        revealY = (int) event.getY();
        return getActivity().onTouchEvent(event);
    }

    @OnTouch(R.id.rv_child)
    public boolean getRecyclerTouch(View view, MotionEvent event) {
        revealX = (int) event.getRawX();
        revealY = (int) event.getY();
        return getActivity().onTouchEvent(event);
    }

    @OnClick(R.id.btn_attendance_next)
    public void setNext(View v) {
        ArrayList<Modal_Student> checkedStds = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).isChecked())
                checkedStds.add(students.get(i));
        }
        if (checkedStds.size() > 0) {
            PrathamApplication.bubble_mp.start();
            FastSave.getInstance().saveString(PD_Constant.AVATAR, "avatars/dino_dance.json");
            markAttendance(students);
            presentActivity(v);
        } else {
            Toast.makeText(getContext(), "Please Select Students !", Toast.LENGTH_SHORT).show();
        }
    }

    public void markAttendance(ArrayList<Modal_Student> stud) {
        FastSave.getInstance().saveString(PD_Constant.SESSIONID, PD_Utility.getUUID().toString());
        ArrayList<Attendance> attendances = new ArrayList<>();
        for (Modal_Student stu : stud) {
            Attendance attendance = new Attendance();
            attendance.SessionID = FastSave.getInstance().getString(PD_Constant.SESSIONID, "");
            attendance.StudentID = stu.getStudentId();
            attendance.Date = PD_Utility.getCurrentDateTime();
            attendance.GroupID = groupID;
            FastSave.getInstance().saveString(PD_Constant.GROUPID, groupID);
            attendances.add(attendance);
        }
        BaseActivity.attendanceDao.insertAttendance(attendances);
        Modal_Session s = new Modal_Session();
        s.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        s.setFromDate(PD_Utility.getCurrentDateTime());
        s.setToDate("NA");
        BaseActivity.sessionDao.insert(s);
    }

    public void presentActivity(View view) {
        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, false);
        Intent mActivityIntent = new Intent(getActivity(), ActivityMain.class);
        startActivity(mActivityIntent);
        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        getActivity().finishAfterTransition();
    }

    @OnClick(R.id.add_child)
    public void setAdd_child(View view) {
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        PD_Utility.showFragment(getActivity(), new Fragment_SelectAvatar(), R.id.frame_attendance,
                bundle, Fragment_SelectAvatar.class.getSimpleName());
    }

    @Override
    public void onRevealed() {

    }

    @Override
    public void onUnRevealed() {

    }
}
