package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import com.pratham.prathamdigital.models.Modal_Village;

import java.util.List;

@Dao
public interface VillageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllVillages(List<Modal_Village> villagesList);

    @Query("DELETE FROM Village")
    public void deleteAllVillages();

    @Query("SELECT * FROM Village")
    public List<Modal_Village> getAllVillages();
}