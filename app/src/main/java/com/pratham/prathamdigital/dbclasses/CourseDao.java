package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.prathamdigital.models.Model_CourseEnrollment;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(Model_CourseEnrollment courseEnrolled);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourses(List<Model_CourseEnrollment> courseEnrollments);

    @Query("Select * from COURSEENROLLED where courseId=:course_Id and groupId=:grp_id " +
            "and planFromDate LIKE '%' || :week || '%' and coachVerified=0")
    Model_CourseEnrollment checkIfCourseEnrolled(String course_Id, String grp_id, String week);

    @Query("Select * from COURSEENROLLED where groupId=:grp_id " +
            "and planFromDate LIKE '%' || :week || '%' and coachVerified=0")
    List<Model_CourseEnrollment> fetchEnrolledCourses(String grp_id, String week);

    @Query("Delete from CourseEnrolled WHERE  courseId=:nodeId and groupId=:grpId and planFromDate LIKE '%' || :week || '%'")
    void deleteCourse(String nodeId, String grpId, String week);
}
