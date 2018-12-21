package com.pratham.prathamdigital.models;


/**
 * Created by PEF on 24/01/2018.
 */

public class File_Model implements Comparable {
    int progress;
    Modal_ContentDetail detail;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Modal_ContentDetail getDetail() {
        return detail;
    }

    public void setDetail(Modal_ContentDetail detail) {
        this.detail = detail;
    }

    @Override
    public int compareTo(Object o) {
        File_Model compare = (File_Model) o;
        if (compare.getDetail().getNodeid() != null) {
            if (compare.getDetail().getNodeid().equalsIgnoreCase(this.detail.getNodeid()))
                return 0;
            else return 1;
        } else {
            return 0;
        }
    }
}
