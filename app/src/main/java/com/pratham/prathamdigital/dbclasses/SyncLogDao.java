package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Model_NewSyncLog;

import java.util.List;

@Dao
public interface SyncLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLog(Model_NewSyncLog syncLog);

    @Insert
    void insertAllLogs(List<Model_NewSyncLog> syncLogs);

    @Query("DELETE FROM SyncLog")
    void deleteLogs();

    @Query("select * from SyncLog where PushId !=0 ORDER BY PushId DESC")
    List<Model_NewSyncLog> getAllSyncLogs();

    @Query("select * from SyncLog where pushType='DB_PUSH'" +
            "ORDER By substr(pushDate,7,4)||\"-\"||substr(pushDate,4,2)||\"-\"||substr(pushDate,1,2)||\" \"||substr(pushDate,12,2)||\":\"||substr(pushDate,15,2)||\":\"||substr(pushDate,18,2) DESC")
    List<Model_NewSyncLog> getAllDBSyncLogs();

    @Query("UPDATE SyncLog set Status =:status where PushId =:pushId AND uuid =:uuId")
    void updateSyncStatus(int pushId, String uuId, String status);

}