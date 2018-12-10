package com.pratham.prathamdigital.ui.avatar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.services.AppKillService;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

import static com.pratham.prathamdigital.BaseActivity.studentDao;

public class Fragment_SelectAvatar extends Fragment implements AvatarContract.avatarView, CircularRevelLayout.CallBacks {

    private static final String TAG = Fragment_SelectAvatar.class.getSimpleName();
    @BindView(R.id.avatar_circular_reveal)
    CircularRevelLayout avatar_circular_reveal;
    @BindView(R.id.et_child_name)
    EditText et_child_name;
    @BindView(R.id.avatar_rv)
    DiscreteScrollView avatar_rv;
    @BindView(R.id.btn_avatar_next)
    Button btn_avatar_next;
    @BindView(R.id.spinner_class)
    Spinner spinner_class;
    @BindView(R.id.spinner_age)
    Spinner spinner_age;

    ArrayList<String> avatarList = new ArrayList<>();
    private Context context;
    private String avatar_selected = "";
    int revealX;
    int revealY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.select_avatar, container, false);
        ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            avatar_circular_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    avatar_circular_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    avatar_circular_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeAvatars();
        initializeAdapter();
    }

    private void initializeAvatars() {
        String[] avatars = getResources().getStringArray(R.array.avatars);
        avatarList.addAll(Arrays.asList(avatars));
    }

    private void initializeAdapter() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.student_class, R.layout.simple_spinner_item);
        spinner_class.setAdapter(adapter);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.age, R.layout.simple_spinner_item);
        spinner_age.setAdapter(adapter2);
        avatar_rv.setOrientation(DSVOrientation.VERTICAL);
        avatar_rv.addOnItemChangedListener(onItemChangedListener);
        avatar_rv.setAdapter(new AvatarAdapter(context, avatarList));
        avatar_rv.setItemTransitionTimeMillis(150);
//        avatar_rv.setSlideOnFling(true);
//        avatar_rv.setSlideOnFlingThreshold(1800);
        avatar_rv.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.9f)
                .setMaxScale(1.05f)
                .build());
//        avatar_rv.smoothScrollToPosition(0);
    }

    DiscreteScrollView.OnItemChangedListener onItemChangedListener = new DiscreteScrollView.OnItemChangedListener() {
        @Override
        public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
            ((LottieAnimationView) viewHolder.itemView).playAnimation();
            ((LottieAnimationView) viewHolder.itemView).loop(true);
            avatar_selected = avatarList.get(avatar_rv.getCurrentItem());
        }
    };

    @OnTouch(R.id.btn_avatar_next)
    public boolean setNextAvatar(View view, MotionEvent event) {
        revealX = (int) event.getRawX();
        revealY = (int) event.getY();
        return getActivity().onTouchEvent(event);
    }

    @OnClick(R.id.btn_avatar_next)
    public void setNext() {
        PrathamApplication.bubble_mp.start();
        if (!et_child_name.getText().toString().isEmpty()) {
            FastSave.getInstance().saveString(PD_Constant.AVATAR, avatar_selected);
            Modal_Student modal_student = new Modal_Student();
            modal_student.setStudentId(PD_Utility.getUUID().toString());
            modal_student.setFullName(et_child_name.getText().toString());
            modal_student.setGroupId("SmartPhone");
            modal_student.setGroupName("SmartPhone");
            modal_student.setGroupName("SmartPhone");
            modal_student.setFirstName(et_child_name.getText().toString());
            modal_student.setMiddleName(et_child_name.getText().toString());
            modal_student.setLastName(et_child_name.getText().toString());
            modal_student.setStud_Class(spinner_class.getSelectedItem().toString());
            modal_student.setAge(spinner_age.getSelectedItem().toString());
            modal_student.setGender("M");
            modal_student.setSentFlag(0);
            modal_student.setAvatarName(avatar_selected);
            studentDao.insertStudent(modal_student);
            markAttendance(modal_student);
            presentActivity();
        } else
            Toast.makeText(getActivity(), "Please enter the name", Toast.LENGTH_SHORT).show();
    }

    public void presentActivity() {
        getActivity().startService(new Intent(getActivity(), AppKillService.class));
        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, false);
        Intent mActivityIntent = new Intent(getActivity(), ActivityMain.class);
        startActivity(mActivityIntent);
        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        getActivity().finishAfterTransition();
    }

    public void markAttendance(Modal_Student stud) {
        FastSave.getInstance().saveString(PD_Constant.SESSIONID, PD_Utility.getUUID().toString());
        ArrayList<Attendance> attendances = new ArrayList<>();
        Attendance attendance = new Attendance();
        attendance.SessionID = FastSave.getInstance().getString(PD_Constant.SESSIONID, "");
        attendance.StudentID = stud.getStudentId();
        attendance.Date = PD_Utility.getCurrentDateTime();
        attendance.GroupID = "SmartPhone";
        attendance.sentFlag = 0;
        FastSave.getInstance().saveString(PD_Constant.GROUPID, "SmartPhone");
        attendances.add(attendance);
        BaseActivity.attendanceDao.insertAttendance(attendances);
        Modal_Session s = new Modal_Session();
        s.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        s.setFromDate(PD_Utility.getCurrentDateTime());
        s.setToDate("NA");
        BaseActivity.sessionDao.insert(s);
    }


    @Override
    public void openDashboard() {
    }

    @Override
    public void onRevealed() {

    }

    @Override
    public void onUnRevealed() {

    }
}
