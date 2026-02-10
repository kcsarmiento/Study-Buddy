package com.example.jru.studybuddy;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private EditText eventTitleEditText, eventDescriptionEditText;
    private Button selectEventDateButton, selectEventTimeButton, selectReminderButton, saveEventButton;
    private FirebaseRepository repository;
    
    private Calendar eventDateTime;
    private Calendar reminderDateTime;
    private long preSelectedDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        repository = new FirebaseRepository();
        eventDateTime = Calendar.getInstance();
        reminderDateTime = Calendar.getInstance();

        if (getIntent().hasExtra("selectedDate")) {
            preSelectedDate = getIntent().getLongExtra("selectedDate", 0);
            eventDateTime.setTimeInMillis(preSelectedDate);
        }

        eventTitleEditText = findViewById(R.id.eventTitleEditText);
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText);
        selectEventDateButton = findViewById(R.id.selectEventDateButton);
        selectEventTimeButton = findViewById(R.id.selectEventTimeButton);
        selectReminderButton = findViewById(R.id.selectReminderButton);
        saveEventButton = findViewById(R.id.saveEventButton);

        if (preSelectedDate > 0) {
            updateEventDateButton();
        }

        selectEventDateButton.setOnClickListener(v -> showDatePicker());
        selectEventTimeButton.setOnClickListener(v -> showTimePicker());
        selectReminderButton.setOnClickListener(v -> showReminderPicker());
        saveEventButton.setOnClickListener(v -> saveEvent());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    eventDateTime.set(Calendar.YEAR, year);
                    eventDateTime.set(Calendar.MONTH, month);
                    eventDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateEventDateButton();
                },
                eventDateTime.get(Calendar.YEAR),
                eventDateTime.get(Calendar.MONTH),
                eventDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    eventDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    eventDateTime.set(Calendar.MINUTE, minute);
                    updateEventTimeButton();
                },
                eventDateTime.get(Calendar.HOUR_OF_DAY),
                eventDateTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void showReminderPicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    reminderDateTime = (Calendar) eventDateTime.clone();
                    reminderDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    reminderDateTime.set(Calendar.MINUTE, minute);
                    updateReminderButton();
                },
                eventDateTime.get(Calendar.HOUR_OF_DAY),
                eventDateTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateEventDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        selectEventDateButton.setText(sdf.format(eventDateTime.getTime()));
    }

    private void updateEventTimeButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        selectEventTimeButton.setText(sdf.format(eventDateTime.getTime()));
    }

    private void updateReminderButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        selectReminderButton.setText(sdf.format(reminderDateTime.getTime()));
    }

    private void saveEvent() {
        String title = eventTitleEditText.getText().toString().trim();
        String description = eventDescriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, R.string.error_title_required, Toast.LENGTH_SHORT).show();
            return;
        }

        long reminderTime = reminderDateTime.getTimeInMillis();
        if (reminderTime <= System.currentTimeMillis()) {
            reminderTime = 0;
        }

        Event event = new Event(
                null,
                title,
                description,
                eventDateTime.getTimeInMillis(),
                reminderTime,
                System.currentTimeMillis()
        );

        repository.addEvent(event, new FirebaseRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                if (reminderTime > 0) {
                    scheduleReminder(title, description, reminderTime);
                }
                Toast.makeText(AddEventActivity.this, R.string.event_added, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddEventActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleReminder(String title, String description, long reminderTime) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            Toast.makeText(this, R.string.reminder_set, Toast.LENGTH_SHORT).show();
        }
    }
}
