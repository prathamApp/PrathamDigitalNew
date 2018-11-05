package com.pratham.prathamdigital.ui.download_list;

import com.pratham.prathamdigital.models.Modal_FileDownloading;

import java.util.Map;

public interface DowloadContract {
    void updateDownloadProgress(Map<Integer, Modal_FileDownloading> downloadings);
}
