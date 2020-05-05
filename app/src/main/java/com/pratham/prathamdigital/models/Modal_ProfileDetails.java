package com.pratham.prathamdigital.models;

//Modal Class to show RecyclerView Contents
public class Modal_ProfileDetails {
    String date, videoCnt, gameCnt, pdfCnt;
    boolean expanded;

    public Modal_ProfileDetails(String date, String videoCnt, String gameCnt, String pdfCnt) {
        this.date = date;
        this.videoCnt = videoCnt;
        this.gameCnt = gameCnt;
        this.pdfCnt = pdfCnt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVideoCnt() {
        return videoCnt;
    }

    public void setVideoCnt(String videoCnt) {
        this.videoCnt = videoCnt;
    }

    public String getGameCnt() {
        return gameCnt;
    }

    public void setGameCnt(String gameCnt) {
        this.gameCnt = gameCnt;
    }

    public String getPdfCnt() {
        return pdfCnt;
    }

    public void setPdfCnt(String pdfCnt) {
        this.pdfCnt = pdfCnt;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }
}
