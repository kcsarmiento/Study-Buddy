package com.example.jru.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView tasksRecyclerView;
    private TextView emptyTasksText;
    private TaskAdapter taskAdapter;
    private FirebaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new FirebaseRepository();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        emptyTasksText = findViewById(R.id.emptyTasksText);
        FloatingActionButton addTaskFab = findViewById(R.id.addTaskFab);

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this);
        tasksRecyclerView.setAdapter(taskAdapter);

        loadTasks();

        addTaskFab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navAddTask);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navAnalytics) {
                Intent analyticsIntent = new Intent(MainActivity.this, AnalyticsActivity.class);
                startActivity(analyticsIntent);
                return true;
            } else if (id == R.id.navTimer) {
                Toast.makeText(MainActivity.this, "Timer Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.navAddTask) {
                return true;
            } else if (id == R.id.navNotes) {
                Intent notesIntent = new Intent(MainActivity.this, NotesActivity.class);
                startActivity(notesIntent);
                return true;
            } else if (id == R.id.navCalendar) {
                Intent calendarIntent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(calendarIntent);
                return true;
            }
            return false;
        });
    }

    private void loadTasks() {
        repository.getTasks(new FirebaseRepository.DataCallback<Task>() {
            @Override
            public void onSuccess(List<Task> data) {
                taskAdapter.setTasks(data);
                if (data.isEmpty()) {
                    emptyTasksText.setVisibility(View.VISIBLE);
                    tasksRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyTasksText.setVisibility(View.GONE);
                    tasksRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Error loading tasks: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra("taskId", task.getId());
        intent.putExtra("taskTitle", task.getTitle());
        intent.putExtra("taskDescription", task.getDescription());
        intent.putExtra("taskDueDate", task.getDueDate());
        intent.putExtra("taskCompleted", task.isCompleted());
        startActivity(intent);
    }

    @Override
    public void onTaskComplete(Task task, boolean completed) {
        task.setCompleted(completed);
        if (completed) {
            task.setCompletedAt(System.currentTimeMillis());
        } else {
            task.setCompletedAt(0);
        }
        
        repository.updateTask(task, new FirebaseRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                String message = completed ? getString(R.string.mark_complete) : getString(R.string.mark_incomplete);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Error updating task: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskDelete(Task task) {
        repository.deleteTask(task.getId(), new FirebaseRepository.OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Error deleting task: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
