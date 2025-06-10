package com.bzu.educore.activity.registrar.ui.student_management;

import com.bzu.educore.activity.registrar.User;
import com.bzu.educore.model.user.Person;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DummyStudent extends User implements Serializable {

    private DummyClassroom classroom;

    public DummyStudent(Integer id, String fname, String lname, String email, String password,
                        LocalDate dateOfBirth, DummyClassroom classroom) {
        super(id, fname, lname, email, password, dateOfBirth);
        this.classroom = classroom;
    }
}

