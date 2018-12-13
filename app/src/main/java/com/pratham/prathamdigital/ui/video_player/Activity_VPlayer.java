package com.pratham.prathamdigital.ui.video_player;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.VideoView;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.media_controller.PlayerControlView;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.services.BackgroundSoundService;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_VPlayer extends BaseActivity {

    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.player_control_view)
    PlayerControlView player_control_view;

    private String myVideo;
    private String startTime = "no_resource";
    private String resId;
    private long videoDuration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_vplayer);
        ButterKnife.bind(this);
        myVideo = getIntent().getStringExtra("videoPath");
        resId = getIntent().getStringExtra("resId");
        if (PD_Utility.isServiceRunning(BackgroundSoundService.class, this))
            stopService(new Intent(this, BackgroundSoundService.class));
        initializePlayer(myVideo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PD_Utility.isServiceRunning(BackgroundSoundService.class, this))
            stopService(new Intent(this, BackgroundSoundService.class));
    }

    private void initializePlayer(String myVideo) {
        videoView.setVideoPath(myVideo);
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
    }

    @OnClick(R.id.close_video)
    public void setClose_video() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        this.finish();
        overridePendingTransition(R.anim.nothing, R.anim.pop_out);
    }

    @Override
    protected void onDestroy() {
        addScoreToDB();
        super.onDestroy();
    }

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
