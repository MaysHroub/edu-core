package com.bzu.educore.model.Assessments;

import java.io.Serializable;
import java.time.LocalDate;
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
public class Assignment extends Assessment implements Serializable {
    private String pdfFile;
    private LocalDate deadline;
    private Integer assessmentId;
    public Assignment(Integer id, Integer subjectId, Integer sectionId, LocalDate date, Integer teacherId, String type,
                      String pdfFile, LocalDate deadline, Integer assessmentId) {
        super(id, subjectId, sectionId, date, teacherId, type);
        this.pdfFile = pdfFile;
        this.deadline = deadline;
        this.assessmentId = assessmentId;
    }

}
