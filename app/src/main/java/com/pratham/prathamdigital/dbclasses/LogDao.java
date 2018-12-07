package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.prathamdigital.models.Modal_Log;

import java.util.List;

@Dao
public interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertLog(Modal_Log log);

    @Query("DELETE FROM Logs")
    public void deleteLogs();

    @Query("select * from Logs where sentFlag=0 AND sessionId=:s_id")
    public List<Modal_Log> getAllLogs(String s_id);
}