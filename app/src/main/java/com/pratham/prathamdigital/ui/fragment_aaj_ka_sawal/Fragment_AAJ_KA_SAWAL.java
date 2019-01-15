package com.pratham.prathamdigital.ui.fragment_aaj_ka_sawal;

import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.pratham.prathamdigital.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.frag_aaj_ka_sawal)
public class Fragment_AAJ_KA_SAWAL extends Fragment {
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

    @AfterViews
    public void init() {

    }

    @Click(R.id.aaj_btn_skip)
    public void setskip() {
    }

    @Click(R.id.aaj_okay_skip)
    public void setOkay() {

    }
}
