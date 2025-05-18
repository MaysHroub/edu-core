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
@ToString
@EqualsAndHashCode
public class Assessment implements Serializable {
    private Integer id;
    private Integer subjectId;
    private Integer sectionId;
    private LocalDate date;
    private Integer teacherId;
    private String type;
    public Assessment(Integer id, Integer subjectId, Integer sectionId, LocalDate date, Integer teacherId, String type) {
        this.id = id;
        this.subjectId = subjectId;
        this.sectionId = sectionId;
        this.date = date;
        this.teacherId = teacherId;
        this.type = type;
    }
}
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
class AssessmentType implements Serializable {
    private String title;
}
