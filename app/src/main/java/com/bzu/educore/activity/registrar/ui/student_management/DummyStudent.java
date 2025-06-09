package com.bzu.educore.activity.registrar.ui.student_management;

import com.bzu.educore.model.user.Person;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DummyStudent extends Person implements Serializable {
    private int id;
    private String fname, lname, email;
    private DummyClassroom classroom;
    private LocalDate dateOfBirth;

}

