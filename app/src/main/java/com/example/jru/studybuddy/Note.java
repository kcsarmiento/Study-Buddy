package com.example.jru.studybuddy;

import java.util.HashMap;
import java.util.Map;

public class Note {
    private String id;
    private String title;
    private String content;
    private long createdAt;
    private long updatedAt;

    public Note() {
    }

    public Note(String id, String title, String content, long createdAt, long updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("createdAt", createdAt);
        map.put("updatedAt", updatedAt);
        return map;
    }
}
