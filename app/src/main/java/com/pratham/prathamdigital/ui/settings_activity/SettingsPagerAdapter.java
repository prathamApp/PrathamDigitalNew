package com.pratham.prathamdigital.ui.settings_activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pratham.prathamdigital.ui.fragment_language.FragmentLanguage;
import com.pratham.prathamdigital.ui.fragment_settings.FragmentSettings;
import com.pratham.prathamdigital.ui.fragment_share_recieve.FragmentShareRecieve;

public class SettingsPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;

    public SettingsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentLanguage();
            case 1:
                return new FragmentShareRecieve();
            case 2:
                return new FragmentSettings();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
