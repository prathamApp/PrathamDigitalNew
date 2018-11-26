package com.pratham.prathamdigital.ui.connect_dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.isupatches.wisefy.callbacks.AddNetworkCallbacks;
import com.isupatches.wisefy.callbacks.ConnectToNetworkCallbacks;
import com.isupatches.wisefy.callbacks.GetNearbyAccessPointsCallbacks;
import com.isupatches.wisefy.callbacks.GetSavedNetworkCallbacks;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.prathamdigital.PrathamApplication.wiseF;

public class ConnectDialog extends DialogFragment {

    private static final String TAG = ConnectDialog.class.getSimpleName();
    private Context context;
    private ConnectInterface connectInterface;
    @BindView(R.id.wifi_list)
    ListView wifi_list;
    @BindView(R.id.refresh)
    LottieAnimationView refresh;
    @BindView(R.id.dialog_txt)
    TextView dialog_txt;

    String ssid;
    String[] wifi_result;

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_connecction, container);
        ButterKnife.bind(this, view);
        PrathamApplication.wiseF.getNearbyAccessPoints(true, nearbyAccessPointsCallbacks);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }

//    @SuppressLint("MissingPermission")
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        return null;
//    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PrathamApplication.getInstance(),
                            R.layout.connect_dialog_item, android.R.id.text1, wifi_result);
                    wifi_list.setAdapter(adapter);
                    wifi_list.setOnItemClickListener(onItemClickListener);
            }
        }
    };
    GetNearbyAccessPointsCallbacks nearbyAccessPointsCallbacks = new GetNearbyAccessPointsCallbacks() {
        @Override
        public void retrievedNearbyAccessPoints(@NotNull List<ScanResult> scanResults) {
            wifi_result = new String[scanResults.size()];
            for (int i = 0; i < scanResults.size(); i++) {
                wifi_result[i] = scanResults.get(i).SSID;
            }
            mHandler.sendEmptyMessage(0);
        }

        @Override
        public void wisefyFailure(int i) {
            Log.d("response:::", "not found nearby networks");
        }
    };

    @SuppressLint("MissingPermission")
    @OnClick(R.id.refresh)
    public void setRefresh() {
        PrathamApplication.wiseF.getNearbyAccessPoints(true, nearbyAccessPointsCallbacks);
    }

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ssid = (String) wifi_list.getItemAtPosition(position);
            if (ssid.equalsIgnoreCase(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
                dialog_txt.setText("Connecting...");
                if (!PD_Utility.checkWhetherConnectedToSelectedNetwork(PrathamApplication.getInstance(), ssid)) {
                    wiseF.disconnectFromCurrentNetwork();
                }
                wiseF.getSavedNetwork(ssid, savedNetworkCallbacks);
            } else {
                Toast.makeText(getActivity(), "Please select " + PD_Constant.PRATHAM_KOLIBRI_HOTSPOT, Toast.LENGTH_SHORT).show();
            }
        }
    };

    GetSavedNetworkCallbacks savedNetworkCallbacks = new GetSavedNetworkCallbacks() {
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
            dialog_txt.setText("Failed");
        }
    };

    private void addNetwork(String ssid) {
        PrathamApplication.wiseF.addWPA2Network(ssid, PD_Constant.WIFI_AP_PASSWORD, new AddNetworkCallbacks() {
            @Override
            public void failureAddingNetwork(int i) {
                Log.d(TAG, "failureAddingNetwork: ");
                dialog_txt.setText("Failed");
            }

            @Override
            public void networkAdded(int i, @NotNull WifiConfiguration wifiConfiguration) {
                connectToNetwork(ssid);
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure: ");
                dialog_txt.setText("Failed");
            }
        });
    }

    private void connectToNetwork(String ssid) {
        PrathamApplication.wiseF.connectToNetwork(ssid, 10000, new ConnectToNetworkCallbacks() {
            @Override
            public void connectedToNetwork() {
//                Intent intent = new Intent();
//                setResult(Activity.RESULT_OK, intent);
//                finish();
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                dismiss();
            }

            @Override
            public void failureConnectingToNetwork() {
                dialog_txt.setText("Failed");
                Log.d(TAG, "failureConnectingToNetwork: ");
            }

            @Override
            public void networkNotFoundToConnectTo() {
                dialog_txt.setText("Failed");
                Log.d(TAG, "networkNotFoundToConnectTo: ");
            }

            @Override
            public void wisefyFailure(int i) {
                dialog_txt.setText("Failed");
                Log.d(TAG, "wisefyFailure: ");
            }
        });
    }
}
