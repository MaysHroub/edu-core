package com.example.studentsection.model;

public class Mark {
    private String subject;
    private String grade;
    private String semester;

    public Mark(String subject, String grade, String semester) {
        this.subject = subject;
        this.grade = grade;
        this.semester = semester;
    }

    public String getSubject() {
        return subject;
    }

    public String getGrade() {
        return grade;
    }

    public String getSemester() {
        return semester;
    }
} 