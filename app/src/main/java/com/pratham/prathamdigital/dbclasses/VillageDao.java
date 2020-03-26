package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.prathamdigital.models.Modal_Village;

import java.util.List;

@Dao
public interface VillageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllVillages(List<Modal_Village> villagesList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVillage(Modal_Village modal_village);

    @Query("DELETE FROM Village")
    void deleteAllVillages();

    @Query("SELECT * FROM Village")
    List<Modal_Village> getAllVillages();

    @Query("SELECT DISTINCT State FROM Village ORDER BY State ASC")
    List<String> getAllStates();

    @Query("SELECT DISTINCT Block FROM Village WHERE State=:st ORDER BY Block ASC")
    List<String> GetStatewiseBlock(String st);

    @Query("SELECT * FROM Village WHERE Block=:block  ORDER BY VillageName ASC")
    List<Modal_Village> GetVillages(String block);

    @Query("select VillageID from Village where Block=:block")
    int GetVillageIDByBlock(String block);
}