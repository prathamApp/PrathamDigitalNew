package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pratham.prathamdigital.models.Model_ContentProgress;

import java.util.List;

@Dao
public interface ContentProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProgress(Model_ContentProgress contentProgress);

    @Query("select progressPercentage from ContentProgress where studentId=:s_id AND resourceId=:c_id AND updatedDateTime LIKE '%' || :week || '%' ")
    String getCourseProgress(String s_id, String c_id, String week);

    @Query("select * from ContentProgress where studentId=:s_id AND resourceId=:c_id AND updatedDateTime LIKE '%' || :week || '%' ")
    Model_ContentProgress getCourse(String s_id, String c_id, String week);

    //    @Query("Update ContentProgress SET progressPercentage=:percent_progress and updatedDateTime=:date and label=:strlabel " +
//            "WHERE resourceId=:courseId and studentId=:student_id ")
    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateProgress(Model_ContentProgress progress);

    @Query("select * from ContentProgress where sentFlag=0")
    List<Model_ContentProgress> fetchProgress();

    @Query("UPDATE ContentProgress SET sentFlag = 1 where studentId = :s_id and resourceId = :r_id")
    int updateFlag(String s_id, String r_id);

    //to show the max watched percentage of video
    @Query("select max(CAST(progressPercentage as INT)) from ContentProgress\n" +
            "WHERE studentId = :studId and resourceid = :resId")
    String progressPercent(String studId, String resId);
}