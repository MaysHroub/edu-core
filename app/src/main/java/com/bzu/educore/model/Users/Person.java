package com.bzu.educore.model.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Person {
    private int id,age;
    private String name,email,dateOfBirth;
}
