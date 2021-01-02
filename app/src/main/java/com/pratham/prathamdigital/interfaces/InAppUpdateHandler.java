package com.pratham.prathamdigital.interfaces;

import com.google.android.play.core.install.InstallState;

public interface InAppUpdateHandler {
    void onInAppUpdateStatus(InstallState state);
}
