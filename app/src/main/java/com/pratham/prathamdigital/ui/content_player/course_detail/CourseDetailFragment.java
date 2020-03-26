package com.pratham.prathamdigital.ui.content_player.course_detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.flexbox.FlexDirection;
import com.pratham.prathamdigital.custom.flexbox.FlexboxLayoutManager;
import com.pratham.prathamdigital.custom.flexbox.JustifyContent;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.ui.content_player.Activity_ContentPlayer;
import com.pratham.prathamdigital.ui.content_player.ContentPlayerContract;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
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

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void init() {
        enrollment = Objects.requireNonNull(getArguments()).getParcelable(PD_Constant.COURSE_PARENT);
        course_name.setText(enrollment.getCourseDetail().getNodetitle());
        course_detail.setText(enrollment.getCourseDetail().getNodedesc());
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
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
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
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_CONTENT_PLAYER))
                ((Activity_ContentPlayer) Objects.requireNonNull(getActivity())).closeContentPlayer();
            else if (message.getMessage().equalsIgnoreCase(PD_Constant.COURSE_COMPLETED))
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
