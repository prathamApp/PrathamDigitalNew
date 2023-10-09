package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.prathamdigital.models.Modal_Groups;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllGroups(List<Modal_Groups> groupsList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroup(Modal_Groups group);

    @Query("DELETE FROM Groups")
    void deleteAllGroups();

    @Query("SELECT * FROM Groups WHERE sentFlag=0")
    List<Modal_Groups> getNewGroups();

    @Query("SELECT * FROM Groups WHERE VillageID=:vID ORDER BY GroupName ASC")
    List<Modal_Groups> GetGroups(int vID);

    @Query("DELETE FROM Groups WHERE GroupID=:grpID")
    void deleteGroupByGrpID(String grpID);

    @Query("SELECT * FROM Groups WHERE GroupID=:grpID")
    Modal_Groups getGroupByGrpID(String grpID);

    @Query("select * from Groups WHERE DeviceID = 'deleted'")
    List<Modal_Groups> GetAllDeletedGroups();

    @Query("select SchoolName from Groups WHERE GroupId=:grpID")
    String getEnrollmentId(String grpID);

    /** Update sent flag to 1 after push success for new Sync Process*/
    @Query("UPDATE Groups SET sentFlag = 1 where sentFlag = 0")
    int updateSentFlag();
}