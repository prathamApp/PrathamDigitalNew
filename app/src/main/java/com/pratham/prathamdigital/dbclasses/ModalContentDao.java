package com.pratham.prathamdigital.dbclasses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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

    //todo : below three queries are same, check and make it one
    @Query("SELECT * FROM TableContent WHERE (parentid ISNULL or parentid = 0 or parentid='' " +
            "or LTRIM(RTRIM([parentid])) = '') and content_language=:language")
    List<Modal_ContentDetail> getParentsHeaders(String language);

    /*** removed nodeeage not like '3-6' to get data of parentid = 0 on 22Feb21*/
    @Query("SELECT * FROM TableContent WHERE (parentid ISNULL or parentid = 0 or parentid='' " +
            "or LTRIM(RTRIM([parentid])) = '') and content_language=:language")
    List<Modal_ContentDetail> getPrimaryAgeParentsHeaders(String language);

    /*** removed nodeeage not like '3-6' to get data of parentid = 0*/
    @Query("SELECT * FROM TableContent WHERE (parentid ISNULL or parentid = 0 or parentid='' " +
            "or LTRIM(RTRIM([parentid])) = '') and content_language=:language")
    List<Modal_ContentDetail> getAbovePrimaryAgeParentHeaders(String language);

    @Query("SELECT * FROM TableContent WHERE parentid=:id or parentid=:altnodeId and content_language=:language")
    List<Modal_ContentDetail> getChildsOfParent(String id, String altnodeId, String language);


    @Query("SELECT * FROM TableContent WHERE nodeid=:id and content_language=:language")
    Modal_ContentDetail getContent(String id, String language);

    @Query("SELECT * FROM TableContent WHERE altnodeid=:id and content_language=:language")
    Modal_ContentDetail getContentFromAltNodeId(String id, String language);

    @Query("select * from TableContent where nodetype='Course' and content_language=:language")
    List<Modal_ContentDetail> getAllCourses(String language);

    @Query("with tmp(id, level) as (  select nodeid,1  from TableContent  where nodeid = :node_id \n" +
            "union all  select s.parentid, t.level+1  from tmp t  join TableContent s on s.nodeid = t.id)\n" +
            "select s.nodetitle from tmp t join TableContent s on s.nodeid = t.id order by s.level desc")
    List<String> getAllParentsOfCourses(String node_id);

    @Query("Update TableContent Set parentid='0' WHERE (parentid = 20 or parentid=1100 " +
            "or parentid=25 or parentid=30 or parentid=35 or parentid=45 ) and content_language=:language")
    void updateParentsFromPreviousAppVersion(String language);

    //Added this to mark Viewed Content as Viewed in CourseDetailFragment
    @Query("update TableContent set isViewed=1 where nodeid=:node_id")
    void updateIsViewed(String node_id);

    //Get languages from DB
    @Query("SELECT content_language from TableContent GROUP BY content_language")
    List<String> getLanguagesFromDB();
}

