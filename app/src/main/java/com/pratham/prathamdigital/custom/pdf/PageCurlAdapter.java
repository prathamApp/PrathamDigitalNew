package com.pratham.prathamdigital.custom.pdf;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class PageCurlAdapter {

    ArrayList<Bitmap> res_list = new ArrayList<>();

    public PageCurlAdapter(ArrayList<Bitmap> res_list) {
        for (Bitmap res_item : res_list) {
            this.res_list.add(res_item);
        }
    }

    public int getCount() {
        return res_list.size();
    }

    public Bitmap getItemResource(int position) {
        return res_list.get(position);
    }
}
