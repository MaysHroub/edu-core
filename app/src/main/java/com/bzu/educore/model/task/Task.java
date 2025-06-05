// Task.java
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

/**
 * Base model for tasks (e.g., exams or assignments).
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Task implements Serializable {
    private Integer id;
    private Integer subjectId;
    private Integer sectionId;   // corresponds to “class_id” in JSON
    private LocalDate date;      // general date field if used
    private Integer teacherId;
    private String type;
    private Integer maxScore;

    public Task(
            Integer id,
            Integer subjectId,
            Integer sectionId,
            LocalDate date,
            Integer teacherId,
            String type,
            Integer maxScore
    ) {
        this.id = id;
        this.subjectId = subjectId;
        this.sectionId = sectionId;
        this.date = date;
        this.teacherId = teacherId;
        this.type = type;
        this.maxScore = maxScore;
    }

    /**
     * Converts Task fields to a JSONObject.
     * Override or extend in subclasses if additional keys are needed.
     */
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        // Map Task fields to expected JSON keys
        if (subjectId != null) {
            json.put("subject_id", subjectId);
        } else {
            json.put("subject_id", JSONObject.NULL);
        }

        if (sectionId != null) {
            json.put("class_id", sectionId);
        } else {
            json.put("class_id", JSONObject.NULL);
        }

        if (teacherId != null) {
            json.put("teacher_id", teacherId);
        } else {
            json.put("teacher_id", JSONObject.NULL);
        }

        if (type != null) {
            json.put("type", type);
        } else {
            json.put("type", JSONObject.NULL);
        }

        if (maxScore != null) {
            json.put("max_score", maxScore);
        } else {
            json.put("max_score", JSONObject.NULL);
        }

        // If date is set, format as “yyyy-MM-dd”
        if (date != null) {
            json.put("date", date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            json.put("date", JSONObject.NULL);
        }

        return json;
    }
}
