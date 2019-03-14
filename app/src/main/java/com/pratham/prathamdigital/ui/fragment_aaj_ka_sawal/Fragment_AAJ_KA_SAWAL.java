package com.pratham.prathamdigital.ui.fragment_aaj_ka_sawal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.custom.view_animator.ViewAnimator;
import com.pratham.prathamdigital.models.Modal_AajKaSawal;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent_;
import com.pratham.prathamdigital.ui.video_player.Activity_VPlayer_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Random;

@EFragment(R.layout.frag_aaj_ka_sawal)
public class Fragment_AAJ_KA_SAWAL extends Fragment {
    private static final int SHOW_HOME_FRAGMENT = 1;
    private static final int CHECK_VIDEO = 2;
    private static final int HIGHLIGHT_SELECTED_ANSWER = 3;
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
    private boolean hintVideoViewed = false;
    private Modal_ContentDetail vidContent = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_HOME_FRAGMENT:
                    Bundle bundle = new Bundle();
                    bundle.putInt(PD_Constant.REVEALX, 0);
                    bundle.putInt(PD_Constant.REVEALY, 0);
                    PD_Utility.showFragment(getActivity(), new FragmentContent_(), R.id.main_frame,
                            bundle, FragmentContent_.class.getSimpleName());
                    break;
                case CHECK_VIDEO:
                    vidContent = BaseActivity.modalContentDao.getContent(
                            selectedAajKaSawal.getResourceId(), selectedAajKaSawal.getProgramLanguage());
                    if (vidContent == null) aaj_play_video.setVisibility(View.GONE);
                    else aaj_play_video.setVisibility(View.VISIBLE);
                    break;
                case HIGHLIGHT_SELECTED_ANSWER:
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
            }
        }
    };

    @AfterViews
    public void init() {
        startTime = PD_Utility.getCurrentDateTime();
        if (getArguments() != null)
            parseAksJSON(getArguments().getString(PD_Constant.AKS_FILE_PATH));
        else
            mHandler.sendEmptyMessage(SHOW_HOME_FRAGMENT);
    }

    @Background
    public void parseAksJSON(String filePath) {
        String aks = PD_Utility.readJSONFile(filePath);
        Modal_AajKaSawal rootAajKaSawal = new Gson().fromJson(aks, Modal_AajKaSawal.class);
        //get random subject from list
//        if (aajKaSawal.getNodelist().size() > 1)
        Modal_AajKaSawal subjectOfSawal = new Modal_AajKaSawal();
        subjectOfSawal = rootAajKaSawal.getNodelist().get(new Random().nextInt(rootAajKaSawal.getNodelist().size()));
//        else subjectOfSawal = aajKaSawal.getNodelist().get(0);
        //get random question from subject list
//        if (subjectOfSawal.getNodelist().size() > 1)
        Modal_AajKaSawal aajtKaSawal = new Modal_AajKaSawal();
        aajtKaSawal = subjectOfSawal.getNodelist().get(new Random().nextInt(subjectOfSawal.getNodelist().size()));
//        else subjectOfSawal = aajKaSawal.getNodelist().get(0);
        showQuestion(aajtKaSawal);
    }

    @UiThread
    public void showQuestion(Modal_AajKaSawal aajtKaSawal) {
        selectedAajKaSawal = aajtKaSawal;
        mHandler.sendEmptyMessage(CHECK_VIDEO);
        aaj_txt_question.setText(aajtKaSawal.getQuestion());
        txt_option_one.setText(aajtKaSawal.getOption1());
        txt_option_two.setText(aajtKaSawal.getOption2());
        txt_option_three.setText(aajtKaSawal.getOption3());
        txt_option_four.setText(aajtKaSawal.getOption4());
    }

    @Click(R.id.aaj_btn_skip)
    public void setskip() {
        PrathamApplication.bubble_mp.start();
        addScoreToDB(0);
        mHandler.sendEmptyMessage(SHOW_HOME_FRAGMENT);
    }

    @Click(R.id.aaj_okay)
    public void setOkay() {
        PrathamApplication.bubble_mp.start();
        if (selectedAnswer.equalsIgnoreCase(selectedAajKaSawal.getAnswer())) {
//            addScoreToDB(10);
//            mHandler.sendEmptyMessage(SHOW_HOME_FRAGMENT);
        } else {
            mHandler.sendEmptyMessage(HIGHLIGHT_SELECTED_ANSWER);
        }
    }

    @Click(R.id.aaj_play_video)
    public void setPlayVideo() {
        hintVideoViewed = true;
        String f_path;
        if (vidContent.isOnSDCard())
            f_path = PrathamApplication.contentSDPath + "/PrathamVideo/" + vidContent.getResourcepath();
        else
            f_path = PrathamApplication.pradigiPath + "/PrathamVideo/" + vidContent.getResourcepath();
        Intent intent = new Intent(getActivity(), Activity_VPlayer_.class);
        intent.putExtra("videoPath", f_path);
        intent.putExtra("videoTitle", vidContent.getNodetitle());
        intent.putExtra("resId", vidContent.getResourceid());
        intent.putExtra("hint", true);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.pop_in, R.anim.nothing);
    }

    @Background
    public void addScoreToDB(int ttlScore) {
        String endTime = PD_Utility.getCurrentDateTime();
        Modal_Score modalScore = new Modal_Score();
        modalScore.setSessionID(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""));
        if (PrathamApplication.isTablet)
            modalScore.setGroupID(FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group"));
        else
            modalScore.setStudentID(FastSave.getInstance().getString(PD_Constant.STUDENTID, "no_student"));
        modalScore.setDeviceID(PD_Utility.getDeviceID());
        modalScore.setResourceID(selectedAajKaSawal.getResourceId());
        modalScore.setQuestionId(Integer.parseInt(selectedAajKaSawal.getQueId()));
        modalScore.setScoredMarks(ttlScore);
        modalScore.setTotalMarks(10);
        modalScore.setStartDateTime(startTime);
        modalScore.setEndDateTime(endTime);
        modalScore.setLevel((hintVideoViewed) ? 991 : 990);
        modalScore.setLabel("_");
        modalScore.setSentFlag(0);
        BaseActivity.scoreDao.insert(modalScore);
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
}
