package com.pratham.prathamdigital.ui.avatar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Student;
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

public class Activity_SelectAvatar extends BaseActivity implements AvatarContract.avatarView {

    private static final String TAG = Activity_SelectAvatar.class.getSimpleName();
    @BindView(R.id.et_child_name)
    EditText et_child_name;
    //    @BindView(R.id.tv_age_3_7)
//    TextView tv_age_3_7;
//    @BindView(R.id.tv_age_8_14)
//    TextView tv_age_8_14;
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
    AvatarPresenterImpl avatarPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_avatar);
        ButterKnife.bind(this);
        context = this;
        avatarPresenter = new AvatarPresenterImpl(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeAvatars();
        initializeAdapter();
    }

    private void initializeAvatars() {
        String[] avatars = getResources().getStringArray(R.array.avatars);
        avatarList.addAll(Arrays.asList(avatars));
    }

    private void initializeAdapter() {
        avatar_rv.setOrientation(DSVOrientation.HORIZONTAL);
        avatar_rv.addOnItemChangedListener(onItemChangedListener);
        avatar_rv.setAdapter(new AvatarAdapter(this, avatarList));
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

    @OnClick(R.id.btn_avatar_next)
    public void setNext() {
        PrathamApplication.bubble_mp.start();
        FastSave.getInstance().saveString(PD_Constant.AVATAR, avatar_selected);
        Modal_Student modal_student = new Modal_Student();
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
        modal_student.setSentFlag(1);
        avatarPresenter.addStudent(modal_student, studentDao);
    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");
        Point points = PD_Utility.getCenterPointOfView(view);
        int revealX = (int) points.x;
        int revealY = (int) points.y;
        Intent intent = new Intent(this, ActivityMain.class);
        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(ActivityMain.EXTRA_CIRCULAR_REVEAL_Y, revealY);
        ActivityCompat.startActivity(Activity_SelectAvatar.this, intent, options.toBundle());
    }

    @Override
    public void openDashboard() {
        presentActivity(btn_avatar_next);
    }
}
