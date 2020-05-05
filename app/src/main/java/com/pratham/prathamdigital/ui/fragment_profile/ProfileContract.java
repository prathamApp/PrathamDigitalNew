package com.pratham.prathamdigital.ui.fragment_profile;

import com.pratham.prathamdigital.models.Modal_dateWiseResourceCount;

import java.util.List;

public interface ProfileContract {

    interface ProfileView{
        void showTotalResourceCount(String gcnt, String pcnt, String vcnt);
        void showDateWiseResourceCount(List<Modal_dateWiseResourceCount> dateWiseResourceCountList, List<String> endDateList);
    }

    interface ProfilePresenter {
        void setView(ProfileContract.ProfileView profileView);
        void loadTotalUsedResources();
        void loadDateWiseResources();
    }
}
