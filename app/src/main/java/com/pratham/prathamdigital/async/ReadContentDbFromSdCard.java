package com.pratham.prathamdigital.async;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pratham.prathamdigital.custom.shared_preference.FastSave;
import com.pratham.prathamdigital.dbclasses.PrathamDatabase;
import com.pratham.prathamdigital.interfaces.Interface_copying;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.util.FileUtils;
import com.pratham.prathamdigital.util.PD_Constant;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pratham.prathamdigital.PrathamApplication.crLdao;
import static com.pratham.prathamdigital.PrathamApplication.groupDao;
import static com.pratham.prathamdigital.PrathamApplication.modalContentDao;
import static com.pratham.prathamdigital.PrathamApplication.studentDao;
import static com.pratham.prathamdigital.PrathamApplication.villageDao;

@EBean
public class ReadContentDbFromSdCard {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private Interface_copying interface_copying;
    private File folder_file;
    private File db_file;

    public ReadContentDbFromSdCard(Context context) {
        this.context = context;
    }

    @Background
    public void doInBackground(Interface_copying _interface_copying) {
        try {
            this.interface_copying = _interface_copying;
//            if (FastSave.getInstance().getBoolean(PD_Constant.READ_CONTENT_FROM_SDCARD, true)) {
            ArrayList<String> sdPath = FileUtils.getExtSdCardPaths(context);
            folder_file = new File(sdPath.get(0) + "/" + PD_Constant.PRADIGI_FOLDER + "/" +
                    FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            /*} else {
                folder_file = new File(Environment.getExternalStorageDirectory() + "/" + PD_Constant.PRADIGI_FOLDER
                        + "/" + FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            }*/
            if (folder_file.exists()) {
                if (dbFilePresentInAssets()) {
                    //the df file stored in asset folder is the updated one
                    AssetManager assetManager = context.getResources().getAssets();
                    InputStream inputStream = assetManager.open(PrathamDatabase.DB_NAME
                            + FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
                    db_file = copyDbFileToInternal(null, inputStream);
                    startDatabaseCloning();
                    onPostExecute(true);
                } else {
                    //else continue with the db file stored in sd-card
                    db_file = new File(folder_file.getAbsolutePath(), PrathamDatabase.DB_NAME);
                    if (db_file.exists()) {
                        db_file = copyDbFileToInternal(db_file, null);
                        startDatabaseCloning();
                        onPostExecute(true);
                    } else
                        onPostExecute(false);
                }
            } else
                onPostExecute(false);
        } catch (Exception e) {
            e.printStackTrace();
            onPostExecute(false);
        }
    }

    private boolean dbFilePresentInAssets() {
        AssetManager assetManager = context.getResources().getAssets();
        InputStream is = null;
        boolean filePresent;
        try {
            is = assetManager.open(PrathamDatabase.DB_NAME + FastSave.getInstance().getString(PD_Constant.LANGUAGE, PD_Constant.HINDI));
            filePresent = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            filePresent = false;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePresent;
    }

    private void startDatabaseCloning() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(db_file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
        if (db == null) onPostExecute(false);
        try {
            Cursor content_cursor = Objects.requireNonNull(db).rawQuery("SELECT * FROM TableContent", null);
            //populate contents
            List<Modal_ContentDetail> contents = new ArrayList<>();
            if (content_cursor.moveToFirst()) {
                while (!content_cursor.isAfterLast()) {
                    Modal_ContentDetail detail = new Modal_ContentDetail();
                    detail.setNodeid(content_cursor.getString(content_cursor.getColumnIndex("nodeid")));
                    detail.setNodetype(content_cursor.getString(content_cursor.getColumnIndex("nodetype")));
                    detail.setNodetitle(content_cursor.getString(content_cursor.getColumnIndex("nodetitle")));
                    detail.setNodekeywords(content_cursor.getString(content_cursor.getColumnIndex("nodekeywords")));
                    detail.setNodeeage(content_cursor.getString(content_cursor.getColumnIndex("nodeeage")));
                    detail.setNodedesc(content_cursor.getString(content_cursor.getColumnIndex("nodedesc")));
                    detail.setNodeimage(content_cursor.getString(content_cursor.getColumnIndex("nodeimage")));
                    detail.setNodeserverimage(content_cursor.getString(content_cursor.getColumnIndex("nodeserverimage")));
                    detail.setResourceid(content_cursor.getString(content_cursor.getColumnIndex("resourceid")));
                    detail.setResourcetype(content_cursor.getString(content_cursor.getColumnIndex("resourcetype")));
                    detail.setResourcepath(content_cursor.getString(content_cursor.getColumnIndex("resourcepath")));
                    detail.setLevel(content_cursor.getInt(content_cursor.getColumnIndex("level")));
                    detail.setContent_language(content_cursor.getString(content_cursor.getColumnIndex("content_language")));
                    detail.setParentid(content_cursor.getString(content_cursor.getColumnIndex("parentid")));

                    //made this change for july 21 db
                    if(content_cursor.getString(content_cursor.getColumnIndex("contentType")).equalsIgnoreCase("Assessment")||
                            content_cursor.getString(content_cursor.getColumnIndex("contentType")).equalsIgnoreCase("Course"))
                        detail.setContentType("folder");
                    else
                        detail.setContentType(content_cursor.getString(content_cursor.getColumnIndex("contentType")));

                    detail.setAltnodeid(content_cursor.getString(content_cursor.getColumnIndex("altnodeid")));
                    detail.setVersion(content_cursor.getString(content_cursor.getColumnIndex("version")));
                    detail.setAssignment(content_cursor.getString(content_cursor.getColumnIndex("assignment")));
                    detail.setSeq_no(content_cursor.getString(content_cursor.getColumnIndex("seq_no")));
                    detail.setDownloaded(true);
                    detail.setOnSDCard(true);
                    contents.add(detail);
                    content_cursor.moveToNext();
                }
            }
            modalContentDao.addContentList(contents);
            content_cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (FastSave.getInstance().getBoolean(PD_Constant.READ_DATA_FROM_DB, false)) {
            //populate villages
            try {
                Cursor village_cursor = db.rawQuery("SELECT * FROM Village", null);
                List<Modal_Village> villages = new ArrayList<>();
                if (village_cursor.moveToFirst()) {
                    while (!village_cursor.isAfterLast()) {
                        Modal_Village vill = new Modal_Village();
                        vill.setVillageId(village_cursor.getInt(village_cursor.getColumnIndex("VillageId")));
                        vill.setVillageCode(village_cursor.getString(village_cursor.getColumnIndex("VillageCode")));
                        vill.setVillageName(village_cursor.getString(village_cursor.getColumnIndex("VillageName")));
                        vill.setBlock(village_cursor.getString(village_cursor.getColumnIndex("Block")));
                        vill.setDistrict(village_cursor.getString(village_cursor.getColumnIndex("District")));
                        vill.setState(village_cursor.getString(village_cursor.getColumnIndex("State")));
                        vill.setCRLId(village_cursor.getString(village_cursor.getColumnIndex("CRLId")));
                        villages.add(vill);
                        village_cursor.moveToNext();
                    }
                }
                villageDao.insertAllVillages(villages);
                village_cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //populate crls
            try {
                Cursor crl_cursor = db.rawQuery("SELECT * FROM CRL", null);
                List<Modal_Crl> crls = new ArrayList<>();
                if (crl_cursor.moveToFirst()) {
                    while (!crl_cursor.isAfterLast()) {
                        Modal_Crl crl = new Modal_Crl();
                        crl.setCRLId(crl_cursor.getString(crl_cursor.getColumnIndex("CRLId")));
                        crl.setRoleId(crl_cursor.getString(crl_cursor.getColumnIndex("RoleId")));
                        crl.setRoleName(crl_cursor.getString(crl_cursor.getColumnIndex("RoleName")));
                        crl.setProgramId(crl_cursor.getString(crl_cursor.getColumnIndex("ProgramId")));
                        crl.setProgramName(crl_cursor.getString(crl_cursor.getColumnIndex("ProgramName")));
                        crl.setState(crl_cursor.getString(crl_cursor.getColumnIndex("State")));
                        crl.setFirstName(crl_cursor.getString(crl_cursor.getColumnIndex("FirstName")));
                        crl.setLastName(crl_cursor.getString(crl_cursor.getColumnIndex("LastName")));
                        crl.setMobile(crl_cursor.getString(crl_cursor.getColumnIndex("Mobile")));
                        crl.setEmail(crl_cursor.getString(crl_cursor.getColumnIndex("Email")));
                        crl.setBlock(crl_cursor.getString(crl_cursor.getColumnIndex("Block")));
                        crl.setDistrict(crl_cursor.getString(crl_cursor.getColumnIndex("District")));
                        crl.setUserName(crl_cursor.getString(crl_cursor.getColumnIndex("UserName")));
                        crl.setPassword(crl_cursor.getString(crl_cursor.getColumnIndex("Password")));
                        crls.add(crl);
                        crl_cursor.moveToNext();
                    }
                }
                crLdao.insertAllCRL(crls);
                crl_cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //populate groups
            try {
                Cursor grp_cursor = db.rawQuery("SELECT * FROM Groups", null);
                List<Modal_Groups> groups = new ArrayList<>();
                if (grp_cursor.moveToFirst()) {
                    while (!grp_cursor.isAfterLast()) {
                        Modal_Groups grp = new Modal_Groups();
                        grp.setGroupId(grp_cursor.getString(grp_cursor.getColumnIndex("GroupId")));
                        grp.setGroupName(grp_cursor.getString(grp_cursor.getColumnIndex("GroupName")));
                        grp.setVillageId(grp_cursor.getString(grp_cursor.getColumnIndex("VillageId")));
                        grp.setProgramId(grp_cursor.getInt(grp_cursor.getColumnIndex("ProgramId")));
                        grp.setGroupCode(grp_cursor.getString(grp_cursor.getColumnIndex("GroupCode")));
                        grp.setSchoolName(grp_cursor.getString(grp_cursor.getColumnIndex("SchoolName")));
                        grp.setVIllageName(grp_cursor.getString(grp_cursor.getColumnIndex("VIllageName")));
                        grp.setDeviceId(grp_cursor.getString(grp_cursor.getColumnIndex("DeviceId")));
                        groups.add(grp);
                        grp_cursor.moveToNext();
                    }
                }
                groupDao.insertAllGroups(groups);
                grp_cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //populate students
            try {
                Cursor stu_cursor = db.rawQuery("SELECT * FROM Students", null);
                List<Modal_Student> students = new ArrayList<>();
                if (stu_cursor.moveToFirst()) {
                    while (!stu_cursor.isAfterLast()) {
                        Modal_Student stu = new Modal_Student();
                        stu.setGroupId(stu_cursor.getString(stu_cursor.getColumnIndex("GroupId")));
                        stu.setStudentId(stu_cursor.getString(stu_cursor.getColumnIndex("StudentId")));
                        stu.setFirstName(stu_cursor.getString(stu_cursor.getColumnIndex("FirstName")));
                        stu.setMiddleName(stu_cursor.getString(stu_cursor.getColumnIndex("MiddleName")));
                        stu.setLastName(stu_cursor.getString(stu_cursor.getColumnIndex("LastName")));
                        stu.setStud_Class(stu_cursor.getString(stu_cursor.getColumnIndex("Stud_Class")));
                        stu.setStud_Class(stu_cursor.getString(stu_cursor.getColumnIndex("Stud_Class")));
                        stu.setAge(stu_cursor.getString(stu_cursor.getColumnIndex("Age")));
                        stu.setGender(stu_cursor.getString(stu_cursor.getColumnIndex("Gender")));
                        if (stu.getFirstName() == null || stu.getFirstName().isEmpty())
                            stu.setFullName(stu_cursor.getString(stu_cursor.getColumnIndex("FullName")));
                        else
                            stu.setFullName(stu.getFirstName() + " " + stu.getMiddleName() + " " + stu.getLastName());
                        stu.setSentFlag(1);
                        //                    stu.setGroupName();
                        students.add(stu);
                        stu_cursor.moveToNext();
                    }
                }
                studentDao.insertAllStudents(students);
                stu_cursor.close();
                FastSave.getInstance().saveBoolean(PD_Constant.READ_DATA_FROM_DB, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File copyDbFileToInternal(File db_file, InputStream dbFileInputStream) {
        try {
            InputStream inputStream;
            File[] intDir = context.getExternalFilesDirs("");
            File copied_db_file = new File(intDir[0], PrathamDatabase.DB_NAME);
            if (dbFileInputStream == null) {
                inputStream = new FileInputStream(db_file);
            } else
                inputStream = dbFileInputStream;
            OutputStream os = new FileOutputStream(copied_db_file);
            byte[] buff = new byte[1024];
            int len;
            while ((len = inputStream.read(buff)) > 0) {
                os.write(buff, 0, len);
            }
            inputStream.close();
            os.close();
            return copied_db_file;
        } catch (IOException e) {
            e.printStackTrace();
            return db_file;
        }
    }

    @UiThread
    public void onPostExecute(Boolean copied) {
        if (db_file != null && db_file.getAbsolutePath().contains("storage/emulated/0"))
            db_file.delete();
        if (copied)
            interface_copying.successCopyingExisting(folder_file.getAbsolutePath());
        else
            interface_copying.failedCopyingExisting();
    }
}
