package com.jimla.birthdayreminder;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ProjectEntry.class, RoomEntry.class, ItemEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProjectDao projectDao();
    public abstract ItemDao itemDao();
    public abstract RoomDao roomDao();

    private static AppDatabase db;

    public static AppDatabase getDatabaseInstance(Context context, String dbName) {
        if(db == null) {
            db = Room.databaseBuilder(context, AppDatabase.class, dbName).build();
        }
        return db;
    }
}