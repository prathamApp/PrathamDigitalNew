package com.pratham.prathamdigital.async;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.provider.DocumentFile;

import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.models.EventMessage;
import com.pratham.prathamdigital.util.PD_Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import static com.pratham.prathamdigital.dbclasses.PrathamDatabase.DB_NAME;

public class CopyDbToOTG extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Uri treeUri = (Uri) objects[0];
            DocumentFile rootFile = DocumentFile.fromTreeUri(PrathamApplication.getInstance(), treeUri);
            DocumentFile pradigi_backup_file = rootFile.findFile("PraDigi_DBs");
            if (pradigi_backup_file == null)
                pradigi_backup_file = rootFile.createDirectory("PraDigi_DBs");
            String thisdeviceFolderName = "DeviceId" + PrathamApplication.statusDao.getValue("DeviceId");
            DocumentFile thisTabletFolder = pradigi_backup_file.findFile(thisdeviceFolderName);
            if (thisTabletFolder == null)
                thisTabletFolder = pradigi_backup_file.createDirectory(thisdeviceFolderName);
            //copy db files
            File currentDB = PrathamApplication.getInstance().getDatabasePath(DB_NAME);
            File parentPath = currentDB.getParentFile();
            for (File f : parentPath.listFiles()) {
                DocumentFile file = thisTabletFolder.findFile(f.getName());
                if (file != null) file.delete();
                file = thisTabletFolder.createFile("image", f.getName());
                OutputStream out = PrathamApplication.getInstance().getContentResolver().openOutputStream(file.getUri());
                FileInputStream in = new FileInputStream(f.getAbsolutePath());
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                // You have now copied the file
                out.flush();
                out.close();
            }
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
        if ((boolean) o) message.setMessage(PD_Constant.BACKUP_DB_COPIED);
        else message.setMessage(PD_Constant.BACKUP_DB_NOT_COPIED);
        EventBus.getDefault().post(message);
    }
}
