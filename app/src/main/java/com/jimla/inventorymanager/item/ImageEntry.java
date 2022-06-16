package com.jimla.inventorymanager.item;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "images")
public class ImageEntry {

    public ImageEntry(int id, int itemId, String description, String photo, long entryTime) {
        this.id = id;
        this.itemId = itemId;
        this.description = description;
        this.photo = photo;
        this.entryTime = entryTime;
    }

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "itemId")
    public int itemId;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "photo")
    public String photo;

    @ColumnInfo(name = "entryTime")
    public long entryTime;
}