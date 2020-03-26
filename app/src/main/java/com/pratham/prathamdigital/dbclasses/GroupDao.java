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

    @Query("DELETE FROM Groups")
    void deleteAllGroups();

    @Query("SELECT * FROM Groups ")
    List<Modal_Groups> getAllGroups();

    @Query("SELECT * FROM Groups WHERE VillageID=:vID ORDER BY GroupName ASC")
    List<Modal_Groups> GetGroups(int vID);

    @Query("DELETE FROM Groups WHERE GroupID=:grpID")
    void deleteGroupByGrpID(String grpID);

    @Query("SELECT * FROM Groups WHERE GroupID=:grpID")
    Modal_Groups getGroupByGrpID(String grpID);

    @Query("select * from Groups WHERE DeviceID = 'deleted'")
    List<Modal_Groups> GetAllDeletedGroups();

}