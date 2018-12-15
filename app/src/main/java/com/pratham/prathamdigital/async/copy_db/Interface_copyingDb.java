package com.pratham.prathamdigital.async.copy_db;

public interface Interface_copyingDb {
    void copyingExistingDb();

    void successCopyingExistingDb(String path);

    void failedCopyingExistingDb();
}
