package com.pratham.prathamdigital.ui.fragment_language;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.ContentItemDecoration;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Language;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentLanguage extends Fragment implements ContractLanguage, CircularRevelLayout.CallBacks {

    private static final String TAG = FragmentLanguage.class.getSimpleName();
    @BindView(R.id.circular_language_reveal)
    CircularRevelLayout circular_language_reveal;
    @BindView(R.id.rv_language)
    RecyclerView rv_language;

    LanguageAdapter adapter;
    private int revealX;
    private int revealY;

//    public static FragmentLanguage newInstance(int centerX, int centerY, int color) {
//        Bundle args = new Bundle();
//        args.putInt("cx", centerX);
//        args.putInt("cy", centerY);
//        args.putInt("color", color);
//        FragmentLanguage fragment = new FragmentLanguage();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_language, container, false);
        ButterKnife.bind(this, rootView);
        circular_language_reveal.setListener(this);
        if (getArguments() != null) {
            revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            circular_language_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    circular_language_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    circular_language_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initializeAdapter() {
        if (adapter == null) {
            adapter = new LanguageAdapter(getActivity(), getLanguageList(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)), FragmentLanguage.this);
            rv_language.setLayoutManager(new GridLayoutManager(getActivity(), 5));
            rv_language.setHasFixedSize(true);
            rv_language.addItemDecoration(new ContentItemDecoration(PD_Constant.LANGUAGE, 10));
            rv_language.setAdapter(adapter);
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
        circular_language_reveal.unReveal();
    }

    @Override
    public void onRevealed() {
        initializeAdapter();
    }

    @Override
    public void onUnRevealed() {
        Bundle bundle = new Bundle();
        bundle.putInt(PD_Constant.REVEALX, 0);
        bundle.putInt(PD_Constant.REVEALY, 0);
        PD_Utility.showFragment(getActivity(), new FragmentContent(), R.id.main_frame,
                bundle, FragmentContent.class.getSimpleName());
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onMainBackPressed(final String pressed) {
        Log.d(TAG, "onMainBackPressed:");
        if (pressed.equalsIgnoreCase(PD_Constant.CONTENT_BACK)) {
            if (circular_language_reveal != null)
                circular_language_reveal.unReveal();
            else
                getActivity().getSupportFragmentManager().popBackStack();
        }
    }

}
