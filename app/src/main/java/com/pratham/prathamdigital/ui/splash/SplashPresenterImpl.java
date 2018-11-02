package com.pratham.prathamdigital.ui.splash;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.isupatches.wisefy.callbacks.EnableWifiCallbacks;
import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.GetLatestVersion;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

public class SplashPresenterImpl implements SplashContract.splashPresenter,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = SplashPresenterImpl.class.getSimpleName();
    Context context;
    SplashContract.splashview splashview;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public SplashPresenterImpl(Context context, SplashContract.splashview splashview) {
        this.context = context;
        this.splashview = splashview;
    }

    public void getVersion() {
        new GetLatestVersion(context, SplashPresenterImpl.this).execute();
    }

    @Override
    public void checkVersion(String latestVersion) {
        String currentVersion = PD_Utility.getCurrentVersion(context);
        Log.d("version::", "Current version = $currentVersion");
        if (latestVersion != null && !latestVersion
                .isEmpty() && (!currentVersion.equalsIgnoreCase(latestVersion))) {
            splashview.showAppUpdateDialog();
        } else {
            splashview.signInUsingGoogle();
        }
    }

    public void checkConnectivity() {
        if (PrathamApplication.wiseF.isDeviceConnectedToWifiNetwork()) {
            getVersion();
        } else if (PrathamApplication.wiseF.isDeviceConnectedToMobileNetwork()) {
            getVersion();
        } else {
            PrathamApplication.wiseF.enableWifi(enableWifiCallbacks);
        }
    }

    public GoogleApiClient configureSignIn() {
        // Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
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

    public void validateSignIn(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            Log.d(TAG, "validateSignIn: "+result.toString());
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
            AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
            firebaseAuthWithGoogle(credential);
        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential) {
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

    public void checkStudentList() {
        if (!BaseActivity.studentDao.getAllStudents().isEmpty()) {
            splashview.redirectToDashboard();
        } else {
            splashview.redirectToAvatar();
        }
    }
}
