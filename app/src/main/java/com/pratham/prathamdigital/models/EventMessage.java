package com.pratham.prathamdigital.models;

import java.util.ArrayList;

public class EventMessage {
    String message;
    int downlaodContentSize;
    Modal_ContentDetail contentDetail;
    ArrayList<Modal_ContentDetail> content;

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
