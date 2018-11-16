package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

import com.isupatches.wisefy.callbacks.AddNetworkCallbacks;
import com.isupatches.wisefy.callbacks.ConnectToNetworkCallbacks;
import com.isupatches.wisefy.callbacks.GetSavedNetworkCallbacks;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.services.MessageReveiver;
import com.pratham.prathamdigital.socket.udp.IPMSGConst;
import com.pratham.prathamdigital.socket.udp.IPMSGProtocol;
import com.pratham.prathamdigital.socket.udp.UDPMessageListener;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SharePresenter {
    private static final String TAG = SharePresenter.class.getSimpleName();
    Context context;
    ContractShare.shareView shareView;

    public SharePresenter(Context context, ContractShare.shareView shareView) {
        this.context = context;
        this.shareView = shareView;
    }

    public void connectToWify(String ssid) {
        PrathamApplication.wiseF.getSavedNetwork(ssid, new GetSavedNetworkCallbacks() {
            @Override
            public void savedNetworkNotFound() {
                addNetwork(ssid);
            }

            @Override
            public void retrievedSavedNetwork(@NotNull WifiConfiguration wifiConfiguration) {
                connectToNetwork(ssid);
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure:");
            }
        });
    }

    private void addNetwork(String ssid) {
        PrathamApplication.wiseF.addWPA2Network(ssid, PD_Constant.WIFI_AP_PASSWORD, new AddNetworkCallbacks() {
            @Override
            public void failureAddingNetwork(int i) {
                Log.d(TAG, "failureAddingNetwork: ");
            }

            @Override
            public void networkAdded(int i, @NotNull WifiConfiguration wifiConfiguration) {
                connectToNetwork(ssid);
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure: ");
            }
        });
    }

    private void connectToNetwork(String ssid) {
        PrathamApplication.wiseF.connectToNetwork(ssid, 10000, new ConnectToNetworkCallbacks() {
            @Override
            public void connectedToNetwork() {
                shareView.onWifiConnected(ssid);
            }

            @Override
            public void failureConnectingToNetwork() {
                Log.d(TAG, "failureConnectingToNetwork: ");
            }

            @Override
            public void networkNotFoundToConnectTo() {
                Log.d(TAG, "networkNotFoundToConnectTo: ");
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure: ");
            }
        });
    }

    public void showFolders(String path) {
        List<File> files = new ArrayList<>();
        files = PD_Utility.getCurrentFileList(path);
        if (files != null && files.size() > 0) {
            Log.d(TAG, "showFolders:" + files.size());
            shareView.showFilesList(files);
        }
    }

    public void registerListener(FragmentShareRecieve fragmentShareRecieve) {
        MessageReveiver messageReveiver = new MessageReveiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PD_Constant.ACTION_NEW_MSG);
        context.registerReceiver(messageReveiver, filter);

        UDPMessageListener udpMessageListener = UDPMessageListener.getInstance(context);
        udpMessageListener.addMsgListener(fragmentShareRecieve);
    }

    public void sendMessage(String content, com.pratham.prathamdigital.socket.entity.Message.CONTENT_TYPE type, String localIPaddress, String serverIPaddres) {
        String nowtime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        IPMSGProtocol command = new IPMSGProtocol();
        command.targetIP = serverIPaddres;
        command.senderIP = localIPaddress;
        command.packetNo = new Date().getTime() + "";
        command.addObject = new com.pratham.prathamdigital.socket.entity.Message("", nowtime, content, type);
        command.commandNo = IPMSGConst.NO_SEND_TXT;
        UDPMessageListener.sendUDPdata(command);
    }
}
