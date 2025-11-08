package com.vineet.campusconnect.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.vineet.campusconnect.models.Task;

import java.util.List;

public class TaskRepository {

    private TaskDao mTaskDao;
    private LiveData<List<Task>> mAllTasks;

    // Constructor that gets a handle to the database and initializes the member variables
    public TaskRepository(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        // We initialize the "default" view: sorted by created time
        mAllTasks = mTaskDao.getAllTasksSortedByCreated();
    }

    // --- API Methods for the UI to call ---

    // Get all tasks (observed by the UI)
    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    // You must call this on a non-UI thread or the app will crash.
    // We use the databaseWriteExecutor we created in TaskDatabase.
    public void insert(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.insert(task);
        });
    }

    public void update(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.update(task);
        });
    }

    public void delete(Task task) {
        TaskDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.delete(task);
        });
    }
}