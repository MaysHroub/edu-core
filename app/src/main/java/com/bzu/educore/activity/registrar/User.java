package com.bzu.educore.activity.registrar;

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
    private LocalDate dateOfBirth;

}
