package com.pratham.prathamdigital.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Session")
public class Modal_Session {
    @PrimaryKey
    @NonNull
    @SerializedName("SessionID")
    private String SessionID;
    @SerializedName("fromDate")
    private String fromDate;
    @SerializedName("toDate")
    private String toDate;

    @Override
    public String toString() {
        return "Modal_Session{" +
                "SessionID='" + SessionID + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                '}';
    }

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
}
