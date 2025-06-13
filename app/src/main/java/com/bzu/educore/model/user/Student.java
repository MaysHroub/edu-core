package com.bzu.educore.model.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Student extends User implements Serializable {

    @SerializedName("class_id")
    private Integer classId;

    public Student(Integer id, String fname, String lname, String email, String password, LocalDate dateOfBirth, Integer classId) {
        super(id, fname, lname, email, password, dateOfBirth);
        this.classId = classId;
    }

    public Student(String fname, String lname, String email, String password,
                   LocalDate dateOfBirth, Integer classId) {
        super(fname, lname, email, password, dateOfBirth);
        this.classId = classId;
    }
}

