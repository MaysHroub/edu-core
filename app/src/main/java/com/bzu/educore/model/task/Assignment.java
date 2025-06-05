// Assignment.java
package com.bzu.educore.model.task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Assignment extends Task implements Serializable {
    private String pdfFile;
    private LocalDate deadline;
    private Integer assessmentId;

    public Assignment(
            Integer id,
            Integer subjectId,
            Integer sectionId,
            LocalDate date,
            Integer teacherId,
            String type,
            String pdfFile,
            LocalDate deadline,
            Integer assessmentId,
            Integer maxScore
    ) {
        super(id, subjectId, sectionId, date, teacherId, type, maxScore);
        this.pdfFile = pdfFile;
        this.deadline = deadline;
        this.assessmentId = assessmentId;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        // Inherited fields from Task
        json.put("subject_id", getSubjectId());
        json.put("class_id", getSectionId());
        json.put("teacher_id", getTeacherId());
        json.put("type", getType());
        json.put("max_score", getMaxScore());

        // PDF file URL
        if (pdfFile != null) {
            json.put("question_file_url", pdfFile);
        } else {
            json.put("question_file_url", JSONObject.NULL);
        }

        // Deadline (format yyyy-MM-dd)
        if (deadline != null) {
            String formattedDeadline = deadline.format(DateTimeFormatter.ISO_LOCAL_DATE);
            json.put("deadline", formattedDeadline);
        } else {
            json.put("deadline", JSONObject.NULL);
        }

        return json;
    }
}
