package com.pratham.prathamdigital.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "CourseEnrolled")
public class Model_CourseEnrollment {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("c_autoID")
    private int c_autoID;
    @SerializedName("courseId")
    private String courseId;
    @SerializedName("groupId")
    private String groupId;
    @SerializedName("planFromDate")
    private String planFromDate;
    @SerializedName("planToDate")
    private String planToDate;
    @SerializedName("coachVerified")
    private boolean coachVerified;
    @SerializedName("coachVerificationDate")
    private String coachVerificationDate;
    @SerializedName("courseExperience")
    private String courseExperience;
    @SerializedName("sentFlag")
    public int sentFlag;
    @Ignore
    private Modal_ContentDetail courseDetail;

    public int getC_autoID() {
        return c_autoID;
    }

    public void setC_autoID(int c_autoID) {
        this.c_autoID = c_autoID;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPlanFromDate() {
        return planFromDate;
    }

    public void setPlanFromDate(String planFromDate) {
        this.planFromDate = planFromDate;
    }

    public String getPlanToDate() {
        return planToDate;
    }

    public void setPlanToDate(String planToDate) {
        this.planToDate = planToDate;
    }

    public boolean isCoachVerified() {
        return coachVerified;
    }

    public void setCoachVerified(boolean coachVerified) {
        this.coachVerified = coachVerified;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    public String getCoachVerificationDate() {
        return coachVerificationDate;
    }

    public void setCoachVerificationDate(String coachVerificationDate) {
        this.coachVerificationDate = coachVerificationDate;
    }

    public String getCourseExperience() {
        return courseExperience;
    }

    public void setCourseExperience(String courseExperience) {
        this.courseExperience = courseExperience;
    }

    public Modal_ContentDetail getCourseDetail() {
        return courseDetail;
    }

    public void setCourseDetail(Modal_ContentDetail courseDetail) {
        this.courseDetail = courseDetail;
    }
}
