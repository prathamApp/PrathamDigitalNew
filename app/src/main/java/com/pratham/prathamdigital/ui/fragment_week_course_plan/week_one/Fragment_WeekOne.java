package com.pratham.prathamdigital.ui.fragment_week_course_plan.week_one;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.custom.view_animators.Animate;
import com.pratham.prathamdigital.custom.view_animators.Techniques;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.ui.content_player.Activity_ContentPlayer_;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.Fragment_CourseExperience;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.Fragment_CourseExperience_;
import com.pratham.prathamdigital.ui.fragment_week_course_plan.PlanningContract;
import com.pratham.prathamdigital.ui.fragment_week_course_plan.RV_CourseAdapter;
import com.pratham.prathamdigital.ui.fragment_week_course_plan.RV_SelectCourseAdapter;
import com.pratham.prathamdigital.ui.fragment_week_course_plan.WeekPlanningPresenter;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@EFragment(R.layout.fragment_week_one)
public class Fragment_WeekOne extends Fragment implements PlanningContract.weekOnePlanningView {

    private static final int SHOW_DATE_PICKER = 1;
    private static final int HIDE_DATE_PICKER = 2;
    private static final int CAMERA_REQUEST = 3;
    private static final int COURSE_ACTIVITY = 4;
    @ViewById(R.id.rv_courses_enrolled)
    RecyclerView rv_courses_enrolled;
    @ViewById(R.id.rv_selectCourse)
    RecyclerView rv_selectCourse;
    @ViewById(R.id.txt_week)
    TextView txt_week;
    @ViewById(R.id.rl_sel_crs)
    RelativeLayout rl_sel_crs;
    @ViewById(R.id.rl_add_new_course)
    RelativeLayout rl_add_new_course;
    @ViewById(R.id.rl_calendar_view)
    RelativeLayout rl_calendar_view;
    @ViewById(R.id.rl_enrolled_courses)
    RelativeLayout rl_enrolled_courses;
    @ViewById(R.id.course_date_picker)
    DateRangeCalendarView course_date_picker;
    @ViewById(R.id.ll_no_data)
    RelativeLayout ll_no_data;
    @ViewById(R.id.rl_verify_coach)
    RelativeLayout rl_verify_coach;
    @ViewById(R.id.txt_verify_status)
    TextView txt_verify_status;
    @ViewById(R.id.coach_image)
    SimpleDraweeView coach_image;

    @Bean(WeekPlanningPresenter.class)
    PlanningContract.weekOnePlanningPresenter planningPresenter;
    RV_CourseAdapter adapter;
    RV_SelectCourseAdapter selectCourseAdapter;
    private Calendar startDate;
    private Calendar endDate;
    private Modal_ContentDetail selectedCourse;
    private Uri capturedImageUri;
    private BlurPopupWindow exitDialog;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DATE_PICKER:
                    Animate.with(Techniques.BounceInUp)
                            .duration(700)
                            .onStart(animator -> rl_calendar_view.setVisibility(View.VISIBLE))
                            .playOn(rl_calendar_view);
                    initializeCalendar();
                    break;
                case HIDE_DATE_PICKER:
                    Animate.with(Techniques.SlideOutDown)
                            .duration(700)
                            .onEnd(animator -> rl_calendar_view.setVisibility(View.GONE))
                            .playOn(rl_calendar_view);
                    break;
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void init() {
        planningPresenter.setWeekOneView(this);
        txt_week.setText("Week 1");
        planningPresenter.fetchEnrolledCourses("WEEK_1");
    }

    @Override
    public void onResume() {
        super.onResume();
        planningPresenter.setWeekOneView(this);
    }

    @UiThread
    @Override
    public void loadEnrolledCourses(List<Model_CourseEnrollment> coursesEnrolled) {
        if (coursesEnrolled != null && !coursesEnrolled.isEmpty()) {
            rl_add_new_course.setVisibility(View.GONE);
            rl_enrolled_courses.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new RV_CourseAdapter(getActivity(), Fragment_WeekOne.this);
                FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
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

    @UiThread
    @Override
    public void loadCourses(HashMap<String, List<Modal_ContentDetail>> courses) {
        rl_sel_crs.setVisibility(View.VISIBLE);
        if (courses != null && !courses.isEmpty()) {
            ll_no_data.setVisibility(View.GONE);
            if (selectCourseAdapter == null) {
                selectCourseAdapter = new RV_SelectCourseAdapter(getActivity(), courses, Fragment_WeekOne.this);
                FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
                flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
                rv_selectCourse.setLayoutManager(flexboxLayoutManager);
                rv_selectCourse.setAdapter(selectCourseAdapter);
            }
            selectCourseAdapter.notifyDataSetChanged();
        } else {
            ll_no_data.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void selectCourse(View view, int adapterPosition) {
        rl_sel_crs.setVisibility(View.INVISIBLE);
        planningPresenter.loadCourses();
        reveal(rl_sel_crs, view);
    }

    @UiThread
    public void reveal(View view, View startView) {
        // previously invisible view
        try {
            int[] outLocation = new int[2];
            startView.getLocationOnScreen(outLocation);
            outLocation[0] += startView.getWidth() / 2;
            int centerX = view.getWidth();
            int centerY = view.getHeight();
            int startRadius = 0;
            int endRadius = (int) Math.hypot(centerX, centerY);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, outLocation[0], outLocation[1], startRadius, endRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(300);
            view.setVisibility(View.VISIBLE);
            anim.start();
            new Handler().postDelayed(() -> rl_add_new_course.setVisibility(View.GONE), 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Click(R.id.btn_addNewCourse)
    public void addNewCourse(View view) {
        showViewAndCourse(view);
    }

    private void showViewAndCourse(View view) {
        rl_sel_crs.setVisibility(View.INVISIBLE);
        planningPresenter.loadCourses();
        reveal(rl_sel_crs, view);
    }

    @Click(R.id.iv_close_calendar)
    public void setCloseCalendar() {
        mHandler.sendEmptyMessage(HIDE_DATE_PICKER);
    }

    @Override
    public void showDatePicker(Modal_ContentDetail selectedCourse, int adapterPosition) {
        this.selectedCourse = selectedCourse;
        mHandler.sendEmptyMessage(SHOW_DATE_PICKER);
    }

    @UiThread
    @Override
    public void showEnrolledList(List<Model_CourseEnrollment> courseEnrollments) {
        rl_enrolled_courses.setVisibility(View.VISIBLE);
        rl_sel_crs.setVisibility(View.GONE);
        rl_calendar_view.setVisibility(View.GONE);
        rl_add_new_course.setVisibility(View.GONE);
        loadEnrolledCourses(courseEnrollments);
    }

    @UiThread
    @Override
    public void courseAlreadySelected() {
        Toast.makeText(getActivity(), "Course already enrolled!", Toast.LENGTH_SHORT).show();
    }

    @Click(R.id.btn_course_time_select)
    public void onCourseTimeSelected() {
        if (endDate == null || startDate == null) {
            Toast.makeText(getActivity(), "Please select the correct timeline.", Toast.LENGTH_SHORT).show();
            return;
        }
        mHandler.sendEmptyMessage(HIDE_DATE_PICKER);
        planningPresenter.addCourseToDb("WEEK_1", selectedCourse, startDate, endDate);
    }

    private void initializeCalendar() {
        Calendar startSelectionDate = Calendar.getInstance();
//        startSelectionDate.add(Calendar.MONTH, -1);
        Calendar endSelectionDate = (Calendar) startSelectionDate.clone();
        endSelectionDate.add(Calendar.DATE, 4);
        course_date_picker.setSelectedDateRange(startSelectionDate, endSelectionDate);
        course_date_picker.setCalendarListener(new DateRangeCalendarView.CalendarListener() {
            @Override
            public void onFirstDateSelected(Calendar startDate) {

            }

            @Override
            public void onDateRangeSelected(Calendar s_Date, Calendar e_Date) {
                startDate = s_Date;
                endDate = e_Date;
            }
        });
    }

    @Override
    public void addAnotherCourse(View view) {
        showViewAndCourse(view);
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
            planningPresenter.markCoursesVerified("WEEK_1",
                    PD_Utility.getRealPathFromURI(capturedImageUri, Objects.requireNonNull(getActivity())));
            verifiedSuccessfully(null);
        } else if (requestCode == COURSE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            planningPresenter.checkProgress("WEEK_1", data.getStringExtra(PD_Constant.COURSE_ID));
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
        rl_sel_crs.setVisibility(View.GONE);
    }

    @Override
    public void courseCompleted(int pos, Model_CourseEnrollment c_enrolled) {
        exitDialog = new BlurPopupWindow.Builder(getActivity())
                .setContentView(R.layout.dialog_course_completed)
                .bindClickListener(v -> {
                    exitDialog.dismiss();
                    new Handler().postDelayed(() -> {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(PD_Constant.ENROLLED_COURSE, c_enrolled);
                        bundle.putString(PD_Constant.WEEK, "WEEK_1");
                        PD_Utility.addFragment(getActivity(), new Fragment_CourseExperience_(), R.id.main_frame,
                                bundle, Fragment_CourseExperience.class.getSimpleName());
                    }, 300);
                }, R.id.dialog_btn_lets_go)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        exitDialog.show();
    }

    @Override
    public void playCourse(int pos, Model_CourseEnrollment c_enrolled) {
        planningPresenter.fetchCourseChilds(c_enrolled);
    }

    @UiThread
    @Override
    public void showChilds(List<Modal_ContentDetail> childs, String courseId) {
        KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                .permissions(Manifest.permission.RECORD_AUDIO)
                .onAccepted(permissionResult -> {
                    Intent intent = new Intent(getActivity(), Activity_ContentPlayer_.class);
                    intent.putExtra(PD_Constant.CONTENT_TYPE, PD_Constant.COURSE);
                    intent.putExtra(PD_Constant.COURSE_ID, courseId);
                    intent.putExtra(PD_Constant.WEEK, "WEEK_1");
                    intent.putParcelableArrayListExtra(PD_Constant.CONTENT, (ArrayList<? extends Parcelable>) childs);
                    startActivityForResult(intent, COURSE_ACTIVITY);
                    getActivity().overridePendingTransition(R.anim.shrink_enter, R.anim.nothing);
                })
                .ask();
    }
}