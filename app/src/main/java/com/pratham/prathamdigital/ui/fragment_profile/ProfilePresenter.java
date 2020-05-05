package com.pratham.prathamdigital.ui.fragment_profile;

import android.content.Context;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_JoinScoreContentTable;
import com.pratham.prathamdigital.models.Modal_dateWiseResourceCount;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@EBean
public class ProfilePresenter implements ProfileContract.ProfilePresenter {

    Context context;
    ProfileContract.ProfileView profileView;

    public ProfilePresenter(Context context){
        this.context=context;
    }

    @Override
    public void setView(ProfileContract.ProfileView profileView) {
        this.profileView=profileView;
    }

    @Override
    public void loadTotalUsedResources() {
//        HashMap<String, List<Modal_JoinScoreContentTable>> courses = new HashMap<>();
        String gameCnt="", videoCnt="", pdfCnt="";
        List<Modal_JoinScoreContentTable> details;
        String studId;
        if (PrathamApplication.isTablet)
            studId = FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group");
        else
            studId = FastSave.getInstance().getString(PD_Constant.GROUPID, "no_student");
        details = PrathamApplication.scoreDao.getUsedResources(studId);
        for (Modal_JoinScoreContentTable mjsct : details) {
            switch (mjsct.getResourceType()) {
                case "Game":
                    gameCnt=mjsct.getTotalCount();
                    break;
                case "PDF":
                    pdfCnt=mjsct.getTotalCount();
                    break;
                case "Video":
                    videoCnt=mjsct.getTotalCount();
                    break;
            }
        }
        profileView.showTotalResourceCount(gameCnt,pdfCnt,videoCnt);
    }

    @Override
    public void loadDateWiseResources() {
        List<Modal_dateWiseResourceCount> dateWiseResourceCountList;
        List<String> endDateList = new ArrayList<>();
        String vcnt = "0", gcnt = "0", pcnt = "0";
        String studId;
        if (PrathamApplication.isTablet)
            studId = FastSave.getInstance().getString(PD_Constant.GROUPID, "no_group");
        else
            studId = FastSave.getInstance().getString(PD_Constant.GROUPID, "no_student");
        dateWiseResourceCountList = PrathamApplication.scoreDao.getDateWiseResourceCount(studId);
        Collections.sort(dateWiseResourceCountList, new StringDateComparator());
        for (Modal_dateWiseResourceCount dateWise : dateWiseResourceCountList) {
            endDateList.add(dateWise.getStartDate());
        }
        profileView.showDateWiseResourceCount(dateWiseResourceCountList,endDateList);
    }

    //class for sorting date in descending order
    class StringDateComparator implements Comparator<Modal_dateWiseResourceCount> {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        public int compare(Modal_dateWiseResourceCount lhs, Modal_dateWiseResourceCount rhs) {
            try {
                return dateFormat.parse(rhs.getStartDate()).compareTo(dateFormat.parse(lhs.getStartDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

}
