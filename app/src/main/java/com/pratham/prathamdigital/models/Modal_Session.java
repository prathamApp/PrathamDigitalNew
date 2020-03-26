package com.pratham.prathamdigital.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Session")
public class Modal_Session {
    @PrimaryKey
    @NonNull
    @SerializedName("SessionID")
    public String SessionID;
    @SerializedName("fromDate")
    public String fromDate;
    @SerializedName("toDate")
    public String toDate;
    @SerializedName("sentFlag")
    public int sentFlag;

    @NonNull
    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(@NonNull String sessionID) {
        SessionID = sessionID;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}
