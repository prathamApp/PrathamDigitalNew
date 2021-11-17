package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pratham.prathamdigital.models.Modal_Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllStudents(List<Modal_Student> studentsList);

    @Query("DELETE FROM Students")
    void deleteAllStudents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStudent(Modal_Student studentsList);

    @Update
    int updateAllStudent(List<Modal_Student> studList);

    @Query("DELETE FROM Students WHERE Gender='Deleted'")
    void deleteDeletedStdRecords();

    @Query("DELETE FROM Students WHERE GroupID=:grpID")
    void deleteDeletedGrpsStdRecords(String grpID);

    @Query("SELECT * FROM Students")
    List<Modal_Student> getAllStudents();

    @Query("select * from Students where sentFlag = 0")
    List<Modal_Student> getAllNewStudents();

    @Query("update Students set sentFlag=1 where StudentId=:s_id")
    void updateSentStudentFlags(String s_id);

    @Query("update Students set avatarName=:avatar_path where StudentId=:studentID")
    void updateStudentAvatar(String studentID, String avatar_path);

    @Query("SELECT * FROM Students WHERE GroupId=:gID")
    List<Modal_Student> getGroupwiseStudents(String gID);

    @Query("SELECT * FROM Students WHERE GroupId Like :gID")
    List<Modal_Student> getGroupwiseStudentsLike(String gID);

    @Query("DELETE FROM Students Where StudentId=:stdID")
    void deleteStudentByID(String stdID);

    @Query("SELECT count(*) FROM Students")
    int getStudentsCount();

    @Query("select * from Students where StudentId = :studentID")
    Modal_Student getStudent(String studentID);

    @Query("select * from Students where StudentId = :studentID")
    List<Modal_Student> getAllStudent(String studentID);

    //Edit profile
    @Query("update Students set FirstName=:studName, MiddleName=:studName, LastName=:studName, FullName=:studName, Age=:studAge," +
            "Stud_Class=:studClass, avatarName=:avatarName, sentFlag=0 where StudentId=:sId")
    void editStudent(String studName, String studAge, String studClass, String avatarName, String sId);

    @Query("select GroupName from Students where StudentId = :studentID")
    String getStudGroupName(String studentID);

    @Query("select LastName from Students where StudentId = :studentID")
    String getEnrollmentId(String studentID);

    //   void deleteAllStudents();
}