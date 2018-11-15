package com.pratham.prathamdigital.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Students")
public class Modal_Student {
    @SerializedName("GroupId")
    public String GroupId;
    @SerializedName("GroupName")
    public String GroupName;
    @SerializedName("FullName")
    public String FullName;
    @SerializedName("FirstName")
    public String FirstName;
    @SerializedName("MiddleName")
    public String MiddleName;
    @SerializedName("LastName")
    public String LastName;
    @SerializedName("Class")
    public String Stud_Class;
    @SerializedName("Age")
    public String Age;
    @SerializedName("Gender")
    public String Gender;
    @SerializedName("sentFlag")
    public int sentFlag;
    @NonNull
    @PrimaryKey
    @SerializedName("StudentId")
    public String StudentId;
    @Ignore
    transient boolean isChecked = false;

    public Modal_Student() {

    }
    public Modal_Student(String sid, String sname, String qrGroupID) {
        this.StudentId = sid;
        this.FirstName = sname;
        this.GroupId = qrGroupID;
    }

    @Override
    public String toString() {
        return "Modal_Student{" +
                "GroupId='" + GroupId + '\'' +
                ", GroupName='" + GroupName + '\'' +
                ", FullName='" + FullName + '\'' +
                ", Stud_Class='" + Stud_Class + '\'' +
                ", Age='" + Age + '\'' +
                ", Gender='" + Gender + '\'' +
                ", sentFlag=" + sentFlag +
                ", StudentId='" + StudentId + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getStud_Class() {
        return Stud_Class;
    }

    public void setStud_Class(String stud_Class) {
        Stud_Class = stud_Class;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @NonNull
    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(@NonNull String studentId) {
        StudentId = studentId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}