package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.List;

@Dao
public interface ModalContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addContentList(List<Modal_ContentDetail> contentList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addContent(Modal_ContentDetail content);

    @Delete
    void deleteContent(Modal_ContentDetail contentDetail);

    @Query("SELECT * FROM TableContent WHERE parentid ISNULL or parentid = 0 or parentid=''")
    public List<Modal_ContentDetail> getParents();

    @Query("SELECT * FROM TableContent WHERE parentid=:id")
    public List<Modal_ContentDetail> getChild(String id);

}

