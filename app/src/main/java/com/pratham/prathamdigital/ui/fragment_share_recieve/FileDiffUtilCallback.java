package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.io.File;
import java.util.List;

public class FileDiffUtilCallback extends DiffUtil.Callback {
    List<File> newList;
    List<File> oldList;

    public FileDiffUtilCallback(List<File> newList, List<File> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int result = oldList.get(oldItemPosition).compareTo(newList.get(newItemPosition));
        if (result == 0)
            return true;
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return newList.get(newItemPosition);
    }
}
