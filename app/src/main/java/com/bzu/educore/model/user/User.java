package com.bzu.educore.model.user;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {

    private Integer id;
    private String fname, lname, email, password;

    @SerializedName("date_of_birth")
    private LocalDate dateOfBirth;

    public User(String lname, String fname, String email, String password, LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.email = email;
        this.lname = lname;
        this.fname = fname;
    }
}
