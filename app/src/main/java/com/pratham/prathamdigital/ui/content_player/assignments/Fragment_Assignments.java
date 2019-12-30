package com.pratham.prathamdigital.ui.content_player.assignments;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.explosion_effect.ExplosionField;
import com.pratham.prathamdigital.custom.file_picker.Configurations;
import com.pratham.prathamdigital.custom.file_picker.FilePickerActivity;
import com.pratham.prathamdigital.custom.file_picker.MediaFile;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Model_Assignment;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.models.Model_CourseExperience;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Objects;

@EFragment(R.layout.fragment_assignments)
public class Fragment_Assignments extends Fragment implements ContentPlayerContract.assignment_submission {

    private static final int SELECT_IMAGE = 1;
    @ViewById(R.id.rv_assignments)
    RecyclerView rv_assignments;

    private AssignmentAdapter assignmentAdapter;
    private int parentPositionClicked;
    private Model_CourseEnrollment enrolledCourse;
    private ExplosionField explosionField;
    private BlurPopupWindow exitDialog;

    @AfterViews
    public void init() {
        explosionField = ExplosionField.attach2Window(Objects.requireNonNull(getActivity()));
        initializeAdapter();
    }

    private void initializeAdapter() {
        enrolledCourse = Objects.requireNonNull(getArguments()).getParcelable(PD_Constant.ENROLLED_COURSE);
        assignmentAdapter = new AssignmentAdapter(getActivity(), parseAssignments(), this);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        rv_assignments.setLayoutManager(flexboxLayoutManager);
        rv_assignments.setAdapter(assignmentAdapter);
    }

    private ArrayList<Model_Assignment> parseAssignments() {
        ArrayList<Model_Assignment> asgnmt = new ArrayList<>();
        String assignments = Objects.requireNonNull(getArguments()).getString(PD_Constant.OPEN_ASSIGNMENTS);
        if (assignments != null) {
            String[] splits = assignments.split("~");
            for (String str : splits) {
                Model_Assignment model_assignment = new Model_Assignment();
                model_assignment.setAssignment_desc(str);
                ArrayList<String> files = new ArrayList<>();
                files.add(null);                                // adding footer to show plus sign
                model_assignment.setAssignment_files(files);
                asgnmt.add(model_assignment);
            }
        }
        return asgnmt;
    }

    @Click(R.id.btn_submit)
    public void submitAssignment() {
        boolean assignmentSubmitted = false;
        for (Model_Assignment agmt : assignmentAdapter.getItems())
            assignmentSubmitted = agmt.getAssignment_files().size() > 1;
        if (assignmentSubmitted)
            addAssignmentEntryInDB();
        else
            Toast.makeText(getActivity(), "Please submit all assignments", Toast.LENGTH_SHORT).show();
    }

    @Background
    public void addAssignmentEntryInDB() {
        Gson gson = new Gson();
        Model_CourseExperience model_courseExperience = gson.fromJson(
                enrolledCourse.getCourseExperience(), Model_CourseExperience.class);
        model_courseExperience.setAssignments(assignmentAdapter.getItems());
        model_courseExperience.setAssignment_submission_date(PD_Utility.getCurrentDateTime());
        model_courseExperience.setStatus(PD_Constant.ASSIGNMENT_SUBMITTED);

        enrolledCourse.setCourseExperience(gson.toJson(model_courseExperience));
        enrolledCourse.setCourseCompleted(true);
        enrolledCourse.setSentFlag(0);
        PrathamApplication.courseDao.updateCourse(enrolledCourse);
        closeView();
    }

    @UiThread
    public void closeView() {
        exitDialog = new BlurPopupWindow.Builder(getActivity())
                .setContentView(R.layout.dialog_course_completed)
                .bindClickListener(v -> {
                    exitDialog.dismiss();
                    EventMessage message = new EventMessage();
                    message.setMessage(PD_Constant.ASSIGNMENT_SUBMITTED);
                    EventBus.getDefault().post(message);
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
    public void addFileClicked(int parentPosition) {
        this.parentPositionClicked = parentPosition;
        Intent intent = new Intent(getActivity(), FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(true)
                .setShowVideos(true)
                .setShowAudios(false)
                .setCheckPermission(true)
                .setMaxSelection(5)
                .setSingleClickSelection(true)
                .setLandscapeSpanCount(10)
                .build());
        startActivityForResult(intent, SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    ArrayList<String> files_selected = assignmentAdapter.getItems().get(parentPositionClicked).getAssignment_files();
                    ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                    for (MediaFile m : files)
                        files_selected.add(files_selected.size() - 1, m.getPath());
                    assignmentAdapter.getItems().get(parentPositionClicked).setAssignment_files(files_selected);
                    assignmentAdapter.notifyItemChanged(parentPositionClicked);
                }
            }
        }
    }

    @Override
    public void onFilePreviewClicked(String file_path) {

    }

    @Override
    public void deleteFile(View itemView, int parentPosition, int selfPosition) {
        explosionField.explode(itemView);
        ArrayList<String> files = assignmentAdapter.getItems().get(parentPosition).getAssignment_files();
        files.remove(selfPosition);
        assignmentAdapter.getItems().get(parentPosition).setAssignment_files(files);
        assignmentAdapter.notifyItemChanged(parentPosition);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_CONTENT_PLAYER)) {
                EventMessage message1 = new EventMessage();
                message1.setMessage(PD_Constant.SHOW_COURSE_DETAIL);
                EventBus.getDefault().post(message1);
            }
        }
    }
}
