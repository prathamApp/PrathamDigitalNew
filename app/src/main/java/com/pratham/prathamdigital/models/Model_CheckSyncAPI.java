package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

//Used to get response from check sync API
public class Model_CheckSyncAPI {

    @SerializedName("app_version")
    private String appVersion;
    @SerializedName("status_code")
    private String statusCode;
    @SerializedName("status")
    private String status;
    @SerializedName("maintenance_closing_time")
    private String maintenanceClosingTime;
    @SerializedName("message")
    private String message;
    @SerializedName("last_updated")
    private String lastUpdated;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaintenanceClosingTime() {
        return maintenanceClosingTime;
    }

    public void setMaintenanceClosingTime(String maintenanceClosingTime) {
        this.maintenanceClosingTime = maintenanceClosingTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
