package com.pratham.prathamdigital.ui.fragment_course_enrollment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.SpeechResult;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.services.STTService;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

@EFragment(R.layout.fragment_course_completed)
public class Fragment_CourseExperience extends Fragment implements SpeechResult {

    private static final int MIC_1 = 1;
    private static final int MIC_2 = 2;
    private static final int MIC_3 = 3;
    private static final int MIC_4 = 4;
    private static final int CAMERA_REQUEST = 5;
    private static final int PREFILL_EXPERIENCE_IF_ANY = 6;
    private static int SELECTED_MIC = -1;

    @ViewById(R.id.et_new_words_learnt)
    EditText et_new_words_learnt;
    @ViewById(R.id.et_no_of_assignments_completed)
    EditText et_no_of_assignments_completed;
    @ViewById(R.id.et_assignment_desc)
    EditText et_assignment_desc;
    @ViewById(R.id.et_coach_comments)
    EditText et_coach_comments;
    @ViewById(R.id.mic_1)
    ImageView mic_1;
    @ViewById(R.id.mic_2)
    ImageView mic_2;
    @ViewById(R.id.mic_3)
    ImageView mic_3;
    @ViewById(R.id.mic_4)
    ImageView mic_4;
    @ViewById(R.id.exp_coach_image)
    SimpleDraweeView exp_coach_image;
    @ViewById(R.id.btn_exp_done)
    RelativeLayout btn_exp_done;

    private STTService sttService;
    private boolean isChecked=true;
    private Uri capturedImageUri;
    private Model_CourseEnrollment model_courseEnrollment;
    private BlurPopupWindow exitDialog;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isChecked = !isChecked;
            final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
            switch (msg.what) {
                case MIC_1:
                    SELECTED_MIC = 1;
                    sttService.startListening();
                    mic_1.setImageState(stateSet, true);
                    break;
                case MIC_2:
                    SELECTED_MIC = 2;
                    sttService.startListening();
                    mic_2.setImageState(stateSet, true);
                    break;
                case MIC_3:
                    SELECTED_MIC = 3;
                    sttService.startListening();
                    mic_3.setImageState(stateSet, true);
                    break;
                case MIC_4:
                    SELECTED_MIC = 4;
                    sttService.startListening();
                    mic_4.setImageState(stateSet, true);
                    break;
                case PREFILL_EXPERIENCE_IF_ANY:
                    try {
                        JSONObject jsonObject = new JSONObject(model_courseEnrollment.getCourseExperience());
                        et_assignment_desc.setText((jsonObject.has("assignments_description")) ? jsonObject.getString("assignments_description") : "");
                        et_coach_comments.setText((jsonObject.has("coach_comments")) ? jsonObject.getString("coach_comments") : "");
                        et_new_words_learnt.setText((jsonObject.has("words_learnt")) ? jsonObject.getString("words_learnt") : "");
                        et_no_of_assignments_completed.setText((jsonObject.has("assignments_completed")) ? jsonObject.getString("assignments_completed") : "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @AfterViews
    public void init() {
        model_courseEnrollment = Objects.requireNonNull(getArguments()).getParcelable(PD_Constant.ENROLLED_COURSE);
        KotlinPermissions.with(Objects.requireNonNull(getActivity()))
                .permissions(Manifest.permission.RECORD_AUDIO)
                .onAccepted(permissionResult -> {
                    sttService = STTService.init(getActivity());
                    sttService.initCallback(Fragment_CourseExperience.this);
                    mHandler.sendEmptyMessage(PREFILL_EXPERIENCE_IF_ANY);
                })
                .ask();
    }

    @Click(R.id.mic_1)
    public void enableSTT1() {
        mHandler.sendEmptyMessage(MIC_1);
    }

    @Click(R.id.mic_2)
    public void enableSTT2() {
        mHandler.sendEmptyMessage(MIC_2);
    }

    @Click(R.id.mic_3)
    public void enableSTT3() {
        mHandler.sendEmptyMessage(MIC_3);
    }

    @Click(R.id.mic_4)
    public void enableSTT4() {
        mHandler.sendEmptyMessage(MIC_4);
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
        if (et_new_words_learnt.getText().toString().isEmpty()) {
            et_new_words_learnt.setError("Please fill this");
            return;
        }
        if (et_no_of_assignments_completed.getText().toString().isEmpty()) {
            et_no_of_assignments_completed.setError("Please fill this");
            return;
        }
        if (et_assignment_desc.getText().toString().isEmpty()) {
            et_assignment_desc.setError("Please fill this");
            return;
        }
        if (et_coach_comments.getText().toString().isEmpty()) {
            et_coach_comments.setError("Please fill this");
            return;
        }
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(PrathamApplication.pradigiPath, PD_Constant.HELPER_FOLDER);
        if (!imagesFolder.exists()) imagesFolder.mkdirs();
        File image = new File(imagesFolder, FastSave.getInstance().getString(PD_Constant.SESSIONID, "na") +
                (getArguments() != null ? getArguments().getString(PD_Constant.WEEK) : "na") + "_exp.jpg");
        capturedImageUri = Uri.fromFile(image);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, capturedImageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            exp_coach_image.setVisibility(View.VISIBLE);
            exp_coach_image.setImageURI(capturedImageUri);
            completeAndUpdateCourseInDb(PD_Utility.getRealPathFromURI(capturedImageUri, Objects.requireNonNull(getActivity())));
            new Handler().postDelayed(this::showKeepItUpDialog, 500);
        }
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
        try {
            JSONObject c_exp = new JSONObject();
            c_exp.put("words_learnt", et_new_words_learnt.getText().toString());
            c_exp.put("assignments_completed", et_no_of_assignments_completed.getText().toString());
            c_exp.put("assignments_description", et_assignment_desc.getText().toString());
            c_exp.put("coach_comments", et_coach_comments.getText().toString());
            c_exp.put("coach_verification_date", PD_Utility.getCurrentDateTime());
            c_exp.put("coach_image", coachImage);
            model_courseEnrollment.setCourseExperience(c_exp.toString());
            model_courseEnrollment.setCourseCompleted(true);
            model_courseEnrollment.setSentFlag(0);
            PrathamApplication.courseDao.updateCourse(model_courseEnrollment);
//            PrathamApplication.courseDao.addExperienceToCourse(c_exp.toString(), model_courseEnrollment.getCourseId(),
//                    model_courseEnrollment.getGroupId(), (getArguments() != null ? getArguments().getString(PD_Constant.WEEK) : "na"),
//                    model_courseEnrollment.getLanguage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResult(String result) {
        switch (SELECTED_MIC) {
            case MIC_1:
                et_new_words_learnt.append(result);
                break;
            case MIC_2:
                et_no_of_assignments_completed.append(result);
                break;
            case MIC_3:
                et_assignment_desc.append(result);
                break;
            case MIC_4:
                et_coach_comments.append(result);
                break;
        }
    }

    @Override
    public void sttStopped() {
        isChecked = !isChecked;
        final int[] stateSet = {android.R.attr.state_checked * (isChecked ? 1 : -1)};
        switch (SELECTED_MIC) {
            case MIC_1:
                mic_1.setImageState(stateSet, true);
                break;
            case MIC_2:
                mic_2.setImageState(stateSet, true);
                break;
            case MIC_3:
                mic_3.setImageState(stateSet, true);
                break;
            case MIC_4:
                mic_4.setImageState(stateSet, true);
                break;
        }
    }
}
