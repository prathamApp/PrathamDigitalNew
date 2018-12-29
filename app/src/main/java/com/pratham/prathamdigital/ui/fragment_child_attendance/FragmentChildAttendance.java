package com.pratham.prathamdigital.ui.fragment_child_attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
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
import com.pratham.prathamdigital.services.AppKillService;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar_;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(R.layout.fragment_child_attendance)
public class FragmentChildAttendance extends Fragment implements ContractChildAttendance.attendanceView,
        CircularRevelLayout.CallBacks {

    private static final String TAG = FragmentChildAttendance.class.getSimpleName();
    @ViewById(R.id.chid_attendance_reveal)
    CircularRevelLayout chid_attendance_reveal;
    @ViewById(R.id.rv_child)
    RecyclerView rv_child;
    @ViewById(R.id.btn_attendance_next)
    Button btn_attendance_next;
    @ViewById(R.id.add_child)
    RelativeLayout add_child;

    ChildAdapter childAdapter;
    ArrayList<Modal_Student> students;
    ArrayList<String> avatars;
    private int revealX;
    private int revealY;
    private String groupID = "";

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_child_attendance, container, false);
//
//        return null;
//    }

    //    @Override
    @AfterViews
    public void initialize() {
        chid_attendance_reveal.setListener(this);
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
        students = new ArrayList<>();
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

    @UiThread
    public void setChilds(ArrayList<Modal_Student> childs) {
        childAdapter = new ChildAdapter(getActivity(), childs, avatars, FragmentChildAttendance.this);
        rv_child.setHasFixedSize(true);
        rv_child.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_child.setAdapter(childAdapter);
    }

    @Override
    public void childItemClicked(Modal_Student stud, int position) {
        PrathamApplication.bubble_mp.start();
        for (Modal_Student stu : students) {
            if (stu.getStudentId().equalsIgnoreCase(stud.getStudentId())) {
                if (stu.isChecked()) stu.setChecked(false);
                else stu.setChecked(true);
                stud = stu;
                break;
            }
        }
        childAdapter.notifyItemChanged(position, stud);
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

    @Touch(R.id.btn_attendance_next)
    public boolean setNextAvatar(View view, MotionEvent event) {
        revealX = (int) event.getRawX();
        revealY = (int) event.getY();
        return getActivity().onTouchEvent(event);
    }

    @Touch(R.id.rv_child)
    public boolean getRecyclerTouch(View view, MotionEvent event) {
        revealX = (int) event.getRawX();
        revealY = (int) event.getY();
        return getActivity().onTouchEvent(event);
    }

    @Click(R.id.btn_attendance_next)
    public void setNext(View v) {
        ArrayList<Modal_Student> checkedStds = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).isChecked())
                checkedStds.add(students.get(i));
        }
        if (checkedStds.size() > 0) {
            PrathamApplication.bubble_mp.start();
            FastSave.getInstance().saveString(PD_Constant.AVATAR, "avatars/dino_dance.json");
            markAttendance(checkedStds);
            presentActivity(v);
        } else {
            Toast.makeText(getContext(), "Please Select Students !", Toast.LENGTH_SHORT).show();
        }
    }

    @Background
    public void markAttendance(ArrayList<Modal_Student> stud) {
        FastSave.getInstance().saveString(PD_Constant.SESSIONID, PD_Utility.getUUID().toString());
        ArrayList<Attendance> attendances = new ArrayList<>();
        for (Modal_Student stu : stud) {
            Attendance attendance = new Attendance();
            attendance.SessionID = FastSave.getInstance().getString(PD_Constant.SESSIONID, "");
            attendance.StudentID = stu.getStudentId();
            attendance.Date = PD_Utility.getCurrentDateTime();
            attendance.GroupID = groupID;
            attendance.sentFlag = 0;
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

    @UiThread
    public void presentActivity(View view) {
        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, false);
        getActivity().startService(new Intent(getActivity(), AppKillService.class));
        Intent mActivityIntent = new Intent(getActivity(), ActivityMain_.class);
        startActivity(mActivityIntent);
        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        getActivity().finishAfterTransition();
    }

    @Click(R.id.add_child)
    public void setAdd_child(View view) {
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        PD_Utility.showFragment(getActivity(), new Fragment_SelectAvatar_(), R.id.frame_attendance,
                bundle, Fragment_SelectAvatar_.class.getSimpleName());
    }

    @Override
    public void onRevealed() {
//        initialize();
    }

    @Override
    public void onUnRevealed() {
//        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
//        if (fragment != null)
//            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
}
