package com.pratham.prathamdigital.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

//Used to receive response from server after pushing zip. Response is added in DB
@Entity(tableName = "SyncLog")
public class Model_NewSyncLog {
    @androidx.annotation.NonNull
    @PrimaryKey
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("PushDate")
    private String pushDate;
    @NonNull
    @SerializedName("PushId")
    private Integer pushId;
    @SerializedName("Error")
    private String error;
    @SerializedName("Status")
    private String status;

    private String pushType;
    @NonNull
    private Integer sentFlag;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPushDate() {
        return pushDate;
    }

    public void setPushDate(String pushDate) {
        this.pushDate = pushDate;
    }

    public Integer getPushId() {
        return pushId;
    }

    public void setPushId(Integer pushId) {
        this.pushId = pushId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public Integer getSentFlag() {return sentFlag;}

    public void setSentFlag(Integer sentFlag) {this.sentFlag = sentFlag;}
}