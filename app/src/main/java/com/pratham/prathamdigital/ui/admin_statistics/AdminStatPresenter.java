package com.pratham.prathamdigital.ui.admin_statistics;

import android.content.Context;
import android.util.Log;

import com.pratham.prathamdigital.models.Modal_ResourcePlayedByGroups;
import com.pratham.prathamdigital.models.Modal_TotalDaysGroupsPlayed;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pratham.prathamdigital.PrathamApplication.scoreDao;

/**
 * Created by PEF on 19/11/2018.
 */
@EBean
public class AdminStatPresenter implements AdminStatContract.StatPresenter {

    AdminStatContract.StatView statView;

    public AdminStatPresenter(Context context) {
        Context context1 = context;
    }

    @Override
    public void setView(Fragment_AdminStatistics fragment_adminStatistics) {
        this.statView = (AdminStatContract.StatView) fragment_adminStatistics;
        getDeviceActiveDays();
        getActiveGroups();
    }

    @Background
    public void getActiveGroups() {
        List<Modal_TotalDaysGroupsPlayed> modal_totalDaysGroupsPlayeds = scoreDao.getTotalDaysGroupsPlayed();
        Log.d("getActiveGroups: ", modal_totalDaysGroupsPlayeds.size() + "");
        statView.showTotalDaysPlayedByGroups(modal_totalDaysGroupsPlayeds);
    }

    @Background
    @Override
    public void getRecourcesPlayedByGroups(String groupId) {
        List<Modal_ResourcePlayedByGroups> modal_resourcePlayedByGroups = scoreDao.getRecourcesPlayedByGroups(groupId);
        Log.d("getActiveGroups: ", modal_resourcePlayedByGroups.size() + "");
        HashMap<String, List<Modal_ResourcePlayedByGroups>> map = new HashMap<>();
        for (Modal_ResourcePlayedByGroups gr : modal_resourcePlayedByGroups) {
            if (map.containsKey(gr.getDates())) {
                map.get(gr.getDates()).add(gr);
            } else {
                List<Modal_ResourcePlayedByGroups> res = new ArrayList<>();
                res.add(gr);
                map.put(gr.getDates(), res);
            }
        }
        if (map.size() > 0) statView.showResourcesPlayedByGroups(map);
    }

    @Background
    @Override
    public void getDeviceActiveDays() {
        int days = scoreDao.getTotalActiveDeviceDays();
        statView.showDeviceDays(days);
    }
}
