package com.bzu.educore.model.user;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString

// TODO: rename Person to User
public abstract class Person {
    private Integer age;
    private String id;
    private String name,email;
    private LocalDate dateOfBirth;
    public Person(String id, Integer age, String name, String email, LocalDate dateOfBirth) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }
}
