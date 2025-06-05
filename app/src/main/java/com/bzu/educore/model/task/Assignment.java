package com.bzu.educore.model.task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Assignment extends Task implements Serializable {
    private String subjectTitle;
    private String teacherName;
    private String pdfFile;
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
            Double maxScore
    ) {
        super(id, subjectId, sectionId, date, teacherId, type, maxScore);
        this.pdfFile = pdfFile;
        this.assessmentId = assessmentId;
        this.subjectTitle = null;
        this.teacherName = null;
    }

    public Assignment(
            int id,
            String subjectTitle,
            int gradeNumber,
            String teacherName,
            double maxScore,
            String date,
            String deadline,
            String questionFileUrl
    ) {
        super(
                id,
                0,
                gradeNumber,
                LocalDate.parse(date),
                0,
                "assignment",
                (Double) maxScore
        );
        this.subjectTitle = subjectTitle;
        this.teacherName = teacherName;
        this.pdfFile = questionFileUrl;
        this.assessmentId = null;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject json = super.toJson();
        json.put("question_file_url", pdfFile != null ? pdfFile : JSONObject.NULL);
        json.put("assessment_id", assessmentId != null ? assessmentId : JSONObject.NULL);
        return json;
    }
}
