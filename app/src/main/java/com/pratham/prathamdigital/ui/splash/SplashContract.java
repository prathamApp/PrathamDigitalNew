package com.pratham.prathamdigital.ui.splash;

import android.content.Intent;

public interface SplashContract {
    interface splashview {
        void showAppUpdateDialog();

        void redirectToDashboard();

        void redirectToAvatar();

        void redirectToAttendance();

        void signInUsingGoogle();
    }

    interface splashPresenter {
        void checkIfContentinSDCard();

        void populateDefaultDB();

        void checkConnectivity();

        void checkStudentList();

        void validateSignIn(Intent data);

        void checkVersion(String latestVersion);
    }
}
