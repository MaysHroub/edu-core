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
public class Student {
    private int id,age;
    private String name,email,class_id,dateOfBirth;
}
