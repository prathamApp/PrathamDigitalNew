package com.pratham.prathamdigital.ui.fragment_content;

import android.view.View;

import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.ArrayList;

public interface ContentContract {
    interface contentView {
        void showNoConnectivity();

        void displayContents(ArrayList<Modal_ContentDetail> content);

        void displayHeader(Modal_ContentDetail contentDetail);

        void hideViews();

        void exitApp();

        void increaseNotification(int number);

        void decreaseNotification(int number, Modal_ContentDetail detail, ArrayList<String> selectedNodeIds);

        void displayLevel(ArrayList<Modal_ContentDetail> levelContents);

        void onDownloadError(String file_name, ArrayList<String> selectedNodeIds);
    }

    interface contentPresenter {
        void recievedContent(String header, String response, ArrayList<Modal_ContentDetail> contentList);

        void recievedError(String header, ArrayList<Modal_ContentDetail> contentList);

        void downloadContent(Modal_ContentDetail contentDetail);

        void fileDownloadStarted(int downloadId, String filename, Modal_ContentDetail contentDetail);

        void updateFileProgress(int downloadId, String filename, int progress);

        void onDownloadCompleted(int downloadId);

        void onDownloadPaused(int downloadId);

        void ondownloadCancelled(int downloadId);

        void ondownloadError(String f_name);

        void checkConnectionForRaspberry();
    }

    ;

    interface contentClick {
        void onfolderClicked(int position, Modal_ContentDetail contentDetail);

        void onDownloadClicked(int position, Modal_ContentDetail contentDetail, View reveal_view);

        void openContent(int position, Modal_ContentDetail contentDetail);
    }

    interface mainView {
        void showNotificationBadge(int downloadNumber);

        void hideNotificationBadge(int number);
    }
}
