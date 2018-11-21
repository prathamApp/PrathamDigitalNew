package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;

import com.pratham.prathamdigital.models.Modal_Groups;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllGroups(List<Modal_Groups> groupsList);

    @Query("DELETE FROM Groups")
    public void deleteAllGroups();

    @Query("SELECT * FROM Groups ")
    public List<Modal_Groups> getAllGroups();

    @Query("SELECT * FROM Groups WHERE VillageID=:vID ORDER BY GroupName ASC")
    public List<Modal_Groups> GetGroups(int vID);

    @Query("DELETE FROM Groups WHERE GroupID=:grpID")
    public void deleteGroupByGrpID(String grpID);

    @Query("select * from Groups WHERE DeviceID = 'deleted'")
    public List<Modal_Groups> GetAllDeletedGroups();

}