package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Modal_Enrollment {

    @SerializedName("DeviceId")
    private String mDeviceId;
    @SerializedName("EnrollmentType")
    private String mEnrollmentType;
    @SerializedName("GroupCode")
    private String mGroupCode;
    @SerializedName("GroupEnrollment")
    private String mGroupEnrollment;
    @SerializedName("GroupId")
    private String mGroupId;
    @SerializedName("GroupName")
    private String mGroupName;
    @SerializedName("lstCourseEnroll")
    private List<LstCourseEnroll> mLstCourseEnroll;
    @SerializedName("lstStudent")
    private List<LstStudent> mLstStudent;
    @SerializedName("ProgramId")
    private Integer mProgramId;
    @SerializedName("SchoolName")
    private String mSchoolName;
    @SerializedName("VIllageName")
    private String mVIllageName;
    @SerializedName("VillageId")
    private String mVillageId;

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
    }

    public String getEnrollmentType() {
        return mEnrollmentType;
    }

    public void setEnrollmentType(String enrollmentType) {
        mEnrollmentType = enrollmentType;
    }

    public String getGroupCode() {
        return mGroupCode;
    }

    public void setGroupCode(String groupCode) {
        mGroupCode = groupCode;
    }

    public String getGroupEnrollment() {
        return mGroupEnrollment;
    }

    public void setGroupEnrollment(String groupEnrollment) {
        mGroupEnrollment = groupEnrollment;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String groupId) {
        mGroupId = groupId;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public List<LstCourseEnroll> getLstCourseEnroll() {
        return mLstCourseEnroll;
    }

    public void setLstCourseEnroll(List<LstCourseEnroll> lstCourseEnroll) {
        mLstCourseEnroll = lstCourseEnroll;
    }

    public List<LstStudent> getLstStudent() {
        return mLstStudent;
    }

    public void setLstStudent(List<LstStudent> lstStudent) {
        mLstStudent = lstStudent;
    }

    public Integer getProgramId() {
        return mProgramId;
    }

    public void setProgramId(Integer programId) {
        mProgramId = programId;
    }

    public String getSchoolName() {
        return mSchoolName;
    }

    public void setSchoolName(String schoolName) {
        mSchoolName = schoolName;
    }

    public String getVIllageName() {
        return mVIllageName;
    }

    public void setVIllageName(String vIllageName) {
        mVIllageName = vIllageName;
    }

    public String getVillageId() {
        return mVillageId;
    }

    public void setVillageId(String villageId) {
        mVillageId = villageId;
    }

}