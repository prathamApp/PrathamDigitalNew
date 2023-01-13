package com.pratham.prathamdigital.ui.content_player.fragment_aaj_ka_sawal;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.custom.view_animator.ViewAnimator;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_AajKaSawal;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.scoreDao;

@EFragment(R.layout.frag_aaj_ka_sawal)
public class Fragment_AAJ_KA_SAWAL extends Fragment {
    private static final int HIGHLIGHT_SELECTED_ANSWER = 3;
    private static final int CLOSE_CONTENT_PLAYER_ACTIVITY = 4;
    @ViewById(R.id.aks_reveal)
    CircularRevelLayout aks_reveal;
    @ViewById(R.id.aaj_txt_question)
    TextView aaj_txt_question;
    @ViewById(R.id.card_option_one)
    MaterialCardView card_option_one;
    @ViewById(R.id.card_option_two)
    MaterialCardView card_option_two;
    @ViewById(R.id.card_option_three)
    MaterialCardView card_option_three;
    @ViewById(R.id.card_option_four)
    MaterialCardView card_option_four;
    @ViewById(R.id.txt_option_one)
    TextView txt_option_one;
    @ViewById(R.id.txt_option_two)
    TextView txt_option_two;
    @ViewById(R.id.txt_option_three)
    TextView txt_option_three;
    @ViewById(R.id.txt_option_four)
    TextView txt_option_four;
    @ViewById(R.id.aaj_play_video)
    ImageView aaj_play_video;

    private String selectedAnswer = "";
    private Modal_AajKaSawal selectedAajKaSawal;
    private String startTime;
    private final MediaPlayer.OnCompletionListener applauseCompletionListener = mp -> checkIfCourseAndPlayNext();
    private MediaPlayer wrong_mp;
    private MediaPlayer correct_mp;
    private boolean scoreAdded = false;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIGHLIGHT_SELECTED_ANSWER:
                    wrong_mp.start();
                    if (txt_option_one.getText().toString().equalsIgnoreCase(selectedAajKaSawal.getAnswer()))
                        ViewAnimator.animate(card_option_one)
                                .flash()
                                .duration(350)
                                .start();
                    else if (txt_option_two.getText().toString().equalsIgnoreCase(selectedAajKaSawal.getAnswer()))
                        ViewAnimator.animate(card_option_two)
                                .flash()
                                .duration(350)
                                .start();
                    else if (txt_option_three.getText().toString().equalsIgnoreCase(selectedAajKaSawal.getAnswer()))
                        ViewAnimator.animate(card_option_three)
                                .flash()
                                .duration(350)
                                .start();
                    else if (txt_option_four.getText().toString().equalsIgnoreCase(selectedAajKaSawal.getAnswer()))
                        ViewAnimator.animate(card_option_four)
                                .flash()
                                .duration(350)
                                .start();
                    break;
                case CLOSE_CONTENT_PLAYER_ACTIVITY:
                    EventMessage eventMessage1 = new EventMessage();
                    eventMessage1.setMessage(PD_Constant.CLOSE_CONTENT_ACTIVITY);
                    EventBus.getDefault().post(eventMessage1);
                    break;
            }
        }
    };
    private String resid;

    @AfterViews
    public void init() {
//        wrong_mp = MediaPlayer.create(getActivity(), R.raw.wrong_buzzer); //removed this audio files for reducing apk size
//        correct_mp = MediaPlayer.create(getActivity(), R.raw.applause);
        wrong_mp = MediaPlayer.create(getActivity(), R.raw.bubble_pop);
        correct_mp = MediaPlayer.create(getActivity(), R.raw.bubble_pop);
        correct_mp.setOnCompletionListener(applauseCompletionListener);
        wrong_mp.setOnCompletionListener(applauseCompletionListener);
        startTime = PD_Utility.getCurrentDateTime();
        if (getArguments() != null) {
            showQuestion(Objects.requireNonNull(getArguments().getParcelable(PD_Constant.AKS_QUESTION)));
            resid = getArguments().getString(PD_Constant.RESOURSE_ID);
        } else {
            mHandler.sendEmptyMessage(CLOSE_CONTENT_PLAYER_ACTIVITY);
        }
        aks_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int[] outLocation = new int[2];
                aaj_play_video.getLocationOnScreen(outLocation);
                outLocation[0] += aaj_play_video.getWidth() / 2;
                aks_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                aks_reveal.revealFrom(outLocation[0], outLocation[1], 0);
                return true;
            }
        });
    }

    @UiThread
    public void showQuestion(Modal_AajKaSawal aajKaSawal) {
        selectedAajKaSawal = aajKaSawal;
        aaj_txt_question.setText(aajKaSawal.getQuestion());
        txt_option_one.setText(aajKaSawal.getOption1());
        txt_option_two.setText(aajKaSawal.getOption2());
        txt_option_three.setText(aajKaSawal.getOption3());
        txt_option_four.setText(aajKaSawal.getOption4());
    }

    @Click(R.id.aaj_btn_skip)
    public void setskip() {
        PrathamApplication.bubble_mp.start();
        addScoreToDB(0, true, false);
        checkIfCourseAndPlayNext();
    }

    private void checkIfCourseAndPlayNext() {
        if (Objects.requireNonNull(getArguments()).getBoolean("isCourse")) {
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.SHOW_NEXT_CONTENT);
            message.setDownloadId(resid);
            EventBus.getDefault().post(message);
        } else
            mHandler.sendEmptyMessage(CLOSE_CONTENT_PLAYER_ACTIVITY);
    }

    @Click(R.id.aaj_okay)
    public void setOkay() {
        PrathamApplication.bubble_mp.start();
        if (selectedAnswer.equalsIgnoreCase(selectedAajKaSawal.getAnswer())) {
            correct_mp.start();
            addScoreToDB(10, false, false);
        } else {
            addScoreToDB(0, false, false);
            mHandler.sendEmptyMessage(HIGHLIGHT_SELECTED_ANSWER);
        }
    }

    @Click(R.id.aaj_play_video)
    public void setPlayVideo() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void messageReceived(EventMessage message) {
        if (message != null) {
            if (message.getMessage().equalsIgnoreCase(PD_Constant.CLOSE_CONTENT_PLAYER)) {
                if (!scoreAdded) addScoreToDB(0, true, true);
                if (Objects.requireNonNull(getArguments()).getBoolean("isCourse")) {
                    EventMessage message1 = new EventMessage();
                    message1.setMessage(PD_Constant.SHOW_COURSE_DETAIL);
                    EventBus.getDefault().post(message1);
                } else {
                    mHandler.sendEmptyMessage(CLOSE_CONTENT_PLAYER_ACTIVITY);
                }
            }
        }
    }

    @Click(R.id.card_option_one)
    public void setOption1() {
        card_option_one.setCardBackgroundColor(getResources().getColor(R.color.aks_selected));
        card_option_two.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        card_option_three.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        card_option_four.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        selectedAnswer = txt_option_one.getText().toString();
    }

    @Click(R.id.card_option_two)
    public void setOption2() {
        card_option_one.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        card_option_two.setCardBackgroundColor(getResources().getColor(R.color.aks_selected));
        card_option_three.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        card_option_four.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        selectedAnswer = txt_option_two.getText().toString();
    }

    @Click(R.id.card_option_three)
    public void setOption3() {
        card_option_one.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        card_option_two.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        card_option_three.setCardBackgroundColor(getResources().getColor(R.color.aks_selected));
        card_option_four.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        selectedAnswer = txt_option_three.getText().toString();
    }

    @Click(R.id.card_option_four)
    public void setOption4() {
        card_option_one.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        card_option_two.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        card_option_three.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        card_option_four.setCardBackgroundColor(getResources().getColor(R.color.aks_selected));
        selectedAnswer = txt_option_four.getText().toString();
    }

    @Background
    public void addScoreToDB(int ttlScore, boolean isSkipped, boolean isClosed) {
        scoreAdded = true;
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
        modalScore.setResourceID(selectedAajKaSawal.getResourceId());
        modalScore.setQuestionId(Integer.parseInt(selectedAajKaSawal.getQueId()));
        modalScore.setScoredMarks(ttlScore);
        modalScore.setTotalMarks(10);
        modalScore.setStartDateTime(startTime);
        modalScore.setEndDateTime(endTime);
        modalScore.setLevel(990);
        if (isClosed)
            modalScore.setLabel("Video Closed. Sawal was not attempted");
        else
            modalScore.setLabel(isSkipped ? "Skipped" : "Video not Viewed");
        modalScore.setSentFlag(0);
        scoreDao.insert(modalScore);
    }
}
