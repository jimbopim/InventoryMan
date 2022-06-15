package com.jimla.inventorymanager.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rooms")
public class RoomEntry {

    public RoomEntry(int id, int projectId, String name, String description, int floor, long entryTime) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.floor = floor;
        this.entryTime = entryTime;
    }

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "projectId")
    public int projectId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "floor")
    public int floor;

    @ColumnInfo(name = "entryTime")
    public long entryTime;
}