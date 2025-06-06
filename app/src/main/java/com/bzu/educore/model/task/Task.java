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
    private Integer subjectId;     // e.g. foreign key to Subject
    private Integer sectionId;     // corresponds to class_id
    private LocalDate date;        // a LocalDate if you want to store creation date as a LocalDate
    private Integer teacherId;     // foreign key to Teacher
    private String type;           // “assignment” or “exam”
    private Double maxScore;

    private String subjectTitle;
    private Integer gradeNumber;
    private String teacherName;
    private String deadline;            // (as “YYYY-MM-DD” string)
    private String questionFileUrl;

    public Task(
            Integer id,
            Integer subjectId,
            Integer sectionId,
            LocalDate date,
            Integer teacherId,
            String type,
            Double maxScore
    ) {
        this.id = id;
        this.subjectId = subjectId;
        this.sectionId = sectionId;
        this.date = date;
        this.teacherId = teacherId;
        this.type = type;
        this.maxScore = maxScore;
    }

    public Task(
            Integer id,
            String subjectTitle,
            Integer gradeNumber,
            String teacherName,
            Double maxScore,
            String dateString,
            String deadline,
            String questionFileUrl,
            String type
    ) {
        this.id = id;

        // We don’t know subjectId or sectionId or teacherId from these JSON fields,
        // so leave them null (or fetch them separately if needed).
        this.subjectId = null;
        this.sectionId = null;
        this.teacherId = null;

        // Store display fields:
        this.subjectTitle    = subjectTitle;
        this.gradeNumber     = gradeNumber;
        this.teacherName     = teacherName;
        this.maxScore        = (maxScore == null) ? null : maxScore.doubleValue();
        this.deadline        = deadline;
        this.questionFileUrl = questionFileUrl;
        this.type            = type;

        // Parse the JSON “date” string into a LocalDate, if you really want it:
        if (dateString != null && !dateString.isEmpty()) {
            this.date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            this.date = null;
        }
    }


    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();

        // Map the fields you care about:
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

        if (date != null) {
            json.put("date", date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            json.put("date", JSONObject.NULL);
        }

        if (subjectTitle != null) {
            json.put("subject_title", subjectTitle);
        } else {
            json.put("subject_title", JSONObject.NULL);
        }
        if (gradeNumber != null) {
            json.put("grade_number", gradeNumber);
        } else {
            json.put("grade_number", JSONObject.NULL);
        }
        if (teacherName != null) {
            json.put("teacher_name", teacherName);
        } else {
            json.put("teacher_name", JSONObject.NULL);
        }
        if (deadline != null) {
            json.put("deadline", deadline);
        } else {
            json.put("deadline", JSONObject.NULL);
        }
        if (questionFileUrl != null) {
            json.put("question_file_url", questionFileUrl);
        } else {
            json.put("question_file_url", JSONObject.NULL);
        }

        return json;
    }
}
