package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.prathamdigital.models.Attendance;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAttendance(List<Attendance> attendancesList);

    @Query("DELETE FROM Attendance")
    public void deleteAllAttendances();

    @Query("SELECT * FROM Attendance")
    public List<Attendance> getAllAttendances();

    @Query("UPDATE Attendance SET sentFlag=:pushStatus WHERE AttendanceID=:aID")
    void updateSentFlag(int pushStatus, String aID);

    @Query("SELECT * FROM Attendance WHERE sentFlag=0 AND SessionID=:s_id")
    public List<Attendance> getNewAttendances(String s_id);

    @Query("UPDATE Attendance SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);

    @Query("SELECT DISTINCT SessionID FROM Attendance")
    public List<String> getAllDistinctSessions();

    @Query("select GroupID from Attendance where SessionID=:SessID")
    public String GetGrpIDBySessionID(String SessID);

    @Query("select Present from Attendance where SessionID=:SessID")
    public List<Integer> GetAllPresentStdBySessionId(String SessID);

}
