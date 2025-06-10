package com.bzu.educore.activity.registrar.ui.student_management;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DummyClassroom implements Serializable {

    @SerializedName("grade_number")
    private int gradeNumber;

    private char section;

    @SerializedName("homeroom_teacher_id")
    private int homeroomTeacherId;

    @NonNull
    @Override
    public String toString() {
        return gradeNumber + " - " + section;
    }
}
