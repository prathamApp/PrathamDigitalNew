package com.pratham.prathamdigital.ui.fragment_content;

import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.ArrayList;

public interface ContentContract {
    interface contentView {
        void showNoConnectivity();

        void displayContents(ArrayList<Modal_ContentDetail> content);

        void displayHeader(Modal_ContentDetail contentDetail);

        void hideViews();

        void showViews();

        void increaseNotification(int number);

        void decreaseNotification(int number, Modal_ContentDetail detail);

        void displayLevel(ArrayList<Modal_ContentDetail> levelContents);
    }

    interface contentPresenter {
        void recievedContent(String header, String response);

        void recievedError(String header);

        void fileDownloadStarted(int downloadId, String filename, Modal_ContentDetail contentDetail);

        void updateFileProgress(int downloadId, String filename, int progress);

        void onDownloadCompleted(int downloadId);

        void onDownloadPaused(int downloadId);

        void ondownloadCancelled(int downloadId);

        void checkConnectionForRaspberry();
    }

    ;

    interface contentClick {
        void onfolderClicked(int position, Modal_ContentDetail contentDetail);

        void onDownloadClicked(int position, Modal_ContentDetail contentDetail);

        void openContent(int position, Modal_ContentDetail contentDetail);
    }

    interface mainView {
        void showNotificationBadge(int downloadNumber);

        void hideNotificationBadge(int number);
    }
}
