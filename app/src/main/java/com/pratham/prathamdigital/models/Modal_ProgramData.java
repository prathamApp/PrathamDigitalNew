package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

public class Modal_ProgramData {
    @SerializedName("programId")
    String programId;
    @SerializedName("State")
    String State;
    @SerializedName("programName")
    String programName;

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}