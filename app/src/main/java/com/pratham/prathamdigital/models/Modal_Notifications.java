package com.pratham.prathamdigital.models;

import androidx.annotation.NonNull;

public class Modal_Notifications implements Comparable{

    String nfDate, nfTitle, nfDesc, nfVideolink;
    private boolean isselected;

    public Modal_Notifications(String nfDate, String nfTitle, String nfDesc, String nfVideolink) {
        this.nfDate = nfDate;
        this.nfTitle = nfTitle;
        this.nfDesc = nfDesc;
        this.nfVideolink = nfVideolink;
    }

    public String getNfDate() {
        return nfDate;
    }

    public void setNfDate(String nfDate) {
        this.nfDate = nfDate;
    }

    public String getNfTitle() {
        return nfTitle;
    }

    public void setNfTitle(String nfTitle) {
        this.nfTitle = nfTitle;
    }

    public String getNfDesc() {
        return nfDesc;
    }

    public void setNfDesc(String nfDesc) {
        this.nfDesc = nfDesc;
    }

    public String getNfVideolink() {
        return nfVideolink;
    }

    public void setNfVideolink(String nfVideolink) {
        this.nfVideolink = nfVideolink;
    }

    public boolean isIsselected() {
        return isselected;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Modal_Notifications compare = (Modal_Notifications) o;
        if (compare.isIsselected() == this.isIsselected())
            return 0;
        else return 1;
    }
}