package com.bzu.educore.activity.registrar.ui.student_management;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DummyStudent {

    private Integer id;
    private String fname, lname, email;
    private DummyClassroom classroom;
    private LocalDate dateOfBirth;

}

@AllArgsConstructor
@Getter
@Setter
class DummyClassroom {
    private int grade_number;
    private char section;
    private int homeroom_teacher_id;
}