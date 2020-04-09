package com.pratham.prathamdigital.ui.splash;

import android.content.Intent;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;

public interface SplashContract {
    interface splashview {
        void showAppUpdateDialog();

        void redirectToDashboard();

        void redirectToAvatar();

        void signInUsingGoogle();

        void googleSignInFailed();

        void loadSplash();

        void showEnterPrathamCodeDialog();

        void checkPermissions();
    }

    interface splashPresenter {
        void checkIfContentinSDCard();

        void populateDefaultDB();

        void checkConnectivity();

        void checkStudentList();

        void validateSignIn(Intent data);

        void checkVersion(String latestVersion);

        void clearPreviousBuildData();

        GoogleApiClient configureSignIn();

        void onLocationChanged(Location location);

        void startGpsTimer();

        void savePrathamCode(String code);

        void checkPrathamCode();
    }
}
