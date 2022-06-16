package com.jimla.inventorymanager.item;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDao {
    @Query("SELECT * FROM images")
    List<ImageEntry> getAll();

    @Query("SELECT * FROM images WHERE id IN (:userId) LIMIT 1")
    ImageEntry loadById(int userId);

    @Query("SELECT * FROM images WHERE id IN (:userId)")
    List<ImageEntry> loadAllById(int userId);

    @Insert
    void insert(ImageEntry imageEntry);

    @Insert
    void insertAll(ImageEntry... imageEntries);

    @Query("DELETE FROM images WHERE id LIKE :id")
    void delete(int id);

    @Delete
    void delete(ImageEntry imageEntry);

    @Query("DELETE FROM images")
    void clear();

    @Update
    void update(ImageEntry imageEntry);

    @Query("SELECT * FROM images WHERE itemId IN (:itemId)")
    List<ImageEntry> loadByItemId(int itemId);
}