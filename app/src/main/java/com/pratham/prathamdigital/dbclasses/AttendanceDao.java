package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.prathamdigital.models.Attendance;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAttendance(List<Attendance> attendancesList);

    @Query("DELETE FROM Attendance")
    void deleteAllAttendances();

    @Query("SELECT * FROM Attendance")
    List<Attendance> getAllAttendances();

    @Query("UPDATE Attendance SET sentFlag=1 WHERE SessionID=:s_id")
    void updateSentFlag(String s_id);

    /** Update sent flag to 1 after push success for new Sync Process*/
    @Query("UPDATE Attendance SET sentFlag = 1 where sentFlag = 0")
    int updateSentFlag();

    @Query("SELECT * FROM Attendance WHERE sentFlag=0 AND SessionID=:s_id")
    List<Attendance> getNewAttendances(String s_id);

    @Query("SELECT * FROM Attendance WHERE sentFlag=0")
    List<Attendance> getNewAttendances();

    @Query("UPDATE Attendance SET sentFlag=1")
    void updateAllSentFlag();

    @Query("SELECT DISTINCT SessionID FROM Attendance")
    List<String> getAllDistinctSessions();

    @Query("select GroupID from Attendance where SessionID=:SessID")
    String GetGrpIDBySessionID(String SessID);

    @Query("select Present from Attendance where SessionID=:SessID")
    List<Integer> GetAllPresentStdBySessionId(String SessID);

}
