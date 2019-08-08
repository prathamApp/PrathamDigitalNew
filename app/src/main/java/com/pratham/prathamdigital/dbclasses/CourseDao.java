package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import com.pratham.prathamdigital.models.Model_CourseEnrollment;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(Model_CourseEnrollment courseEnrolled);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourses(List<Model_CourseEnrollment> courseEnrollments);

//    @Query("")
//    void checkIfCourseEnrolled()
}
