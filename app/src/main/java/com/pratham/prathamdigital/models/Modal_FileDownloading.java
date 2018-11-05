package com.pratham.prathamdigital.models;

import android.support.annotation.NonNull;

public class Modal_FileDownloading implements Comparable {
    int downloadId;
    String filename;
    int progress;
    Modal_ContentDetail contentDetail;

    public Modal_ContentDetail getContentDetail() {
        return contentDetail;
    }

    public void setContentDetail(Modal_ContentDetail contentDetail) {
        this.contentDetail = contentDetail;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Modal_FileDownloading compare = (Modal_FileDownloading) o;
        if (compare.getContentDetail().getNodeid() != null) {
            if (compare.getDownloadId() == (this.downloadId) && compare.getProgress() == this.progress)
                return 0;
            else return 1;
        } else
            return 0;
    }
}
