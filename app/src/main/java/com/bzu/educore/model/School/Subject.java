package com.bzu.educore.model.School;

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
public class Subject implements Serializable {
    private Integer id;
    private String title;
    private Integer gradeNumber;
    private Integer semesterNumber;
}