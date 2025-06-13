package com.example.studentsection.model;

public class TimetableEntry {
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String subject;
    private String teacherName;

    public TimetableEntry(String dayOfWeek, String startTime, String endTime, String subject, String teacherName) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.teacherName = teacherName;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacherName() {
        return teacherName;
    }
} 