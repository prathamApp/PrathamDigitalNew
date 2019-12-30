package com.pratham.prathamdigital.custom.file_picker;

import android.support.annotation.Nullable;

import java.util.ArrayList;

public interface FileResultCallback {
    void onResult(@Nullable ArrayList<MediaFile> mediaFiles);
}