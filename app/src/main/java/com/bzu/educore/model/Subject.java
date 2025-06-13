package com.bzu.educore.model;

public class Subject {
    private String id;
    private String title;

    public Subject(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title; // This is important for ArrayAdapter to display the title in AutoCompleteTextView
    }
} 