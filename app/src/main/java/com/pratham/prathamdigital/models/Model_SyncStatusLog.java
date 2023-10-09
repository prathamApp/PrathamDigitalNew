package com.pratham.prathamdigital.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "SyncStatusLog")
public class Model_SyncStatusLog {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int syncStatusId;
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("PushId")
    private String pushId;
    @SerializedName("SyncId")
    private String syncId;
    @SerializedName("PushDate")
    private String pushDate;
    @SerializedName("PushStatus")
    private String pushStatus;
    @SerializedName("DeviceId")
    private String deviceId;
    @SerializedName("ScorePushed")
    private Integer scorePushed;
    @SerializedName("ScoreSynced")
    private Integer scoreSynced;
    @SerializedName("ScoreError")
    private Integer scoreError;
    @SerializedName("AttendancePushed")
    private Integer attendancePushed;
    @SerializedName("AttendanceSynced")
    private Integer attendanceSynced;
    @SerializedName("AttendanceError")
    private Integer attendanceError;
    @SerializedName("StudentPushed")
    private Integer studentPushed;
    @SerializedName("StudentSynced")
    private Integer studentSynced;
    @SerializedName("StudentError")
    private Integer studentError;
    @SerializedName("SessionCount")
    private Integer sessionCount;
    @SerializedName("SessionSynced")
    private Integer sessionSynced;
    @SerializedName("SessionError")
    private Integer sessionError;
    @SerializedName("cpCount")
    private Integer cpCount;
    @SerializedName("cpSynced")
    private Integer cpSynced;
    @SerializedName("cpError")
    private Integer cpError;
    @SerializedName("KeywordsCount")
    private Integer keywordsCount;
    @SerializedName("KeywordsSynced")
    private Integer keywordsSynced;
    @SerializedName("KeywordsError")
    private Integer keywordsError;
    @SerializedName("logsCount")
    private Integer logsCount;
    @SerializedName("logsSynced")
    private Integer logsSynced;
    @SerializedName("logsError")
    private Integer logsError;
    @SerializedName("CourseEnrollmentCount")
    private Integer courseEnrollmentCount;
    @SerializedName("CourseEnrollmentSynced")
    private Integer courseEnrollmentSynced;
    @SerializedName("CourseEnrollmentError")
    private Integer courseEnrollmentError;
    @SerializedName("GroupsDataCount")
    private Integer groupsDataCount;
    @SerializedName("GroupsDataSynced")
    private Integer groupsDataSynced;
    @SerializedName("GroupsDataError")
    private Integer groupsDataError;
    @SerializedName("LastChecked")
    private String lastChecked;
    @SerializedName("Error")
    private String error;

    public int sentFlag;

    public int getSyncStatusId() {
        return syncStatusId;
    }

    public void setSyncStatusId(int syncStatusId) {
        this.syncStatusId = syncStatusId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public String getPushDate() {
        return pushDate;
    }

    public void setPushDate(String pushDate) {
        this.pushDate = pushDate;
    }

    public String getPushStatus() {
        return pushStatus;
    }

    public void setPushStatus(String pushStatus) {
        this.pushStatus = pushStatus;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getScorePushed() {
        return scorePushed;
    }

    public void setScorePushed(Integer scorePushed) {
        this.scorePushed = scorePushed;
    }

    public Integer getScoreSynced() {
        return scoreSynced;
    }

    public void setScoreSynced(Integer scoreSynced) {
        this.scoreSynced = scoreSynced;
    }

    public Integer getScoreError() {
        return scoreError;
    }

    public void setScoreError(Integer scoreError) {
        this.scoreError = scoreError;
    }

    public Integer getAttendancePushed() {
        return attendancePushed;
    }

    public void setAttendancePushed(Integer attendancePushed) {
        this.attendancePushed = attendancePushed;
    }

    public Integer getAttendanceSynced() {
        return attendanceSynced;
    }

    public void setAttendanceSynced(Integer attendanceSynced) {
        this.attendanceSynced = attendanceSynced;
    }

    public Integer getAttendanceError() {
        return attendanceError;
    }

    public void setAttendanceError(Integer attendanceError) {
        this.attendanceError = attendanceError;
    }

    public Integer getStudentPushed() {
        return studentPushed;
    }

    public void setStudentPushed(Integer studentPushed) {
        this.studentPushed = studentPushed;
    }

    public Integer getStudentSynced() {
        return studentSynced;
    }

    public void setStudentSynced(Integer studentSynced) {
        this.studentSynced = studentSynced;
    }

    public Integer getStudentError() {
        return studentError;
    }

    public void setStudentError(Integer studentError) {
        this.studentError = studentError;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public Integer getSessionSynced() {
        return sessionSynced;
    }

    public void setSessionSynced(Integer sessionSynced) {
        this.sessionSynced = sessionSynced;
    }

    public Integer getSessionError() {
        return sessionError;
    }

    public void setSessionError(Integer sessionError) {
        this.sessionError = sessionError;
    }

    public Integer getCpCount() {
        return cpCount;
    }

    public void setCpCount(Integer cpCount) {
        this.cpCount = cpCount;
    }

    public Integer getCpSynced() {
        return cpSynced;
    }

    public void setCpSynced(Integer cpSynced) {
        this.cpSynced = cpSynced;
    }

    public Integer getCpError() {
        return cpError;
    }

    public void setCpError(Integer cpError) {
        this.cpError = cpError;
    }

    public Integer getKeywordsCount() {
        return keywordsCount;
    }

    public void setKeywordsCount(Integer keywordsCount) {
        this.keywordsCount = keywordsCount;
    }

    public Integer getKeywordsSynced() {
        return keywordsSynced;
    }

    public void setKeywordsSynced(Integer keywordsSynced) {
        this.keywordsSynced = keywordsSynced;
    }

    public Integer getKeywordsError() {
        return keywordsError;
    }

    public void setKeywordsError(Integer keywordsError) {
        this.keywordsError = keywordsError;
    }

    public Integer getLogsCount() {
        return logsCount;
    }

    public void setLogsCount(Integer logsCount) {
        this.logsCount = logsCount;
    }

    public Integer getLogsSynced() {
        return logsSynced;
    }

    public void setLogsSynced(Integer logsSynced) {
        this.logsSynced = logsSynced;
    }

    public Integer getLogsError() {
        return logsError;
    }

    public void setLogsError(Integer logsError) {
        this.logsError = logsError;
    }

    public Integer getCourseEnrollmentCount() {
        return courseEnrollmentCount;
    }

    public void setCourseEnrollmentCount(Integer courseEnrollmentCount) {
        this.courseEnrollmentCount = courseEnrollmentCount;
    }

    public Integer getCourseEnrollmentSynced() {
        return courseEnrollmentSynced;
    }

    public void setCourseEnrollmentSynced(Integer courseEnrollmentSynced) {
        this.courseEnrollmentSynced = courseEnrollmentSynced;
    }

    public Integer getCourseEnrollmentError() {
        return courseEnrollmentError;
    }

    public void setCourseEnrollmentError(Integer courseEnrollmentError) {
        this.courseEnrollmentError = courseEnrollmentError;
    }

    public Integer getGroupsDataCount() {
        return groupsDataCount;
    }

    public void setGroupsDataCount(Integer groupsDataCount) {
        this.groupsDataCount = groupsDataCount;
    }

    public Integer getGroupsDataSynced() {
        return groupsDataSynced;
    }

    public void setGroupsDataSynced(Integer groupsDataSynced) {
        this.groupsDataSynced = groupsDataSynced;
    }

    public Integer getGroupsDataError() {
        return groupsDataError;
    }

    public void setGroupsDataError(Integer groupsDataError) {
        this.groupsDataError = groupsDataError;
    }

    public String getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(String lastChecked) {
        this.lastChecked = lastChecked;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}