package com.pratham.prathamdigital.ui.video_player;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.VideoView;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.media_controller.PlayerControlView;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_VPlayer extends BaseActivity {

    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.player_control_view)
    PlayerControlView player_control_view;

    private String myVideo;
    private String StartTime;
    private String resId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_vplayer);
        ButterKnife.bind(this);
        myVideo = getIntent().getStringExtra("videoPath");
        StartTime = PD_Utility.getCurrentDateTime();
        resId = getIntent().getStringExtra("resId");

        initializePlayer(myVideo);
    }

    private void initializePlayer(String myVideo) {
        videoView.setVideoPath(myVideo);
        videoView.setMediaController(player_control_view.getMediaControllerWrapper());
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player_control_view.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Runtime rs = Runtime.getRuntime();
        rs.freeMemory();
        rs.gc();
        rs.freeMemory();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        addScoreToDB();
        super.onDestroy();
    }

    public void addScoreToDB() {
        Modal_Score modalScore = new Modal_Score();
        modalScore.setSessionID("");
        modalScore.setStudentID("");
        modalScore.setResourceID("");
        modalScore.setQuestionId(0);
        modalScore.setScoredMarks(0);
        modalScore.setTotalMarks(0);
        // Unique Device ID
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        modalScore.setDeviceID(deviceId.equals(null) ? "0000" : deviceId);
        modalScore.setStartDateTime(PD_Utility.getCurrentDateTime());
        modalScore.setEndDateTime(PD_Utility.getCurrentDateTime());
        BaseActivity.scoreDao.insert(modalScore);
    }
}
