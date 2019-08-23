package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.prathamdigital.models.Modal_ContentDetail;

import java.util.List;

@Dao
public interface ModalContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addContentList(List<Modal_ContentDetail> contentList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addContent(Modal_ContentDetail content);

    @Query("Delete from TableContent WHERE nodeid=:nodeId")
    void deleteContent(String nodeId);

    @Query("SELECT COUNT(*) from TableContent WHERE parentid=:nodeId and content_language=:language")
    int getChildCountOfParent(String nodeId, String language);

    @Query("SELECT * FROM TableContent WHERE (parentid ISNULL or parentid = 0 or parentid='' " +
            "or LTRIM(RTRIM([parentid])) = '') and content_language=:language")
    List<Modal_ContentDetail> getParentsHeaders(String language);

    @Query("SELECT * FROM TableContent WHERE (parentid ISNULL or parentid = 0 or parentid='' " +
            "or LTRIM(RTRIM([parentid])) = '') and content_language=:language and nodeeage='3-6'")
    List<Modal_ContentDetail> getPrimaryAgeParentsHeaders(String language);

    @Query("SELECT * FROM TableContent WHERE (parentid ISNULL or parentid = 0 or parentid='' " +
            "or LTRIM(RTRIM([parentid])) = '') and content_language=:language and nodeeage not like '3-6'")
    List<Modal_ContentDetail> getAbovePrimaryAgeParentHeaders(String language);

    @Query("SELECT * FROM TableContent WHERE parentid=:id or parentid=:altnodeId and content_language=:language")
    List<Modal_ContentDetail> getChildsOfParent(String id, String altnodeId, String language);


    @Query("SELECT * FROM TableContent WHERE nodeid=:id and content_language=:language")
    Modal_ContentDetail getContent(String id, String language);

    @Query("SELECT * FROM TableContent WHERE altnodeid=:id and content_language=:language")
    Modal_ContentDetail getContentFromAltNodeId(String id, String language);

    @Query("select * from TableContent where nodetype='Course'")
    List<Modal_ContentDetail> getAllCourses();

    @Query("with tmp(id, level) as (  select nodeid,1  from TableContent  where nodeid = :node_id \n" +
            "union all  select s.parentid, t.level+1  from tmp t  join TableContent s on s.nodeid = t.id)\n" +
            "select s.nodetitle from tmp t join TableContent s on s.nodeid = t.id order by s.level desc")
    List<String> getAllParentsOfCourses(String node_id);
}

