package com.pratham.prathamdigital.ui.splash;

import android.location.Location;

public interface SplashContract {
    interface splashview {
        void redirectToDashboard();

        void redirectToAvatar();

//        void signInUsingGoogle();

//        void googleSignInFailed();

        void loadSplash();

        void checkPermissions();
    }

    interface splashPresenter {
        void checkIfContentinSDCard();

        void populateDefaultDB();

        void checkConnectivity();

        void checkStudentList();

//        void validateSignIn(Intent data);

        void checkVersion(String latestVersion);

        void clearPreviousBuildData();

//        GoogleApiClient configureSignIn();

        void onLocationChanged(Location location);

        void startGpsTimer();

        void savePrathamCode(String code);

        void checkPrathamCode();
    }
}
