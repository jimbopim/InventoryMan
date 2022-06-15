package com.jimla.inventorymanager.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoomDao {
    @Query("SELECT * FROM rooms")
    List<RoomEntry> getAll();

    @Query("SELECT * FROM rooms WHERE id IN (:userId) LIMIT 1")
    RoomEntry loadById(int userId);

    @Query("SELECT * FROM rooms WHERE id IN (:userIds)")
    List<RoomEntry> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM rooms WHERE name LIKE :name LIMIT 1")
    RoomEntry findByName(String name);

    @Insert
    void insert(RoomEntry roomEntry);

    @Insert
    void insertAll(RoomEntry... itemEntries);

    @Query("DELETE FROM rooms WHERE id LIKE :id")
    void delete(int id);

    @Delete
    void delete(RoomEntry roomEntry);

    @Query("DELETE FROM rooms")
    void clear();

    @Update
    void update(RoomEntry roomEntry);

    @Query("SELECT * FROM rooms WHERE projectId IN (:projectId)")
    List<RoomEntry> loadByProjectId(int projectId);
}