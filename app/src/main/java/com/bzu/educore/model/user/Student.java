package com.bzu.educore.model.user;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder


public class Student extends Person{
    private String grade_number,class_id;
    public Student(Integer id, Integer age, String name, String email, LocalDate dateOfBirth,
                   String grade_number, String class_id) {
        super(id, age, name, email, dateOfBirth);
        this.grade_number = grade_number;
        this.class_id = class_id;
    }

}
