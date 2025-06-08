package com.bzu.educore.activity.registrar.ui.teacher_management;

import androidx.annotation.NonNull;

import com.bzu.educore.model.school.Subject;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DummyTeacher {
    private Integer id;
    private String fname, lname, email, phoneNumber;
    private Subject subjectTaught;
    private LocalDate dateOfBirth;

    @NonNull
    @Override
    public String toString() {
        return fname + " - " + id;
    }
}
