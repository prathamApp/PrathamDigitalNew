package com.pratham.prathamdigital.ui.content_player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.gson.Gson;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_ContentProgress;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;
import com.pratham.prathamdigital.models.Model_CourseExperience;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import static com.pratham.prathamdigital.PrathamApplication.pradigiPath;

@EBean
public class ContentPlayerPresenter implements ContentPlayerContract.contentPlayerPresenter {
    Context context;
    private Model_CourseEnrollment courseParent;
    private List<Modal_ContentDetail> courseChilds;
    private List<Modal_ContentDetail> coursePlayingQueue;
    private ContentPlayerContract.contentPlayerView contentPlayerView;
    private String courseId;
    private String course_week;

    public ContentPlayerPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void setView(Activity_ContentPlayer activity_contentPlayer) {
        contentPlayerView = activity_contentPlayer;
    }

    @Override
    public void setCourse(Intent intent) {
        courseParent = intent.getParcelableExtra(PD_Constant.COURSE_PARENT);
        courseChilds = intent.getParcelableArrayListExtra(PD_Constant.CONTENT);
        coursePlayingQueue = new ArrayList<>(intent.getParcelableArrayListExtra(PD_Constant.CONTENT));
        course_week = intent.getStringExtra(PD_Constant.WEEK);
        courseId = intent.getStringExtra(PD_Constant.COURSE_ID);
    }

    @Background
    @Override
    public void categorizeIntent(Intent intent) {
        if (intent != null) {
            if (intent.getStringExtra(PD_Constant.CONTENT_TYPE).equalsIgnoreCase(PD_Constant.PDF)) {
                Modal_ContentDetail contentDetail = intent.getParcelableExtra(PD_Constant.CONTENT);
                bundlePdf(contentDetail, false);
            } else if (intent.getStringExtra(PD_Constant.CONTENT_TYPE).equalsIgnoreCase(PD_Constant.VIDEO)) {
                Modal_ContentDetail contentDetail = intent.getParcelableExtra(PD_Constant.CONTENT);
                bundleVideo(contentDetail, false, null);
            } else if (intent.getStringExtra(PD_Constant.CONTENT_TYPE).equalsIgnoreCase(PD_Constant.GAME)) {
                Modal_ContentDetail contentDetail = intent.getParcelableExtra(PD_Constant.CONTENT);
                bundleGame(contentDetail, false);
            } else if (intent.getStringExtra(PD_Constant.CONTENT_TYPE).equalsIgnoreCase(PD_Constant.COURSE)) {
                setCourse(intent);
                bundleCourse(intent);
            } else if (intent.getStringExtra(PD_Constant.CONTENT_TYPE).equalsIgnoreCase(PD_Constant.YOUTUBE_LINK)) {
                bundleVideo(null, false, intent.getStringExtra(PD_Constant.CONTENT));
            }
        }
    }

    private void bundleCourse(Intent intent) {
        Bundle courseDetailBundle = new Bundle();
        courseDetailBundle.putParcelable(PD_Constant.COURSE_PARENT, intent.getParcelableExtra(PD_Constant.COURSE_PARENT));
        courseDetailBundle.putParcelableArrayList(PD_Constant.CONTENT, intent.getParcelableArrayListExtra(PD_Constant.CONTENT));
        contentPlayerView.showCourseDetail(courseDetailBundle);
    }

    private void bundleGame(Modal_ContentDetail gContentDetail, boolean isCourse) {
        Bundle gBundle = new Bundle();
        String f_path;
        if (gContentDetail.isOnSDCard())
            f_path = PrathamApplication.externalContentPath + "/PrathamGame/" + gContentDetail.getResourcepath();
        else
            f_path = pradigiPath + "/PrathamGame/" + gContentDetail.getResourcepath();
        gBundle.putString("index_path", f_path);
        gBundle.putString("resId", gContentDetail.getResourceid());
        gBundle.putBoolean("isOnSdCard", gContentDetail.isOnSDCard());
        gBundle.putBoolean("isCourse", isCourse);
        contentPlayerView.showGame(gBundle);
    }

    private void bundleVideo(Modal_ContentDetail vidContentDetail, boolean isCourse, String youtube_link) {
        Bundle vidBundle = new Bundle();
        String vid_path;
        if (vidContentDetail != null) {
            if (vidContentDetail.isOnSDCard())
                vid_path = PrathamApplication.externalContentPath + "/PrathamVideo/" + vidContentDetail.getResourcepath();
            else
                vid_path = pradigiPath + "/PrathamVideo/" + vidContentDetail.getResourcepath();
            vidBundle.putString("videoPath", vid_path);
            vidBundle.putString("videoTitle", vidContentDetail.getNodetitle());
            vidBundle.putString("resId", vidContentDetail.getResourceid());
        }
        vidBundle.putBoolean("isCourse", isCourse);
        if (youtube_link != null && !youtube_link.isEmpty())
            vidBundle.putString(PD_Constant.YOUTUBE_LINK, youtube_link);
        contentPlayerView.showVideo(vidBundle);
    }

    private void bundlePdf(Modal_ContentDetail pdfContentDetail, boolean isCourse) {
        Bundle pdfBundle = new Bundle();
        String pdf_path;
        if (pdfContentDetail.isOnSDCard())
            pdf_path = PrathamApplication.externalContentPath + "/PrathamPdf/" + pdfContentDetail.getResourcepath();
        else
            pdf_path = pradigiPath + "/PrathamPdf/" + pdfContentDetail.getResourcepath();
        pdfBundle.putString("pdfPath", pdf_path);
        pdfBundle.putString("resId", pdfContentDetail.getResourceid());
        pdfBundle.putBoolean("isCourse", isCourse);
        contentPlayerView.showPdf(pdfBundle);
    }

    @Background
    @Override
    public void resumeCourse() {
        if (coursePlayingQueue.size() > 0) {
            if (coursePlayingQueue.get(0).getResourcetype().equalsIgnoreCase(PD_Constant.PDF)) {
                bundlePdf(coursePlayingQueue.get(0), true);
            } else if (coursePlayingQueue.get(0).getResourcetype().equalsIgnoreCase(PD_Constant.VIDEO)) {
                bundleVideo(coursePlayingQueue.get(0), true, null);
            } else if (coursePlayingQueue.get(0).getResourcetype().equalsIgnoreCase(PD_Constant.GAME)) {
                bundleGame(coursePlayingQueue.get(0), true);
            }
            coursePlayingQueue.remove(0);
        } else contentPlayerView.onCourseCompleted();
    }

    @Override
    public void showNextContent(String contentId) {
        addProgress(contentId);
        if (coursePlayingQueue != null && coursePlayingQueue.size() > 0) {
            contentPlayerView.showNextContentPlayingTimer();
            new Handler().postDelayed(this::resumeCourse, 5000);
        } else
            contentPlayerView.onCourseCompleted();
    }

    @Background
    public void addProgress(String resID) {
        String stuid = FastSave.getInstance().getString(PD_Constant.GROUPID, "NA");
        Model_ContentProgress contentProgress = PrathamApplication.contentProgressDao.getCourse(stuid, courseId, course_week);
        if (contentProgress != null) {
            if (!contentProgress.getLabel().toLowerCase().contains(resID.toLowerCase())) {
                int percentage_progress = 100 / courseChilds.size();
                int ttl = Integer.parseInt(contentProgress.getProgressPercentage().isEmpty() ? "0" : contentProgress.getProgressPercentage()) + percentage_progress;
                String label = contentProgress.getLabel() + resID + ",";
                contentProgress.setLabel(label);
                contentProgress.setUpdatedDateTime(course_week + " " + PD_Utility.getCurrentDateTime());
                contentProgress.setProgressPercentage(String.valueOf(ttl));
                contentProgress.setSentFlag(false);
                PrathamApplication.contentProgressDao.updateProgress(contentProgress);
                if (ttl > 95) {
                    Gson gson = new Gson();
                    Model_CourseExperience model_courseExperience = gson.fromJson(
                            courseParent.getCourseExperience(), Model_CourseExperience.class);
                    if (model_courseExperience.getStatus().equalsIgnoreCase(PD_Constant.COURSE_ENROLLED)) {
                        model_courseExperience.setStatus(PD_Constant.COURSE_COMPLETED);
                        courseParent.setCourseExperience(gson.toJson(model_courseExperience));
                        courseParent.setCourse_status(PD_Constant.COURSE_COMPLETED);
                        courseParent.setCourseCompleted(true);
                        courseParent.setSentFlag(0);
                        PrathamApplication.courseDao.updateCourse(courseParent);
                    }
                }
            }
        } else {
            int percentage_progress = 100 / courseChilds.size();
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
    public void playSpecificCourseContent(String contentId) {
        if (contentId != null && courseChilds.size() > 0) {
            List<Modal_ContentDetail> tempList = new ArrayList<>(courseChilds);
            //resume course from the selected content
            for (int i = 0; i < tempList.size(); i++) {
                if (i == tempList.size() - 1) {
                    coursePlayingQueue.clear();
                    coursePlayingQueue.add(tempList.get(i));
                    break;
                }
                if (tempList.get(i).getNodeid().equalsIgnoreCase(contentId)) {
                    coursePlayingQueue = tempList.subList(i, tempList.size());
                    break;
                }
            }
        }
        resumeCourse();
    }

    @Override
    public boolean hasNextContentInQueue() {
        return coursePlayingQueue != null && !coursePlayingQueue.isEmpty();
    }

    @Override
    public Model_CourseEnrollment getCourse() {
        return courseParent;
    }

    @Override
    public void addContentProgress(String pdfResId) {
        addProgress(pdfResId);
    }
}
