package com.pratham.prathamdigital.ui.fragment_course_enrollment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.custom.verticalstepperform.StepperFormListener;
import com.pratham.prathamdigital.custom.verticalstepperform.VerticalStepperFormView;
import com.pratham.prathamdigital.interfaces.SpeechResult;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.models.Model_CourseExperience;
import com.pratham.prathamdigital.services.STTService;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.course_experience_steps.Step_AssignmentsDescription;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.course_experience_steps.Step_CoachComments;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.course_experience_steps.Step_NewWords;
import com.pratham.prathamdigital.ui.fragment_course_enrollment.course_experience_steps.Step_TotalAssignmnetsDone;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Objects;

@EFragment(R.layout.fragment_course_completed)
public class Fragment_CourseExperience extends Fragment implements StepperFormListener, SpeechResult {

    private static final int CAMERA_REQUEST = 5;
    private static final int PREFILL_EXPERIENCE_IF_ANY = 6;

    @ViewById(R.id.stepper_form)
    VerticalStepperFormView stepper_form;
    @ViewById(R.id.exp_coach_image)
    SimpleDraweeView exp_coach_image;

    private Model_CourseEnrollment model_courseEnrollment;
    private BlurPopupWindow exitDialog;
    private Step_NewWords step_newWords;
    private Step_TotalAssignmnetsDone step_totalAssignmnetsDone;
    private Step_AssignmentsDescription step_assignmentsDescription;
    private Step_CoachComments step_coachComments;
    public static STTService sttService;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PREFILL_EXPERIENCE_IF_ANY) {
                Model_CourseExperience courseExperience = new Gson().fromJson(
                        model_courseEnrollment.getCourseExperience(), Model_CourseExperience.class);
                step_assignmentsDescription.restoreStepData(courseExperience.getAssignments_description());
                step_coachComments.restoreStepData(courseExperience.getCoach_comments());
                step_newWords.restoreStepData(courseExperience.getWords_learnt());
                step_totalAssignmnetsDone.restoreStepData(courseExperience.getAssignments_completed());
            }
        }
    };

    @AfterViews
    public void init() {
        setUpForm();
        model_courseEnrollment = Objects.requireNonNull(getArguments()).getParcelable(PD_Constant.ENROLLED_COURSE);
        KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                .permissions(Manifest.permission.RECORD_AUDIO)
                .onAccepted(permissionResult -> {
                    sttService = STTService.init(getContext());
                    sttService.initCallback(this);
                    mHandler.sendEmptyMessage(PREFILL_EXPERIENCE_IF_ANY);
                })
                .ask();
    }

    private void setUpForm() {
        String[] stepTitles = getResources().getStringArray(R.array.steps_experience);
        step_newWords = new Step_NewWords(stepTitles[0], "", "Next");
        step_totalAssignmnetsDone = new Step_TotalAssignmnetsDone(stepTitles[1], "", "Next");
        step_assignmentsDescription = new Step_AssignmentsDescription(stepTitles[2], "", "Next");
        step_coachComments = new Step_CoachComments(stepTitles[3], "", "Done");
        stepper_form
                .setup(this, step_newWords, step_totalAssignmnetsDone,
                        step_assignmentsDescription, step_coachComments)
                .confirmationStepSubtitle("Please check the answer before confirm")
                .init();
    }

    @Click(R.id.btn_exp_done)
    public void submitExp() {
        KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                .permissions(Manifest.permission.CAMERA)
                .onAccepted(permissionResult -> {
                    verifyDetails();
                })
                .ask();
    }

    private void verifyDetails() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            exp_coach_image.setVisibility(View.VISIBLE);
            copyPhotoAndUpdateList(getActivity(), data);
        }
    }

    @Background
    public void copyPhotoAndUpdateList(FragmentActivity activity, Intent data) {
        Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        File imagesFolder = new File(PrathamApplication.pradigiPath, PD_Constant.HELPER_FOLDER);
        if (!imagesFolder.exists()) imagesFolder.mkdirs();
        File image = new File(imagesFolder, FastSave.getInstance().getString(PD_Constant.SESSIONID, "na") +
                (getArguments() != null ? getArguments().getString(PD_Constant.WEEK) : "na") + "_exp.jpg");
        Uri capturedImageUri = PD_Utility.getImageUri(Objects.requireNonNull(getActivity()), Objects.requireNonNull(photo));
        String filePath = PD_Utility.getRealPathFromURI(capturedImageUri, getActivity());
        //copy file to another directory
        try {
            FileChannel src = new FileInputStream(filePath).getChannel();
            FileChannel dst = new FileOutputStream(image).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        capturedImageUri = Uri.fromFile(image);
        updateCoachPhoto(capturedImageUri);
    }

    @UiThread
    public void updateCoachPhoto(Uri capturedImageUri) {
        exp_coach_image.setImageURI(capturedImageUri);
        completeAndUpdateCourseInDb(PD_Utility.getRealPathFromURI(capturedImageUri, Objects.requireNonNull(getActivity())));
        new Handler().postDelayed(this::showKeepItUpDialog, 500);
    }

    @SuppressLint("SetTextI18n")
    private void showKeepItUpDialog() {
        exitDialog = new BlurPopupWindow.Builder(getActivity())
                .setContentView(R.layout.dialog_course_completed)
                .bindClickListener(v -> {
                    exitDialog.dismiss();
                    new Handler().postDelayed(() -> {
                        EventMessage message = new EventMessage();
                        message.setMessage(PD_Constant.SHOW_HOME);
                        EventBus.getDefault().post(message);
                    }, 300);
                }, R.id.dialog_btn_lets_go)
                .setGravity(Gravity.CENTER)
                .setDismissOnTouchBackground(false)
                .setDismissOnClickBack(false)
                .setScaleRatio(0.2f)
                .setBlurRadius(10)
                .setTintColor(0x30000000)
                .build();
        LottieAnimationView view = exitDialog.findViewById(R.id.lottie_completed);
        TextView msg = exitDialog.findViewById(R.id.txt_completed_msg);
        Button dialog_btn_lets_go = exitDialog.findViewById(R.id.dialog_btn_lets_go);
        view.setAnimation("clap.json");
        msg.setText("Good Work! \n Let's enroll for a new course");
        dialog_btn_lets_go.setText("Goto Home");
        exitDialog.show();
    }

    @Background
    public void completeAndUpdateCourseInDb(String coachImage) {
        Gson gson = new Gson();
        Model_CourseExperience courseExperience = gson.fromJson(
                model_courseEnrollment.getCourseExperience(), Model_CourseExperience.class);
        courseExperience.setWords_learnt(step_newWords.getStepData());
        courseExperience.setAssignments_completed(step_totalAssignmnetsDone.getStepData());
        courseExperience.setAssignments_description(step_assignmentsDescription.getStepData());
        courseExperience.setCoach_comments(step_coachComments.getStepData());
        courseExperience.setCoach_verification_date(PD_Utility.getCurrentDateTime());
        courseExperience.setCoach_image(coachImage);
        if (!coachImage.equalsIgnoreCase("not verified"))
            courseExperience.setStatus(PD_Constant.FEEDBACK_GIVEN);

        model_courseEnrollment.setCourseExperience(gson.toJson(courseExperience));
        model_courseEnrollment.setCourseCompleted(true);
        model_courseEnrollment.setSentFlag(0);
        PrathamApplication.courseDao.updateCourse(model_courseEnrollment);
    }

    @Override
    public void onCompletedForm() {
        if (step_newWords.getStepData().isEmpty()) {
            stepper_form.cancelFormCompletionOrCancellationAttempt();
            stepper_form.goToStep(0, true);
            return;
        }
        if (step_totalAssignmnetsDone.getStepData().isEmpty()) {
            stepper_form.cancelFormCompletionOrCancellationAttempt();
            stepper_form.goToStep(1, true);
            return;
        }
        if (step_assignmentsDescription.getStepData().isEmpty()) {
            stepper_form.cancelFormCompletionOrCancellationAttempt();
            stepper_form.goToStep(2, true);
            return;
        }
        if (step_coachComments.getStepData().isEmpty()) {
            stepper_form.cancelFormCompletionOrCancellationAttempt();
            stepper_form.goToStep(3, true);
            return;
        }
        completeAndUpdateCourseInDb("not verified");
    }

    @Override
    public void onCancelledForm() {

    }

    @Override
    public void onResult(String result) {
        setDataToRespectiveStep(result);
    }

    @Override
    public void sttStopped() {
        setDataToRespectiveStep(null);
    }

    private void setDataToRespectiveStep(String result) {
        switch (stepper_form.getOpenStepPosition()) {
            case 0:
                step_newWords.restoreStepData(result);
                break;
            case 1:
                step_totalAssignmnetsDone.restoreStepData(result);
                break;
            case 2:
                step_assignmentsDescription.restoreStepData(result);
                break;
            case 3:
                step_coachComments.restoreStepData(result);
                break;
        }
    }

}
