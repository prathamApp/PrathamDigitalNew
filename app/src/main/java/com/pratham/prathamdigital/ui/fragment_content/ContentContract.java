package com.pratham.prathamdigital.ui.fragment_content;

import android.view.View;

import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.ArrayList;
import java.util.List;

public interface ContentContract {
    interface contentView {
        void showNoConnectivity();

        void displayContents(List<Modal_ContentDetail> content);

        void displayHeader(Modal_ContentDetail contentDetail);

        void hideViews();

        void exitApp();

        void increaseNotification(int number);

        void decreaseNotification(int number, Modal_ContentDetail detail, ArrayList<String> selectedNodeIds);

        void displayLevel(ArrayList<Modal_ContentDetail> levelContents);

        void onDownloadError(String file_name, ArrayList<String> selectedNodeIds);
    }

    interface contentPresenter {
        void setView(FragmentContent context);

        void downloadContent(Modal_ContentDetail contentDetail);

        void fileDownloadStarted(int downloadId, String filename, Modal_ContentDetail contentDetail);

        void updateFileProgress(int downloadId, String filename, int progress);

        void onDownloadCompleted(int downloadId);

        void onDownloadPaused(int downloadId);

        void ondownloadCancelled(int downloadId);

        void ondownloadError(String f_name);

        void checkConnectionForRaspberry();

        void showPreviousContent();

        void getContent(Modal_ContentDetail contentDetail);

        void getLevels();

        void eventFileDownloadStarted(EventMessage message);

        void eventUpdateFileProgress(EventMessage message);

        void eventOnDownloadCompleted(EventMessage message);
    }

    interface contentClick {
        void onfolderClicked(int position, Modal_ContentDetail contentDetail);

        void onDownloadClicked(int position, Modal_ContentDetail contentDetail, View reveal_view);

        void openContent(int position, Modal_ContentDetail contentDetail);
    }

    interface mainView {
//        void showNotificationBadge(int downloadNumber);
//
//        void hideNotificationBadge(int number);
    }
}
