package com.pratham.prathamdigital.async;

import android.net.Uri;
import android.os.AsyncTask;

import androidx.documentfile.provider.DocumentFile;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.util.PD_Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class UpdateDatabaseInSdCard extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Uri treeUri = (Uri) objects[0];
            DocumentFile rootFile = DocumentFile.fromTreeUri(PrathamApplication.getInstance(), treeUri);
            DocumentFile pradigi_backup_file = rootFile.findFile(PD_Constant.PRADIGI_FOLDER);
            if (pradigi_backup_file == null) return false;
            String language = FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI);
            DocumentFile languageFolder = pradigi_backup_file.findFile(language);
            if (languageFolder == null) return false;
            //copy db files
            File currentDB = new File((String) objects[1]);
            DocumentFile file = languageFolder.findFile(currentDB.getName());
            if (file != null) file.delete();
            file = languageFolder.createFile("image", currentDB.getName());
            OutputStream out = PrathamApplication.getInstance().getContentResolver().openOutputStream(file.getUri());
            FileInputStream in = new FileInputStream(currentDB.getAbsolutePath());
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            // You have now copied the file
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        EventMessage message = new EventMessage();
        if ((boolean) o) message.setMessage(PD_Constant.DB_FILE_UPDATED);
        EventBus.getDefault().post(message);
    }
}
