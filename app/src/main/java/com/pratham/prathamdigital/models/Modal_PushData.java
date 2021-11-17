package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Modal_PushData {
    @SerializedName("session")
    private List<Modal_PushSessionData> pushSession;
    @SerializedName("students")
    private List<Modal_Student> students;
    @SerializedName("course_enrolled")
    private List<Model_CourseEnrollment> course_enrolled;
    @SerializedName("course_progress")
    private List<Model_ContentProgress> course_progress;

    public List<Modal_PushSessionData> getPushSession() {
        return pushSession;
    }

    public List<Modal_Student> getStudents() {
        return students;
    }

    public void setStudents(List<Modal_Student> students) {
        this.students = students;
    }

    public List<Model_CourseEnrollment> getCourse_enrolled() {
        return course_enrolled;
    }

    public void setCourse_enrolled(List<Model_CourseEnrollment> course_enrolled) {
        this.course_enrolled = course_enrolled;
    }

    public List<Model_ContentProgress> getCourse_progress() {
        return course_progress;
    }

    public void setCourse_progress(List<Model_ContentProgress> course_progress) {
        this.course_progress = course_progress;
    }

    public class Modal_PushSessionData {
        @SerializedName("sessionid")
        String sessionId;
        @SerializedName("scores")
        List<Modal_Score> scores;
        @SerializedName("attendances")
        List<Attendance> attendances;
        @SerializedName("logs")
        List<Modal_Log> logs;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public List<Modal_Score> getScores() {
            return scores;
        }

        public void setScores(List<Modal_Score> scores) {
            this.scores = scores;
        }

        public List<Attendance> getAttendances() {
            return attendances;
        }

        public void setAttendances(List<Attendance> attendances) {
            this.attendances = attendances;
        }

        public List<Modal_Log> getLogs() { return logs; }

        public void setLogs(List<Modal_Log> logs) { this.logs = logs; }

    }
}
