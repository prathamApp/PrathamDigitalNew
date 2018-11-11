package com.pratham.prathamdigital.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Score")
public class Modal_Score {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("ScoreId")
    private int ScoreId;
    @SerializedName("SessionID")
    private String SessionID;
    @SerializedName("StudentID")
    private String StudentID;
    @SerializedName("DeviceID")
    private String DeviceID;
    @SerializedName("ResourceID")
    private String ResourceID;
    @SerializedName("QuestionId")
    private int QuestionId;
    @SerializedName("ScoredMarks")
    private int ScoredMarks;
    @SerializedName("TotalMarks")
    private int TotalMarks;
    @SerializedName("StartDateTime")
    private String StartDateTime;
    @SerializedName("EndDateTime")
    private String EndDateTime;
    @SerializedName("Level")
    private int Level;
    private int sentFlag;

    @Override
    public String toString() {
        return "Modal_Score{" +
                "ScoreId='" + ScoreId + '\'' +
                ", SessionID='" + SessionID + '\'' +
                ", StudentID='" + StudentID + '\'' +
                ", DeviceID='" + DeviceID + '\'' +
                ", ResourceID='" + ResourceID + '\'' +
                ", QuestionId=" + QuestionId +
                ", ScoredMarks=" + ScoredMarks +
                ", TotalMarks=" + TotalMarks +
                ", StartDateTime='" + StartDateTime + '\'' +
                ", EndDateTime='" + EndDateTime + '\'' +
                ", Level=" + Level +
                ", sentFlag=" + sentFlag +
                '}';
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    @NonNull
    public int getScoreId() {
        return ScoreId;
    }

    public void setScoreId(@NonNull int scoreId) {
        ScoreId = scoreId;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getResourceID() {
        return ResourceID;
    }

    public void setResourceID(String resourceID) {
        ResourceID = resourceID;
    }

    public int getQuestionId() {
        return QuestionId;
    }

    public void setQuestionId(int questionId) {
        QuestionId = questionId;
    }

    public int getScoredMarks() {
        return ScoredMarks;
    }

    public void setScoredMarks(int scoredMarks) {
        ScoredMarks = scoredMarks;
    }

    public int getTotalMarks() {
        return TotalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        TotalMarks = totalMarks;
    }

    public String getStartDateTime() {
        return StartDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        StartDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return EndDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        EndDateTime = endDateTime;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }
}