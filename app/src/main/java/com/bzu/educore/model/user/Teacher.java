package com.bzu.educore.model.user;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Teacher extends User implements Serializable {

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("subject_id")
    private Integer subjectTaughtId;

    public Teacher(String fname, String lname, String email, String password,
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
