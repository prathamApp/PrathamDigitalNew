package com.pratham.prathamdigital.models;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Status")
public class Modal_Status {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "statusID")
    private int statusID;
    @ColumnInfo(name = "statusKey")
    private String statusKey;
    @NonNull
    @ColumnInfo(name = "value")
    private String value="";
    @ColumnInfo(name = "description")
    private String description;

    @Override
    public String toString() {
        return "Modal_Status{" +
                "statusID=" + statusID +
                ", statusKey='" + statusKey + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    public void setValue(@NonNull String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}