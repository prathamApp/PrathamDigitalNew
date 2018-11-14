package com.pratham.prathamdigital.ui.splash;

public interface SplashContract {
    interface splashview {
        void showAppUpdateDialog();

        void redirectToDashboard();

        void redirectToAvatar();

        void redirectToAttendance();

        void signInUsingGoogle();
    }

    interface splashPresenter {
        void checkVersion(String latestVersion);
    }
}
