package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.prathamdigital.models.Modal_Crl;

import java.util.List;

@Dao
public interface CRLdao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCRL(List<Modal_Crl> crlList);

    @Query("DELETE FROM CRL")
    public void deleteAllCRLs();

    @Query("SELECT * FROM CRL")
    public List<Modal_Crl> getAllCRLs();

    @Query("SELECT count(*) FROM CRL")
    public int getCRLsCount();

    @Query("SELECT RoleId FROM CRL where CRLId=:id")
    public String getCRLsRoleById(String id);

    @Query("SELECT DISTINCT ProgramName FROM CRL")
    public List<String> getDistinctCRLsdProgram();

    @Query("SELECT DISTINCT  RoleName FROM CRL")
    public List<String> getDistinctCRLsRoleId();

    @Query("SELECT DISTINCT UserName,CRLId,FirstName FROM CRL WHERE RoleName=:roleName and ProgramName=:programName")
    public List<Modal_Crl> getDistinctCRLsUserName(String roleName, String programName);
}