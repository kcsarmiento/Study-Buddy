package com.example.jru.studybuddy;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String id;
    private String title;
    private String description;
    private long eventDate;
    private long reminderAt;
    private long createdAt;

    public Event() {
    }

    public Event(String id, String title, String description, long eventDate, long reminderAt, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.reminderAt = reminderAt;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public long getReminderAt() {
        return reminderAt;
    }

    public void setReminderAt(long reminderAt) {
        this.reminderAt = reminderAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description);
        map.put("eventDate", eventDate);
        map.put("reminderAt", reminderAt);
        map.put("createdAt", createdAt);
        return map;
    }
}
