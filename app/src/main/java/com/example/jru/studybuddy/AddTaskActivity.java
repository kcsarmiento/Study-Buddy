package com.example.jru.studybuddy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText taskTitleEditText, taskDescriptionEditText;
    private Button selectDateButton, selectTimeButton, saveTaskButton;
    private FirebaseRepository repository;
    
    private Calendar dueDateTime;
    private String taskId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        repository = new FirebaseRepository();
        dueDateTime = Calendar.getInstance();

        taskTitleEditText = findViewById(R.id.taskTitleEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        selectDateButton = findViewById(R.id.selectDateButton);
        selectTimeButton = findViewById(R.id.selectTimeButton);
        saveTaskButton = findViewById(R.id.saveTaskButton);

        if (getIntent().hasExtra("taskId")) {
            isEditMode = true;
            taskId = getIntent().getStringExtra("taskId");
            taskTitleEditText.setText(getIntent().getStringExtra("taskTitle"));
            taskDescriptionEditText.setText(getIntent().getStringExtra("taskDescription"));
            
            long dueDateMillis = getIntent().getLongExtra("taskDueDate", 0);
            if (dueDateMillis > 0) {
                dueDateTime.setTimeInMillis(dueDateMillis);
                updateDateButton();
                updateTimeButton();
            }
        }

        selectDateButton.setOnClickListener(v -> showDatePicker());
        selectTimeButton.setOnClickListener(v -> showTimePicker());
        saveTaskButton.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    dueDateTime.set(Calendar.YEAR, year);
                    dueDateTime.set(Calendar.MONTH, month);
                    dueDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateButton();
                },
                dueDateTime.get(Calendar.YEAR),
                dueDateTime.get(Calendar.MONTH),
                dueDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    dueDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    dueDateTime.set(Calendar.MINUTE, minute);
                    updateTimeButton();
                },
                dueDateTime.get(Calendar.HOUR_OF_DAY),
                dueDateTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        selectDateButton.setText(sdf.format(dueDateTime.getTime()));
    }

    private void updateTimeButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        selectTimeButton.setText(sdf.format(dueDateTime.getTime()));
    }

    private void saveTask() {
        String title = taskTitleEditText.getText().toString().trim();
        String description = taskDescriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, R.string.error_title_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            Task task = new Task(
                    taskId,
                    title,
                    description,
                    dueDateTime.getTimeInMillis(),
                    getIntent().getBooleanExtra("taskCompleted", false),
                    0,
                    System.currentTimeMillis()
            );

            repository.updateTask(task, new FirebaseRepository.OperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddTaskActivity.this, R.string.task_updated, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(AddTaskActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Task task = new Task(
                    null,
                    title,
                    description,
                    dueDateTime.getTimeInMillis(),
                    false,
                    0,
                    System.currentTimeMillis()
            );

            repository.addTask(task, new FirebaseRepository.OperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(AddTaskActivity.this, R.string.task_added, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(AddTaskActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
