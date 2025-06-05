package com.bzu.educore.model.user;

import java.io.Serializable;
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
public class TimeTable implements Serializable {
    private Integer teacherId;
    private Integer subjectId;
    private String subjectTitle;   // new
    private Integer classId;
    private Integer gradeNumber;   // new
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
