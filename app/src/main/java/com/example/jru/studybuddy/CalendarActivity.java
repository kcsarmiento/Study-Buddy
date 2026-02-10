package com.example.jru.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private CalendarView calendarView;
    private RecyclerView eventsRecyclerView;
    private TextView emptyEventsText;
    private EventAdapter eventAdapter;
    private FirebaseRepository repository;
    
    private long selectedDate;
    private List<Event> allEvents = new ArrayList<>();
    private List<Task> allTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        repository = new FirebaseRepository();

        calendarView = findViewById(R.id.calendarView);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        emptyEventsText = findViewById(R.id.emptyEventsText);
        FloatingActionButton addEventFab = findViewById(R.id.addEventFab);

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(this);
        eventsRecyclerView.setAdapter(eventAdapter);

        selectedDate = getTodayStartOfDay();
        
        loadEvents();
        loadTasks();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            selectedDate = calendar.getTimeInMillis();
            filterEventsForDate();
        });

        addEventFab.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, AddEventActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });
    }

    private long getTodayStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void loadEvents() {
        repository.getEvents(new FirebaseRepository.DataCallback<Event>() {
            @Override
            public void onSuccess(List<Event> data) {
                allEvents = data;
                filterEventsForDate();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CalendarActivity.this, "Error loading events: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTasks() {
        repository.getTasks(new FirebaseRepository.DataCallback<Task>() {
            @Override
            public void onSuccess(List<Task> data) {
                allTasks = data;
                filterEventsForDate();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CalendarActivity.this, "Error loading tasks: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterEventsForDate() {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTimeInMillis(selectedDate);
        int selectedYear = selectedCalendar.get(Calendar.YEAR);
        int selectedMonth = selectedCalendar.get(Calendar.MONTH);
        int selectedDay = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        List<Event> filteredEvents = new ArrayList<>();
        
        for (Event event : allEvents) {
            Calendar eventCalendar = Calendar.getInstance();
            eventCalendar.setTimeInMillis(event.getEventDate());
            if (eventCalendar.get(Calendar.YEAR) == selectedYear &&
                eventCalendar.get(Calendar.MONTH) == selectedMonth &&
                eventCalendar.get(Calendar.DAY_OF_MONTH) == selectedDay) {
                filteredEvents.add(event);
            }
        }

        for (Task task : allTasks) {
            if (task.getDueDate() > 0) {
                Calendar taskCalendar = Calendar.getInstance();
                taskCalendar.setTimeInMillis(task.getDueDate());
                if (taskCalendar.get(Calendar.YEAR) == selectedYear &&
                    taskCalendar.get(Calendar.MONTH) == selectedMonth &&
                    taskCalendar.get(Calendar.DAY_OF_MONTH) == selectedDay) {
                    
                    Event taskAsEvent = new Event(
                            task.getId(),
                            task.getTitle(),
                            task.getDescription(),
                            task.getDueDate(),
                            0,
                            task.getCreatedAt()
                    );
                    filteredEvents.add(taskAsEvent);
                }
            }
        }

        eventAdapter.setEvents(filteredEvents);
        
        if (filteredEvents.isEmpty()) {
            emptyEventsText.setVisibility(View.VISIBLE);
            eventsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyEventsText.setVisibility(View.GONE);
            eventsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEventClick(Event event) {
    }

    @Override
    public void onEventDelete(Event event) {
        repository.deleteEvent(event.getId(), new FirebaseRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(CalendarActivity.this, R.string.event_deleted, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(CalendarActivity.this, "Error deleting event: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
