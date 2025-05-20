package com.bzu.educore.model.users;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString

public abstract class Person {
    private Integer id,age;
    private String name,email;
    private LocalDate dateOfBirth;
    public Person(Integer id, Integer age, String name, String email, LocalDate dateOfBirth) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }
}
