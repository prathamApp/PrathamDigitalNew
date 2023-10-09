package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.prathamdigital.models.Model_NewSyncLog;
import com.pratham.prathamdigital.models.Model_SyncStatusLog;

import java.util.List;

@Dao
public interface SyncStatusLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLog(Model_SyncStatusLog syncStatusLog);

    @Insert
    void insertAllLogs(List<Model_SyncStatusLog> syncStatusLogs);

    @Query("DELETE FROM SyncStatusLog")
    void deleteLogs();

    @Query("select * from SyncStatusLog where PushId !=0 ORDER BY PushId DESC")
    List<Model_NewSyncLog> getAllSyncLogs();
}