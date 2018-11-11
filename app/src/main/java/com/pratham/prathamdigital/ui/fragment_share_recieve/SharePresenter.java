package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

import com.isupatches.wisefy.callbacks.AddNetworkCallbacks;
import com.isupatches.wisefy.callbacks.ConnectToNetworkCallbacks;
import com.isupatches.wisefy.callbacks.GetSavedNetworkCallbacks;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.util.PD_Utility;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
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
        PrathamApplication.wiseF.addOpenNetwork(ssid, new AddNetworkCallbacks() {
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
        Log.d(TAG, "showFolders:" + files.size());
        shareView.showFilesList(files);
    }
}
