package com.pratham.prathamdigital.dbclasses;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.pratham.prathamdigital.models.Attendance;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Crl;
import com.pratham.prathamdigital.models.Modal_Groups;
import com.pratham.prathamdigital.models.Modal_Score;
import com.pratham.prathamdigital.models.Modal_Session;
import com.pratham.prathamdigital.models.Modal_Status;
import com.pratham.prathamdigital.models.Modal_Student;
import com.pratham.prathamdigital.models.Modal_Village;

@Database(entities = {Attendance.class, Modal_ContentDetail.class, Modal_Crl.class, Modal_Groups.class, Modal_Score.class, Modal_Session.class, Modal_Status.class, Modal_Student.class, Modal_Village.class}, version = 1, exportSchema = false)
public abstract class PrathamDatabase extends RoomDatabase {
    private static PrathamDatabase INSTANCE;
    public static final String DB_NAME = "pradigi";

    public abstract AttendanceDao getAttendanceDao();

    public abstract CRLdao getCrLdao();

    public abstract GroupDao getGroupDao();

    public abstract ModalContentDao getModalContentDao();

    public abstract ScoreDao getScoreDao();

    public abstract SessionDao getSessionDao();

    public abstract StatusDao getStatusDao();

    public abstract StudentDao getStudentDao();

    public abstract VillageDao getVillageDao();

    public static PrathamDatabase getDatabaseInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    PrathamDatabase.class, DB_NAME)
                    .allowMainThreadQueries() // SHOULD NOT BE USED IN PRODUCTION !!!
//                    .addCallback(new RoomDatabase.Callback() {
//                        @Override
//                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//                            super.onCreate(db);
//                            Log.d("MoviesDatabase", "populating with data...");
//                            new PopulateDbAsync(INSTANCE).execute();
//                        }
//                    })
                    .build();
        }
        return INSTANCE;
    }

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
