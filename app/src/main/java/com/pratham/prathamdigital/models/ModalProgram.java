package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

public class ModalProgram {
    @SerializedName("ProgramId")
    String ProgramId;
    @SerializedName("ProgramName")
    String ProgramName;
    @SerializedName("programId")
    String kolibriProgramId;
    @SerializedName("programName")
    String kolibriProgramName;

    public String getKolibriProgramId() {
        return kolibriProgramId;
    }

    public void setKolibriProgramId(String kolibriProgramId) {
        this.kolibriProgramId = kolibriProgramId;
    }

    public String getKolibriProgramName() {
        return kolibriProgramName;
    }

    public void setKolibriProgramName(String kolibriProgramName) {
        this.kolibriProgramName = kolibriProgramName;
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
}
