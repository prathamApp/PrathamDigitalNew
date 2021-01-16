package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

public class LstCourseEnroll {

    @SerializedName("CourseId")
    private String mCourseId;
    @SerializedName("GroupId")
    private String mGroupId;
    @SerializedName("planFromDate")
    private String mPlanFromDate;
    @SerializedName("planToDate")
    private String mPlanToDate;
    @SerializedName("language")
    private String Language;

    public String getCourseId() {
        return mCourseId;
    }

    public void setCourseId(String courseId) {
        mCourseId = courseId;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String groupId) {
        mGroupId = groupId;
    }

    public String getPlanFromDate() {
        return mPlanFromDate;
    }

    public void setPlanFromDate(String planFromDate) {
        mPlanFromDate = planFromDate;
    }

    public String getPlanToDate() {
        return mPlanToDate;
    }

    public void setPlanToDate(String planToDate) {
        mPlanToDate = planToDate;
    }

    public String getLanguage() { return Language; }

    public void setLanguage(String language) { Language = language; }

}
