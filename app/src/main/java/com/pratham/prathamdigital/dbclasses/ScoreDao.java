package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.prathamdigital.models.Modal_ResourcePlayedByGroups;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_TotalDaysGroupsPlayed;

import java.util.List;

@Dao
public interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Modal_Score score);

    @Insert
    void insertAll(List<Modal_Score> score);

    @Query("UPDATE Score SET sentFlag = 1 where SessionID = :s_id")
    int updateFlag(String s_id);

    @Query("UPDATE Score SET sentFlag = 1")
    int updateAllFlag();

    @Delete
    void delete(Modal_Score score);

    @Delete
    void deleteAll(Modal_Score... scores);

    @Query("select * from Score where sentFlag = 0 AND SessionID=:s_id")
    List<Modal_Score> getAllNewScores(String s_id);

    @Query("select * from Score where sentFlag = 0")
    List<Modal_Score> getAllNewScores();

    @Query("DELETE FROM Score")
    void deleteAllScores();

    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9')) as dates from Score sc where length(startdatetime)>5")
    int getTotalActiveDeviceDays();

    @Query("Select count(distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9')) as dates,at.groupid,g.groupname from Score sc inner join Attendance at on sc.sessionid=at.sessionid inner join Groups g on at.groupid=g.groupid where length(startdatetime)>5 group by at.groupid,g.groupname")
    List<Modal_TotalDaysGroupsPlayed> getTotalDaysGroupsPlayed();

    @Query("Select distinct REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(substr(startdatetime,1,instr(startdatetime,' ')),'01','1'),'02','2'),'03','3'),'04','4'),'05','5'),'06','6'),'07','7'),'08','8'),'09','9') as dates,sc.resourceid,tc.nodetitle,at.groupid,g.groupname from Score sc inner join TableContent tc on tc.resourceid=sc.resourceid inner join Attendance at on sc.sessionid=at.sessionid inner join Groups g on at.groupid=g.groupid where length(startdatetime)>5 and at.groupid=:grpId")
    List<Modal_ResourcePlayedByGroups> getRecourcesPlayedByGroups(String grpId);
}