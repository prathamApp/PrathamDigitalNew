package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

public class LstStudent {

    @SerializedName("Age")
    private String mAge;
    @SerializedName("Class")
    private String mClass;
    @SerializedName("FullName")
    private String mFullName;
    @SerializedName("Gender")
    private String mGender;
    @SerializedName("GroupId")
    private String mGroupId;
    @SerializedName("GroupName")
    private String mGroupName;
    @SerializedName("StudentEnrollment")
    private String mStudentEnrollment;
    @SerializedName("StudentId")
    private String mStudentId;

    public String getAge() {
        return mAge;
    }

    public void setAge(String age) {
        mAge = age;
    }

    public String getClasss() {
        return mClass;
    }

    public void setClasss(String classs) {
        mClass = classs;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
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

    public String getStudentEnrollment() {
        return mStudentEnrollment;
    }

    public void setStudentEnrollment(String studentEnrollment) {
        mStudentEnrollment = studentEnrollment;
    }

    public String getStudentId() {
        return mStudentId;
    }

    public void setStudentId(String studentId) {
        mStudentId = studentId;
    }

}
