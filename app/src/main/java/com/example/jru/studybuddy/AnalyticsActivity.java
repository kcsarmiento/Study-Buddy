package com.example.jru.studybuddy;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnalyticsActivity extends AppCompatActivity {

    private TextView totalTasksText;
    private TextView completedTasksText;
    private TextView pendingTasksText;
    private TextView todayTasksText;
    private TextView weekTasksText;
    private TextView currentStreakText;
    private TextView longestStreakText;
    
    private FirebaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        repository = new FirebaseRepository();

        totalTasksText = findViewById(R.id.totalTasksText);
        completedTasksText = findViewById(R.id.completedTasksText);
        pendingTasksText = findViewById(R.id.pendingTasksText);
        todayTasksText = findViewById(R.id.todayTasksText);
        weekTasksText = findViewById(R.id.weekTasksText);
        currentStreakText = findViewById(R.id.currentStreakText);
        longestStreakText = findViewById(R.id.longestStreakText);

        loadAnalytics();
    }

    private void loadAnalytics() {
        repository.getTasks(new FirebaseRepository.DataCallback<Task>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                calculateAndDisplayMetrics(tasks);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AnalyticsActivity.this, "Error loading analytics: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateAndDisplayMetrics(List<Task> tasks) {
        int total = tasks.size();
        int completed = 0;
        int pending = 0;
        int today = 0;
        int week = 0;

        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        long todayStartMillis = todayStart.getTimeInMillis();

        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        long todayEndMillis = todayEnd.getTimeInMillis();

        Calendar weekStart = Calendar.getInstance();
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek());
        weekStart.set(Calendar.HOUR_OF_DAY, 0);
        weekStart.set(Calendar.MINUTE, 0);
        weekStart.set(Calendar.SECOND, 0);
        weekStart.set(Calendar.MILLISECOND, 0);
        long weekStartMillis = weekStart.getTimeInMillis();

        Set<String> completionDates = new HashSet<>();

        for (Task task : tasks) {
            if (task.isCompleted()) {
                completed++;
                
                if (task.getCompletedAt() > 0) {
                    Calendar completionCal = Calendar.getInstance();
                    completionCal.setTimeInMillis(task.getCompletedAt());
                    String dateKey = completionCal.get(Calendar.YEAR) + "-" + 
                                   completionCal.get(Calendar.MONTH) + "-" + 
                                   completionCal.get(Calendar.DAY_OF_MONTH);
                    completionDates.add(dateKey);
                }
            } else {
                pending++;
            }

            if (task.getDueDate() >= todayStartMillis && task.getDueDate() <= todayEndMillis) {
                today++;
            }

            if (task.getDueDate() >= weekStartMillis && task.getDueDate() <= todayEndMillis) {
                week++;
            }
        }

        int[] streaks = calculateStreaks(completionDates);
        int currentStreak = streaks[0];
        int longestStreak = streaks[1];

        totalTasksText.setText(String.valueOf(total));
        completedTasksText.setText(String.valueOf(completed));
        pendingTasksText.setText(String.valueOf(pending));
        todayTasksText.setText(String.valueOf(today));
        weekTasksText.setText(String.valueOf(week));
        currentStreakText.setText(currentStreak + " " + getString(R.string.days));
        longestStreakText.setText(longestStreak + " " + getString(R.string.days));
    }

    private int[] calculateStreaks(Set<String> completionDates) {
        if (completionDates.isEmpty()) {
            return new int[]{0, 0};
        }

        Calendar calendar = Calendar.getInstance();
        int currentStreak = 0;
        int longestStreak = 0;
        int tempStreak = 0;

        for (int i = 0; i < 365; i++) {
            String dateKey = calendar.get(Calendar.YEAR) + "-" + 
                           calendar.get(Calendar.MONTH) + "-" + 
                           calendar.get(Calendar.DAY_OF_MONTH);
            
            if (completionDates.contains(dateKey)) {
                tempStreak++;
                if (i == 0 || currentStreak > 0) {
                    currentStreak = tempStreak;
                }
                if (tempStreak > longestStreak) {
                    longestStreak = tempStreak;
                }
            } else {
                if (i == 0) {
                    currentStreak = 0;
                }
                tempStreak = 0;
            }

            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        return new int[]{currentStreak, longestStreak};
    }
}
