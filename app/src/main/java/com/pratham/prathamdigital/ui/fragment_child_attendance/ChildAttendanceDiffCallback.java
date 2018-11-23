package com.pratham.prathamdigital.ui.fragment_child_attendance;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.pratham.prathamdigital.models.Modal_Student;

import java.util.ArrayList;

public class ChildAttendanceDiffCallback extends DiffUtil.Callback {
    private ArrayList<Modal_Student> oldLanguageList = new ArrayList<>();
    private ArrayList<Modal_Student> newLanguageList = new ArrayList<>();

    public ChildAttendanceDiffCallback(ArrayList<Modal_Student> oldLanguageList, ArrayList<Modal_Student> newLanguageList) {
        this.oldLanguageList = oldLanguageList;
        this.newLanguageList = newLanguageList;
    }

    @Override
    public int getOldListSize() {
        return oldLanguageList != null ? oldLanguageList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newLanguageList != null ? newLanguageList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        int result = newLanguageList.get(newItemPosition).compareTo(oldLanguageList.get(oldItemPosition));
        if (result == 0) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Modal_Student newLanguage = newLanguageList.get(newItemPosition);
        return newLanguage;
    }
}
