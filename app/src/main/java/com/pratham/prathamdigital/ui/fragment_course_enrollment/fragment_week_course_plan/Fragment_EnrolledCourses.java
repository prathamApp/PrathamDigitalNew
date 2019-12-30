package com.pratham.prathamdigital.ui.fragment_course_enrollment.fragment_week_course_plan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.ui.content_player.Activity_ContentPlayer_;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.Fragment_CourseExperience;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.Fragment_CourseExperience_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EFragment(R.layout.fragment_enrolled_courses)
public class Fragment_EnrolledCourses extends Fragment implements PlanningContract.enrolledView {

    private static final int CAMERA_REQUEST = 3;
    private static final int COURSE_ACTIVITY = 4;
    @ViewById(R.id.rv_courses_enrolled)
    RecyclerView rv_courses_enrolled;
    @ViewById(R.id.rl_add_new_course)
    RelativeLayout rl_add_new_course;
    @ViewById(R.id.rl_enrolled_courses)
    RelativeLayout rl_enrolled_courses;
    @ViewById(R.id.rl_verify_coach)
    RelativeLayout rl_verify_coach;
    @ViewById(R.id.txt_verify_status)
    TextView txt_verify_status;
    @ViewById(R.id.coach_image)
    SimpleDraweeView coach_image;

    @Bean(WeekPlanningPresenter.class)
    PlanningContract.weekOnePlanningPresenter planningPresenter;
    RV_CourseAdapter adapter;
    private Uri capturedImageUri;


    @SuppressLint("SetTextI18n")
    @AfterViews
    public void init() {
        planningPresenter.setEnrolledView(this);
        new Handler().postDelayed(() -> planningPresenter.fetchEnrolledCourses("WEEK_1"), 400);
    }

    @UiThread
    @Override
    public void loadEnrolledCourses(List<Model_CourseEnrollment> coursesEnrolled) {
        if (coursesEnrolled != null && !coursesEnrolled.isEmpty()) {
            rl_add_new_course.setVisibility(View.GONE);
            rl_enrolled_courses.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new RV_CourseAdapter(getActivity(), Fragment_EnrolledCourses.this);
                FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.COLUMN);
                flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
                rv_courses_enrolled.setLayoutManager(flexboxLayoutManager);
                rv_courses_enrolled.setAdapter(adapter);
            }
            adapter.updateData(coursesEnrolled);
        } else {
            rl_add_new_course.setVisibility(View.VISIBLE);
            rl_enrolled_courses.setVisibility(View.GONE);
        }
    }

    @Click(R.id.btn_addNewCourse)
    public void addNewCourse(View view) {
        showNewCourses();
    }

    private void showNewCourses() {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.SHOW_NEW_COURSES);
        EventBus.getDefault().post(message);
    }

    @UiThread
    @Override
    public void showEnrolledList(List<Model_CourseEnrollment> courseEnrollments) {
        rl_enrolled_courses.setVisibility(View.VISIBLE);
        rl_add_new_course.setVisibility(View.GONE);
        loadEnrolledCourses(courseEnrollments);
    }

    @Override
    public void addAnotherCourse(View view) {
        showNewCourses();
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    @Override
    public void showVerificationButton() {
        txt_verify_status.setText("Verify By Coach");
        txt_verify_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_exclamation_mark, 0, 0, 0);
        rl_verify_coach.setClickable(true);
        coach_image.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    @UiThread
    @Override
    public void verifiedSuccessfully(Model_CourseEnrollment model_courseEnrollment) {
        if (model_courseEnrollment != null) {
            capturedImageUri = Uri.fromFile(new File(model_courseEnrollment.getCoachImage()));
            coach_image.setImageURI(capturedImageUri);
            coach_image.setVisibility(View.VISIBLE);
        }
        rl_verify_coach.setBackground(getResources().getDrawable(R.drawable.button_green_selector));
        txt_verify_status.setText("Verified By Coach");
        txt_verify_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked_white, 0, 0, 0);
        rl_verify_coach.setClickable(false);
    }

    @Click(R.id.rl_verify_coach)
    public void openCamera() {
        KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                .permissions(Manifest.permission.CAMERA)
                .onAccepted(permissionResult -> {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File imagesFolder = new File(PrathamApplication.pradigiPath, PD_Constant.HELPER_FOLDER);
                    if (!imagesFolder.exists()) imagesFolder.mkdirs();
                    File image = new File(imagesFolder, FastSave.getInstance().getString(PD_Constant.SESSIONID, "na") + "_WEEK_1.jpg");
                    capturedImageUri = Uri.fromFile(image);
                    cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, capturedImageUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                })
                .ask();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            coach_image.setVisibility(View.VISIBLE);
            coach_image.setImageURI(capturedImageUri);
            planningPresenter.markCoursesVerified(adapter.getData(),
                    PD_Utility.getRealPathFromURI(capturedImageUri, Objects.requireNonNull(getActivity())));
            verifiedSuccessfully(null);
        } else if (requestCode == COURSE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            planningPresenter.fetchEnrolledCourses("WEEK_1");
//            planningPresenter.checkProgress("WEEK_1", data.getStringExtra(PD_Constant.COURSE_ID));
        }
    }

    @Override
    public void deleteCourse(int pos, Model_CourseEnrollment c_enrolled) {
        planningPresenter.deleteCourse(c_enrolled, "WEEK_1");
        adapter.removeItem(pos);
    }

    @Override
    public void noCoursesEnrolled() {
        rl_add_new_course.setVisibility(View.VISIBLE);
        rl_enrolled_courses.setVisibility(View.GONE);
//        rl_sel_crs.setVisibility(View.GONE);
    }

    @Override
    public void playCourse(int pos, Model_CourseEnrollment c_enrolled) {
        planningPresenter.fetchCourseChilds(c_enrolled);
    }

    @UiThread
    @Override
    public void showChilds(Model_CourseEnrollment parent_course, List<Modal_ContentDetail> childs, String courseId) {
        KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                .permissions(Manifest.permission.RECORD_AUDIO)
                .onAccepted(permissionResult -> {
                    Intent intent = new Intent(getActivity(), Activity_ContentPlayer_.class);
                    intent.putExtra(PD_Constant.CONTENT_TYPE, PD_Constant.COURSE);
                    intent.putExtra(PD_Constant.COURSE_ID, courseId);
                    intent.putExtra(PD_Constant.WEEK, "WEEK_1");
                    intent.putExtra(PD_Constant.COURSE_PARENT, parent_course);
                    intent.putParcelableArrayListExtra(PD_Constant.CONTENT, (ArrayList<? extends Parcelable>) childs);
                    startActivityForResult(intent, COURSE_ACTIVITY);
                    getActivity().overridePendingTransition(R.anim.shrink_enter, R.anim.nothing);
                })
                .ask();
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

    @UiThread
    @Subscribe
    public void msgReceived(EventMessage eventMessage) {
        if (eventMessage != null)
            if (eventMessage.getMessage().equalsIgnoreCase(PD_Constant.NEW_COURSE_ENROLLED)) {
                planningPresenter.fetchEnrolledCourses("WEEK_1");
            }
    }

    @Override
    public void provideFeedback(int pos, Model_CourseEnrollment c_enrolled) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PD_Constant.ENROLLED_COURSE, c_enrolled);
        bundle.putString(PD_Constant.WEEK, "WEEK_1");
        PD_Utility.addFragment(getActivity(), new Fragment_CourseExperience_(), R.id.main_frame,
                bundle, Fragment_CourseExperience.class.getSimpleName());
    }
}