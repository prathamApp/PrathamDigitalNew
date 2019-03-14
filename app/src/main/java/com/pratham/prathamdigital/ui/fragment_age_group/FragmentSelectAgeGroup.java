package com.pratham.prathamdigital.ui.fragment_age_group;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.BlurPopupDialog.BlurPopupWindow;
import com.pratham.prathamdigital.custom.permissions.KotlinPermissions;
import com.pratham.prathamdigital.custom.permissions.ResponsePermissionCallback;
import com.pratham.prathamdigital.ui.QRLogin.QRLogin_;
import com.pratham.prathamdigital.ui.connect_dialog.ConnectDialog;
import com.pratham.prathamdigital.ui.fragment_admin_panel.AdminPanelFragment;
import com.pratham.prathamdigital.ui.fragment_admin_panel.AdminPanelFragment_;
import com.pratham.prathamdigital.ui.fragment_select_group.FragmentSelectGroup;
import com.pratham.prathamdigital.ui.fragment_select_group.FragmentSelectGroup_;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EFragment(R.layout.fragment_age_group)
public class FragmentSelectAgeGroup extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;

    @Click(R.id.scan_qr)
    public void setScanQR() {
        KotlinPermissions.with(getActivity())
                .permissions(Manifest.permission.CAMERA)
                .onAccepted(new ResponsePermissionCallback() {
                    @Override
                    public void onResult(@NotNull List<String> permissionResult) {
                        Intent intent = new Intent(getActivity(), QRLogin_.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    }
                })
                .ask();
    }

//    protected void checkPermission() {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            // Do something, when permissions not granted
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    getActivity(), Manifest.permission.CAMERA)) {
//                // If we should give explanation of requested permissions
//
//                // Show an alert dialog here with request explanation
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Permissions");
////                builder.setIcon(R.drawable.ic_warning);
//                builder.setMessage("Please Grant All Permissions in order to Run the App.");
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
//                                MY_PERMISSIONS_REQUEST_CODE);
//                    }
//                });
//                builder.setNeutralButton("Cancel", null);
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            } else {
//                // Directly request for required permissions, without explanation
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
//                        MY_PERMISSIONS_REQUEST_CODE);
//            }
//        } else {
//            // Do something, when permissions are already granted
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_CODE: {
//                // When request is cancelled, the results array are empty
//                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
//                    // if granted
//                    Intent intent = new Intent(getActivity(), QRLogin.class);
//                    startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
//                } else {
//                    // Permissions are denied
//                    checkPermission();
////                    Toast.makeText(mContext, "Permissions denied.", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//        }
//    }

    @Click(R.id.iv_age_3_to_6)
    public void open3to6Groups(View view) {
        PrathamApplication.bubble_mp.start();
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putBoolean(PD_Constant.GROUP_AGE_BELOW_7, true);
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        PD_Utility.addFragment(getActivity(), new FragmentSelectGroup_(), R.id.frame_attendance,
                bundle, FragmentSelectGroup.class.getSimpleName());
    }

    @Click(R.id.iv_age_8_to_14)
    public void open8to14Groups(View view) {
        PrathamApplication.bubble_mp.start();
        int[] outLocation = new int[2];
        view.getLocationOnScreen(outLocation);
        outLocation[0] += view.getWidth() / 2;
        Bundle bundle = new Bundle();
        bundle.putBoolean(PD_Constant.GROUP_AGE_BELOW_7, false);
        bundle.putInt(PD_Constant.REVEALX, outLocation[0]);
        bundle.putInt(PD_Constant.REVEALY, outLocation[1]);
        PD_Utility.addFragment(getActivity(), new FragmentSelectGroup_(), R.id.frame_attendance,
                bundle, FragmentSelectGroup.class.getSimpleName());
    }

    @Click(R.id.admin_panel)
    public void openAdminPanel() {
        if (!PrathamApplication.wiseF.isWifiEnabled())
            PrathamApplication.wiseF.enableWifi();
        if (!PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork() && !PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            ConnectDialog connectDialog = new ConnectDialog.Builder(getActivity()).build();
            connectDialog.isDismissOnClickBack();
            connectDialog.isDismissOnTouchBackground();
            connectDialog.setOnDismissListener(new BlurPopupWindow.OnDismissListener() {
                @Override
                public void onDismiss(BlurPopupWindow popupWindow) {
                    onActivityResult(3, Activity.RESULT_OK, null);
                }
            });
            connectDialog.show();
        } else {
            onActivityResult(3, Activity.RESULT_OK, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                PD_Utility.showFragment(getActivity(), new AdminPanelFragment_(), R.id.frame_attendance,
                        null, AdminPanelFragment.class.getSimpleName());
            }
        }
    }
}