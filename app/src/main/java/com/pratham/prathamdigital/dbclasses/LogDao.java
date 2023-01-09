package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pratham.prathamdigital.models.Modal_Log;

import java.util.List;

@Dao
public interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLog(Modal_Log log);

    @Insert
    void insertAllLogs(List<Modal_Log> log);

    @Query("DELETE FROM Logs")
    void deleteLogs();

    @Query("select * from Logs where sentFlag=0 AND sessionId=:s_id")
    List<Modal_Log> getAllLogs(String s_id);

    @Query("select * from Logs where sentFlag=0")
    List<Modal_Log> getAllLogs();

    @Query("UPDATE Logs SET sentFlag=1 WHERE SessionID=:s_id")
    void updateSentFlag(String s_id);

    @Query("UPDATE Logs SET sentFlag=1")
    void updateAllSentFlag();

//    @Query("SELECT * FROM Logs WHERE exceptionMessage='App_Auto_Sync' OR exceptionMessage = 'App_Manual_Sync' ORDER by currentDateTime DESC")
    //Query to get sync log entries from table
    @Query("select * from Logs WHERE exceptionMessage='Auto_Sync' OR exceptionMessage = 'Manual_Sync' " +
            "ORDER By substr(currentDateTime,7,4)||\"-\"||substr(currentDateTime,4,2)||\"-\"||substr(currentDateTime,1,2)||\" \"||substr(currentDateTime,12,2)||\":\"||substr(currentDateTime,15,2)||\":\"||substr(currentDateTime,18,2) DESC")
    List<Modal_Log> getDataSyncLogs();

    //Query to get db sync log entries from table
    @Query("SELECT * FROM Logs WHERE exceptionMessage='DB_Sync'" +
            "ORDER By substr(currentDateTime,7,4)||\"-\"||substr(currentDateTime,4,2)||\"-\"||substr(currentDateTime,1,2)||\" \"||substr(currentDateTime,12,2)||\":\"||substr(currentDateTime,15,2)||\":\"||substr(currentDateTime,18,2) DESC")
    List<Modal_Log> getDbSyncLogs();

    //Query to get successful sync log entries from table
    @Query("select * from Logs WHERE errorType = 'successfully_pushed' " +
            "ORDER By substr(currentDateTime,7,4)||\"-\"||substr(currentDateTime,4,2)||\"-\"||substr(currentDateTime,1,2)||\" \"||substr(currentDateTime,12,2)||\":\"||substr(currentDateTime,15,2)||\":\"||substr(currentDateTime,18,2) DESC LIMIT 1")
    Modal_Log getLastDataSyncLog();

    //Query to get successful db sync log entries from table
   @Query("select * from Logs WHERE errorType = 'db_successfully_pushed' " +
           "ORDER By substr(currentDateTime,7,4)||\"-\"||substr(currentDateTime,4,2)||\"-\"||substr(currentDateTime,1,2)||\" \"||substr(currentDateTime,12,2)||\":\"||substr(currentDateTime,15,2)||\":\"||substr(currentDateTime,18,2) DESC LIMIT 1")
    Modal_Log getLastDbSyncLog();

   //Query to check whether the resource is entered in db or not
   @Query("select * from Logs where exceptionMessage=:nodeTitle and methodName=:nodeId")
    Modal_Log checkResourceLog(String nodeTitle, String nodeId);
}