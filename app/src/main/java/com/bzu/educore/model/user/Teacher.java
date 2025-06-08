package com.bzu.educore.model.user;

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
    private Integer id;
    public Teacher( Integer age, String name, String email, LocalDate dateOfBirth, String phone_number) {
        super( age, name, email, dateOfBirth);
        this.phone_number = phone_number;
    }

}
