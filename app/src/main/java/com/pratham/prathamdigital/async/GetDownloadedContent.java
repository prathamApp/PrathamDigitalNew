package com.pratham.prathamdigital.async;

import android.os.AsyncTask;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.interfaces.DownloadedContents;
import com.pratham.prathamdigital.util.PD_Constant;

public class GetDownloadedContent extends AsyncTask {
    String parentId;
    DownloadedContents downloadedContents;

    public GetDownloadedContent(DownloadedContents downloadedContents, String parentId) {
        this.parentId = parentId;
        this.downloadedContents = downloadedContents;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        String lang = FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI);
        if (parentId != null && !parentId.equalsIgnoreCase("0") && !parentId.isEmpty())
            return BaseActivity.modalContentDao.getChildsOfParent(parentId, lang);
        else
            return BaseActivity.modalContentDao.getParentsHeaders(lang);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (downloadedContents != null)
            downloadedContents.downloadedContents(o, parentId);
    }
}
