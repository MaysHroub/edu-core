package com.bzu.educore.model;

public class Teacher {
    private String id;
    private String fname;
    private String lname;

    public Teacher(String id, String fname, String lname) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
    }

    public String getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    @Override
    public String toString() {
        return fname + " " + lname; // For displaying in AutoCompleteTextView
    }
} 