package com.pratham.prathamdigital.ui.fragment_age_group;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.ui.QRLogin.QRLogin;
import com.pratham.prathamdigital.ui.fragment_admin_panel.AdminPanelFragment;
import com.pratham.prathamdigital.ui.fragment_select_group.FragmentSelectGroup;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentSelectAgeGroup extends Fragment {

  /*  @BindView(R.id.admin_panel)
    com.airbnb.lottie.LottieAnimationView admin_panel;*/

    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_age_group, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.scan_qr)
    public void setScanQR() {
        //todo check camera permission
        checkPermission();
    }

    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(), Manifest.permission.CAMERA)) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Permissions");
//                builder.setIcon(R.drawable.ic_warning);
                builder.setMessage("Please Grant All Permissions in order to Run the App.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CODE);
                    }
                });
                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CODE);
            }
        } else {
            // Do something, when permissions are already granted
            Intent intent = new Intent(getActivity(), QRLogin.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // When request is cancelled, the results array are empty
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    // if granted
                    Intent intent = new Intent(getActivity(), QRLogin.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                } else {
                    // Permissions are denied
                    checkPermission();
//                    Toast.makeText(mContext, "Permissions denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @OnClick(R.id.iv_age_3_to_6)
    public void open3to6Groups() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(PD_Constant.GROUP_AGE_BELOW_7, true);
        PD_Utility.showFragment(getActivity(), new FragmentSelectGroup(), R.id.frame_attendance,
                bundle, FragmentSelectGroup.class.getSimpleName());
    }

    @OnClick(R.id.iv_age_8_to_14)
    public void open8to14Groups() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(PD_Constant.GROUP_AGE_BELOW_7, false);
        PD_Utility.showFragment(getActivity(), new FragmentSelectGroup(), R.id.frame_attendance,
                bundle, FragmentSelectGroup.class.getSimpleName());
    }

    @OnClick(R.id.admin_panel)
    public void openAdminPanel() {
//        if (!PrathamApplication.wiseF.isWifiEnabled())
//            PrathamApplication.wiseF.enableWifi();
//        Intent intent = new Intent(getActivity(), ConnectDialog.class);
//        startActivityForResult(intent, 3);
        PD_Utility.showFragment(getActivity(), new AdminPanelFragment(), R.id.frame_attendance,
                null, AdminPanelFragment.class.getSimpleName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                PD_Utility.showFragment(getActivity(), new AdminPanelFragment(), R.id.frame_attendance,
                        null, AdminPanelFragment.class.getSimpleName());
            }
        }
    }
}