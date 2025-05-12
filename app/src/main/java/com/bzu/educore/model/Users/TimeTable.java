package com.bzu.educore.model.Users;

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
    private Integer classId;
    private String startTime;
    private String day;
}

