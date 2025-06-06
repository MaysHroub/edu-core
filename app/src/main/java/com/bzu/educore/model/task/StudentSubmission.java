package com.bzu.educore.model.task;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class StudentSubmission {
    private final String studentId;
    private final String studentName;
    private final String submissionDate;
    private final String submissionFileUrl;
    private Double mark;
    private final String feedback;
    private final String status;
}