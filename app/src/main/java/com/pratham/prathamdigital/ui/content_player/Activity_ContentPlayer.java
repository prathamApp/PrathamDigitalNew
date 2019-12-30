package com.pratham.prathamdigital.ui.content_player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CountDownTextView;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.ui.content_player.assignments.Fragment_Assignments;
import com.pratham.prathamdigital.ui.content_player.assignments.Fragment_Assignments_;
import com.pratham.prathamdigital.ui.content_player.course_detail.CourseDetailFragment;
import com.pratham.prathamdigital.ui.content_player.course_detail.CourseDetailFragment_;
import com.pratham.prathamdigital.ui.content_player.fragment_aaj_ka_sawal.Fragment_AAJ_KA_SAWAL;
import com.pratham.prathamdigital.ui.content_player.fragment_aaj_ka_sawal.Fragment_AAJ_KA_SAWAL_;
import com.pratham.prathamdigital.ui.content_player.pdf_viewer.Fragment_PdfViewer;
import com.pratham.prathamdigital.ui.content_player.pdf_viewer.Fragment_PdfViewer_;
import com.pratham.prathamdigital.ui.content_player.video_player.Fragment_VideoPlayer;
import com.pratham.prathamdigital.ui.content_player.video_player.Fragment_VideoPlayer_;
import com.pratham.prathamdigital.ui.content_player.web_view.Fragment_WebView;
import com.pratham.prathamdigital.ui.content_player.web_view.Fragment_WebView_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@EActivity(R.layout.activity_content_player)
public class Activity_ContentPlayer extends BaseActivity implements ContentPlayerContract.contentPlayerView {

    @ViewById(R.id.txt_next_countdown)
    CountDownTextView txt_next_countdown;
    @ViewById(R.id.pdf_play_next)
    TextView pdf_play_next;

    private String pdfResId;

    @Bean(ContentPlayerPresenter.class)
    ContentPlayerContract.contentPlayerPresenter contentPlayerPresenter;

    @AfterViews
    public void init() {
        contentPlayerPresenter.setView(this);
        contentPlayerPresenter.categorizeIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        contentPlayerPresenter.setView(this);
    }

    @Click(R.id.close_content_player)
    public void setClose() {
        if (txt_next_countdown.getVisibility() == View.VISIBLE)
            txt_next_countdown.cancel();
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.CLOSE_CONTENT_PLAYER);
        EventBus.getDefault().post(message);
    }

    @Override
    public void onBackPressed() {
        setClose();
    }

    public void closeContentPlayer() {
        Intent data = new Intent();
        String courseId = "";
        data.putExtra(PD_Constant.COURSE_ID, courseId);
        setResult(RESULT_OK, data);
        finish();
    }

    @Subscribe
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.SHOW_NEXT_CONTENT)) {
                contentPlayerPresenter.showNextContent(message.getDownloadId());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.PLAY_COURSE)) {
                contentPlayerPresenter.resumeCourse();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.ADD_VIDEO_PROGRESS_AND_SHOW_SAWAL)) {
                contentPlayerPresenter.addContentProgress(message.getDownloadId());
                PD_Utility.showFragment(Activity_ContentPlayer.this, new Fragment_AAJ_KA_SAWAL_(),
                        R.id.content_player_frame, message.getBundle(), Fragment_AAJ_KA_SAWAL.class.getSimpleName());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.PLAY_SPECIFIC_COURSE_CONTENT)) {
                contentPlayerPresenter.playSpecificCourseContent(message.getDownloadId());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.SHOW_COURSE_DETAIL)) {
                hideNextButton();
                getSupportFragmentManager().popBackStackImmediate(CourseDetailFragment.class.getSimpleName(), 0);
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.SHOW_NEXT_BUTTON)) {
                pdfResId = message.getDownloadId();
                if (contentPlayerPresenter.hasNextContentInQueue())
                    pdf_play_next.setVisibility(View.VISIBLE);
                else {
                    contentPlayerPresenter.addContentProgress(pdfResId);
                    pdf_play_next.setVisibility(View.GONE);
                }
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.OPEN_ASSIGNMENTS)) {
                PD_Utility.showFragment(Activity_ContentPlayer.this, new Fragment_Assignments_(),
                        R.id.content_player_frame, message.getBundle(), Fragment_Assignments.class.getSimpleName());
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.ASSIGNMENT_SUBMITTED)) {
                closeContentPlayer();
            } else if (message.getMessage().equalsIgnoreCase(PD_Constant.CHECK_COURSE_COMPLETION)) {
                if (contentPlayerPresenter.getCourse().getCourse_status().equalsIgnoreCase(PD_Constant.COURSE_COMPLETED)) {
                    message.setMessage(PD_Constant.COURSE_COMPLETED);
                    EventBus.getDefault().post(message);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        txt_next_countdown.cancel();
    }

    public void hideNextButton() {
        if (pdf_play_next.getVisibility() == View.VISIBLE)
            pdf_play_next.setVisibility(View.GONE);
    }

    @Click(R.id.pdf_play_next)
    public void setPdf_play_next() {
        pdf_play_next.setVisibility(View.GONE);
        contentPlayerPresenter.showNextContent(pdfResId);
    }

    @UiThread
    @Override
    public void showCourseDetail(Bundle courseDetailBundle) {
        PD_Utility.showFragment(Activity_ContentPlayer.this, new CourseDetailFragment_(), R.id.content_player_frame,
                courseDetailBundle, CourseDetailFragment.class.getSimpleName());
    }

    @UiThread
    @Override
    public void showGame(Bundle gBundle) {
        hideNextButton();
        PD_Utility.showFragment(Activity_ContentPlayer.this, new Fragment_WebView_(), R.id.content_player_frame,
                gBundle, Fragment_WebView.class.getSimpleName());
    }

    @UiThread
    @Override
    public void showVideo(Bundle vidBundle) {
        hideNextButton();
        PD_Utility.showFragment(Activity_ContentPlayer.this, new Fragment_VideoPlayer_(), R.id.content_player_frame,
                vidBundle, Fragment_VideoPlayer.class.getSimpleName());
    }

    @UiThread
    @Override
    public void showPdf(Bundle pdfBundle) {
        hideNextButton();
        PD_Utility.showFragment(Activity_ContentPlayer.this, new Fragment_PdfViewer_(), R.id.content_player_frame,
                pdfBundle, Fragment_PdfViewer.class.getSimpleName());
    }

    @UiThread
    @Override
    public void showNextContentPlayingTimer() {
        txt_next_countdown.setVisibility(View.VISIBLE);
        txt_next_countdown.reSet();
        txt_next_countdown.start();
        new Handler().postDelayed(() -> txt_next_countdown.setVisibility(View.GONE), 6000);
    }

    @UiThread
    @Override
    public void onCourseCompleted() {
        onBackPressed();
    }
}
