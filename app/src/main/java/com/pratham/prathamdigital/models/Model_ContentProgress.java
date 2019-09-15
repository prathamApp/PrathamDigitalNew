package com.pratham.prathamdigital.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "ContentProgress")
public class Model_ContentProgress {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("progressId")
    private int progressId;
    @SerializedName("studentId")
    private String studentId;
    @SerializedName("resourceId")
    private String resourceId;
    @SerializedName("updatedDateTime")
    private String updatedDateTime;
    @SerializedName("progressPercentage")
    private String progressPercentage;
    @SerializedName("label")
    private String label;
    @SerializedName("sentFlag")
    private boolean sentFlag;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(String updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public String getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(String progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public boolean isSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(boolean sentFlag) {
        this.sentFlag = sentFlag;
    }
}
