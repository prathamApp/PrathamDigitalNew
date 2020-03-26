package com.pratham.prathamdigital.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Status")
public class Modal_Status {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "statusKey")
    public String statusKey;
    @NonNull
    @ColumnInfo(name = "value")
    public String value = "";
    @ColumnInfo(name = "description")
    public String description;

    @NonNull
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