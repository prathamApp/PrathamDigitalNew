package com.pratham.prathamdigital.ui.fragment_profile;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_JoinScoreContentTable;
import com.pratham.prathamdigital.models.Modal_ProfileDetails;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_dateWiseResourceCount;
import com.pratham.prathamdigital.ui.attendance_activity.AttendanceActivity_;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar;
import com.pratham.prathamdigital.ui.avatar.Fragment_SelectAvatar_;
import com.pratham.prathamdigital.ui.dashboard.ActivityMain;
import com.pratham.prathamdigital.ui.fragment_child_attendance.FragmentChildAttendance;
import com.pratham.prathamdigital.ui.fragment_child_attendance.FragmentChildAttendance_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.studentDao;

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
    @ViewById(R.id.iv_editProfile)
    ImageView iv_editProfile;

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
        String groupName = studentDao.getStudGroupName(FastSave.getInstance().getString(PD_Constant.GROUPID,"no_student"));
        try{
            if(groupName.equals("SmartPhone"))
                iv_editProfile.setVisibility(View.VISIBLE);
            else
                iv_editProfile.setVisibility(View.GONE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @UiThread
    public void initializeAdapter() {
        if (profileAdapter == null) {
            profileAdapter = new ProfileAdapter(getActivity(), detailsList);
            rv_activityDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
            rv_activityDetail.setHasFixedSize(true);
            rv_activityDetail.setItemViewCacheSize(10);
            rv_activityDetail.setItemAnimator(new DefaultItemAnimator());
            rv_activityDetail.setAdapter(profileAdapter);
        }
        else {
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
        detailsList.clear();
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
            vcnt = "0";
            gcnt = "0";
            pcnt = "0";
        }
    }

    @Click(R.id.profileImage_lottie)
    public void changeProfile(){
//        Toast.makeText(getActivity(), FastSave.getInstance().getString(PD_Constant.GROUPID,"no_student"), Toast.LENGTH_SHORT).show();
        Intent cpintent = new Intent(getActivity(), AttendanceActivity_.class);
        cpintent.putExtra(PD_Constant.STUDENT_ADDED, true);
        startActivity(cpintent);
        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        getActivity().finishAfterTransition();
    }

    @Click(R.id.iv_editProfile)
    public void editProfile(){
        Bundle bundleProf = new Bundle();
        bundleProf.putInt(PD_Constant.REVEALX, 0);
        bundleProf.putInt(PD_Constant.REVEALY, 0);
        bundleProf.putString(PD_Constant.EDIT_PROFILE,"edit_profile");
        PD_Utility.showFragment(getActivity(), new Fragment_SelectAvatar_(), R.id.main_frame,
                bundleProf, Fragment_SelectAvatar.class.getSimpleName());
    }

    //method mainly used to refresh the recycler view, while coming back from editprofile
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rv_activityDetail.setAdapter(null);
        profileAdapter = null;
        rv_activityDetail = null;
    }
}