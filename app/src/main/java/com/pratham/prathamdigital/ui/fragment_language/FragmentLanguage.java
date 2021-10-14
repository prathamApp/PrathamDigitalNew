package com.pratham.prathamdigital.ui.fragment_language;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.isupatches.wisefy.WiseFy;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ReadContentDbFromSdCard;
import com.pratham.prathamdigital.custom.CircularRevelLayout;
import com.pratham.prathamdigital.custom.ContentItemDecoration;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.ApiResult;
import com.pratham.prathamdigital.interfaces.Interface_copying;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Language;
import com.pratham.prathamdigital.ui.fragment_content.FragmentContent_;
import com.pratham.prathamdigital.util.FileUtils;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.pratham.prathamdigital.async.PD_ApiRequest.downloadAajKaSawal;

@EFragment(R.layout.fragment_language)
public class FragmentLanguage extends Fragment implements ContractLanguage, CircularRevelLayout.CallBacks,
        Interface_copying, ApiResult.languageResult {

    private static final String TAG = FragmentLanguage.class.getSimpleName();

    @ViewById(R.id.circular_language_reveal)
    CircularRevelLayout circular_language_reveal;
    @ViewById(R.id.rv_language)
    RecyclerView rv_language;

    @ViewById(R.id.rl_network_error)
    RelativeLayout rl_network_error;


    private LanguageAdapter adapter;
    private int revealX;
    private int revealY;
    private boolean isAvatar = false;

    @Bean(ReadContentDbFromSdCard.class)
    ReadContentDbFromSdCard readContentDbFromSdCard;

    @Bean(PD_ApiRequest.class)
    PD_ApiRequest pd_apiRequest;

    ArrayList<String> lang = new ArrayList<>();
    ArrayList<String> langCode = new ArrayList<>();

    private File folder_file;

    private Dialog dialog;

    @AfterViews
    public void initialize() {
        circular_language_reveal.setListener(this);

        if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork() || PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            showDialog();
            pd_apiRequest.setLangApiResult(this);
            pd_apiRequest.getLanguageFromInternet(PD_Constant.BROWSE_INTERNET,
                    PD_Constant.URL.BROWSE_BY_ID + "2000001" + "&deviceid=" + PD_Utility.getDeviceID());
        } else {
            getLanguageFromAssets();
        }

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
    }

    @UiThread
    public void initializeAdapter() {
        try {
            adapter = new LanguageAdapter(getActivity(), getLanguageList(FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI)), FragmentLanguage.this);
            rv_language.setLayoutManager(new GridLayoutManager(getActivity(), 5));
            rv_language.setHasFixedSize(true);
            rv_language.addItemDecoration(new ContentItemDecoration(PD_Constant.LANGUAGE, 10));
            rv_language.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Modal_Language> getLanguageList(String selectedLanguage) {
        ArrayList<Modal_Language> tempLang = new ArrayList<>();
        for (int i = 0; i < lang.size(); i++) {
            Modal_Language modal_language = new Modal_Language();
            modal_language.setLanguage(lang.get(i));
            modal_language.setMain_language(lang.get(i));
            modal_language.setLanguage_id(langCode.get(i));
            if (lang.get(i).equalsIgnoreCase(selectedLanguage))
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

    @Override
    public void recievedLang(String header, String response) {
        dismissDialog();
        Log.e("url api response : ", response);
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
        }.getType();
        List<Modal_ContentDetail> languageList = gson.fromJson(response, listType);
        for (Modal_ContentDetail language : languageList) {
            lang.add(language.getContent_language());
            langCode.add(language.getNodeid());
        }
        if (languageList.size() > 0)
            initializeAdapter();
    }

    @Override
    public void recievedLangError(String header) {
        dismissDialog();
        getLanguageFromAssets();
    }

    public void getLanguageFromAssets() {
        ArrayList<String> dbLanguage = (ArrayList<String>) PrathamApplication.modalContentDao.getLanguagesFromDB();

        ArrayList<String> sdCardLanguage = new ArrayList<>();

        //getting language folder from sdCard and adding it to list
        ArrayList<String> sdPath = FileUtils.getExtSdCardPaths(getActivity());
        if (!sdPath.isEmpty()) {
            folder_file = new File(sdPath.get(0) + "/" + PD_Constant.PRADIGI_FOLDER);
            if (folder_file.exists()) {
                for (File f : Objects.requireNonNull(folder_file.listFiles())) {
                    if (f.isDirectory()) dbLanguage.add(f.getName());
                }
            }
        }
        Set<String> set = new HashSet<>(dbLanguage);
        dbLanguage.clear();
        dbLanguage.addAll(set);
        parseJson(dbLanguage);
    }

    public void parseJson(ArrayList<String> sdCardLanguage) {
        String jsonString = "";
        //reading language.json from asset folder
        try {
            InputStream is = Objects.requireNonNull(getActivity()).getAssets().open("language.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            //converting json into string
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Type listUserType = new TypeToken<List<Modal_Language>>() {
        }.getType();
        List<Modal_Language> languages = gson.fromJson(jsonString, listUserType);
        //adding language and language code to list
        for (int i = 0; i < sdCardLanguage.size(); i++) {
            for (int j = 0; j < languages.size(); j++) {
                if (sdCardLanguage.get(i).equalsIgnoreCase(languages.get(j).getLanguage())) {
                    lang.add(languages.get(j).getLanguage());
                    langCode.add(languages.get(j).getLanguage_id());
                }
            }
        }
        if (lang.size() == 0) {
            Toast.makeText(getActivity(), "No Data Found in SD Card.", Toast.LENGTH_SHORT).show();
            showNoConnectivity();
        }
    }

    @UiThread
    public void showDialog() {
        if (dialog == null) {
            dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.cat_loading_dialog);
        }
        dialog.show();
    }

    @UiThread
    public void dismissDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    @UiThread
    public void showNoConnectivity() {
        dismissDialog();
        rv_language.setVisibility(View.GONE);
        rl_network_error.setVisibility(View.VISIBLE);
    }
}
