package com.bzu.educore.model;

import androidx.annotation.NonNull;

public class EventData {
    private String subject_title;
    private String type;
    private String date;
    private String deadline;


    public EventData() {
    }

    public EventData(String subject_title, String type, String date) {
        this.subject_title = subject_title;
        this.type = type;
        this.date = date;
    }


    public String getSubject_title() {
        return subject_title;
    }
    public void setSubject_title(String subject_title) {
        this.subject_title = subject_title;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    @NonNull
    @Override
    public String toString() {
        return subject_title + " (" + type + ") on " + date;
    }
}
