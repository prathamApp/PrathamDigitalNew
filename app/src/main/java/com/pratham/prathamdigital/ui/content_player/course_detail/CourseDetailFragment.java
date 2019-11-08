package com.pratham.prathamdigital.ui.content_player.course_detail;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pratham.prathamdigital.PrathamApplication;
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

import java.io.File;
import java.util.List;
import java.util.Objects;

@EFragment(R.layout.fragment_course_detail)
public class CourseDetailFragment extends Fragment implements ContentPlayerContract.courseDetailAdapterClick {
    @ViewById(R.id.root_detail)
    NestedScrollView root_detail;
    @ViewById(R.id.rv_course_childs)
    RecyclerView rv_course_childs;
    @ViewById(R.id.course_image)
    SimpleDraweeView course_image;
    @ViewById(R.id.course_name)
    TextView course_name;
    @ViewById(R.id.course_detail)
    TextView course_detail;
    @ViewById(R.id.course_assign)
    TextView course_assign;
    @ViewById(R.id.course_dates)
    TextView course_dates;

    @SuppressLint("SetTextI18n")
    @AfterViews
    public void init() {
        Model_CourseEnrollment enrollment = Objects.requireNonNull(getArguments()).getParcelable(PD_Constant.COURSE_PARENT);
        Uri imgUri;
        if (enrollment.getCourseDetail().isOnSDCard())
            imgUri = Uri.fromFile(new File(
                    PrathamApplication.contentSDPath + "/PrathamImages/" +
                            Objects.requireNonNull(enrollment).getCourseDetail().getNodeimage()));
        else
            imgUri = Uri.fromFile(new File(
                    PrathamApplication.pradigiPath + "/PrathamImages/" +
                            Objects.requireNonNull(enrollment).getCourseDetail().getNodeimage()));
        course_image.setImageURI(imgUri);
        course_name.setText(enrollment.getCourseDetail().getNodetitle());
        course_detail.setText("Description:-\n" + enrollment.getCourseDetail().getNodedesc());
        course_assign.setText("Assignment:-\n" + enrollment.getCourseDetail().getAssignment());
        course_dates.setText("Course Timeline:-\n" + parseDate(enrollment.getPlanFromDate()) + "  -  " + parseDate(enrollment.getPlanToDate()));
        initializeAdapter();
    }

    private String parseDate(String date) {
        String[] date_split = date.split(" ");
        return date_split[1] + " " + date_split[2] + " " + date_split[3] + "," + date_split[6];
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void initializeAdapter() {
        List<Modal_ContentDetail> childs = Objects.requireNonNull(getArguments()).getParcelableArrayList(PD_Constant.CONTENT);
        CourseDetailAdapter adapter = new CourseDetailAdapter(getActivity(), childs, this);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        rv_course_childs.setLayoutManager(flexboxLayoutManager);
        rv_course_childs.setAdapter(adapter);
    }

    @Click(R.id.course_lottie)
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
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_CONTENT_PLAYER)) {
                ((Activity_ContentPlayer) Objects.requireNonNull(getActivity())).closeContentPlayer();
            }
        }
    }
}
