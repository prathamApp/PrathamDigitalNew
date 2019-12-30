package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pratham.prathamdigital.models.Model_CourseEnrollment;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(Model_CourseEnrollment courseEnrolled);

    @Query("Select * from COURSEENROLLED where sentFlag=0")
    List<Model_CourseEnrollment> fetchUnpushedCourses();

    @Query("Select * from COURSEENROLLED where courseId=:course_Id and groupId=:grp_id " +
            "and planFromDate LIKE '%' || :week || '%' and coachVerified=0")
    Model_CourseEnrollment checkIfCourseEnrolled(String course_Id, String grp_id, String week);

    @Query("Select * from COURSEENROLLED where groupId=:grp_id " +
            "and planFromDate LIKE '%' || :week || '%' and language=:language")
    List<Model_CourseEnrollment> fetchEnrolledCourses(String grp_id, String week, String language);

    @Query("Delete from CourseEnrolled WHERE  courseId=:nodeId and groupId=:grpId and planFromDate LIKE " +
            "'%' || :week || '%' and language=:lang")
    void deleteCourse(String nodeId, String grpId, String week, String lang);

    @Query("Update CourseEnrolled SET coachVerified=1 and coachImage=:imagePath and coachVerificationDate=:verificationDate" +
            " WHERE courseId=:courseId and groupId=:grpId " +
            "and planFromDate LIKE '%' || :week || '%' and coachVerified=1 and language=:language")
    void coachVerifiedTheCourse(String imagePath, String verificationDate, String courseId, String grpId, String week, String language);

    @Query("Update CourseEnrolled SET courseCompleted=1 and courseExperience=:courseExp and sentFlag=0 " +
            " WHERE courseId=:courseId and groupId=:grpId " +
            "and planFromDate LIKE '%' || :week || '%' and coachVerified=1 and language=:language")
    void addExperienceToCourse(String courseExp, String courseId, String grpId, String week, String language);

    @Query("UPDATE CourseEnrolled SET sentFlag = 1 where courseId=:courseId and groupId=:grpId and language=:language")
    int updateFlag(String courseId, String grpId, String language);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCourse(Model_CourseEnrollment enrollment);
}
