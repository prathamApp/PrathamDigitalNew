package com.pratham.prathamdigital.services;

/**
 * Created by PEF on 13/06/2017.
 */

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.util.PD_Constant;

import org.greenrobot.eventbus.EventBus;

public class ConnectivityReceiver extends BroadcastReceiver {

    public ConnectivityReceiver() {
        super();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        EventMessage message = new EventMessage();
        message.setMessage(PD_Constant.CONNECTION_STATUS);
        if (wifi != null && wifi.isConnected()) {
            message.setConnection_resource(context.getResources().getDrawable(R.drawable.ic_dialog_connect_wifi_item));
            message.setConnection_name(PrathamApplication.wiseF.getCurrentNetwork().getSSID());
        } else if (mobile != null && mobile.isConnected()) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String carrierName = manager.getNetworkOperatorName();
            message.setConnection_resource(context.getResources().getDrawable(R.drawable.ic_4g_network));
            message.setConnection_name(carrierName);
        } else {
            message.setConnection_resource(context.getResources().getDrawable(R.drawable.ic_no_wifi));
            message.setConnection_name(PD_Constant.NO_CONNECTION);
        }
        EventBus.getDefault().post(message);
    }
}