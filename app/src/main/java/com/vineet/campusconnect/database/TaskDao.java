package com.vineet.campusconnect.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.vineet.campusconnect.models.Task;

import java.util.List;

@Dao
public interface TaskDao {

    // 1. Basic Operations (Insert, Update, Delete)
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    // 2. DELETE ALL (Useful for a "Clear all" button)
    @Query("DELETE FROM task_table")
    void deleteAllTasks();

    // 3. READ Operations (Queries)
    // We use LiveData so the UI updates automatically when data changes

    // Default view: Sorted by created time (newest on top)
    @Query("SELECT * FROM task_table ORDER BY createdTimestamp DESC")
    LiveData<List<Task>> getAllTasksSortedByCreated();

    // Sort by Priority (High priority = 3, so DESC puts 3 at the top)
    @Query("SELECT * FROM task_table ORDER BY priority DESC, dueDate ASC")
    LiveData<List<Task>> getAllTasksSortedByPriority();

    // Sort by Due Date (Soonest date at the top)
    @Query("SELECT * FROM task_table ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasksSortedByDueDate();

    // 4. ANALYTICS Operations
    // Count completed tasks
    @Query("SELECT COUNT(*) FROM task_table WHERE isCompleted = 1")
    LiveData<Integer> getCompletedTaskCount();

    // Count pending tasks
    @Query("SELECT COUNT(*) FROM task_table WHERE isCompleted = 0")
    LiveData<Integer> getPendingTaskCount();
}