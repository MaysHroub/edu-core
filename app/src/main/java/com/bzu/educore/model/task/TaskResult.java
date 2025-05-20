package com.bzu.educore.model.task;
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
public class TaskResult implements Serializable {
    private Integer assessmentId;
    private Integer studentId;
    private Double mark;
    private String feedBack;
}

