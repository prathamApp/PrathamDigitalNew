package com.pratham.prathamdigital.ui.connect_dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.isupatches.wisefy.callbacks.AddNetworkCallbacks;
import com.isupatches.wisefy.callbacks.ConnectToNetworkCallbacks;
import com.isupatches.wisefy.callbacks.GetNearbyAccessPointsCallbacks;
import com.isupatches.wisefy.callbacks.GetSavedNetworkCallbacks;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.interfaces.OnWifiConnected;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.pratham.prathamdigital.PrathamApplication.wiseF;

public class ConnectDialog extends BlurPopupWindow implements ConnectInterface {

    private static final String TAG = ConnectDialog.class.getSimpleName();
    RecyclerView wifi_list;
    TextView dialog_txt;
    RelativeLayout rl_enter_password;
    RelativeLayout rl_connect_option;
    RelativeLayout rl_progress;
    EditText et_wifi_pass;
    ImageView wifi_back;

    private String ssid;
    private String password;
    private ArrayList<String> wifi_result;
    private OnWifiConnected onWifiConnected;

    public ConnectDialog(@NonNull Context context, OnWifiConnected connected) {
        super(context);
        this.onWifiConnected = connected;
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_connecction, parent, false);
        mHandler = new Handler();
        wifi_list = view.findViewById(R.id.wifi_list);
        dialog_txt = view.findViewById(R.id.dialog_txt);
        rl_enter_password = view.findViewById(R.id.rl_enter_password);
        rl_connect_option = view.findViewById(R.id.rl_connect_option);
        rl_progress = view.findViewById(R.id.rl_progress);
        et_wifi_pass = view.findViewById(R.id.et_wifi_pass);
        wifi_back = view.findViewById(R.id.wifi_back);
        Button btn_connect_wifi = view.findViewById(R.id.btn_connect_wifi);
        Button btn_try_again = view.findViewById(R.id.btn_try_again);
        ImageView wifi_refresh = view.findViewById(R.id.wifi_refresh);
        btn_connect_wifi.setOnClickListener(v -> setBtnConnectWifi());
        btn_try_again.setOnClickListener(v -> setTryButton());
        wifi_refresh.setOnClickListener(v -> setRefresh());
        wifi_back.setOnClickListener(v -> setWifiBack());
        this.isDismissOnTouchBackground();
        this.isDismissOnClickBack();
        new Handler().postDelayed(() -> mHandler.sendEmptyMessage(2), 300);
        return view;
    }

    @SuppressLint("HandlerLeak")
    private
    Handler mHandler = new Handler() {
        @SuppressLint({"MissingPermission", "SetTextI18n"})
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    WifiAdapter adapter = new WifiAdapter(getContext(), wifi_result, ConnectDialog.this);
                    wifi_list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    wifi_list.setHasFixedSize(true);
                    wifi_list.setAdapter(adapter);
                    break;
                case 1:
                    rl_progress.setVisibility(GONE);
                    dialog_txt.setText("Failed");
                    break;
                case 2:
                    if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
                        wifi_list.setVisibility(GONE);
                        rl_connect_option.setVisibility(VISIBLE);
                    } else if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
                        wifi_list.setVisibility(VISIBLE);
                        rl_connect_option.setVisibility(GONE);
                        PrathamApplication.wiseF.getNearbyAccessPoints(true, nearbyAccessPointsCallbacks);
                    } else {
                        wifi_list.setVisibility(VISIBLE);
                        rl_connect_option.setVisibility(GONE);
                        if (!PrathamApplication.wiseF.isWifiEnabled())
                            PrathamApplication.wiseF.enableWifi();
                        PrathamApplication.wiseF.getNearbyAccessPoints(true, nearbyAccessPointsCallbacks);
                    }
                    break;
                case 3:
                    try {
                        @SuppressLint("WrongConstant")
                        Object sbservice = getContext().getSystemService("statusbar");
                        @SuppressLint("PrivateApi") Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                        Method showsb = statusbarManager.getMethod("expand");
                        showsb.invoke(sbservice);
                    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    rl_progress.setVisibility(VISIBLE);
                    break;
            }
        }
    };
    private final GetNearbyAccessPointsCallbacks nearbyAccessPointsCallbacks = new GetNearbyAccessPointsCallbacks() {
        @Override
        public void retrievedNearbyAccessPoints(@NotNull List<ScanResult> scanResults) {
            wifi_result = new ArrayList<>();
            for (ScanResult sc : scanResults)
                wifi_result.add(sc.SSID);
            mHandler.sendEmptyMessage(0);
        }

        @Override
        public void wisefyFailure(int i) {
            Log.d("response:::", "not found nearby networks");
        }
    };
    private final GetSavedNetworkCallbacks savedNetworkCallbacks = new GetSavedNetworkCallbacks() {
        @Override
        public void savedNetworkNotFound() {
            if (!password.isEmpty())
                addNetwork(ssid, password);
            else
                addOpenNetwork(ssid);
        }

        @Override
        public void retrievedSavedNetwork(@NotNull WifiConfiguration wifiConfiguration) {
            connectToNetwork(ssid);
        }

        @Override
        public void wisefyFailure(int i) {
            Log.d(TAG, "wisefyFailure:");
            mHandler.sendEmptyMessage(1);
        }
    };

    private void addOpenNetwork(String ssid) {
        PrathamApplication.wiseF.addOpenNetwork(ssid, new AddNetworkCallbacks() {
            @Override
            public void failureAddingNetwork(int i) {
                Log.d(TAG, "failureAddingNetwork: ");
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void networkAdded(int i, @NotNull WifiConfiguration wifiConfiguration) {
                connectToNetwork(ssid);
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure: ");
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    private void addNetwork(String ssid, String pass) {
        PrathamApplication.wiseF.addWPA2Network(ssid, pass, new AddNetworkCallbacks() {
            @Override
            public void failureAddingNetwork(int i) {
                Log.d(TAG, "failureAddingNetwork: ");
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void networkAdded(int i, @NotNull WifiConfiguration wifiConfiguration) {
                connectToNetwork(ssid);
            }

            @Override
            public void wisefyFailure(int i) {
                Log.d(TAG, "wisefyFailure: ");
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    private void connectToNetwork(String ssid) {
        PrathamApplication.wiseF.connectToNetwork(ssid, 10000, new ConnectToNetworkCallbacks() {
            @Override
            public void connectedToNetwork() {
                if (onWifiConnected != null) onWifiConnected.isWifiConnectedSuccessfully(true);
                dismiss();
            }

            @Override
            public void failureConnectingToNetwork() {
                if (onWifiConnected != null) onWifiConnected.isWifiConnectedSuccessfully(false);
                mHandler.sendEmptyMessage(1);
                Log.d(TAG, "failureConnectingToNetwork: ");
            }

            @Override
            public void networkNotFoundToConnectTo() {
                if (onWifiConnected != null) onWifiConnected.isWifiConnectedSuccessfully(false);
                mHandler.sendEmptyMessage(1);
                Log.d(TAG, "networkNotFoundToConnectTo: ");
            }

            @Override
            public void wisefyFailure(int i) {
                if (onWifiConnected != null) onWifiConnected.isWifiConnectedSuccessfully(false);
                mHandler.sendEmptyMessage(1);
                Log.d(TAG, "wisefyFailure: ");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void wifiClicked(String wifi_name) {
        PrathamApplication.bubble_mp.start();
        ssid = wifi_name;
        if (ssid.equalsIgnoreCase(PD_Constant.PRATHAM_KOLIBRI_HOTSPOT)) {
            mHandler.sendEmptyMessage(4);
            password = PD_Constant.WIFI_AP_PASSWORD;
            dialog_txt.setText("Connecting...");
            if (!PD_Utility.checkWhetherConnectedToSelectedNetwork(PrathamApplication.getInstance(), ssid)) {
                wiseF.disconnectFromCurrentNetwork();
                wiseF.getSavedNetwork(ssid, savedNetworkCallbacks);
            } else {
                dismiss();
            }
        } else {
            rl_enter_password.setVisibility(VISIBLE);
            wifi_back.setVisibility(VISIBLE);
        }
    }

    public void setBtnConnectWifi() {
        PrathamApplication.bubble_mp.start();
        if (!PD_Utility.checkWhetherConnectedToSelectedNetwork(PrathamApplication.getInstance(), ssid)) {
            mHandler.sendEmptyMessage(4);
            password = et_wifi_pass.getText().toString();
            wiseF.disconnectFromCurrentNetwork();
            wiseF.getSavedNetwork(ssid, savedNetworkCallbacks);
        } else {
            if (onWifiConnected != null) onWifiConnected.isWifiConnectedSuccessfully(true);
            dismiss();
        }
    }

    public void setTryButton() {
        PrathamApplication.bubble_mp.start();
        if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork())
            mHandler.sendEmptyMessage(3);
        else {
            rl_connect_option.setVisibility(GONE);
            wifi_list.setVisibility(VISIBLE);
        }
    }

    public void setRefresh() {
        PrathamApplication.bubble_mp.start();
        mHandler.sendEmptyMessage(2);
    }

    public void setWifiBack() {
        rl_connect_option.setVisibility(GONE);
        rl_enter_password.setVisibility(GONE);
        wifi_list.setVisibility(VISIBLE);
        wifi_back.setVisibility(GONE);
    }

    public static class Builder extends BlurPopupWindow.Builder<ConnectDialog> {
        private OnWifiConnected onWifiConnected;

        public Builder(Context context, OnWifiConnected connected) {
            super(context);
            this.onWifiConnected = connected;
            this.setScaleRatio(0.25f).setBlurRadius(8).setTintColor(0x30000000);
        }

        @Override
        protected ConnectDialog createPopupWindow() {
            return new ConnectDialog(mContext, onWifiConnected);
        }
    }
}
