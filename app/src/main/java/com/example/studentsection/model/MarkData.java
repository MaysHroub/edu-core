package com.example.studentsection.model;

import androidx.annotation.NonNull;

public class MarkData {
    private String subject_title;
    private String type;
    private double mark;
    private double max_score;
    private String date;
    private String feedback;

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

    public double getMark() {
        return mark;
    }
    public void setMark(double mark) {
        this.mark = mark;
    }

    public double getMax_score() {
        return max_score;
    }
    public void setMax_score(double max_score) {
        this.max_score = max_score;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getFeedback() {
        return feedback;
    }
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @NonNull
    @Override
    public String toString() {
        return subject_title + " (" + type + ") - " + mark + "/" + max_score + "\n" + date + "\nFeedback: " + feedback;
    }

}
