package com.cokimutai.med_manager.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;



@Database(entities = {MedEntry.class}, version = 3, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MedDatabase extends RoomDatabase {

    private static MedDatabase sInstance;
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "medication";

    public static MedDatabase getsInstance(Context context){
        if (sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MedDatabase.class,
                        DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return sInstance;
    }
    public abstract MedDao medDao();
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };
}
