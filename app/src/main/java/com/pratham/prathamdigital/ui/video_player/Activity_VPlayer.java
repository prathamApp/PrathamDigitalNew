package com.pratham.prathamdigital.ui.video_player;

import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.View;
import android.widget.VideoView;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.media_controller.PlayerControlView;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_generic_vplayer)
public class Activity_VPlayer extends BaseActivity {

    @ViewById(R.id.videoView)
    VideoView videoView;
    @ViewById(R.id.player_control_view)
    PlayerControlView player_control_view;

    private String myVideo;
    private String startTime = "no_resource";
    private String resId;
    private long videoDuration = 0;
    BlurPopupWindow nextDialog;

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_vplayer);
        ButterKnife.bind(this);
        myVideo = getIntent().getStringExtra("videoPath");
        resId = getIntent().getStringExtra("resId");
//        PrathamApplication.getInstance().toggleBackgroundMusic(false);
        initializePlayer(myVideo);
    }
*/

    @AfterViews
    public void initialize() {
        myVideo = getIntent().getStringExtra("videoPath");
        resId = getIntent().getStringExtra("resId");
        initializePlayer(myVideo);
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

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
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                addScoreToDB();
                showNextVideoDialog();
            }
        });
    }

    @UiThread
    public void showNextVideoDialog() {
        nextDialog = new BlurPopupWindow.Builder(Activity_VPlayer.this)
                .setContentView(R.layout.dialog_next_content)
                .setGravity(Gravity.CENTER)
                .setScaleRatio(0.2f)
                .bindClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextDialog.dismiss();
                        finish();
                    }
                }, R.id.txt_close)
                .setScaleRatio(0.2f)
                .setBlurRadius(8)
                .setTintColor(0x30000000)
                .build();
        nextDialog.show();
    }

    @Click(R.id.close_video)
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
