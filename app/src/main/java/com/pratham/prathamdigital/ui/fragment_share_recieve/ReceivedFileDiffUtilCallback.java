package com.pratham.prathamdigital.ui.fragment_share_recieve;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.pratham.prathamdigital.models.Modal_ReceivingFilesThroughFTP;

import java.util.ArrayList;

public class ReceivedFileDiffUtilCallback extends DiffUtil.Callback {
    ArrayList<Modal_ReceivingFilesThroughFTP> newList;
    ArrayList<Modal_ReceivingFilesThroughFTP> oldList;

    public ReceivedFileDiffUtilCallback(ArrayList<Modal_ReceivingFilesThroughFTP> newList, ArrayList<Modal_ReceivingFilesThroughFTP> oldList) {
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
