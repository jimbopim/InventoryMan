package com.jimla.inventorymanager.project;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProjectDao {
    @Query("SELECT * FROM projects")
    List<ProjectEntry> getAll();

    @Query("SELECT * FROM projects WHERE id IN (:userId) LIMIT 1")
    ProjectEntry loadById(int userId);

    @Query("SELECT * FROM projects WHERE id IN (:userIds)")
    List<ProjectEntry> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM projects WHERE name LIKE :name LIMIT 1")
    ProjectEntry findByName(String name);

    @Insert
    void insert(ProjectEntry projectEntry);

    @Insert
    void insertAll(ProjectEntry... itemEntries);

    @Query("DELETE FROM projects WHERE id LIKE :id")
    void delete(int id);

    @Delete
    void delete(ProjectEntry projectEntry);

    @Query("DELETE FROM projects")
    void clear();

    @Update
    void update(ProjectEntry projectEntry);
}