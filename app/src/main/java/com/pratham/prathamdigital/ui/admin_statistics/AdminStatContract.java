package com.pratham.prathamdigital.ui.admin_statistics;

import com.pratham.prathamdigital.models.Modal_ResourcePlayedByGroups;
import com.pratham.prathamdigital.models.Modal_TotalDaysGroupsPlayed;

import java.util.HashMap;
import java.util.List;

/**
 * Created by PEF on 19/11/2018.
 */

public interface AdminStatContract {
    interface StatView {
        void showDeviceDays(int days);

        void showTotalDaysPlayedByGroups(List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds);

        void showResourcesPlayedByGroups(HashMap<String, List<Modal_ResourcePlayedByGroups>> modal_resourcePlayedByGroups);
    }

    interface StatPresenter {
        void getDeviceActiveDays();

        void setView(Fragment_AdminStatistics fragment_adminStatistics);

        void getRecourcesPlayedByGroups(String groupId);
    }
}
