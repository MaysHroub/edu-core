package com.bzu.educore.activity.registrar.ui.student_registration;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DummyStudent {

    private Integer id, grade_num;
    private String fname, lname, email, section;
    private LocalDate dateOfBirth;

}
