package com.bzu.educore.activity.registrar.ui.student_management;

import com.bzu.educore.activity.registrar.User;
import com.bzu.educore.model.user.Person;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DummyStudent extends User implements Serializable {

    @SerializedName("class_id")
    private Integer classId;

    public DummyStudent(String fname, String lname, String email, String password,
                        LocalDate dateOfBirth, Integer classId) {
        super(fname, lname, email, password, dateOfBirth);
        this.classId = classId;
    }
}

