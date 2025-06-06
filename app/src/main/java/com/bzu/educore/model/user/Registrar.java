package com.bzu.educore.model.user;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public class Registrar extends Person{
    public Registrar(Integer id, Integer age, String name, String email, LocalDate dateOfBirth) {
        super(id, age, name, email, dateOfBirth);
    }

}
