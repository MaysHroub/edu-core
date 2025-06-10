package com.bzu.educore.model.school;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject implements Serializable {

    private Integer id;

    private String title;

    private String description;

    @SerializedName("grade_number")
    private Integer gradeNumber;

    @SerializedName("semester_number")
    private Integer semesterNumber;


    public Subject(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s-G%d-S%d", title, gradeNumber, semesterNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        Subject subject = (Subject) o;
        return Objects.equals(id, subject.id);
    }

}