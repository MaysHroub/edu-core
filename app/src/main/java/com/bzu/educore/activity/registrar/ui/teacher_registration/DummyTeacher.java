package com.bzu.educore.activity.registrar.ui.teacher_registration;

import com.bzu.educore.model.school.Subject;

import java.time.LocalDate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DummyTeacher {
    private Integer id;
    private String fname, lname, email, phoneNumber;
    private Subject subjectTaught;
    private LocalDate dateOfBirth;
}
