package com.pratham.prathamdigital.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Modal_DownloadContent {
    @SerializedName("contentlist") //new api has key named contentlist instead of nodelist
    //@SerializedName("nodelist")
    private
    List<Modal_ContentDetail> nodelist;

    @SerializedName("downloadurl")
    private
    String downloadurl;

    @SerializedName("resourcetype")//new api has key named resourcetype instead of foldername
    //@SerializedName("foldername")
    private
    String foldername;

    public List<Modal_ContentDetail> getNodelist() {
        return nodelist;
    }

    public void setNodelist(List<Modal_ContentDetail> nodelist) {
        this.nodelist = nodelist;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }
}
