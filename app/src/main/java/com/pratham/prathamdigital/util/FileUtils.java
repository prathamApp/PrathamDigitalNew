package com.pratham.prathamdigital.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.pratham.prathamdigital.socket.entity.MusicEntity;
import com.pratham.prathamdigital.socket.entity.PictureEntity;
import com.pratham.prathamdigital.socket.entity.PictureFolderEntity;
import com.pratham.prathamdigital.socket.entity.VedioEntity;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileUtils {

    public static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File isSdcard0Exist() {
        return Environment.getExternalStorageDirectory();
    }

    public static void createDirFile(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static File createNewFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }
        return file;
    }

    public static void delFolder(String folderPath) {
        delAllFile(folderPath);
        String filePath = folderPath;
        filePath = filePath.toString();
        java.io.File myFilePath = new java.io.File(filePath);
        myFilePath.delete();
    }

    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        int mLength = tempList.length;
        for (int i = 0; i < mLength; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
            }
        }
    }

    public static Uri getUriFromFile(String path) {
        File file = new File(path);
        return Uri.fromFile(file);
    }

    public static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "Unknown size";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String getPathByFullPath(String fullpath) {
        return fullpath.substring(0, fullpath.lastIndexOf(File.separator));
    }

    public static String getNameByPath(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    public static boolean isFileExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.getAbsolutePath();
        } else {
            return Environment.getDataDirectory().getAbsolutePath();
        }
    }

    public static List<PictureFolderEntity> getPictureFolderList(Context context) {
        List<PictureFolderEntity> list = new ArrayList<PictureFolderEntity>();

        HashMap<String, Integer> tmpDir = new HashMap<String, Integer>();

        ContentResolver mContentResolver = context.getContentResolver();
        Cursor mCursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.DATA}, "", null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC");
        if (mCursor.moveToFirst()) {
            int _date = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                String path = mCursor.getString(_date);
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                PictureFolderEntity pictureFoldery = null;
                String dirPath = parentFile.getAbsolutePath();
                if (!tmpDir.containsKey(dirPath)) {
                    pictureFoldery = new PictureFolderEntity();
                    pictureFoldery.setDir(dirPath);
                    pictureFoldery.setFirstImagePath(path);
                    list.add(pictureFoldery);
                    // Log.d("zyh", dirPath + "," + path);
                    tmpDir.put(dirPath, list.indexOf(pictureFoldery));
                } else {
                    pictureFoldery = list.get(tmpDir.get(dirPath));
                }
                pictureFoldery.images.add(new PictureEntity(path));
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
        }
        tmpDir = null;
        return list;
    }

    public static List<MusicEntity> getMusicList(Context context) {
        List<MusicEntity> list = new ArrayList<MusicEntity>();

        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA
                }, null,
                null,
                null);
        while (cursor.moveToNext()) {
            MusicEntity music = new MusicEntity(cursor.getString(5));
            music.setTitle(cursor.getString(0));
            music.setDuration(cursor.getLong(1));
            music.setArtist(cursor.getString(2));
            music.setId(cursor.getInt(3));
            music.setDisplayName(cursor.getString(4));
            // L.i(music.toString());
            if (music.getDuration() < 60 * 1000)
                continue;
            list.add(music);
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public static List<VedioEntity> getVedioList(Context context) {
        List<VedioEntity> list = new ArrayList<VedioEntity>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, //
                new String[]{MediaStore.Video.Media._ID,//
                        MediaStore.Video.Media.DATA, //
                        MediaStore.Video.Media.DURATION, //
                        MediaStore.Video.Media.DISPLAY_NAME,//
                        MediaStore.Video.Media.SIZE //
                }, null, // 查询条件，相当于sql中的where语句
                null, // 查询条件中使用到的数据
                null);
        while (cursor.moveToNext()) {
            VedioEntity vedio = new VedioEntity(cursor.getString(1));
            vedio.setId(cursor.getInt(0));
            vedio.setDuration(cursor.getLong(2));
            vedio.setDisplayName(cursor.getString(3));
//            vedio.setSize(cursor.getLong(4));
            list.add(vedio);
        }
        return list;
    }

/*
    public static String getProjectDir() {
        File file = new File(getSDPath() + "/WifiProject");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static String getProjectPictureDir() {
        File file = new File(getProjectDir() + "/pictures");
        if (!file.exists()) {
            file.mkdirs();
        } else {
            for (File f : file.listFiles()) {
                f.delete();
            }
        }
        return file.getAbsolutePath();
    }*/
}
