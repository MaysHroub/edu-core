package com.bzu.educore.model.Assessments;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Assessment implements Serializable {
    private Integer id;
    private Integer subjectId;
    private Integer sectionId;
    private LocalDate date;
    private Integer teacherId;
    private String type;
}
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
class AssessmentType implements Serializable {
    private String title;
}
