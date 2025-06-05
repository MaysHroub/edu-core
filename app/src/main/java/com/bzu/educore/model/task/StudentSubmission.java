package com.bzu.educore.model.task;

public class StudentSubmission {
    private final String studentId;
    private final String studentName;
    private final String submissionDate;     // only for assignments
    private final String submissionFileUrl;  // only for assignments
    private Double mark;
    private final String feedback;
    private final String status;             // only for assignments

    public StudentSubmission(
            String studentId,
            String studentName,
            String submissionDate,
            String submissionFileUrl,
            Double mark,
            String feedback,
            String status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.submissionDate = submissionDate;
        this.submissionFileUrl = submissionFileUrl;
        this.mark = mark;
        this.feedback = feedback;
        this.status = status;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getSubmissionDate() { return submissionDate; }
    public String getSubmissionFileUrl() { return submissionFileUrl; }
    public Double getMark() { return mark; }
    public void setMark(Double mark) { this.mark = mark; }
    public String getFeedback() { return feedback; }
    public String getStatus() { return status; }
}
