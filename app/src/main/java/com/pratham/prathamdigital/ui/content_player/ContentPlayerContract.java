package com.pratham.prathamdigital.ui.content_player;

import android.content.Intent;
import android.os.Bundle;

import com.pratham.prathamdigital.models.Modal_ContentDetail;

public interface ContentPlayerContract {
    interface contentPlayerView {
        void showCourseDetail(Bundle courseDetailBundle);

        void showGame(Bundle gBundle);

        void showVideo(Bundle vidBundle);

        void showPdf(Bundle pdfBundle);

        void showNextContentPlayingTimer();

        void onCourseCompleted();
    }

    interface contentPlayerPresenter {
        void setCourse(Intent intent);

        void setView(Activity_ContentPlayer activity_contentPlayer);

        void categorizeIntent(Intent intent);

        void resumeCourse();

        void showNextContent(String contentId);

        void playSpecificCourseContent(String contentId);

        boolean hasNextContentInQueue();
    }

    interface courseDetailAdapterClick {
        void onChildItemClicked(Modal_ContentDetail modal_contentDetail);
    }
}
