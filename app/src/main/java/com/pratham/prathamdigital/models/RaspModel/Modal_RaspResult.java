
package com.pratham.prathamdigital.models.RaspModel;

import android.util.Log;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.util.PD_Constant;

public class Modal_RaspResult {

    @SerializedName("NodeId")
    @Expose
    private String nodeId;
    @SerializedName("NodeType")
    @Expose
    private String nodeType;
    @SerializedName("NodeTitle")
    @Expose
    private String nodeTitle;
    @SerializedName("JsonData")
    @Expose
    private String jsonData;
    @SerializedName("ParentId")
    @Expose
    private String parentId;
    @SerializedName("AppId")
    @Expose
    private String appId;
    @SerializedName("DateUpdated")
    @Expose
    private String dateUpdated;
    @SerializedName("LstFileList")
    @Expose
    private List<LstFileList> lstFileList = null;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
        this.nodeTitle = nodeTitle;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public List<LstFileList> getLstFileList() {
        return lstFileList;
    }

    public void setLstFileList(List<LstFileList> lstFileList) {
        this.lstFileList = lstFileList;
    }

    public Modal_ContentDetail setContentToConfigNodeStructure(Modal_RaspResult modal_raspResult, Modal_Rasp_JsonData modal_rasp_jsonData) {
        Modal_ContentDetail modal_contentDetail = null;
        try {
            modal_contentDetail = new Modal_ContentDetail();
            if (modal_rasp_jsonData.getResourceType().equalsIgnoreCase("Game")
                    || modal_rasp_jsonData.getResourceType().equalsIgnoreCase("Video")
                    || modal_rasp_jsonData.getResourceType().equalsIgnoreCase("Pdf")
                    || modal_rasp_jsonData.getResourceType().equalsIgnoreCase("Audio"))
                modal_contentDetail.setContentType("file");
            else
                modal_contentDetail.setContentType("folder");
            if (modal_raspResult.getParentId().equalsIgnoreCase(FastSave.getInstance().getString(PD_Constant.LANGUAGE_CODE, "78672")))
                modal_contentDetail.setParentid("0");
            else modal_contentDetail.setParentid(modal_raspResult.getParentId());
            modal_contentDetail.setNodeid(modal_raspResult.getNodeId());
            modal_contentDetail.setNodetitle(modal_raspResult.getNodeTitle());
            modal_contentDetail.setNodetype(modal_raspResult.getNodeType());
            modal_contentDetail.setNodekeywords(modal_rasp_jsonData.getNodeKeyword());
            modal_contentDetail.setNodeeage(modal_rasp_jsonData.getNodeage());
            modal_contentDetail.setNodedesc("");
            modal_contentDetail.setNodeimage(modal_rasp_jsonData.getNodeimage());
            modal_contentDetail.setNodeserverimage(modal_rasp_jsonData.getContThumburl());
            modal_contentDetail.setResourceid(modal_rasp_jsonData.getResourceId());
            modal_contentDetail.setResourcetype(modal_rasp_jsonData.getResourceType());
            modal_contentDetail.setResourcepath(modal_rasp_jsonData.getResourcepath());
            modal_contentDetail.setResourcezip(modal_rasp_jsonData.getResourcezip());
            modal_contentDetail.setAltnodeid(modal_rasp_jsonData.getAltnodeid());
            modal_contentDetail.setVersion(modal_rasp_jsonData.getContVersion());
            modal_contentDetail.setSubject(modal_rasp_jsonData.getSubject());
            modal_contentDetail.setSeq_no(modal_rasp_jsonData.getSeqNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modal_contentDetail;
    }

}
