package com.bzu.educore.activity.registrar.ui.teacher_management;

import com.bzu.educore.model.school.Subject;
import com.bzu.educore.model.user.Person;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DummyTeacher extends Person {
    private Integer id;
    private String fname, lname, email, phoneNumber;
    private Subject subjectTaught;
    private LocalDate dateOfBirth;
}
