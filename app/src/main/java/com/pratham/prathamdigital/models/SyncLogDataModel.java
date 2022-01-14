package com.pratham.prathamdigital.models;

//used to store course count and sync time in log detail column in log table
public class SyncLogDataModel {
    private String syncTime;
    private Integer syncCourseEnrollmentLength;
    private String syncDataLength;
    private String syncMediaLength;
    private String scoreTable;
    private String mediaCount;
    private String coursesCount;

    public String getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(String syncTime) {
        this.syncTime = syncTime;
    }

    public Integer getSyncCourseEnrollmentLength() {
        return syncCourseEnrollmentLength;
    }

    public void setSyncCourseEnrollmentLength(Integer syncCourseEnrollmentLength) {
        this.syncCourseEnrollmentLength = syncCourseEnrollmentLength;
    }

    public String getSyncDataLength() {
        return syncDataLength;
    }

    public void setSyncDataLength(String syncDataLength) {
        this.syncDataLength = syncDataLength;
    }

    public String getSyncMediaLength() {
        return syncMediaLength;
    }

    public void setSyncMediaLength(String syncMediaLength) {
        this.syncMediaLength = syncMediaLength;
    }

    public String getScoreTable() {
        return scoreTable;
    }

    public void setScoreTable(String scoreTable) {
        this.scoreTable = scoreTable;
    }

    public String getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(String mediaCount) {
        this.mediaCount = mediaCount;
    }

    public String getCoursesCount() {
        return coursesCount;
    }

    public void setCoursesCount(String coursesCount) {
        this.coursesCount = coursesCount;
    }

}