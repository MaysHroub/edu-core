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
    private Integer teacherId;    // Corresponds to teacher_id
    private Integer subjectId;    // Corresponds to subject_id
    private Integer classId;      // Corresponds to class_id
    private String dayOfWeek;     // Corresponds to day_of_week
    private String startTime;     // Corresponds to start_time (TIME)
    private String endTime;       // Corresponds to end_time (TIME)
}
