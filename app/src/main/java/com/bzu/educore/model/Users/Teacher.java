package com.bzu.educore.model.Users;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Teacher extends Person {
    private String phone_number;
    public Teacher(Integer id, Integer age, String name, String email, LocalDate dateOfBirth, String phone_number) {
        super(id, age, name, email, dateOfBirth);
        this.phone_number = phone_number;
    }

}
