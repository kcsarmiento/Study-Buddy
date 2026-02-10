package com.example.jru.studybuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events = new ArrayList<>();
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
        void onEventDelete(Event event);
    }

    public EventAdapter(OnEventClickListener listener) {
        this.listener = listener;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private final TextView timeText;
        private final ImageButton deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.eventTitle);
            descriptionText = itemView.findViewById(R.id.eventDescription);
            timeText = itemView.findViewById(R.id.eventTime);
            deleteButton = itemView.findViewById(R.id.eventDeleteButton);
        }

        public void bind(Event event) {
            titleText.setText(event.getTitle());
            descriptionText.setText(event.getDescription());
            
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeText.setText(sdf.format(new Date(event.getEventDate())));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventDelete(event);
                }
            });
        }
    }
}
