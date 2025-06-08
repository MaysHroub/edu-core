package com.bzu.educore.activity.registrar.ui.student_management;

import androidx.annotation.NonNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DummyClassroom implements Serializable {

    private int gradeNumber;
    private char section;
    private int homeroomTeacherId;

    @NonNull
    @Override
    public String toString() {
        return gradeNumber + " - " + section;
    }
}
