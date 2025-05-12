package com.bzu.educore.model.Users;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

abstract class Person {
    private Integer id,age;
    private String name,email;
    private LocalDate dateOfBirth;
}
