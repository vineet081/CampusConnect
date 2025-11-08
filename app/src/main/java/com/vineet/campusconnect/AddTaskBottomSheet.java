package com.vineet.campusconnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.vineet.campusconnect.models.Task;
import com.vineet.campusconnect.utils.AlarmUtils;
import com.vineet.campusconnect.viewmodels.TaskViewModel;

import java.util.Calendar;
import java.util.Locale;

public class AddTaskBottomSheet extends BottomSheetDialogFragment {

    private TaskViewModel taskViewModel;
    private final Calendar dueDateCalendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_task, container, false);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        TextInputEditText etTitle = view.findViewById(R.id.et_task_title);
        TextInputEditText etDescription = view.findViewById(R.id.et_task_description);
        ChipGroup chipGroupPriority = view.findViewById(R.id.chip_group_priority);
        MaterialButton btnPickDate = view.findViewById(R.id.btn_pick_date);
        MaterialButton btnPickTime = view.findViewById(R.id.btn_pick_time);
        MaterialButton btnSaveTask = view.findViewById(R.id.btn_save_task);

        // Default priority to Low (1)
        chipGroupPriority.check(R.id.chip_priority_low);

        // Date Picker
        btnPickDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view1, year, month, dayOfMonth) -> {
                dueDateCalendar.set(Calendar.YEAR, year);
                dueDateCalendar.set(Calendar.MONTH, month);
                dueDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Format the date for the button text
                String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                btnPickDate.setText(selectedDate);
            }, dueDateCalendar.get(Calendar.YEAR), dueDateCalendar.get(Calendar.MONTH), dueDateCalendar.get(Calendar.DAY_OF_MONTH));
            // Optional: restrict to future dates only
            // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        // Time Picker
        btnPickTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view12, hourOfDay, minute) -> {
                dueDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dueDateCalendar.set(Calendar.MINUTE, minute);
                dueDateCalendar.set(Calendar.SECOND, 0); // Reset seconds to 0

                // Format time for the button text (e.g., 14:05)
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                btnPickTime.setText(selectedTime);
            }, dueDateCalendar.get(Calendar.HOUR_OF_DAY), dueDateCalendar.get(Calendar.MINUTE), false); // false = 12h format, true = 24h format
            timePickerDialog.show();
        });

        // Save Task
        btnSaveTask.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            // Determine priority based on selected chip
            int priority = 1; // Default Low
            int checkedId = chipGroupPriority.getCheckedChipId();
            if (checkedId == R.id.chip_priority_medium) priority = 2;
            else if (checkedId == R.id.chip_priority_high) priority = 3;

            long dueDate = dueDateCalendar.getTimeInMillis();

            // Create the new task object
            Task newTask = new Task(title, description, dueDate, priority);

            // Save to Database
            taskViewModel.insert(newTask);

            // --- Schedule the Alarm Notification ---
            // Only schedule if the due date is in the future
            if (dueDate > System.currentTimeMillis()) {
                AlarmUtils.scheduleAlarm(requireContext(), newTask);
                Toast.makeText(requireContext(), "Task saved & reminder set!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Task saved", Toast.LENGTH_SHORT).show();
            }

            dismiss(); // Close the bottom sheet
        });

        return view;
    }
}