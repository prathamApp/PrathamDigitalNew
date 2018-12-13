package com.pratham.prathamdigital.async;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.pratham.prathamdigital.BaseActivity;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.ui.splash.SplashContract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CopyExistingDb extends AsyncTask<String, String, Boolean> {

    File db_file;
    File folder_file;
    SplashContract.splashPresenter presenter;

    public CopyExistingDb(File folder_file, File db_file, SplashContract.splashPresenter presenter) {
        this.folder_file = folder_file;
        this.db_file = db_file;
        this.presenter = presenter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        presenter.copyingExistingDb();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(db_file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            if (db == null) return false;
            Cursor c = db.rawQuery("SELECT * FROM TableContent", null);
            List<Modal_ContentDetail> contents = new ArrayList<>();
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    Modal_ContentDetail detail = new Modal_ContentDetail();
                    detail.setNodeid(c.getString(c.getColumnIndex("nodeid")));
                    detail.setNodetype(c.getString(c.getColumnIndex("nodetype")));
                    detail.setNodetitle(c.getString(c.getColumnIndex("nodetitle")));
                    detail.setNodekeywords(c.getString(c.getColumnIndex("nodekeywords")));
                    detail.setNodeeage(c.getString(c.getColumnIndex("nodeeage")));
                    detail.setNodedesc(c.getString(c.getColumnIndex("nodedesc")));
                    detail.setNodeimage(c.getString(c.getColumnIndex("nodeimage")));
                    detail.setNodeserverimage(c.getString(c.getColumnIndex("nodeserverimage")));
                    detail.setResourceid(c.getString(c.getColumnIndex("resourceid")));
                    detail.setResourcetype(c.getString(c.getColumnIndex("resourcetype")));
                    detail.setResourcepath(c.getString(c.getColumnIndex("resourcepath")));
                    detail.setLevel(c.getInt(c.getColumnIndex("level")));
                    detail.setContent_language(c.getString(c.getColumnIndex("content_language")));
                    detail.setParentid(c.getString(c.getColumnIndex("parentid")));
                    detail.setContentType(c.getString(c.getColumnIndex("contentType")));
                    detail.setDownloaded(true);
                    detail.setOnSDCard(true);
                    contents.add(detail);
                    c.moveToNext();
                }
            }
            BaseActivity.modalContentDao.addContentList(contents);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean copied) {
        super.onPostExecute(copied);
        if (copied)
            presenter.successCopyingExistingDb(folder_file.getAbsolutePath());
        else
            presenter.failedCopyingExistingDb();
    }
}
