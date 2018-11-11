package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pratham.prathamdigital.models.Modal_Score;

import java.util.List;

@Dao
public interface ScoreDao {
    @Insert
    long insert(Modal_Score score);

    @Insert
    long[] insertAll(Modal_Score... score);

    @Update
    int update(Modal_Score score);

    @Delete
    void delete(Modal_Score score);

    @Delete
    void deleteAll(Modal_Score... scores);

    @Query("select * from Score where sentFlag = 0")
    List<Modal_Score> getAllNewScores();

    @Query("DELETE FROM Score")
    void deleteAllScores();
}
