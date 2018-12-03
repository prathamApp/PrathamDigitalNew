package com.pratham.prathamdigital.models;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class EventMessage {
    String message;
    int downlaodContentSize;
    Modal_ContentDetail contentDetail;
    ArrayList<Modal_ContentDetail> content;
    Drawable connection_resource;
    String connection_name;

    public ArrayList<Modal_ContentDetail> getContent() {
        return content;
    }

    public void setContent(ArrayList<Modal_ContentDetail> content) {
        this.content = content;
    }

    public Drawable getConnection_resource() {
        return connection_resource;
    }

    public void setConnection_resource(Drawable connection_resource) {
        this.connection_resource = connection_resource;
    }

    public String getConnection_name() {
        return connection_name;
    }

    public void setConnection_name(String connection_name) {
        this.connection_name = connection_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDownlaodContentSize() {
        return downlaodContentSize;
    }

    public void setDownlaodContentSize(int downlaodContentSize) {
        this.downlaodContentSize = downlaodContentSize;
    }

    public Modal_ContentDetail getContentDetail() {
        return contentDetail;
    }

    public void setContentDetail(Modal_ContentDetail contentDetail) {
        this.contentDetail = contentDetail;
    }

    public ArrayList<Modal_ContentDetail> getContentList() {
        return content;
    }

    public void setContentList(ArrayList<Modal_ContentDetail> content) {
        this.content = content;
    }
}
