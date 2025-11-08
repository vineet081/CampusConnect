package com.vineet.campusconnect.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.vineet.campusconnect.R;
import com.vineet.campusconnect.models.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// We use ListAdapter for automatic animations when data changes
public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private OnItemClickListener listener;

    public TaskAdapter() {
        super(DIFF_CALLBACK);
    }

    // This "DiffUtil" tells the adapter how to check if two tasks are the same.
    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.id == newItem.id; // Same Database ID means same task
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            // Check if the actual content changed
            return oldItem.title.equals(newItem.title) &&
                    oldItem.description.equals(newItem.description) &&
                    oldItem.isCompleted == newItem.isCompleted &&
                    oldItem.priority == newItem.priority &&
                    oldItem.dueDate == newItem.dueDate;
        }
    };

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = getItem(position);

        // 1. Set text
        holder.textViewTitle.setText(currentTask.title);

        // 2. Format the date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
        String dateString = sdf.format(new Date(currentTask.dueDate));
        holder.textViewDueDate.setText("Due: " + dateString);

        // 3. Set checkbox without triggering the listener
        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setChecked(currentTask.isCompleted);

        // 4. Set Priority Color
        int priorityColor;
        switch (currentTask.priority) {
            case 3: // High
                priorityColor = Color.parseColor("#F44336"); // Red
                break;
            case 2: // Medium
                priorityColor = Color.parseColor("#FFC107"); // Amber/Yellow
                break;
            default: // Low (1)
                priorityColor = Color.parseColor("#4CAF50"); // Green
        }
        holder.priorityIndicator.setBackgroundColor(priorityColor);

        // 5. Re-attach listener for user clicks
        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null && buttonView.isPressed()) {
                currentTask.isCompleted = isChecked; // Update the model immediately
                listener.onTaskClick(currentTask);
            }
        });

        // 6. Handle clicks on the whole card (for editing later)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                // We can add edit functionality later
            }
        });
    }

    // --- ViewHolder Class ---
    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDueDate;
        private CheckBox checkBoxCompleted;
        private View priorityIndicator;

        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.tv_task_title);
            textViewDueDate = itemView.findViewById(R.id.tv_task_due_date);
            checkBoxCompleted = itemView.findViewById(R.id.cb_task_completed);
            priorityIndicator = itemView.findViewById(R.id.view_priority_indicator);
        }
    }

    // --- Interface for click events ---
    public interface OnItemClickListener {
        void onTaskClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}