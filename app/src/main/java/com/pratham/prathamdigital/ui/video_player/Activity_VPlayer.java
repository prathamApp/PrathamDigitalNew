package com.pratham.prathamdigital.ui.video_player;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.jakelee.vidsta.VidstaPlayer;
import uk.co.jakelee.vidsta.listeners.OnBackCalledListener;
import uk.co.jakelee.vidsta.listeners.VideoStateListeners;

public class Activity_VPlayer extends BaseActivity {

    @BindView(R.id.v_player)
    VidstaPlayer vidstaPlayer;

    private String myVideo;
    private String StartTime;
    private String resId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_vplayer);
        ButterKnife.bind(this);
        myVideo = getIntent().getStringExtra("videoPath");
        StartTime = PD_Utility.GetCurrentDateTime();
        resId = getIntent().getStringExtra("resId");

        initializePlayer();
    }

    private void initializePlayer() {
        vidstaPlayer.setVideoSource(Uri.parse(myVideo));
        vidstaPlayer.setOnVideoStartedListener(new VideoStateListeners.OnVideoStartedListener() {
            @Override
            public void OnVideoStarted(VidstaPlayer evp) {

            }
        });
        vidstaPlayer.setOnVideoFinishedListener(new VideoStateListeners.OnVideoFinishedListener() {
            @Override
            public void OnVideoFinished(VidstaPlayer evp) {

            }
        });
        vidstaPlayer.setOnBackCalled(new OnBackCalledListener() {
            @Override
            public void onBackCalled() {
                addScoreToDB();
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
        modalScore.setStartDateTime(PD_Utility.GetCurrentDateTime());
        modalScore.setEndDateTime(PD_Utility.GetCurrentDateTime());
        BaseActivity.scoreDao.insert(modalScore);
    }

}
