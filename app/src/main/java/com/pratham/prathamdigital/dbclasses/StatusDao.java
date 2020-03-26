package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pratham.prathamdigital.models.Modal_Status;

import java.util.List;

@Dao
public interface StatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Modal_Status status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<Modal_Status> statuses);

    @Update
    int update(Modal_Status status);

    @Delete
    void delete(Modal_Status status);

    @Delete
    void deleteAll(Modal_Status... statuses);

    @Query("select * from Status")
    List<Modal_Status> getAllStatuses();

    @Query("Select statusKey from Status where statusKey = :key")
    String getKey(String key);

    @Query("Select value from Status where statusKey = :key")
    String getValue(String key);

    @Query("UPDATE Status set value =:value where statusKey =:key")
    void updateValue(String key, String value);

}