package com.pratham.prathamdigital.socket.entity;

public class PictureEntity extends WFile {

    private String path;


    public PictureEntity(String path) {
        super(path);
        this.setPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
