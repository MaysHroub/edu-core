package com.bzu.educore.activity.registrar;

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

}
