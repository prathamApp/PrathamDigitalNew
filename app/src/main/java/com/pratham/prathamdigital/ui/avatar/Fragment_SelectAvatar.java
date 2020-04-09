package com.pratham.prathamdigital.ui.avatar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.services.AppKillService;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain_;
import com.pratham.prathamdigital.ui.fragment_language.FragmentLanguage;
import com.pratham.prathamdigital.ui.fragment_language.FragmentLanguage_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.attendanceDao;
import static com.pratham.prathamdigital.PrathamApplication.sessionDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;

@EFragment(R.layout.select_avatar)
public class Fragment_SelectAvatar extends Fragment implements AvatarContract.avatarView, CircularRevelLayout.CallBacks {

    private static final String TAG = Fragment_SelectAvatar.class.getSimpleName();
    @ViewById(R.id.avatar_circular_reveal)
    CircularRevelLayout avatar_circular_reveal;
    @ViewById(R.id.et_child_name)
    EditText et_child_name;
    @ViewById(R.id.avatar_rv)
    DiscreteScrollView avatar_rv;
    @ViewById(R.id.btn_avatar_next)
    Button btn_avatar_next;
    @ViewById(R.id.spinner_class)
    Spinner spinner_class;
    @ViewById(R.id.spinner_age)
    Spinner spinner_age;
    @ViewById(R.id.img_add_child_back)
    ImageView img_add_child_back;
    @ViewById(R.id.txt_sel_language)
    TextView txt_sel_language;

    private final ArrayList<String> avatarList = new ArrayList<>();
    private Context context;
    private String avatar_selected = "";

    private final DiscreteScrollView.OnItemChangedListener onItemChangedListener = new DiscreteScrollView.OnItemChangedListener() {
        @Override
        public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
            if (viewHolder != null) {
                ((LottieAnimationView) (Objects.requireNonNull(viewHolder.itemView))).playAnimation();
                ((LottieAnimationView) (Objects.requireNonNull(viewHolder.itemView))).loop(true);
                avatar_selected = avatarList.get(avatar_rv.getCurrentItem());
            }
        }
    };
    private int revealX;
    private int revealY;
    private String noti_key;
    private String noti_value;

    @Background
    public void initializeAvatars() {
        String[] avatars = getResources().getStringArray(R.array.avatars);
        avatarList.addAll(Arrays.asList(avatars));
        initializeAdapter();
    }

    @AfterViews
    public void initialize() {
        avatar_circular_reveal.setListener(this);
        if (getArguments() != null && getArguments().getBoolean(PD_Constant.SHOW_BACK))
            img_add_child_back.setVisibility(View.VISIBLE);
        else img_add_child_back.setVisibility(View.GONE);
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
        initializeAvatars();
    }

    @UiThread
    public void initializeAdapter() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getActivity()), R.array.student_class, R.layout.simple_spinner_item);
        spinner_class.setAdapter(adapter);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.age, R.layout.simple_spinner_item);
        spinner_age.setAdapter(adapter2);
        avatar_rv.setOrientation(DSVOrientation.VERTICAL);
        avatar_rv.addOnItemChangedListener(onItemChangedListener);
        avatar_rv.setAdapter(new AvatarAdapter(context, avatarList));
        avatar_rv.setItemTransitionTimeMillis(150);
        avatar_rv.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.9f)
                .setMaxScale(1.05f)
                .build());
    }

    @Click(R.id.btn_avatar_next)
    public void setNext() {
        PrathamApplication.bubble_mp.start();
        if (!et_child_name.getText().toString().isEmpty()) {
            insertStudentAndMarkAttendance();
        } else
            Toast.makeText(getActivity(), "Please enter the name", Toast.LENGTH_SHORT).show();
    }

    @Background
    public void insertStudentAndMarkAttendance() {
        FastSave.getInstance().saveString(PD_Constant.AVATAR, avatar_selected);
        FastSave.getInstance().saveString(PD_Constant.PROFILE_NAME, et_child_name.getText().toString());
        getSelectedAge();
        Modal_Student modal_student = new Modal_Student();
        modal_student.setStudentId(PD_Utility.getUUID().toString());
        modal_student.setFullName(et_child_name.getText().toString());
        modal_student.setGroupId("SmartPhone");
        modal_student.setGroupName("SmartPhone");
        modal_student.setFirstName(et_child_name.getText().toString());
        modal_student.setMiddleName(et_child_name.getText().toString());
        modal_student.setLastName(et_child_name.getText().toString());
        modal_student.setStud_Class(spinner_class.getSelectedItem().toString());
        modal_student.setAge(String.valueOf(FastSave.getInstance().getInt(PD_Constant.STUDENT_PROFILE_AGE, 0)));
        modal_student.setGender("M");
        modal_student.setSentFlag(0);
        modal_student.setAvatarName(avatar_selected);
        studentDao.insertStudent(modal_student);
        FastSave.getInstance().saveString(PD_Constant.GROUPID, modal_student.getStudentId());
        FastSave.getInstance().saveString(PD_Constant.SESSIONID, PD_Utility.getUUID().toString());
        markAttendance(modal_student);
        presentActivity();
    }

    private void getSelectedAge() {
        String age = spinner_age.getSelectedItem().toString();
        String[] split_age = age.split(" ");
        if (split_age.length > 1)
            FastSave.getInstance().saveInt(PD_Constant.STUDENT_PROFILE_AGE, Integer.parseInt(split_age[1]));
        else
            FastSave.getInstance().saveInt(PD_Constant.STUDENT_PROFILE_AGE, 0);
    }

    @UiThread
    public void presentActivity() {
        Objects.requireNonNull(getActivity()).startService(new Intent(getActivity(), AppKillService.class));
        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, false);
        Intent mActivityIntent = new Intent(getActivity(), ActivityMain_.class);
        if (getArguments() != null && getArguments().getBoolean(PD_Constant.DEEP_LINK, false)) {
            mActivityIntent.putExtra(PD_Constant.DEEP_LINK, true);
            mActivityIntent.putExtra(PD_Constant.DEEP_LINK_CONTENT, getArguments().getString(PD_Constant.DEEP_LINK_CONTENT));
        }
        if (doesArgumentContainsNotificationData()) {
            mActivityIntent.putExtra(PD_Constant.PUSH_NOTI_KEY, noti_key);
            mActivityIntent.putExtra(PD_Constant.PUSH_NOTI_VALUE, noti_value);
        }
        startActivity(mActivityIntent);
        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        getActivity().finishAfterTransition();
    }

    private boolean doesArgumentContainsNotificationData() {
        if (noti_key != null && noti_value != null)
            return true; //that means the values are newly assigned in @Subscribe, when a new notification arrives.
        // if not, then we are continuing with the previous one, if exists.
        if (getArguments() != null && getArguments().getString(PD_Constant.PUSH_NOTI_KEY) != null &&
                getArguments().getString(PD_Constant.PUSH_NOTI_VALUE) != null) {
            noti_key = getArguments().getString(PD_Constant.PUSH_NOTI_KEY);
            noti_value = getArguments().getString(PD_Constant.PUSH_NOTI_VALUE);
            return true;
        }
        //no notification received
        return false;
    }

    private void markAttendance(Modal_Student stud) {
        //<editor-fold desc="below code is for saving the student attendance so as to pass it to "meri dukan" game, nothing else">
        ArrayList<Modal_Student> stuList = new ArrayList<>();
        stuList.add(stud);
        String stu_json = new Gson().toJson(stuList);
        FastSave.getInstance().saveString(PD_Constant.PRESENT_STUDENTS, stu_json);
        //</editor-fold>
        ArrayList<Attendance> attendances = new ArrayList<>();
        Attendance attendance = new Attendance();
        attendance.SessionID = FastSave.getInstance().getString(PD_Constant.SESSIONID, "");
        attendance.StudentID = stud.getStudentId();
        attendance.Date = PD_Utility.getCurrentDateTime();
        attendance.GroupID = "SmartPhone";
        attendance.sentFlag = 0;
        attendances.add(attendance);
        attendanceDao.insertAttendance(attendances);
        Modal_Session s = new Modal_Session();
        s.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        s.setFromDate(PD_Utility.getCurrentDateTime());
        s.setToDate("NA");
        sessionDao.insert(s);
    }

    @Click(R.id.img_add_child_back)
    public void setAttBack() {
        try {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.txt_sel_language)
    public void chooseLanguage(View view) {
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        bundle.putBoolean(PD_Constant.IS_AVATAR, true);
        PD_Utility.addFragment(getActivity(), new FragmentLanguage_(), R.id.frame_attendance,
                bundle, FragmentLanguage.class.getSimpleName());
    }

    @Subscribe
    public void onMessageReceived(EventMessage message) {
        if (message != null)
            if (message.getMessage().equalsIgnoreCase(PD_Constant.LANGUAGE))
                txt_sel_language.setText(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            else if (message.getMessage().equalsIgnoreCase(PD_Constant.NOTIFICATION_RECIEVED)) {
                Bundle bundle = message.getBundle();
                noti_key = bundle.getString(PD_Constant.PUSH_NOTI_KEY);
                noti_value = bundle.getString(PD_Constant.PUSH_NOTI_VALUE);
            }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
