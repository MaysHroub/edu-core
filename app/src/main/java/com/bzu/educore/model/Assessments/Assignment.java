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
public class Assignment implements Serializable {
    private Integer id;
    private String pdfFile;
    private LocalDate deadline;
    private Integer assessmentId;
}
