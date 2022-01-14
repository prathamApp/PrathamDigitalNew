package com.pratham.prathamdigital.ui.fragment_content;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.liulishuo.okdownload.DownloadTask;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_FileDownloading;

import java.util.ArrayList;
import java.util.List;

public interface ContentContract {
    interface contentView {
        void showNoConnectivity();

        void displayContents(List<Modal_ContentDetail> content);

        void displayContentsInCourse(Modal_ContentDetail contentDetail, List<Modal_ContentDetail> content);

        void displayContentsInCourseNew(Modal_ContentDetail contentDetail, List<Modal_ContentDetail> content, List<Modal_ContentDetail> content1);

        void displayHeader(Modal_ContentDetail contentDetail);

        void hideViews();

        void exitApp();

        void increaseNotification(int number);

        void decreaseNotification(int number, Modal_ContentDetail detail, ArrayList<String> selectedNodeIds);

        void displayLevel(ArrayList<Modal_ContentDetail> levelContents, String isCourse);

        void onDownloadError(EventMessage message);

        void dismissDialog();

        void animateHamburger();

        void displayDLContents(List<Modal_ContentDetail> details);
    }

    interface contentPresenter {
        void setView(ContentContract.contentView context);

        void downloadContent(Modal_ContentDetail contentDetail);

        void ondownloadError(String downloadId);

        void checkConnectionForRaspberry();

        void showPreviousContent();

        void getContent(Modal_ContentDetail contentDetail, String isCourse);

        void eventFileDownloadStarted(EventMessage message);

        void eventUpdateFileProgress(EventMessage message);

        void eventOnDownloadCompleted(EventMessage message);

        void eventOnDownloadFailed(EventMessage message);

        void fileDownloadStarted(String downloadID, Modal_FileDownloading modal_fileDownloading);

        void updateFileProgress(String downloadID, Modal_FileDownloading mfd);

        void onDownloadCompleted(String downloadID, Modal_ContentDetail content, Context context);

        void broadcast_downloadings();

        void parseSD_UriandPath(Intent data);

        void getContent();

        void viewDestroyed();

        void deleteContent(Modal_ContentDetail contentItem);

        void currentDownloadRunning(String nodeid, DownloadTask task);

        void cancelDownload(String downloadId);

        void openDeepLinkContent(String string);

        List<Modal_ContentDetail> getContentList();

        boolean isFilesDownloading();
    }

    interface contentClick {
        void onfolderClicked(int position, Modal_ContentDetail contentDetail);

        void onCourseClicked(int position, Modal_ContentDetail contentDetail, List<Modal_ContentDetail> childs);

        void onAssessmentItemClicked(Modal_ContentDetail modal_contentDetail);

        void onDownloadClicked(int position, Modal_ContentDetail contentDetail, View reveal_view, View start_reveal_item);

        void openContent(int position, Modal_ContentDetail contentDetail);

        void deleteContent(int pos, Modal_ContentDetail contentItem);
    }

    interface mainView {
//        void showNotificationBadge(int downloadNumber);
//
//        void hideNotificationBadge(int number);
    }
}
