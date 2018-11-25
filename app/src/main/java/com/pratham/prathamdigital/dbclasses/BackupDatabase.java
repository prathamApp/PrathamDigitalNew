package com.pratham.prathamdigital.dbclasses;

import android.content.Context;
import android.os.Environment;

import com.pratham.prathamdigital.util.PD_Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import static com.pratham.prathamdigital.dbclasses.PrathamDatabase.DB_NAME;

public class BackupDatabase {

    public static void backup(Context mContext) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
//                File file = mContext.getDir("databases", Context.MODE_PRIVATE);
//                String currentDBPath = file.getAbsolutePath().replace("app_databases", "databases") + "/" + DB_NAME;
                File currentDB = mContext.getDatabasePath(DB_NAME);
                File parentPath = currentDB.getParentFile();
//            String backupDBPath = DB_NAME + ".db";
//                File currentDB = new File(currentDBPath);
//            File backupDB = new File(sd, backupDBPath);
                for (File f : parentPath.listFiles()) {
                    File temp = new File(sd, f.getName());
                    if (!temp.exists()) temp.createNewFile();
                    FileChannel src = new FileInputStream(f).getChannel();
                    FileChannel dst = new FileOutputStream(temp).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
//            if (currentDB.exists()) {
//                FileChannel src = new FileInputStream(currentDB).getChannel();
//                FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                dst.transferFrom(src, 0, src.size());
//                src.close();
//                dst.close();
//            }
            } else {
                //todo ask storage permission
                EventBus.getDefault().post(PD_Constant.WRITE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
