package com.jimla.inventorymanager;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jimla.inventorymanager.item.ImageDao;
import com.jimla.inventorymanager.item.ImageEntry;
import com.jimla.inventorymanager.item.ItemDao;
import com.jimla.inventorymanager.item.ItemEntry;
import com.jimla.inventorymanager.project.ProjectDao;
import com.jimla.inventorymanager.project.ProjectEntry;
import com.jimla.inventorymanager.room.RoomDao;
import com.jimla.inventorymanager.room.RoomEntry;

@Database(entities = {ProjectEntry.class, RoomEntry.class, ItemEntry.class, ImageEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProjectDao projectDao();
    public abstract RoomDao roomDao();
    public abstract ItemDao itemDao();
    public abstract ImageDao imageDao();


    private static AppDatabase db;

    public static AppDatabase getDatabaseInstance(Context context, String dbName) {
        if(db == null) {
            db = Room.databaseBuilder(context, AppDatabase.class, dbName).build();
        }
        return db;
    }
}