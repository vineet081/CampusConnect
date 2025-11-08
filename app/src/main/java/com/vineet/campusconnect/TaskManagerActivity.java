package com.vineet.campusconnect;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.vineet.campusconnect.adapters.TaskAdapter;
import com.vineet.campusconnect.models.Task;
import com.vineet.campusconnect.utils.AlarmUtils;
import com.vineet.campusconnect.viewmodels.TaskViewModel;

public class TaskManagerActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        // 1. Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final TaskAdapter adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        // 2. Get the ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // 3. Observe the LiveData to update the list automatically
        taskViewModel.getAllTasks().observe(this, tasks -> {
            adapter.submitList(tasks);
        });

        // 4. Handle "Add Task" FAB click
        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(view -> {
            AddTaskBottomSheet bottomSheet = new AddTaskBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "addTaskBottomSheet");
        });

        // 5. Handle Item Clicks (marking checkbox as done)
        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onTaskClick(Task task) {
                taskViewModel.update(task);
            }
        });

        // 6. ADD SWIPE TO DELETE
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // We don't want drag-and-drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Get the task that was swiped
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = adapter.getCurrentList().get(position);

                // Cancel the alarm associated with this task
                AlarmUtils.cancelAlarm(TaskManagerActivity.this, taskToDelete);

                // Delete it from the database
                taskViewModel.delete(taskToDelete);

                // Show a confirmation message (Snackbar is better than Toast here)
                Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            // Optional: You could add logic here to re-insert the task if they clicked Undo
                            taskViewModel.insert(taskToDelete);
                            if (taskToDelete.dueDate > System.currentTimeMillis()) {
                                AlarmUtils.scheduleAlarm(TaskManagerActivity.this, taskToDelete);
                            }
                        }).show();
            }
        }).attachToRecyclerView(recyclerView);
    }
}