package com.pratham.prathamdigital.dbclasses;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.pratham.prathamdigital.util.PD_Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by PEF-2 on 29/10/2015.
 */
public class BackupDatabase {

    public static void backup(Context mContext, String destPath) {
        try {
//            File sd = Environment.getExternalStorageDirectory();
            File file = mContext.getDir("databases", Context.MODE_PRIVATE);
            String currentDBPath = file.getAbsolutePath().replace("app_databases", "databases");
            File file1 = new File(currentDBPath, "PrathamDB");
            Log.d("db_path::", currentDBPath);
            Log.d("db_path::", file1.getAbsolutePath());
            String backupDBPath = "PrathamDB";
//            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(destPath, backupDBPath);
            //File currentDB = new File(sd,backupDBPath);
//             File backupDB = new File(data,currentDBPath);
            if (file.exists()) {
                FileChannel src = new FileInputStream(file1).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
            backupDB.renameTo(new File(destPath, "PrathamDB.db"));
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
