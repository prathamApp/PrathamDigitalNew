package com.pratham.prathamdigital.async;

import android.os.AsyncTask;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.interfaces.DownloadedContents;

public class GetDownloadedContent extends AsyncTask {
    String parentId;
    DownloadedContents downloadedContents;

    public GetDownloadedContent(DownloadedContents downloadedContents, String parentId) {
        this.parentId = parentId;
        this.downloadedContents = downloadedContents;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if (parentId != null && !parentId.equalsIgnoreCase("0") && !parentId.isEmpty())
            return BaseActivity.modalContentDao.getChildsOfParent(parentId);
        else
            return BaseActivity.modalContentDao.getParentsHeaders();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (downloadedContents != null)
            downloadedContents.downloadedContents(o, parentId);
    }
}
