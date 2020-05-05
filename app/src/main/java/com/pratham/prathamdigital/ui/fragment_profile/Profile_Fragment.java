package com.pratham.prathamdigital.ui.fragment_profile;


import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_JoinScoreContentTable;
import com.pratham.prathamdigital.models.Modal_ProfileDetails;
import com.pratham.prathamdigital.models.Modal_dateWiseResourceCount;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@EFragment(R.layout.fragment_profile)
public class Profile_Fragment extends Fragment implements ProfileContract.ProfileView {
    private static final String TAG = Profile_Fragment.class.getSimpleName();
    private List<Modal_ProfileDetails> detailsList = new ArrayList<>();

    @ViewById(R.id.profileImage_lottie)
    LottieAnimationView profileImage_lottie;
    @ViewById(R.id.profile_name)
    TextView profile_name;
    @ViewById(R.id.total_videocount)
    TextView total_videoCount;
    @ViewById(R.id.total_gamecount)
    TextView total_gameCount;
    @ViewById(R.id.total_pdfcount)
    TextView total_pdfCount;
    @ViewById(R.id.rv_activityDetail)
    RecyclerView rv_activityDetail;

    @Bean(ProfilePresenter.class)
    ProfilePresenter profilePresenter;

    private ProfileAdapter profileAdapter;

    @AfterViews
    public void initialize() {
        profilePresenter.setView(this);
        total_gameCount.setText("0");
        total_videoCount.setText("0");
        total_pdfCount.setText("0");
        profileNameImage();
        profilePresenter.loadTotalUsedResources();
        profilePresenter.loadDateWiseResources();
        initializeAdapter();
    }

    @UiThread
    public void initializeAdapter() {
        if (profileAdapter == null) {
            profileAdapter = new ProfileAdapter(getActivity(), detailsList);
            rv_activityDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv_activityDetail.setHasFixedSize(true);
            rv_activityDetail.setItemAnimator(new DefaultItemAnimator());
            rv_activityDetail.setAdapter(profileAdapter);
            //          recyclerData();
        } else {
            profileAdapter.notifyDataSetChanged();
        }
    }

    @UiThread
    public void profileNameImage() {
        profileImage_lottie.setAnimation(FastSave.getInstance().getString(PD_Constant.AVATAR,
                "avatars/dino_dance.json"));
        profile_name.setText(FastSave.getInstance().getString(PD_Constant.PROFILE_NAME, "No Name"));
    }

    @UiThread
    @Override
    public void showTotalResourceCount(String gcnt, String pcnt, String vcnt) {
        if(gcnt.equals(""))
            total_gameCount.setText("0");
        else
        total_gameCount.setText(gcnt);

        if(pcnt.equals(""))
            total_pdfCount.setText("0");
        else
            total_pdfCount.setText(pcnt);

        if(vcnt.equals(""))
            total_videoCount.setText("0");
        else
            total_videoCount.setText(vcnt);
    }

    @UiThread
    @Override
    public void showDateWiseResourceCount(List<Modal_dateWiseResourceCount> dateWiseResourceCountList, List<String> endDateList) {
        if(dateWiseResourceCountList.size()==0){
            Modal_ProfileDetails details = new Modal_ProfileDetails("date", "0", "0", "0");
            detailsList.add(details);
        }
        String vcnt = "0", gcnt = "0", pcnt = "0";
        List<String> dateString = new ArrayList<String>(new LinkedHashSet<String>(endDateList));
        for (int i = 0; i < dateString.size(); i++) {
            String date = dateString.get(i);
            for (Modal_dateWiseResourceCount dateWise : dateWiseResourceCountList) {
                if (date.equals(dateWise.getStartDate())) {
                    switch (dateWise.getResourceType()) {
                        case "Game":
                            gcnt = dateWise.getCount();
                            break;
                        case "PDF":
                            pcnt = dateWise.getCount();
                            break;
                        case "Video":
                            vcnt = dateWise.getCount();
                            break;
                    }
                    date = dateWise.getStartDate();
                }
            }
            Modal_ProfileDetails details = new Modal_ProfileDetails(date.substring(0, 5), vcnt, gcnt, pcnt);
            detailsList.add(details);
//            Log.e("Op",date+" | "+"vcnt : "+vcnt+"gcnt : "+gcnt+"pcnt : "+pcnt);
            vcnt = "0";
            gcnt = "0";
            pcnt = "0";
        }
    }
}