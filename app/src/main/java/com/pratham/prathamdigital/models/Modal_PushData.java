package com.pratham.prathamdigital.models;

import java.util.List;

public class Modal_PushData {
    Modal_PushSession pushSession;
    List<Modal_Student> students;

    public Modal_PushSession getPushSession() {
        return pushSession;
    }

    public void setPushSession(Modal_PushSession pushSession) {
        this.pushSession = pushSession;
    }

    public List<Modal_Student> getStudents() {
        return students;
    }

    public void setStudents(List<Modal_Student> students) {
        this.students = students;
    }

    public class Modal_PushSession {
        List<Modal_PushSessionData> pushSessionData;

        public List<Modal_PushSessionData> getPushSessionData() {
            return pushSessionData;
        }

        public void setPushSessionData(List<Modal_PushSessionData> pushSessionData) {
            this.pushSessionData = pushSessionData;
        }

        public class Modal_PushSessionData {
            String sessionId;
            List<Modal_Score> scores;
            List<Attendance> attendances;

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
        }
    }

}
