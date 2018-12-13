package com.pratham.prathamdigital.async;

import android.os.AsyncTask;

import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.util.PD_Constant;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class UnzipAsync extends AsyncTask<String, String, Boolean> {
    String source;
    String destination;
    //    ContentContract.contentPresenter presenter;
    String downloadId;
    String nodeid;

    public UnzipAsync(String source, String destination, /*ContentContract.contentPresenter presenter,*/
                      String downloadId) {
        this.source = source;
        this.destination = destination;
//        this.presenter = presenter;
        this.downloadId = downloadId;
        this.nodeid = nodeid;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean extracted) {
        super.onPostExecute(extracted);
        if (extracted) {
            new File(source).delete();
//            presenter.onDownloadCompleted(downloadId);
            EventMessage message = new EventMessage();
            message.setMessage(PD_Constant.DOWNLOAD_COMPLETE);
            message.setDownloadId(downloadId);
            EventBus.getDefault().post(message);
        }
    }
}
