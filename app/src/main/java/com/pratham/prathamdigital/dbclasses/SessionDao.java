package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.pratham.prathamdigital.models.Modal_Session;

import java.util.List;

@Dao
public interface SessionDao {

    @Insert
    long insert(Modal_Session session);

    @Insert
    long[] insertAll(Modal_Session... sessions);

    @Update
    int update(Modal_Session session);

    @Delete
    void delete(Modal_Session session);

    @Delete
    void deleteAll(Modal_Session... sessions);

    @Query("select * from Session")
    List<Modal_Session> getAllSessions();

    @Query("UPDATE Session SET toDate = :toDate where SessionID = :SessionID")
    void UpdateToDate(String SessionID, String toDate);

    @Query("select toDate from Session where SessionID = :SessionID")
    String getToDate(String SessionID);

    /*    String query = "select FirstName,time from Student x INNER JOIN(select StudentID,sum(seconds) as time from(select b.StudentID, (strftime('%s',(substr(toDate, 7, 4)||'-'||substr(toDate, 4,2)||'-'||substr(toDate, 1,2)||' '||substr(toDate,11) )) - strftime('%s',(substr(fromDate, 7, 4)||'-'||substr(fromDate, 4,2)||'-'||substr(fromDate, 1,2)||' '||substr(fromDate,11))))" +
                " as seconds from Session a left outer join Attendance b on a.SessionID = b.SessionID) group by StudentID) y on x.StudentID = y.StudentID order by time desc";*/
    String query = "select FullName, result from Students x " +
            "inner join (select distinct studentid , sum (seconds) as result from " +
            "(select distinct a.studentid, a.FullName, (strftime('%s',(substr(c.toDate, 7, 4)||'-'||substr(c.toDate, 4,2)||'-'||substr(c.toDate, 1,2)||' '||substr(c.toDate,11) )) - strftime('%s',(substr(c.fromDate, 7, 4)||'-'||substr(c.fromDate, 4,2)||'-'||substr(c.fromDate, 1,2)||' '||substr(c.fromDate,11)))) " +
            "as seconds from Session c, Students a Inner join Attendance b on a.studentid = b.studentid and b.sessionid = c.sessionid) group by Studentid order by result desc) z on x.studentid = z.studentid";

    @Query(query)
    Cursor getStudentUsageData();
}
