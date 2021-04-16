package com.pratham.prathamdigital.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "TableContent")
public class Modal_ContentDetail implements Comparable, Parcelable {
    @PrimaryKey
    @NonNull
    @SerializedName("nodeid")
    private String nodeid;
    @SerializedName("nodetype")
    private String nodetype;
    @SerializedName("nodetitle")
    private String nodetitle;
    @SerializedName("nodeKeyword")
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
    @SerializedName("nodelang")
    private String content_language;
    @SerializedName("parentid")
    private String parentid;
    private String contentType;
    private boolean isDownloaded = false;
    //for offline content sharing
    private boolean onSDCard = false;
    @SerializedName("altnodeid")
    private String altnodeid;
    @SerializedName("version")
    private String version;
    @SerializedName("assignment")
    private String assignment;
    @SerializedName("subject")
    public String subject;
    @SerializedName("seq_no")
    public String seq_no;
    @SerializedName("seq_rule")
    public String seq_rule;
    //ToDo : check if error
//    @SerializedName("resourcezip")
    public String resourcezip;

    private boolean isViewed = false;
    @Ignore
    private String kolibriNodeImageUrl;
    @Ignore
    private String mappedApiId;
    @Ignore
    private String mappedParentId;

    public Modal_ContentDetail() {
    }

    protected Modal_ContentDetail(Parcel in) {
        nodeid = in.readString();
        nodetype = in.readString();
        nodetitle = in.readString();
        nodekeywords = in.readString();
        nodeeage = in.readString();
        nodedesc = in.readString();
        nodeimage = in.readString();
        nodeserverimage = in.readString();
        resourceid = in.readString();
        resourcetype = in.readString();
        resourcepath = in.readString();
        level = in.readInt();
        content_language = in.readString();
        parentid = in.readString();
        contentType = in.readString();
        isDownloaded = in.readByte() != 0;
        onSDCard = in.readByte() != 0;
        kolibriNodeImageUrl = in.readString();
        altnodeid = in.readString();
        version = in.readString();
        mappedApiId = in.readString();
        mappedParentId = in.readString();
        assignment = in.readString();
        subject = in.readString();
        isViewed = in.readByte() != 0;
        seq_no = in.readString();
        resourcezip=in.readString();
    }

    public static final Creator<Modal_ContentDetail> CREATOR = new Creator<Modal_ContentDetail>() {
        @Override
        public Modal_ContentDetail createFromParcel(Parcel in) {
            return new Modal_ContentDetail(in);
        }

        @Override
        public Modal_ContentDetail[] newArray(int size) {
            return new Modal_ContentDetail[size];
        }
    };

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public boolean isOnSDCard() {
        return onSDCard;
    }

    public void setOnSDCard(boolean onSDCard) {
        this.onSDCard = onSDCard;
    }

    public String getKolibriNodeImageUrl() {
        return kolibriNodeImageUrl;
    }

    public void setKolibriNodeImageUrl(String kolibriNodeImageUrl) {
        this.kolibriNodeImageUrl = kolibriNodeImageUrl;
    }

    public String getAltnodeid() {
        return altnodeid;
    }

    public void setAltnodeid(String altnodeid) {
        this.altnodeid = altnodeid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMappedApiId() {
        return mappedApiId;
    }

    public void setMappedApiId(String mappedApiId) {
        this.mappedApiId = mappedApiId;
    }

    public String getMappedParentId() {
        return mappedParentId;
    }

    public void setMappedParentId(String mappedParentId) {
        this.mappedParentId = mappedParentId;
    }

    public String getAssignment() {
        return assignment;
    }

    public void setAssignment(String assignment) {
        this.assignment = assignment;
    }

    //Added this to mark Viewed Content as Viewed in CourseDetailFragment
    public boolean getIsViewed() {
        return isViewed;
    }

    public void setIsViewed(boolean isViewed) {
        this.isViewed = isViewed;
    }

    public String getSeq_no() {
        return seq_no;
    }

    public void setSeq_no(String seq_no) {
        this.seq_no = seq_no;
    }

    public String getSeq_rule() {
        return seq_rule;
    }

    public void setSeq_rule(String seq_rule) {
        this.seq_rule = seq_rule;
    }

    public String getResourcezip() {
        return resourcezip;
    }

    public void setResourcezip(String resourcezip) {
        this.resourcezip = resourcezip;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeid);
        dest.writeString(nodetype);
        dest.writeString(nodetitle);
        dest.writeString(nodekeywords);
        dest.writeString(nodeeage);
        dest.writeString(nodedesc);
        dest.writeString(nodeimage);
        dest.writeString(nodeserverimage);
        dest.writeString(resourceid);
        dest.writeString(resourcetype);
        dest.writeString(resourcepath);
        dest.writeInt(level);
        dest.writeString(content_language);
        dest.writeString(parentid);
        dest.writeString(contentType);
        dest.writeByte((byte) (isDownloaded ? 1 : 0));
        dest.writeByte((byte) (onSDCard ? 1 : 0));
        dest.writeString(kolibriNodeImageUrl);
        dest.writeString(altnodeid);
        dest.writeString(version);
        dest.writeString(mappedApiId);
        dest.writeString(mappedParentId);
        dest.writeString(assignment);
        dest.writeString(subject);
        dest.writeByte((byte) (isViewed ? 1 : 0));
        dest.writeString(seq_no);
        dest.writeString(resourcezip);
    }
}
