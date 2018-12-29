package com.pratham.prathamdigital.ui.fragment_share_recieve;

import com.pratham.prathamdigital.models.File_Model;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_ReceivingFilesThroughFTP;

import java.io.File;
import java.util.ArrayList;

public interface ContractShare {
    interface shareView {
        void hotspotStarted();

        void onWifiConnected(String ssid);

        void fileItemClicked(Modal_ContentDetail detail, int position);

        void sendItemChecked(File_Model detail, int position);

        void showFilesList(ArrayList<File_Model> contents, String parentId);

        void disconnectFTP();

        void closeFTPJoin();

        void showRecieving(ArrayList<Modal_ReceivingFilesThroughFTP> filesRecieving);
    }

    interface sharePresenter {
        void connectFTP();

        void showFolders(Modal_ContentDetail detail);

        void traverseFolderBackward();

        void connectToWify(String ssid);

        void sendFiles(Modal_ContentDetail detail);

        void readRecievedFiles(File filePath);

        void showFilesRecieving(File filePath);

        void setView(FragmentShareRecieve fragmentShareRecieve);
    }
}
