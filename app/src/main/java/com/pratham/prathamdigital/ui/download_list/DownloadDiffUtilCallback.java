package com.pratham.prathamdigital.ui.download_list;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.pratham.prathamdigital.models.Modal_FileDownloading;

import java.util.List;

public class DownloadDiffUtilCallback extends DiffUtil.Callback {
    List<Modal_FileDownloading> newList;
    List<Modal_FileDownloading> oldList;

    public DownloadDiffUtilCallback(List<Modal_FileDownloading> newList, List<Modal_FileDownloading> oldList) {
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
        return super.getChangePayload(oldItemPosition, newItemPosition);
//        return newList.get(newItemPosition);
    }
}
