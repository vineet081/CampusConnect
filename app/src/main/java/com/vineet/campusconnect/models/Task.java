package com.vineet.campusconnect.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// This tag tells Room that this class is a table in the database
@Entity(tableName = "task_table")
public class Task {

    // Every task needs a unique ID. autoGenerate = true means Room handles this for us.
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;

    // false = not done, true = done
    public boolean isCompleted;

    // We use 'long' for dates because it's easier to sort numbers than Strings.
    // This will store milliseconds since 1970 (standard computer time).
    public long dueDate;

    public long createdTimestamp;

    // Priority: 1 = Low, 2 = Medium, 3 = High
    public int priority;

    // Constructor used when creating a new task
    public Task(String title, String description, long dueDate, int priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isCompleted = false; // Default to not completed
        this.createdTimestamp = System.currentTimeMillis(); // Automatically set created time
    }
}