package com.pratham.prathamdigital.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import static com.pratham.prathamdigital.PrathamApplication.sessionDao;

public class AppKillService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
//            PrathamSmartSyncNew.pushUsageToServer(false, PD_Constant.AUTO_PUSH, getApplicationContext());
            if (!FastSave.getInstance().getString(PD_Constant.SESSIONID, "").isEmpty()) {
                sessionDao.UpdateToDate(FastSave.getInstance().getString(PD_Constant.SESSIONID, ""), PD_Utility.getCurrentDateTime());
                stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}