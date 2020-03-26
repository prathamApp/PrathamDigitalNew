package com.pratham.prathamdigital.custom.file_picker;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public interface FileResultCallback {
    void onResult(@Nullable ArrayList<MediaFile> mediaFiles);
}