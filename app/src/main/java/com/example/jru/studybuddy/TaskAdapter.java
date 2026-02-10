package com.example.jru.studybuddy;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskComplete(Task task, boolean completed);
        void onTaskDelete(Task task);
    }

    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private final TextView dueDateText;
        private final CheckBox completedCheckBox;
        private final ImageButton deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.taskTitle);
            descriptionText = itemView.findViewById(R.id.taskDescription);
            dueDateText = itemView.findViewById(R.id.taskDueDate);
            completedCheckBox = itemView.findViewById(R.id.taskCompletedCheckBox);
            deleteButton = itemView.findViewById(R.id.taskDeleteButton);
        }

        public void bind(Task task) {
            titleText.setText(task.getTitle());
            descriptionText.setText(task.getDescription());
            
            if (task.getDueDate() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                dueDateText.setText(sdf.format(new Date(task.getDueDate())));
                dueDateText.setVisibility(View.VISIBLE);
            } else {
                dueDateText.setVisibility(View.GONE);
            }

            completedCheckBox.setChecked(task.isCompleted());
            
            if (task.isCompleted()) {
                titleText.setPaintFlags(titleText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                descriptionText.setPaintFlags(descriptionText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                titleText.setPaintFlags(titleText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                descriptionText.setPaintFlags(descriptionText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClick(task);
                }
            });

            completedCheckBox.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskComplete(task, completedCheckBox.isChecked());
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskDelete(task);
                }
            });
        }
    }
}
