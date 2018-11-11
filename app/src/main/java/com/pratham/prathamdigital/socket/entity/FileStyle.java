package com.pratham.prathamdigital.socket.entity;

import java.util.Comparator;

public class FileStyle implements Comparator<WFile> {


    @Override
    public int compare(WFile lhs, WFile rhs) {
        int type1 = lhs.isDirectory() ? 1 : 2;
        int type2 = rhs.isDirectory() ? 1 : 2;
        return type1 == type2 ? lhs.getName().compareTo(rhs.getName()) : type1 - type2;
    }
}
