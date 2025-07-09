package com.bzu.educore.model.school;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Classroom implements Serializable {

    private Integer id;

    @SerializedName("grade_number")
    private Integer gradeNumber;

    private String section;

    @SerializedName("homeroom_teacher_id")
    private Integer homeroomTeacherId;

    public Classroom(Integer id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Grade %d - Sec %s", gradeNumber, section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Classroom)) return false;
        Classroom classroom = (Classroom) o;
        return Objects.equals(id, classroom.id);
    }

}
