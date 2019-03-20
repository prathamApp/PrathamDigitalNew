package com.pratham.prathamdigital.ui.video_player;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.media_controller.PlayerControlView;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_AajKaSawal;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.ui.fragment_aaj_ka_sawal.Fragment_AAJ_KA_SAWAL;
import com.pratham.prathamdigital.ui.fragment_aaj_ka_sawal.Fragment_AAJ_KA_SAWAL_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

@EFragment(R.layout.fragment_generic_vplayer)
public class Fragment_VideoPlayer extends Fragment {

    private static final int AAJ_KA_SAWAL_FOR_THIS_VIDEO = 1;
    private static final int SHOW_SAWAL = 2;
    @ViewById(R.id.videoView)
    VideoView videoView;
    @ViewById(R.id.player_control_view)
    PlayerControlView player_control_view;

    private String videoPath;
    private String startTime = "no_resource";
    private String resId;
    private long videoDuration = 0;
    private boolean isHint = false;
    private Modal_AajKaSawal videoSawal = null;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AAJ_KA_SAWAL_FOR_THIS_VIDEO:
                    File aksFile = new File(PrathamApplication.pradigiPath + "/AajKaSawal.json");
                    if (aksFile.exists()) {
                        String aks = PD_Utility.readJSONFile(aksFile.getAbsolutePath());
                        Modal_AajKaSawal rootAajKaSawal = new Gson().fromJson(aks, Modal_AajKaSawal.class);
                        for (Modal_AajKaSawal subjectSawal : rootAajKaSawal.getNodelist()) {
                            boolean found = false;
                            for (Modal_AajKaSawal aajKaSawal : subjectSawal.getNodelist()) {
                                String[] ids = aajKaSawal.getResourceId().split(",");
                                if (ids.length > 0) {
                                    if ((ids[0] != null && ids[0].equalsIgnoreCase(resId)) ||
                                            (ids[1] != null && ids[1].equalsIgnoreCase(resId))) {
                                        found = true;
                                        videoSawal = aajKaSawal;
                                        break;
                                    }
                                }
                            }
                            if (found) break;
                        }
                    }
                    break;
                case SHOW_SAWAL:
                    Bundle aksbundle = new Bundle();
                    aksbundle.putParcelable(PD_Constant.AKS_QUESTION, videoSawal);
                    PD_Utility.addFragment(getActivity(), new Fragment_AAJ_KA_SAWAL_(), R.id.vp_frame,
                            aksbundle, Fragment_AAJ_KA_SAWAL.class.getSimpleName());
                    break;
            }
        }
    };

    @AfterViews
    public void initialize() {
        videoPath = getArguments().getString("videoPath");
        resId = getArguments().getString("resId");
        isHint = getArguments().getBoolean("hint", false);
        mHandler.sendEmptyMessage(AAJ_KA_SAWAL_FOR_THIS_VIDEO);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializePlayer(videoPath);
    }

    private void initializePlayer(String videoPath) {
        videoView.setVideoPath(videoPath);
        videoView.setMediaController(player_control_view.getMediaControllerWrapper());
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                startTime = PD_Utility.getCurrentDateTime();
                player_control_view.show();
                videoDuration = videoView.getDuration();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!isHint) addScoreToDB();
                if (videoSawal == null)
                    ((Activity_VPlayer) getActivity()).onBackPressed();
                else
                    mHandler.sendEmptyMessage(SHOW_SAWAL);
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
            if (message.getMessage().equalsIgnoreCase(PD_Constant.VIDEO_PLAYER_BACK_PRESS)) {
                if (!isHint) addScoreToDB();
                ((Activity_VPlayer) getActivity()).onBackPressed();
            }
        }
    }

    @Background
    public void addScoreToDB() {
        String endTime = PD_Utility.getCurrentDateTime();
        Modal_Score modalScore = new Modal_Score();
        modalScore.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        if (PrathamApplication.isTablet)
            modalScore.setGroupID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group"));
        else
            modalScore.setStudentID(FastSave.getInstance().getString(PD_Constant.STUDENTID, "no_student"));
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
        BaseActivity.scoreDao.insert(modalScore);
    }
}
