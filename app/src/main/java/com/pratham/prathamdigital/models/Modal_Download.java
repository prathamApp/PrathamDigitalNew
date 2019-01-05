package com.pratham.prathamdigital.models;

import com.pratham.prathamdigital.ui.fragment_content.ContentContract;

import java.util.ArrayList;

public class Modal_Download {
    String url;
    String dir_path;
    String f_name;
    String folder_name;
    Modal_ContentDetail content;
    ContentContract.contentPresenter contentPresenter;
    ArrayList<Modal_ContentDetail> levelContents;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDir_path() {
        return dir_path;
    }

    public void setDir_path(String dir_path) {
        this.dir_path = dir_path;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public Modal_ContentDetail getContent() {
        return content;
    }

    public void setContent(Modal_ContentDetail content) {
        this.content = content;
    }

    public ContentContract.contentPresenter getContentPresenter() {
        return contentPresenter;
    }

    public void setContentPresenter(ContentContract.contentPresenter contentPresenter) {
        this.contentPresenter = contentPresenter;
    }

    public ArrayList<Modal_ContentDetail> getLevelContents() {
        return levelContents;
    }

    public void setLevelContents(ArrayList<Modal_ContentDetail> levelContents) {
        this.levelContents = levelContents;
    }
}
