package com.bzu.educore.activity.registrar.ui.teacher_management;

import com.bzu.educore.activity.registrar.User;
import com.bzu.educore.model.school.Subject;
import com.bzu.educore.model.user.Person;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class DummyTeacher extends User implements Serializable {

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("subject_id")
    private Integer subjectTaughtId;

    public DummyTeacher(Integer id, String fname, String lname, String email, String password,
                        LocalDate dateOfBirth, String phoneNumber, Integer subjectTaughtId) {
        super(id, fname, lname, email, password, dateOfBirth);
        this.phoneNumber = phoneNumber;
        this.subjectTaughtId = subjectTaughtId;
    }
}
