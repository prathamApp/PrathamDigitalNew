package com.pratham.prathamdigital.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "TableContent")
public class Modal_ContentDetail implements Comparable {
    @PrimaryKey
    @NonNull
    @SerializedName("nodeid")
    private String nodeid;
    @SerializedName("nodetype")
    private String nodetype;
    @SerializedName("nodetitle")
    private String nodetitle;
    @SerializedName("nodekeywords")
    private String nodekeywords;
    @SerializedName("nodeeage")
    private String nodeeage;
    @SerializedName("nodedesc")
    private String nodedesc;
    @SerializedName("nodeimage")
    private String nodeimage;
    @SerializedName("nodeserverimage")
    private String nodeserverimage;
    @SerializedName("resourceid")
    private String resourceid;
    @SerializedName("resourcetype")
    private String resourcetype;
    @SerializedName("resourcepath")
    private String resourcepath;
    @SerializedName("level")
    private int level;
    @SerializedName("content_language")
    private String content_language;
    @SerializedName("parentid")
    private String parentid;
    private String contentType;
    private boolean isDownloaded = false;

    @Override
    public String toString() {
        return "Modal_ContentDetail{" +
                "nodeid='" + nodeid + '\'' +
                ", nodetype='" + nodetype + '\'' +
                ", nodetitle='" + nodetitle + '\'' +
                ", nodekeywords='" + nodekeywords + '\'' +
                ", nodeeage='" + nodeeage + '\'' +
                ", nodedesc='" + nodedesc + '\'' +
                ", nodeimage='" + nodeimage + '\'' +
                ", nodeserverimage='" + nodeserverimage + '\'' +
                ", resourceid='" + resourceid + '\'' +
                ", resourcetype='" + resourcetype + '\'' +
                ", resourcepath='" + resourcepath + '\'' +
                ", level='" + level + '\'' +
                ", content_language='" + content_language + '\'' +
                ", parentid='" + parentid + '\'' +
                '}';
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getNodetype() {
        return nodetype;
    }

    public void setNodetype(String nodetype) {
        this.nodetype = nodetype;
    }

    public String getNodetitle() {
        return nodetitle;
    }

    public void setNodetitle(String nodetitle) {
        this.nodetitle = nodetitle;
    }

    public String getNodekeywords() {
        return nodekeywords;
    }

    public void setNodekeywords(String nodekeywords) {
        this.nodekeywords = nodekeywords;
    }

    public String getNodeeage() {
        return nodeeage;
    }

    public void setNodeeage(String nodeeage) {
        this.nodeeage = nodeeage;
    }

    public String getNodedesc() {
        return nodedesc;
    }

    public void setNodedesc(String nodedesc) {
        this.nodedesc = nodedesc;
    }

    public String getNodeimage() {
        return nodeimage;
    }

    public void setNodeimage(String nodeimage) {
        this.nodeimage = nodeimage;
    }

    public String getNodeserverimage() {
        return nodeserverimage;
    }

    public void setNodeserverimage(String nodeserverimage) {
        this.nodeserverimage = nodeserverimage;
    }

    public String getResourceid() {
        return resourceid;
    }

    public void setResourceid(String resourceid) {
        this.resourceid = resourceid;
    }

    public String getResourcetype() {
        return resourcetype;
    }

    public void setResourcetype(String resourcetype) {
        this.resourcetype = resourcetype;
    }

    public String getResourcepath() {
        return resourcepath;
    }

    public void setResourcepath(String resourcepath) {
        this.resourcepath = resourcepath;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent_language() {
        return content_language;
    }

    public void setContent_language(String content_language) {
        this.content_language = content_language;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Modal_ContentDetail compare = (Modal_ContentDetail) o;
        if (compare.getNodeid() != null) {
            if (compare.getNodeid().equalsIgnoreCase(this.nodeid) && compare.isDownloaded() == this.isDownloaded)
                return 0;
            else return 1;
        } else {
            return 0;
        }
    }
}
