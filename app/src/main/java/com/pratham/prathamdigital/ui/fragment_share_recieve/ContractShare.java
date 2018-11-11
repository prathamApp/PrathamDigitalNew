package com.pratham.prathamdigital.ui.fragment_share_recieve;

import java.io.File;
import java.util.List;

public interface ContractShare {
    interface shareView {
        void hotspotStarted();

        void onWifiConnected(String ssid);

        void fileItemClicked(File file, int position);

        void showFilesList(List<File> files);
    }
}
