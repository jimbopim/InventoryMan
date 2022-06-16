package com.jimla.inventorymanager.item;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory")
public class ItemEntry {

    public ItemEntry(int id, int roomId, String name, String rfid, String description, long entryTime) {
        this.id = id;
        this.roomId = roomId;
        this.name = name;
        this.rfid = rfid;
        this.description = description;
        this.entryTime = entryTime;
    }

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "roomId")
    public int roomId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "rfid")
    public String rfid;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "entryTime")
    public long entryTime;
}