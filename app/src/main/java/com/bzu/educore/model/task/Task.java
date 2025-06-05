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
@ToString
@EqualsAndHashCode
public class Task implements Serializable {
    private Integer id;
    private Integer subjectId;
    private Integer sectionId;   // in your fragment you called it classGradeId
    private LocalDate date;
    private Integer teacherId;
    private String type;
    private Integer maxScore;

    public Task(Integer id,
                Integer subjectId,
                Integer sectionId,
                LocalDate date,
                Integer teacherId,
                String type,
                Integer maxScore) {
        this.id = id;
        this.subjectId = subjectId;
        this.sectionId = sectionId;
        this.date = date;
        this.teacherId = teacherId;
        this.type = type;
        this.maxScore = maxScore;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("subject_id", subjectId);
        json.put("class_id", sectionId);
        json.put("teacher_id", teacherId);
        json.put("type", type);
        json.put("max_score", maxScore);
        if (date != null) {
            String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            json.put("exam_date", formattedDate);
        } else {
            json.put("exam_date", JSONObject.NULL);
        }

        return json;
    }
}
