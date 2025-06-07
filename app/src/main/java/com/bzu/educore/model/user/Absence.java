package com.bzu.educore.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor
@AllArgsConstructor
public class Absence {
    private int id;
    private String studentId; // Matches VARCHAR(20) in database
    private LocalDate date;
    private String status;    // "excused" or "unexcused"
}
