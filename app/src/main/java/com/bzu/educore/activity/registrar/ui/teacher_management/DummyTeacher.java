package com.bzu.educore.activity.registrar.ui.teacher_management;

import androidx.annotation.NonNull;

import com.bzu.educore.activity.registrar.User;
import com.bzu.educore.model.school.Subject;
import com.bzu.educore.model.user.Person;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DummyTeacher extends User implements Serializable {

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("subject_id")
    private Integer subjectTaughtId;

    public DummyTeacher(String fname, String lname, String email, String password,
                        LocalDate dateOfBirth, String phoneNumber, Integer subjectTaughtId) {
        super(fname, lname, email, password, dateOfBirth);
        this.phoneNumber = phoneNumber;
        this.subjectTaughtId = subjectTaughtId;
    }

    @NonNull
    @Override
    public String toString() {
        if (getFname() == null || getLname() == null)
            return "none";
        return String.format("%s %s", getFname(), getLname());
    }
}
