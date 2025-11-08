package com.vineet.campusconnect.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.vineet.campusconnect.models.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 1. Annotate it as a Room Database, list your tables (entities), and set version to 1
@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    // 2. Define the DAO we just created so the app can access it
    public abstract TaskDao taskDao();

    // 3. Create the Singleton (the one-and-only instance of this database)
    private static volatile TaskDatabase INSTANCE;

    // We need a background thread pool to run database operations so the UI doesn't freeze
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // 4. The method to get the database instance
    public static TaskDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskDatabase.class) {
                if (INSTANCE == null) {
                    // This line actually creates the database file on the phone!
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TaskDatabase.class, "task_database")
                            .fallbackToDestructiveMigration() // If we change the table later, just delete the old one (simple for now)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}