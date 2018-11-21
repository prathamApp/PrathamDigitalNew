package com.pratham.prathamdigital.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "CRL")
public class Modal_Crl {
    @NonNull
    @PrimaryKey
    @SerializedName("CRLId")
    String CRLId;
    @SerializedName("RoleId")
    String RoleId;
    @SerializedName("RoleName")
    String RoleName;
    @SerializedName("ProgramId")
    String ProgramId;
    @SerializedName("ProgramName")
    String ProgramName;
    @SerializedName("State")
    String State;
    @SerializedName("FirstName")
    String FirstName;
    @SerializedName("LastName")
    String LastName;
    @SerializedName("Mobile")
    String Mobile;
    @SerializedName("Email")
    String Email;
    @SerializedName("Block")
    String Block;
    @SerializedName("District")
    String District;
    @SerializedName("UserName")
    String UserName;
    @SerializedName("Password")
    String Password;

    @Override
    public String toString() {
        return "Modal_Crl{" +
                "CRLId='" + CRLId + '\'' +
                ", RoleId='" + RoleId + '\'' +
                ", RoleName='" + RoleName + '\'' +
                ", ProgramId='" + ProgramId + '\'' +
                ", ProgramName='" + ProgramName + '\'' +
                ", State='" + State + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", Mobile='" + Mobile + '\'' +
                ", Email='" + Email + '\'' +
                ", Block='" + Block + '\'' +
                ", District='" + District + '\'' +
                ", UserName='" + UserName + '\'' +
                ", Password='" + Password + '\'' +
                '}';
    }

    @NonNull
    public String getCRLId() {
        return CRLId;
    }

    public void setCRLId(@NonNull String CRLId) {
        this.CRLId = CRLId;
    }

    public String getRoleId() {
        return RoleId;
    }

    public void setRoleId(String roleId) {
        RoleId = roleId;
    }

    public String getRoleName() {
        return RoleName;
    }

    public void setRoleName(String roleName) {
        RoleName = roleName;
    }

    public String getProgramId() {
        return ProgramId;
    }

    public void setProgramId(String programId) {
        ProgramId = programId;
    }

    public String getProgramName() {
        return ProgramName;
    }

    public void setProgramName(String programName) {
        ProgramName = programName;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBlock() {
        return Block;
    }

    public void setBlock(String block) {
        Block = block;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
