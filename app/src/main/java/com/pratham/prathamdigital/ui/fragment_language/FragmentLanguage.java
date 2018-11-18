package com.pratham.prathamdigital.ui.fragment_language;

import android.animation.Animator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.ContentItemDecoration;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Language;
import com.pratham.prathamdigital.util.PD_Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentLanguage extends Fragment implements ContractLanguage {

    @BindView(R.id.root_language)
    RelativeLayout root_language;
    @BindView(R.id.rv_language)
    RecyclerView rv_language;
    AnimationDrawable animationDrawable;

    LanguageAdapter adapter;

    public static FragmentLanguage newInstance(int centerX, int centerY, int color) {
        Bundle args = new Bundle();
        args.putInt("cx", centerX);
        args.putInt("cy", centerY);
        args.putInt("color", color);
        FragmentLanguage fragment = new FragmentLanguage();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_language, container, false);
        if (getArguments() != null) {
            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                           int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                    int cx = getArguments().getInt("cx");
                    int cy = getArguments().getInt("cy");
                    int radius = (int) Math.hypot(right, bottom);
                    Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
                    reveal.setInterpolator(new DecelerateInterpolator(2f));
                    reveal.setDuration(1000);
                    reveal.start();
                }
            });
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        //start gradient animation
        animationDrawable = (AnimationDrawable) root_language.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);
        initializeAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        animationDrawable.start();
    }

    private void initializeAdapter() {
        if (adapter == null) {
            adapter = new LanguageAdapter(getActivity(), getLanguageList(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)), FragmentLanguage.this);
            rv_language.setLayoutManager(new GridLayoutManager(getActivity(), 5));
            rv_language.setHasFixedSize(true);
            rv_language.addItemDecoration(new ContentItemDecoration(PD_Constant.LANGUAGE, 10));
            rv_language.setAdapter(adapter);
//            rv_language.invalidate();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private ArrayList<Modal_Language> getLanguageList(String selectedLanguage) {
        ArrayList<Modal_Language> tempLang = new ArrayList<>();
        String[] languages = getActivity().getResources().getStringArray(R.array.languages);
        String[] main_languages = getActivity().getResources().getStringArray(R.array.main_languages);
        for (int i = 0; i < main_languages.length; i++) {
            Modal_Language modal_language = new Modal_Language();
            modal_language.setLanguage(languages[i]);
            modal_language.setMain_language(main_languages[i]);
            if (main_languages[i].equalsIgnoreCase(selectedLanguage))
                modal_language.setIsselected(true);
            else
                modal_language.setIsselected(false);
            tempLang.add(modal_language);
        }
        return tempLang;
    }

    @Override
    public void languageSelected(int position) {
        PrathamApplication.bubble_mp.start();
        Modal_Language language = adapter.getitem(position);
        FastSave.getInstance().saveString(PD_Constant.LANGUAGE, language.getMain_language());
        PrathamApplication.getInstance().setPradigiPath();
        adapter.updateLanguageItems(getLanguageList(language.getMain_language()));
    }
}
