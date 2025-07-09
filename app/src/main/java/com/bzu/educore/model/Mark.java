package com.bzu.educore.model;

public class Mark {
    private String subject;
    private String grade;
    private String semester;

    public Mark() {
    }

    public Mark(String subject, String grade, String semester) {
        this.subject = subject;
        this.grade = grade;
        this.semester = semester;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}