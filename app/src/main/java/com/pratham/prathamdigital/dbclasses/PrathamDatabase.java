package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Log;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_Village;
import com.pratham.prathamdigital.models.Model_ContentProgress;
import com.pratham.prathamdigital.models.Model_CourseEnrollment;

/*
    always update the version number,
    when their is a modification in any of the table structure.
*/
@Database(entities = {Attendance.class, Modal_ContentDetail.class, Modal_Crl.class, Modal_Groups.class,
        Modal_Score.class, Modal_Session.class, Modal_Status.class, Modal_Student.class, Modal_Village.class,
        Modal_Log.class, Model_CourseEnrollment.class, Model_ContentProgress.class}, version = 5, exportSchema = false)
public abstract class PrathamDatabase extends RoomDatabase {
    private static PrathamDatabase INSTANCE;
    public static final String DB_NAME = "pradigi_db";

    public abstract AttendanceDao getAttendanceDao();

    public abstract CRLdao getCrLdao();

    public abstract GroupDao getGroupDao();

    public abstract ModalContentDao getModalContentDao();

    public abstract ScoreDao getScoreDao();

    public abstract SessionDao getSessionDao();

    public abstract StatusDao getStatusDao();

    public abstract StudentDao getStudentDao();

    public abstract VillageDao getVillageDao();

    public abstract LogDao getLogDao();

    public static PrathamDatabase getDatabaseInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    PrathamDatabase.class, DB_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .allowMainThreadQueries() // SHOULD NOT BE USED IN PRODUCTION !!!
                    .build();
        }
        return INSTANCE;
    }

    private static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE TableContent ADD COLUMN altnodeid TEXT");
            database.execSQL("ALTER TABLE TableContent ADD COLUMN version TEXT");
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };
    private static Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS CourseEnrolled ('c_autoID' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "'courseId' TEXT ,'groupId' TEXT ,'planFromDate' TEXT ," +
                    "'planToDate' TEXT ,'coachVerified' BOOLEAN DEFAULT 0,'coachVerificationDate' TEXT ," +
                    "'courseExperience' TEXT )");
        }
    };

    private static Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE CourseEnrolled ADD COLUMN courseCompleted INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE CourseEnrolled ADD COLUMN coachImage TEXT");
            database.execSQL("ALTER TABLE CourseEnrolled ADD COLUMN sentFlag BOOLEAN DEFAULT 0");
            database.execSQL("ALTER TABLE CourseEnrolled ADD COLUMN language TEXT");
            database.execSQL("CREATE TABLE IF NOT EXISTS ContentProgress ('progressId' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "'studentId' TEXT ,'resourceId' TEXT ,'updatedDateTime' TEXT ," +
                    "'progressPercentage' TEXT ,'label' TEXT ,'sentFlag' BOOLEAN DEFAULT 0)");
        }
    };
    private static Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE TableContent ADD COLUMN assignment TEXT");
        }
    };

    public abstract CourseDao getCourseDao();

    public abstract ContentProgressDao getContentProgressDao();

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }

}
