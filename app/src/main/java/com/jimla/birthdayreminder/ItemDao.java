package com.jimla.birthdayreminder;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM inventory")
    List<ItemEntry> getAll();

    @Query("SELECT * FROM inventory WHERE id IN (:userId) LIMIT 1")
    ItemEntry loadById(int userId);

    @Query("SELECT * FROM inventory WHERE id IN (:userIds)")
    List<ItemEntry> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM inventory WHERE name LIKE :name LIMIT 1")
    ItemEntry findByName(String name);

    @Insert
    void insert(ItemEntry itemEntry);

    @Insert
    void insertAll(ItemEntry... itemEntries);

    @Query("DELETE FROM inventory WHERE id LIKE :id")
    void delete(int id);

    @Delete
    void delete(ItemEntry itemEntry);

    @Query("DELETE FROM inventory")
    void clear();

    @Update
    void update(ItemEntry itemEntry);

    @Query("SELECT * FROM inventory WHERE roomId IN (:roomId)")
    List<ItemEntry> loadByRoomId(int roomId);
}