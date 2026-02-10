package com.example.jru.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private TextView taskCount, taskDate;
    private FirebaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        repository = new FirebaseRepository();

        taskCount = findViewById(R.id.taskCount);
        taskDate = findViewById(R.id.taskDate);

        updateTaskStats();

        ImageView navAddTask = findViewById(R.id.navAddTask);
        navAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTaskStats();
    }

    private void updateTaskStats() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        taskDate.setText(sdf.format(new Date()));

        repository.getTasks(new FirebaseRepository.DataCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                int total = tasks.size();
                int completed = 0;
                
                for (Task task : tasks) {
                    if (task.isCompleted()) {
                        completed++;
                    }
                }
                
                if (total > 20) {
                    taskCount.setText("20+");
                } else {
                    taskCount.setText(String.valueOf(total));
                }
            }

            @Override
            public void onFailure(String error) {
                taskCount.setText("0");
            }
        });
    }
}
