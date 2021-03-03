package com.pratham.prathamdigital.ui.fragment_language;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.ReadContentDbFromSdCard;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.ContentItemDecoration;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.Interface_copying;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_Language;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Objects;

import static com.pratham.prathamdigital.async.PD_ApiRequest.downloadAajKaSawal;

@EFragment(R.layout.fragment_language)
public class FragmentLanguage extends Fragment implements ContractLanguage, CircularRevelLayout.CallBacks,
        Interface_copying {

    private static final String TAG = FragmentLanguage.class.getSimpleName();
    @ViewById(R.id.circular_language_reveal)
    CircularRevelLayout circular_language_reveal;
    @ViewById(R.id.rv_language)
    RecyclerView rv_language;

    private LanguageAdapter adapter;
    private int revealX;
    private int revealY;
    private boolean isAvatar = false;

    @Bean(ReadContentDbFromSdCard.class)
    ReadContentDbFromSdCard readContentDbFromSdCard;

    @AfterViews
    public void initialize() {
        circular_language_reveal.setListener(this);
        if (getArguments() != null) {
            revealX = getArguments().getInt(PD_Constant.REVEALX, 0);
            revealY = getArguments().getInt(PD_Constant.REVEALY, 0);
            isAvatar = getArguments().getBoolean(PD_Constant.IS_AVATAR, false);
            circular_language_reveal.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    circular_language_reveal.getViewTreeObserver().removeOnPreDrawListener(this);
                    circular_language_reveal.revealFrom(revealX, revealY, 0);
                    return true;
                }
            });
        }
        initializeAdapter();
    }

    @UiThread
    public void initializeAdapter() {
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
        String[] languages = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.languages);
        String[] main_languages = getActivity().getResources().getStringArray(R.array.main_languages);
        String[] languages_id = getActivity().getResources().getStringArray(R.array.language_code);
        for (int i = 0; i < main_languages.length; i++) {
            Modal_Language modal_language = new Modal_Language();
            modal_language.setLanguage(languages[i]);
            modal_language.setMain_language(main_languages[i]);
            modal_language.setLanguage_id(languages_id[i]);
            if (main_languages[i].equalsIgnoreCase(selectedLanguage))
                modal_language.setIsselected(true);
            else
                modal_language.setIsselected(false);
            tempLang.add(modal_language);
        }
        return tempLang;
    }

    @UiThread
    @Override
    public void languageSelected(int position) {
        PrathamApplication.bubble_mp.start();
        Modal_Language language = adapter.getitem(position);
        FastSave.getInstance().saveString(PD_Constant.LANGUAGE_CODE, language.getLanguage_id());
        FastSave.getInstance().saveString(PD_Constant.LANGUAGE, language.getMain_language());
        PrathamApplication.getInstance().setPradigiPath();
        adapter.updateLanguageItems(getLanguageList(language.getMain_language()));
        readContentDbFromSdCard.doInBackground(FragmentLanguage.this);
        String filename = "AajKaSawal_" + language.getMain_language() + ".json";
        String aksUrl = PD_Constant.URL.AAJ_KA_SAWAL_URL.toString() + filename;
        downloadAajKaSawal(aksUrl, filename);
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.LANGUAGE);
        EventBus.getDefault().post(message);
    }

    @Override
    public void onRevealed() {
        initializeAdapter();
    }

    @UiThread
    @Override
    public void onUnRevealed() {
        if (isAvatar) {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(PD_Constant.REVEALX, 0);
            bundle.putInt(PD_Constant.REVEALY, 0);
            PD_Utility.showFragment(getActivity(), new FragmentContent_(), R.id.main_frame,
                    bundle, FragmentContent_.class.getSimpleName());
            Fragment fragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentByTag(TAG);
            if (fragment != null)
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainBackPressed(final String pressed) {
        Log.d(TAG, "onMainBackPressed:");
        if (pressed.equalsIgnoreCase(PD_Constant.CONTENT_BACK)) {
            if (circular_language_reveal != null)
                circular_language_reveal.unReveal();
            else
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void copyingExisting() {

    }

    @UiThread
    @Override
    public void successCopyingExisting(String path) {
        PrathamApplication.getInstance().setExistingSDContentPath(path);
        circular_language_reveal.unReveal();
    }

    @UiThread
    @Override
    public void failedCopyingExisting() {
        circular_language_reveal.unReveal();
    }
}
