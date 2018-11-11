package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pratham.prathamdigital.models.Modal_Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllStudents(List<Modal_Student> studentsList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertStudent(Modal_Student studentsList);

    @Update
    public int updateAllStudent(List<Modal_Student> studList);

    @Query("DELETE FROM Students")
    public void deleteAllStudents();

    @Query("SELECT * FROM Students")
    public List<Modal_Student> getAllStudents();

    @Query("select * from Students where sentFlag = 1")
    List<Modal_Student> getAllNewStudents();

    @Query("update Students set sentFlag=0 where sentFlag= 1")
    void updateSentStudentFlags();

    @Query("select FullName from Students where StudentID = :studentID")
    String getStudentName(String studentID);

    @Query("SELECT * FROM Students WHERE GroupId=:gID")
    public List<Modal_Student> getGroupwiseStudents(String gID);

    @Query("DELETE FROM Students Where StudentId=:stdID")
    public void deleteStudentByID(String stdID);
}