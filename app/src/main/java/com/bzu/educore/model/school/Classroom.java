package com.bzu.educore.model.school;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Classroom implements Serializable {
    private Integer id;
    private Integer homeroomTeacherId;
}

