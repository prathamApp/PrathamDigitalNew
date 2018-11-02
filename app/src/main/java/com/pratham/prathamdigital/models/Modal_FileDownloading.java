package com.pratham.prathamdigital.models;

public class Modal_FileDownloading {
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
}
