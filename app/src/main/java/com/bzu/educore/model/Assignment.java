package com.bzu.educore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Assignment {
    private int id;
    private String subjectTitle;
    private int gradeNumber;
    private String teacherName;
    private double maxScore;
    private String date;
    private String deadline;
    private String questionFileUrl;
}
