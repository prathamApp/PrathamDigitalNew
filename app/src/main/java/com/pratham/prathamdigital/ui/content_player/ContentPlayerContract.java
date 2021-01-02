package com.pratham.prathamdigital.ui.content_player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;

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

        void addContentProgress(String pdfResId);

        Model_CourseEnrollment getCourse();
    }

    interface courseDetailAdapterClick {
        void onChildItemClicked(Modal_ContentDetail modal_contentDetail);

        void onAssessmentItemClicked();

        void onDownloadClicked(int position, Modal_ContentDetail contentDetail, View reveal_view, View start_reveal_item);
    }

    interface assignment_submission {
        void addFileClicked(int adapterPosition);

        void onFilePreviewClicked(String file_path);

        void deleteFile(View itemView, int selfPosition, int position);
    }
}
