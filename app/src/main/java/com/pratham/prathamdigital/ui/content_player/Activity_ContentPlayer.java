package com.pratham.prathamdigital.ui.content_player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CountDownTextView;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_ContentProgress;
import com.pratham.prathamdigital.ui.pdf_viewer.Fragment_PdfViewer;
import com.pratham.prathamdigital.ui.pdf_viewer.Fragment_PdfViewer_;
import com.pratham.prathamdigital.ui.video_player.Fragment_VideoPlayer;
import com.pratham.prathamdigital.ui.video_player.Fragment_VideoPlayer_;
import com.pratham.prathamdigital.ui.web_view.Fragment_WebView;
import com.pratham.prathamdigital.ui.web_view.Fragment_WebView_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import static com.pratham.prathamdigital.PrathamApplication.pradigiPath;

@EActivity(R.layout.activity_content_player)
public class Activity_ContentPlayer extends BaseActivity {

    private static final int PDF = 1;
    private static final int VIDEO = 2;
    private static final int GAME = 3;
    private static final int COURSE = 4;
    private static final int SHOW_NEXT_CONTENT = 5;

    @ViewById(R.id.txt_next_countdown)
    CountDownTextView txt_next_countdown;
    @ViewById(R.id.pdf_play_next)
    TextView pdf_play_next;

    private ArrayList<Modal_ContentDetail> course_childs;
    private boolean isCourse = false;
    private String courseId = "";
    private int courseLength;
    private String resId;
    private String course_week;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int contentType = bundle.getInt(PD_Constant.CONTENT_TYPE);
            switch (contentType) {
                case PDF:
                    hideNextButton();
                    Modal_ContentDetail pdfContentDetail = bundle.getParcelable(PD_Constant.CONTENT);
                    Bundle pdfBundle = new Bundle();
                    String pdf_path;
                    if (pdfContentDetail.isOnSDCard())
                        pdf_path = PrathamApplication.contentSDPath + "/PrathamPdf/" + pdfContentDetail.getResourcepath();
                    else
                        pdf_path = pradigiPath + "/PrathamPdf/" + pdfContentDetail.getResourcepath();
                    pdfBundle.putString("pdfPath", pdf_path);
                    pdfBundle.putString("resId", pdfContentDetail.getResourceid());
                    pdfBundle.putBoolean("isCourse", isCourse);
                    PD_Utility.showFragment(Activity_ContentPlayer.this, new Fragment_PdfViewer_(), R.id.content_player_frame,
                            pdfBundle, Fragment_PdfViewer.class.getSimpleName());
                    break;
                case VIDEO:
                    hideNextButton();
                    Modal_ContentDetail vidContentDetail = bundle.getParcelable(PD_Constant.CONTENT);
                    Bundle vidBundle = new Bundle();
                    String vid_path;
                    if (vidContentDetail.isOnSDCard())
                        vid_path = PrathamApplication.contentSDPath + "/PrathamVideo/" + vidContentDetail.getResourcepath();
                    else
                        vid_path = pradigiPath + "/PrathamVideo/" + vidContentDetail.getResourcepath();
                    vidBundle.putString("videoPath", vid_path);
                    vidBundle.putString("videoTitle", vidContentDetail.getNodetitle());
                    vidBundle.putString("resId", vidContentDetail.getResourceid());
                    vidBundle.putBoolean("hint", getIntent().getBooleanExtra("hint", false));
                    vidBundle.putBoolean("isCourse", isCourse);
                    PD_Utility.showFragment(Activity_ContentPlayer.this, new Fragment_VideoPlayer_(), R.id.content_player_frame,
                            vidBundle, Fragment_VideoPlayer.class.getSimpleName());
                    break;
                case GAME:
                    hideNextButton();
                    Modal_ContentDetail gContentDetail = bundle.getParcelable(PD_Constant.CONTENT);
                    Bundle gBundle = new Bundle();
                    String f_path;
                    if (gContentDetail.isOnSDCard())
                        f_path = PrathamApplication.contentSDPath + "/PrathamGame/" + gContentDetail.getResourcepath();
                    else
                        f_path = pradigiPath + "/PrathamGame/" + gContentDetail.getResourcepath();
                    gBundle.putString("index_path", f_path);
                    gBundle.putString("resId", gContentDetail.getResourceid());
                    gBundle.putBoolean("isOnSdCard", gContentDetail.isOnSDCard());
                    gBundle.putBoolean("isCourse", isCourse);
                    PD_Utility.showFragment(Activity_ContentPlayer.this, new Fragment_WebView_(), R.id.content_player_frame,
                            gBundle, Fragment_WebView.class.getSimpleName());
                    break;
                case COURSE:
                    course_childs = bundle.getParcelableArrayList(PD_Constant.CONTENT);
                    courseLength = course_childs.size();
                    if (course_childs != null && course_childs.size() > 0) {
                        Message m_msg = mHandler.obtainMessage();
                        Bundle mbundle = new Bundle();
                        mbundle.putParcelable(PD_Constant.CONTENT, course_childs.get(0));
                        if (course_childs.get(0).getResourcetype().equalsIgnoreCase(PD_Constant.PDF))
                            mbundle.putInt(PD_Constant.CONTENT_TYPE, PDF);
                        else if (course_childs.get(0).getResourcetype().equalsIgnoreCase(PD_Constant.VIDEO))
                            mbundle.putInt(PD_Constant.CONTENT_TYPE, VIDEO);
                        else if (course_childs.get(0).getResourcetype().equalsIgnoreCase(PD_Constant.GAME))
                            mbundle.putInt(PD_Constant.CONTENT_TYPE, GAME);
                        m_msg.setData(mbundle);
                        mHandler.sendMessage(m_msg);
                        course_childs.remove(0);
                    } else
                        onBackPressed();
                    break;
                case SHOW_NEXT_CONTENT:
                    if (course_childs != null && course_childs.size() > 0) {
                        Message m_msg = mHandler.obtainMessage();
                        Bundle mbundle = new Bundle();
                        mbundle.putParcelable(PD_Constant.CONTENT, course_childs.get(0));
                        if (course_childs.get(0).getResourcetype().equalsIgnoreCase(PD_Constant.PDF))
                            mbundle.putInt(PD_Constant.CONTENT_TYPE, PDF);
                        else if (course_childs.get(0).getResourcetype().equalsIgnoreCase(PD_Constant.VIDEO))
                            mbundle.putInt(PD_Constant.CONTENT_TYPE, VIDEO);
                        else if (course_childs.get(0).getResourcetype().equalsIgnoreCase(PD_Constant.GAME))
                            mbundle.putInt(PD_Constant.CONTENT_TYPE, GAME);
                        m_msg.setData(mbundle);
                        mHandler.sendMessage(m_msg);
                        course_childs.remove(0);
                        break;
                    } else
                        onBackPressed();
            }
        }
    };

    @AfterViews
    public void init() {
        if (getIntent() != null) {
            Message msg = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            if (getIntent().getStringExtra(PD_Constant.CONTENT_TYPE).equalsIgnoreCase(PD_Constant.PDF)) {
                isCourse = false;
                bundle.putParcelable(PD_Constant.CONTENT, getIntent().getParcelableExtra(PD_Constant.CONTENT));
                bundle.putInt(PD_Constant.CONTENT_TYPE, PDF);
            } else if (getIntent().getStringExtra(PD_Constant.CONTENT_TYPE).equalsIgnoreCase(PD_Constant.VIDEO)) {
                isCourse = false;
                bundle.putParcelable(PD_Constant.CONTENT, getIntent().getParcelableExtra(PD_Constant.CONTENT));
                bundle.putInt(PD_Constant.CONTENT_TYPE, VIDEO);
            } else if (getIntent().getStringExtra(PD_Constant.CONTENT_TYPE).equalsIgnoreCase(PD_Constant.GAME)) {
                isCourse = false;
                bundle.putParcelable(PD_Constant.CONTENT, getIntent().getParcelableExtra(PD_Constant.CONTENT));
                bundle.putInt(PD_Constant.CONTENT_TYPE, GAME);
            } else if (getIntent().getStringExtra(PD_Constant.CONTENT_TYPE).equalsIgnoreCase(PD_Constant.COURSE)) {
                isCourse = true;
                course_week = getIntent().getStringExtra(PD_Constant.WEEK);
                courseId = getIntent().getStringExtra(PD_Constant.COURSE_ID);
                bundle.putParcelableArrayList(PD_Constant.CONTENT, getIntent().getParcelableArrayListExtra(PD_Constant.CONTENT));
                bundle.putInt(PD_Constant.CONTENT_TYPE, COURSE);
            }
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    @Click(R.id.close_content_player)
    public void setClose() {
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
        data.putExtra(PD_Constant.COURSE_ID, courseId);
        setResult(RESULT_OK, data);
        finish();
    }

    @Subscribe
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.SHOW_NEXT_CONTENT)) {
                addProgress(message.getDownloadId());
                if (course_childs.size() > 0) {
                    txt_next_countdown.setVisibility(View.VISIBLE);
                    txt_next_countdown.reSet();
                    txt_next_countdown.start();
                    new Handler().postDelayed(() -> {
                        Message msg = mHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putInt(PD_Constant.CONTENT_TYPE, SHOW_NEXT_CONTENT);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }, 5000);
                } else
                    onBackPressed();
            }
        }
    }

    @Background
    public void addProgress(String resID) {
        String stuid = "";
        stuid = FastSave.getInstance().getString(PD_Constant.GROUPID, "NA");
        Model_ContentProgress contentProgress = PrathamApplication.contentProgressDao.getCourse(stuid, courseId, course_week);
        if (contentProgress != null) {
            if (!contentProgress.getLabel().toLowerCase().contains(resID.toLowerCase())) {
                int percentage_progress = 100 / courseLength;
                int ttl = Integer.parseInt(contentProgress.getProgressPercentage().isEmpty() ? "0" : contentProgress.getProgressPercentage()) + percentage_progress;
                String label = contentProgress.getLabel() + resID + ",";
                contentProgress.setLabel(label);
                contentProgress.setUpdatedDateTime(course_week + " " + PD_Utility.getCurrentDateTime());
                contentProgress.setProgressPercentage(String.valueOf(ttl));
                contentProgress.setSentFlag(false);
                PrathamApplication.contentProgressDao.updateProgress(contentProgress);
            }
        } else {
            int percentage_progress = 100 / courseLength;
            contentProgress = new Model_ContentProgress();
            contentProgress.setStudentId(stuid);
            contentProgress.setResourceId(courseId);
            contentProgress.setUpdatedDateTime(course_week + " " + PD_Utility.getCurrentDateTime());
            contentProgress.setProgressPercentage(String.valueOf(percentage_progress));
            contentProgress.setLabel(resID + ",");
            contentProgress.setSentFlag(false);
            PrathamApplication.contentProgressDao.insertProgress(contentProgress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        txt_next_countdown.cancel();
    }

    public void showNextButton(String resId) {
        this.resId = resId;
        pdf_play_next.setVisibility(View.VISIBLE);
    }

    public void hideNextButton() {
        if (pdf_play_next.getVisibility() == View.VISIBLE)
            pdf_play_next.setVisibility(View.GONE);
    }

    @Click(R.id.pdf_play_next)
    public void setPdf_play_next() {
        pdf_play_next.setVisibility(View.GONE);
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.SHOW_NEXT_CONTENT);
        message.setDownloadId(resId);
        messageReceived(message);
    }
}
