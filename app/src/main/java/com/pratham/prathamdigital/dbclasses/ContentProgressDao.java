package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pratham.prathamdigital.models.Model_ContentProgress;

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
}