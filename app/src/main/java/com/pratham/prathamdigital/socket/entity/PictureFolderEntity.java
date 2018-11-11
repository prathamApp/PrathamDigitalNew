package com.pratham.prathamdigital.socket.entity;

import java.util.ArrayList;
import java.util.List;

public class PictureFolderEntity {
    private String dir;
    private String firstImagePath;
    private String name;

    public List<PictureEntity> images = new ArrayList<PictureEntity>();


    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.name = this.dir.substring(lastIndexOf);
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public String getName() {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        return name;
    }
}
