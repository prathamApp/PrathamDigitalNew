package com.pratham.prathamdigital.ui.splash;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.isupatches.wisefy.callbacks.EnableWifiCallbacks;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.CopyExistingDb;
import com.pratham.prathamdigital.async.GetLatestVersion;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.Interface_copying;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

@EBean
public class SplashPresenterImpl implements SplashContract.splashPresenter,
        GoogleApiClient.OnConnectionFailedListener, Interface_copying {
    private static final String TAG = SplashPresenterImpl.class.getSimpleName();
    Context context;
    SplashContract.splashview splashview;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String appname;

    public SplashPresenterImpl(Context context) {
        this.context = context;
        splashview = (SplashContract.splashview) context;
    }

    public void getVersion() {
        new GetLatestVersion(context, SplashPresenterImpl.this).execute();
    }

    @Background
    @Override
    public void checkVersion(String latestVersion) {
        String currentVersion = PD_Utility.getCurrentVersion(context);
        Log.d("version::", "Current version = $currentVersion");
        FastSave.getInstance().saveString(PD_Constant.APP_VERSION, latestVersion);
        if (latestVersion != null && !latestVersion
                .isEmpty() && (!currentVersion.equalsIgnoreCase(latestVersion))) {
            splashview.showAppUpdateDialog();
        } else {
            new CopyExistingDb(context, SplashPresenterImpl.this).execute();
        }
    }

    public GoogleApiClient configureSignIn() {
        // Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_notification_channel_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((ActivitySplash) context, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        return mGoogleApiClient;
    }

    EnableWifiCallbacks enableWifiCallbacks = new EnableWifiCallbacks() {
        @Override
        public void failureEnablingWifi() {
            Log.d(TAG, "failureEnablingWifi: wifi enable failure");
        }

        @Override
        public void wifiEnabled() {
            getVersion();
        }

        @Override
        public void wisefyFailure(int i) {
            Log.d(TAG, "wisefyFailure: wisefy failure");
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Background
    @Override
    public void validateSignIn(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            Log.d(TAG, "validateSignIn: " + result.toString());
            // Google Sign In was successful, save Token and a state then authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            String token = account.getIdToken();
            String name = account.getDisplayName();
            String email = account.getEmail();
            // Save Data to Database and sharedPreference
            FastSave.getInstance().saveBoolean(PD_Constant.IS_GOOGLE_SIGNED_IN, true);
            FastSave.getInstance().saveString(PD_Constant.GOOGLE_TOKEN, token);
            Modal_Status status = new Modal_Status();
            status.setStatusKey(PD_Constant.GOOGLE_ID);
            status.setValue(email);
            status.setDescription("");
            BaseActivity.statusDao.insert(status);
//            AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
//            firebaseAuthWithGoogle(credential);
            checkStudentList();
        }
    }

    @Background
    public void firebaseAuthWithGoogle(AuthCredential credential) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((ActivitySplash) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential");
                            checkStudentList();
                        } else {
                            Log.d(TAG, "signInWithCredential" + task.getException().getMessage());
                        }
                    }
                });
    }

    @UiThread
    @Override
    public void checkStudentList() {
        if (!BaseActivity.studentDao.getAllStudents().isEmpty()) {
            splashview.redirectToDashboard();
        } else {
            splashview.redirectToAvatar();
        }
    }

    @Background
    @Override
    public void populateDefaultDB() {
        Modal_Status statusObj = new Modal_Status();

        if (BaseActivity.statusDao.getKey("CRLID") == null) {
            statusObj.statusKey = "CRLID";
            statusObj.value = "default";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("group1") == null) {
            statusObj.statusKey = "group1";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("group2") == null) {
            statusObj.statusKey = "group2";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("group3") == null) {
            statusObj.statusKey = "group3";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("group4") == null) {
            statusObj.statusKey = "group4";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("group5") == null) {
            statusObj.statusKey = "group5";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("DeviceId") == null) {
            statusObj.statusKey = "DeviceId";
            statusObj.value = PD_Utility.getDeviceID();
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("DeviceName") == null) {
            statusObj.statusKey = "DeviceName";
            statusObj.value = PD_Utility.getDeviceName();
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("ActivatedDate") == null) {
            statusObj.statusKey = "ActivatedDate";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("village") == null) {
            statusObj.statusKey = "village";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("ActivatedForGroups") == null) {
            statusObj.statusKey = "ActivatedForGroups";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("Latitude") == null) {
            statusObj.statusKey = "Latitude";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("Longitude") == null) {
            statusObj.statusKey = "Longitude";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("GPSDateTime") == null) {
            statusObj.statusKey = "GPSDateTime";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("SerialID") == null) {
            statusObj.statusKey = "SerialID";
            statusObj.value = PD_Utility.getDeviceSerialID();
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("gpsFixDuration") == null) {
            statusObj.statusKey = "gpsFixDuration";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("prathamCode") == null) {
            statusObj.statusKey = "prathamCode";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("programId") == null) {
            statusObj.statusKey = "programId";
            statusObj.value = "";
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("wifiMAC") == null) {
            statusObj.statusKey = "wifiMAC";
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();
            statusObj.value = macAddress;
            BaseActivity.statusDao.insert(statusObj);
        }
        if (BaseActivity.statusDao.getKey("apkType") == null) {
            if (PrathamApplication.isTablet) {
                statusObj.statusKey = "apkType";
                statusObj.value = "Pratham Digital with New UI, Kolibri, New POS, Raspberry Pie, Tablet Apk";
                BaseActivity.statusDao.insert(statusObj);
            } else {
                statusObj.statusKey = "apkType";
                statusObj.value = "Pratham Digital with New UI, Kolibri, New POS, Raspberry Pie, Smartphone Apk";
                BaseActivity.statusDao.insert(statusObj);
            }
        } else {
            if (PrathamApplication.isTablet) {
                statusObj.statusKey = "apkType";
                statusObj.value = "Pratham Digital with New UI, Kolibri, New POS, Raspberry Pie, Tablet Apk";
                BaseActivity.statusDao.insert(statusObj);
            } else {
                statusObj.statusKey = "apkType";
                statusObj.value = "Pratham Digital with New UI, Kolibri, New POS, Raspberry Pie, Smartphone Apk";
                BaseActivity.statusDao.insert(statusObj);
            }
        }
        if (BaseActivity.statusDao.getKey("appName") == null) {
            CharSequence c = "";
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List l = am.getRunningAppProcesses();
            Iterator i = l.iterator();
            PackageManager pm = context.getPackageManager();
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                appname = c.toString();
                Log.w("LABEL", c.toString());
            } catch (Exception e) {
            }
            statusObj.statusKey = "appName";
            statusObj.value = appname;
            BaseActivity.statusDao.insert(statusObj);
        } else {
            CharSequence c = "";
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List l = am.getRunningAppProcesses();
            Iterator i = l.iterator();
            PackageManager pm = context.getPackageManager();
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                appname = c.toString();
                Log.w("LABEL", c.toString());
            } catch (Exception e) {
            }
            statusObj.statusKey = "appName";
            statusObj.value = appname;
            BaseActivity.statusDao.insert(statusObj);
        }

        if (BaseActivity.statusDao.getKey("apkVersion") == null) {
            statusObj.statusKey = "apkVersion";
            PackageInfo pInfo = null;
            String verCode = "";
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                verCode = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            statusObj.value = verCode;
            BaseActivity.statusDao.insert(statusObj);
        } else {
            statusObj.statusKey = "apkVersion";
            PackageInfo pInfo = null;
            String verCode = "";
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                verCode = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            statusObj.value = verCode;
            BaseActivity.statusDao.insert(statusObj);
        }
    }

    @Background
    @Override
    public void checkConnectivity() {
        if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            getVersion();
        } else if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
            getVersion();
        } else {
            if (!FastSave.getInstance().getString(PD_Constant.APP_VERSION, "").isEmpty())
                checkVersion(FastSave.getInstance().getString(PD_Constant.APP_VERSION, ""));
            else
                checkStudentList();
        }
    }

    @Override
    public void checkIfContentinSDCard() {
        if (PrathamApplication.isTablet)
            new CopyExistingDb(context, SplashPresenterImpl.this).execute();
        else
            checkConnectivity();
    }

    @Override
    public void copyingExisting() {

    }

    @Override
    public void failedCopyingExisting() {
        checkStudentList();
    }

    @Override
    public void successCopyingExisting(String absolutePath) {
        PrathamApplication.getInstance().setExistingSDContentPath(absolutePath);
        checkStudentList();
    }

    @Background
    @Override
    public void clearPreviousBuildData() {
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
            File file = new File(s);
            for (File f : file.listFiles()) {
                if (f.getName().contains("app_Pratham"))
                    deleteRecursive(f);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }
    }

    void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
