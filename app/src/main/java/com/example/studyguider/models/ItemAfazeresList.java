package com.example.studyguider.models;

public class ItemAfazeresList {
    private String id;
    private String task;
    private boolean completed;

    public ItemAfazeresList(String id, String task, boolean completed) {
        this.id = id;
        this.task = task;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
