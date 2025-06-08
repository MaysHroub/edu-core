package com.bzu.educore.activity.registrar.ui.homeroom_teacher;

import com.bzu.educore.activity.registrar.ui.student_management.DummyClassroom;
import com.bzu.educore.activity.registrar.ui.teacher_management.DummyTeacher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HomeroomTeacherAssigning {

    private int teacherId;
    private String teacherName;
    private int classroomGrade;
    private String classroomSection;

}
