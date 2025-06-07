package com.bzu.educore.model.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
public class StudentSubmission {
    private  String studentId;
    private  String studentName;
    private  String submissionDate;
    private  String submissionFileUrl;
    private Double mark;
    private String feedback;
    private String status;
}