package com.example.studentsection.model;

public class AssignmentData {
    private int id;
    private String title;
    private String deadline;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return title + "\nDeadline: " + deadline;
    }
}
