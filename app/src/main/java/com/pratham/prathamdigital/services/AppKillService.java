package com.pratham.prathamdigital.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

public class AppKillService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            PrathamSmartSync.pushUsageToServer(false);
            if (!FastSave.getInstance().getString(PD_Constant.SESSIONID, "").isEmpty()) {
                BaseActivity.sessionDao.UpdateToDate(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""), PD_Utility.getCurrentDateTime());
                stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}