package com.vineet.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.vineet.campusconnect.AddTaskBottomSheet;
import com.vineet.campusconnect.R;
import com.vineet.campusconnect.adapters.TaskAdapter;
import com.vineet.campusconnect.models.Task;
import com.vineet.campusconnect.utils.AlarmUtils;
import com.vineet.campusconnect.viewmodels.TaskViewModel;

public class TaskManagerFragment extends Fragment {

    private TaskViewModel taskViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Inflate the layout
        View view = inflater.inflate(R.layout.fragment_task_manager, container, false);

        // 2. Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final TaskAdapter adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        // 3. Get the ViewModel
        // We use 'this' so the ViewModel dies when the Fragment dies
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // 4. Observe LiveData
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            adapter.submitList(tasks);
        });

        // 5. Handle "Add Task" FAB
        FloatingActionButton fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> {
            AddTaskBottomSheet bottomSheet = new AddTaskBottomSheet();
            // Note: We use getParentFragmentManager() inside a Fragment
            bottomSheet.show(getParentFragmentManager(), "addTaskBottomSheet");
        });

        // 6. Handle Item Clicks (Checkbox)
        adapter.setOnItemClickListener(task -> taskViewModel.update(task));

        // 7. Add Swipe to Delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = adapter.getCurrentList().get(position);

                // Cancel alarm
                AlarmUtils.cancelAlarm(getContext(), taskToDelete);

                // Delete
                taskViewModel.delete(taskToDelete);

                // Undo Snackbar
                Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            taskViewModel.insert(taskToDelete);
                            if (taskToDelete.dueDate > System.currentTimeMillis()) {
                                AlarmUtils.scheduleAlarm(getContext(), taskToDelete);
                            }
                        }).show();
            }
        }).attachToRecyclerView(recyclerView);

        return view;
    }
}