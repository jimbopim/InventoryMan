package com.jimla.birthdayreminder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "projects")
public class ProjectEntry {

    public ProjectEntry(int id, String name, String description, long entryTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.entryTime = entryTime;
    }

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "entryTime")
    public long entryTime;
}