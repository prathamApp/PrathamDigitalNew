package com.pratham.prathamdigital.ui.content_player.video_player;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.custom.video_player.CustomExoPlayerView;
import com.pratham.prathamdigital.custom.video_player.ExoPlayerCallBack;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_AajKaSawal;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.services.youtube_extractor.VideoMeta;
import com.pratham.prathamdigital.services.youtube_extractor.YouTubeExtractor;
import com.pratham.prathamdigital.services.youtube_extractor.YtFile;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.scoreDao;

@EFragment(R.layout.fragment_generic_vplayer)
public class Fragment_VideoPlayer extends Fragment {

    private static final int AAJ_KA_SAWAL_FOR_THIS_VIDEO = 1;
    private static final int SHOW_SAWAL = 2;
    private static final int SHOW_NEXT_CONTENT_OF_THE_COURSE = 3;
    private static final int CLOSE_CONTENT_PLAYER_ACTIVITY = 4;
    private static final int BACK_TO_COURSE_DETAIL = 5;
    @ViewById(R.id.videoView)
    CustomExoPlayerView videoView;
//    @ViewById(R.id.player_control_view)
//    PlayerControlView player_control_view;

    private String videoPath;
    private String startTime = "no_resource";
    private String resId;
    private long videoDuration = 0;
    private Modal_AajKaSawal videoSawal = null;
    private boolean initialized = false;
    private boolean isVideoEnded = false;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AAJ_KA_SAWAL_FOR_THIS_VIDEO:
                    findSawalForThisVideo();
                    break;
                case SHOW_NEXT_CONTENT_OF_THE_COURSE:
                    EventMessage message = new EventMessage();
                    message.setMessage(PD_Constant.SHOW_NEXT_CONTENT);
                    message.setDownloadId(resId);
                    EventBus.getDefault().post(message);
                    break;
                case CLOSE_CONTENT_PLAYER_ACTIVITY:
                    EventMessage eventMessage1 = new EventMessage();
                    eventMessage1.setMessage(PD_Constant.CLOSE_CONTENT_ACTIVITY);
                    EventBus.getDefault().post(eventMessage1);
                    break;
                case SHOW_SAWAL:
                    Bundle aksbundle = new Bundle();
                    aksbundle.putParcelable(PD_Constant.AKS_QUESTION, videoSawal);
                    aksbundle.putString(PD_Constant.RESOURSE_ID, resId);
                    aksbundle.putBoolean("isCourse", getArguments().getBoolean("isCourse"));
                    EventMessage eventMessage = new EventMessage();
                    eventMessage.setMessage(PD_Constant.ADD_VIDEO_PROGRESS_AND_SHOW_SAWAL);
                    eventMessage.setBundle(aksbundle);
                    eventMessage.setDownloadId(resId);
                    EventBus.getDefault().post(eventMessage);
                    break;
                case BACK_TO_COURSE_DETAIL:
                    EventMessage message1 = new EventMessage();
                    message1.setMessage(PD_Constant.SHOW_COURSE_DETAIL);
                    EventBus.getDefault().post(message1);
                    break;
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    @AfterViews
    public void initialize() {
        if (getArguments().getString(PD_Constant.YOUTUBE_LINK) != null) {
            videoPath = Objects.requireNonNull(getArguments()).getString(PD_Constant.YOUTUBE_LINK);
            resId = videoPath;
            new YouTubeExtractor(getActivity()) {
                @Override
                protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                    if (ytFiles != null) initializePlayer(ytFiles.get(22).getUrl());
                }
            }.extract(videoPath, true, true);
        } else {
            videoPath = Objects.requireNonNull(getArguments()).getString("videoPath");
            resId = getArguments().getString("resId");
            mHandler.sendEmptyMessage(AAJ_KA_SAWAL_FOR_THIS_VIDEO);
            initializePlayer(videoPath);
        }
    }

    private void initializePlayer(String videoPath) {
        videoView.setSource(videoPath);
        videoView.setExoPlayerCallBack(new ExoPlayerCallBack() {
            @Override
            public void onError() {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {
                if (!initialized) {
                    startTime = PD_Utility.getCurrentDateTime();
                    videoDuration = videoView.getPlayer().getDuration();
                    initialized = true;
                }
            }

            @Override
            public void onEnded() {
                if (!isVideoEnded) {
                    addScoreToDB();
                    if (videoSawal == null)
                        if (Objects.requireNonNull(getArguments()).getBoolean("isCourse"))
                            mHandler.sendEmptyMessage(SHOW_NEXT_CONTENT_OF_THE_COURSE);
                        else
                            mHandler.sendEmptyMessage(CLOSE_CONTENT_PLAYER_ACTIVITY);
                    else
                        mHandler.sendEmptyMessage(SHOW_SAWAL);
                    isVideoEnded = true;
                }
            }
        });
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
                addScoreToDB();
                if (Objects.requireNonNull(getArguments()).getBoolean("isCourse")) {
                    mHandler.sendEmptyMessage(BACK_TO_COURSE_DETAIL);
                } else
                    mHandler.sendEmptyMessage(CLOSE_CONTENT_PLAYER_ACTIVITY);
            }
        }
    }

    @Background
    public void addScoreToDB() {
        String endTime = PD_Utility.getCurrentDateTime();
        Modal_Score modalScore = new Modal_Score();
        modalScore.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        if (PrathamApplication.isTablet) {
            modalScore.setGroupID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group"));
            modalScore.setStudentID("");
        } else {
            modalScore.setGroupID("");
            modalScore.setStudentID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_student"));
        }
        modalScore.setDeviceID(PD_Utility.getDeviceID());
        modalScore.setResourceID(resId);
        modalScore.setQuestionId(0);
        modalScore.setScoredMarks((int) PD_Utility.getTimeDifference(startTime, endTime));
        modalScore.setTotalMarks((int) videoDuration);
        modalScore.setStartDateTime(startTime);
        modalScore.setEndDateTime(endTime);
        modalScore.setLevel(0);
        modalScore.setLabel("_");
        modalScore.setSentFlag(0);
        scoreDao.insert(modalScore);
    }

    @Background
    public void findSawalForThisVideo() {
        //aaj_ka_sawal is downloaded in main activity.
        try {
            String filename = "AajKaSawal_" + FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI) + ".json";
            File aksFile = new File(PrathamApplication.pradigiPath + "/" + filename);
            if (aksFile.exists()) {
                String aks = PD_Utility.readJSONFile(aksFile.getAbsolutePath());
                Modal_AajKaSawal rootAajKaSawal = new Gson().fromJson(aks, Modal_AajKaSawal.class);
                for (Modal_AajKaSawal subjectSawal : rootAajKaSawal.getNodelist()) {
                    boolean found = false;
                    for (Modal_AajKaSawal aajKaSawal : subjectSawal.getNodelist()) {
                        if (aajKaSawal.getResourceId() != null && aajKaSawal.getAltnodeid() != null)
                            if (aajKaSawal.getResourceId().equalsIgnoreCase(resId) || aajKaSawal.getAltnodeid().equalsIgnoreCase(resId)) {
                                found = true;
                                videoSawal = aajKaSawal;
                                break;
                            }
                    }
                    if (found) break;
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }
}
