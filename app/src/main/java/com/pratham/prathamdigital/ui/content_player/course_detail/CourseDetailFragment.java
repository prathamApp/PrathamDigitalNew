package com.pratham.prathamdigital.ui.content_player.course_detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@EFragment(R.layout.fragment_course_detail)
public class CourseDetailFragment extends Fragment implements ContentPlayerContract.courseDetailAdapterClick {

    @ViewById(R.id.rv_course_childs)
    RecyclerView rv_course_childs;
    @ViewById(R.id.course_name)
    TextView course_name;
    @ViewById(R.id.course_detail)
    TextView course_detail;
    @ViewById(R.id.btn_submit_assignment)
    Button btn_submit_assignment;

    CourseDetailAdapter adapter;
    private Model_CourseEnrollment enrollment;
    private List<Modal_ContentDetail> childs;

    private final Map<String, Integer> filesDownloading = new HashMap<>();
    private int revealX;
    private int revealY;
    private BlurPopupWindow download_builder;

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void init() {
            //if screen open via node click
            if (Objects.requireNonNull(Objects.requireNonNull(getArguments()).getString("NODE_CALL")).equalsIgnoreCase("CALL_FROM_NODE")) {
                course_name.setText(Objects.requireNonNull(getArguments()).getString("NODE_TITLE"));
                course_detail.setText(getArguments().getString("NODE_DESC"));
            }
            //else screen open via Course navigation item click
            else {
                enrollment = Objects.requireNonNull(getArguments()).getParcelable(PD_Constant.COURSE_PARENT);
                assert enrollment != null;
                course_name.setText(enrollment.getCourseDetail().getNodetitle());
                course_detail.setText(enrollment.getCourseDetail().getNodedesc());
            }
            initializeAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkCourseCompletetionStatus();
    }

    private void checkCourseCompletetionStatus() {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.CHECK_COURSE_COMPLETION);
        EventBus.getDefault().post(message);
    }

    private String parseDate(String date) {
        String[] date_split = date.split(" ");
        return date_split[1] + " " + date_split[2] + " " + date_split[3] + "," + date_split[6];
    }

    private void initializeAdapter() {
        if (childs == null || childs.isEmpty())
            childs = Objects.requireNonNull(getArguments()).getParcelableArrayList(PD_Constant.CONTENT);
        adapter = new CourseDetailAdapter(getActivity(), this);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        rv_course_childs.setLayoutManager(flexboxLayoutManager);
        rv_course_childs.setAdapter(adapter);
        adapter.submitList(childs);
    }

    @Click(R.id.play_all_content_serially)
    public void playCourse() {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.PLAY_COURSE);
        message.setDownloadId(null);
        EventBus.getDefault().post(message);
    }

    @Override
    public void onChildItemClicked(Modal_ContentDetail modal_contentDetail) {
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.PLAY_SPECIFIC_COURSE_CONTENT);
        message.setDownloadId(modal_contentDetail.getNodeid());
        EventBus.getDefault().post(message);
    }

    @Override
    public void onAssessmentItemClicked() {

        new MaterialAlertDialogBuilder(Objects.requireNonNull(getActivity()))
                .setTitle("Confirm")
                .setMessage("You will be redirected to Assessment App!")
                .setPositiveButton("YES",(dialog, which) -> {
                    try {
                        //below are test values only, it will be replace by actual studentId and other
                        Intent intent = new Intent("com.pratham.assessment.ui.choose_assessment.science.ScienceAssessmentActivity_");
                        Bundle mBundle = new Bundle();
                        // extras.putString(key, value);
                        mBundle.putString("studentId", "123");
                        mBundle.putString("appName", "FC");
                        mBundle.putString("studentName", "Ankita");
                        mBundle.putString("subjectName", "Science");
                        mBundle.putString("subjectLanguage", "Hindi");
                        mBundle.putString("subjectLevel", "1");
                        mBundle.putString("examId", "3680");
                        mBundle.putString("subjectId", "89");
                        intent.putExtras(mBundle);
                        startActivity(intent);
                        //startActivityForResult(); //use this when actual testing
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "App not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL",(dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    // TODO : Download the clicked content
    //function is called when download icon is clicked
    @Override
    public void onDownloadClicked(int position, Modal_ContentDetail contentDetail, View reveal_view, View start_reveal_item) {
        Toast.makeText(getActivity(), contentDetail.getNodetitle(), Toast.LENGTH_SHORT).show();
        if (FastSave.getInstance().getBoolean(PD_Constant.STORAGE_ASKED, false)) {
            adapter.reveal(reveal_view, start_reveal_item);
            PrathamApplication.bubble_mp.start();
            filesDownloading.put(contentDetail.getNodeid(), position);
            //contentPresenter.downloadContent(contentDetail);
        } else {
            download_builder = new BlurPopupWindow.Builder(getActivity())
                    .setContentView(R.layout.download_alert_dialog)
                    .bindClickListener(v -> {
                        FastSave.getInstance().saveBoolean(PD_Constant.STORAGE_ASKED, true);
                        onDownloadClicked(position, contentDetail, reveal_view, start_reveal_item);
                        download_builder.dismiss();
                    }, R.id.btn_okay)
                    .bindClickListener(v -> download_builder.dismiss(), R.id.txt_download_cancel)
                    .setGravity(Gravity.CENTER)
                    .setDismissOnClickBack(true)
                    .setDismissOnTouchBackground(true)
                    .setScaleRatio(0.2f)
                    .setBlurRadius(8)
                    .setTintColor(0x30000000)
                    .build();
            TextView tv = download_builder.findViewById(R.id.txt_download_alert);
            tv.append(" " + PD_Constant.STORING_IN);
            download_builder.show();
        }
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
                EventMessage eventMessage1 = new EventMessage();
                eventMessage1.setMessage(PD_Constant.CLOSE_CONTENT_ACTIVITY);
                EventBus.getDefault().post(eventMessage1);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.COURSE_COMPLETED))
                btn_submit_assignment.setVisibility(View.VISIBLE);
            }
    }

    @Click(R.id.btn_submit_assignment)
    public void openAssignments() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PD_Constant.ENROLLED_COURSE, enrollment);
        bundle.putString(PD_Constant.OPEN_ASSIGNMENTS, enrollment.getCourseDetail().getAssignment());
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.OPEN_ASSIGNMENTS);
        message.setBundle(bundle);
        EventBus.getDefault().post(message);
    }
}
