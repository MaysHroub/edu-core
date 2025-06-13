package com.bzu.educore.model;

import java.io.Serializable;

public class Class implements Serializable {
    private String id;
    private String gradeNumber;
    private String section;
    private String homeroomTeacherFname;
    private String homeroomTeacherLname;

    public Class(String id, String gradeNumber, String section, String homeroomTeacherFname, String homeroomTeacherLname) {
        this.id = id;
        this.gradeNumber = gradeNumber;
        this.section = section;
        this.homeroomTeacherFname = homeroomTeacherFname;
        this.homeroomTeacherLname = homeroomTeacherLname;
    }

    public String getId() {
        return id;
    }

    public String getGradeNumber() {
        return gradeNumber;
    }

    public String getSection() {
        return section;
    }

    public String getHomeroomTeacherFname() {
        return homeroomTeacherFname;
    }

    public String getHomeroomTeacherLname() {
        return homeroomTeacherLname;
    }

    public String getGradeSection() {
        return "Grade " + gradeNumber + " " + section;
    }

    public String getTeacherName() {
        return homeroomTeacherFname + " " + homeroomTeacherLname;
    }
} 