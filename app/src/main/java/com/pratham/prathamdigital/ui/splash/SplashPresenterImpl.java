package com.pratham.prathamdigital.ui.splash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.async.ReadContentDbFromSdCard;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.Interface_copying;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.pratham.prathamdigital.PrathamApplication.statusDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;

@EBean
public class SplashPresenterImpl implements SplashContract.splashPresenter,
        /*GoogleApiClient.OnConnectionFailedListener,*/ Interface_copying {
    private static final String TAG = SplashPresenterImpl.class.getSimpleName();
    private final Context context;
    private final SplashContract.splashview splashview;
    //    private FirebaseAuth.AuthStateListener mAuthListener;
//    private String appname;
    private Timer gpsFixTimer;
    private int gpsFixCount = 0;

    @Bean(ReadContentDbFromSdCard.class)
    ReadContentDbFromSdCard readContentDbFromSdCard;

    SplashPresenterImpl(Context context) {
        this.context = context;
        splashview = (SplashContract.splashview) context;
    }

    @Background
    @Override
    public void checkVersion(String latestVersion) {
        String currentVersion = PD_Utility.getCurrentVersion(context);
        Log.d("version::", "Current version = $currentVersion");
        FastSave.getInstance().saveString(PD_Constant.APP_VERSION, latestVersion);
        try {
            if (latestVersion != null && !latestVersion
                    .isEmpty() && (Float.parseFloat(currentVersion) < Float.parseFloat(latestVersion)))
                splashview.showAppUpdateDialog();
            else
                readContentDbFromSdCard.doInBackground(SplashPresenterImpl.this);
        } catch (Exception e) {
            e.printStackTrace();
            readContentDbFromSdCard.doInBackground(SplashPresenterImpl.this);
        }
    }

   /* @Override
    public GoogleApiClient configureSignIn() {
        // Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_notification_channel_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        return new GoogleApiClient.Builder(context)
                .enableAutoManage((ActivitySplash) context, this *//* OnConnectionFailedListener *//*)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
    }*/

/*
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: google sign in failed");
    }
*/

/*    @Background
    @Override
    public void validateSignIn(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            Log.d(TAG, "validateSignIn: " + result.toString());
            // Google Sign In was successful, save Token and a state then authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            String token = Objects.requireNonNull(account).getIdToken();
//            String name = account.getDisplayName();
            String email = account.getEmail();
            // Save Data to Database and sharedPreference
            FastSave.getInstance().saveBoolean(PD_Constant.IS_GOOGLE_SIGNED_IN, true);
            FastSave.getInstance().saveString(PD_Constant.GOOGLE_TOKEN, token);
            Modal_Status status = new Modal_Status();
            status.setStatusKey(PD_Constant.GOOGLE_ID);
            status.setValue(Objects.requireNonNull(email));
            status.setDescription("");
            statusDao.insert(status);
//            AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
//            firebaseAuthWithGoogle(credential);
//            checkStudentList();
            checkIfContentinSDCard();
        } else {
            splashview.googleSignInFailed();
        }
    }*/

/*
    @Background
    public void firebaseAuthWithGoogle(AuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((ActivitySplash) context, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential");
                        checkStudentList();
                    } else {
                        Log.d(TAG, "signInWithCredential" + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }
*/

    @Background
    @Override
    public void checkStudentList() {
        if (studentDao.getStudentsCount() > 0) {
            //tablet mode
            splashview.redirectToDashboard();
        } else {
            //smartphone mode
            splashview.redirectToAvatar();
        }
    }

    @Background
    @Override
    public void populateDefaultDB() {
        Modal_Status statusObj = new Modal_Status();
        if (statusDao.getKey("CRLID") == null) {
            statusObj.statusKey = "CRLID";
            statusObj.value = "default";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("group1") == null) {
            statusObj.statusKey = "group1";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("group2") == null) {
            statusObj.statusKey = "group2";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("group3") == null) {
            statusObj.statusKey = "group3";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("group4") == null) {
            statusObj.statusKey = "group4";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("group5") == null) {
            statusObj.statusKey = "group5";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("DeviceId") == null) {
            statusObj.statusKey = "DeviceId";
            statusObj.value = PD_Utility.getDeviceID();
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("DeviceName") == null) {
            statusObj.statusKey = "DeviceName";
            statusObj.value = PD_Utility.getDeviceName();
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("ActivatedDate") == null) {
            statusObj.statusKey = "ActivatedDate";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("village") == null) {
            statusObj.statusKey = "village";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("ActivatedForGroups") == null) {
            statusObj.statusKey = "ActivatedForGroups";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("SerialID") == null) {
            statusObj.statusKey = "SerialID";
            statusObj.value = PD_Utility.getDeviceSerialID();
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("prathamCode") == null) {
            statusObj.statusKey = "prathamCode";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("programId") == null) {
            statusObj.statusKey = "programId";
            statusObj.value = "";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("wifiMAC") == null) {
            statusObj.statusKey = "wifiMAC";
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            statusObj.value = wInfo.getMacAddress();
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("apkType") == null) {
            statusObj.statusKey = "apkType";
            if (PrathamApplication.isTablet)
                statusObj.value = "Pratham Digital with New UI, Kolibri, Raspberry Pie, Tablet Apk";
            else
                statusObj.value = "Pratham Digital with New UI, Kolibri, Raspberry Pie, Smartphone Apk";
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("appName") == null) {
            statusObj.statusKey = "appName";
            statusObj.value = PD_Utility.getApplicationName(context);
            statusDao.insert(statusObj);
        }
        if (statusDao.getKey("apkVersion") == null) {
            statusObj.statusKey = "apkVersion";
            statusObj.value = PD_Utility.getCurrentVersion(context);
            statusDao.insert(statusObj);
        }
    }

    @Background
    @Override
    public void checkConnectivity() {
//        if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
//            getVersion();
//        } else if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
//            getVersion();
//        } else {
//            if (!FastSave.getInstance().getString(PD_Constant.APP_VERSION, "").isEmpty())
//                checkVersion(FastSave.getInstance().getString(PD_Constant.APP_VERSION, ""));
//            else
//        checkStudentList();
        readContentDbFromSdCard.doInBackground(SplashPresenterImpl.this);
    }

    @Override
    public void checkIfContentinSDCard() {
        if (PrathamApplication.isTablet)
            readContentDbFromSdCard.doInBackground(SplashPresenterImpl.this);
        else
            checkConnectivity();
    }

    @Override
    public void copyingExisting() {

    }

    @Override
    public void failedCopyingExisting() {
//        checkStudentList();
        splashview.checkPermissions();
    }

    @Override
    public void successCopyingExisting(String absolutePath) {
        PrathamApplication.getInstance().setExistingSDContentPath(absolutePath);
        splashview.checkPermissions();
//        checkStudentList();
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
        } catch (Exception e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Background
    @Override
    public void onLocationChanged(Location location) {
        gpsFixTimer.cancel();
        Modal_Status statusObj = new Modal_Status();
        statusObj.setStatusKey("Latitude");
        statusObj.setValue(String.valueOf(location.getLatitude()));
        statusDao.insert(statusObj);

        statusObj.setStatusKey("Longitude");
        statusObj.setValue(String.valueOf(location.getLongitude()));
        statusDao.insert(statusObj);

        statusObj.setStatusKey("GPSDateTime");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        Date gdate = new Date(location.getTime());
        String gpsDateTime = format.format(gdate);
        statusObj.setValue(gpsDateTime);
        statusDao.insert(statusObj);

        statusObj.setStatusKey("GPSFixDuration");
        if (statusDao.getKey("GPSFixDuration") == null) {
            statusObj.setValue(String.valueOf(gpsFixCount));
        } else {
            String value = statusDao.getValue("GPSFixDuration");
            value += "," + gpsFixCount;
            statusObj.setValue(value);
        }
        statusDao.insert(statusObj);
    }

    @Override
    public void startGpsTimer() {
        gpsFixTimer = new Timer();
        gpsFixTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gpsFixCount += 1;
            }
        }, 1000, 1000);
    }

    @Background
    @Override
    public void savePrathamCode(String code) {
        statusDao.updateValue("prathamCode", code);
    }

    @Override
    public void checkPrathamCode() {
        if (PrathamApplication.isTablet) {
            if (statusDao.getValue("prathamCode") == null || statusDao.getValue("prathamCode").isEmpty())
                splashview.showEnterPrathamCodeDialog();
            else
                splashview.loadSplash();
        } else {
            splashview.loadSplash();
        }
    }
}
