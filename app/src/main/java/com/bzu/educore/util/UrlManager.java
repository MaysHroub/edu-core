package com.bzu.educore.util;

public class UrlManager {
    private static final String BASE_URL = "http://localhost/edu-core/";
    private static final String TEACHERS = "teachers/",
            STUDENTS = "students/",
            SUBJECTS = "subjects/",
            CLASSROOMS = "classrooms/",
            GRADES = "grades/";
    public static final String URL_GET_TEACHER_COUNT = BASE_URL + TEACHERS + "get_teacher_count.php";
    public static final String URL_GET_TEACHER_PER_SUBJECT = BASE_URL + TEACHERS + "get_teachers_per_subject.php";
    public static final String URL_GENERATE_TCHR_ID = BASE_URL + TEACHERS + "generate_teacher_id.php";
    public static final String URL_ADD_NEW_TEACHER = BASE_URL + TEACHERS + "add_new_teacher.php";
    public static final String URL_GET_ALL_TEACHERS = BASE_URL + TEACHERS + "get_all_teachers.php";
    public static final String URL_UPDATE_TEACHER = BASE_URL + TEACHERS + "update_teacher.php";
    public static final String URL_DELETE_TEACHER = BASE_URL + TEACHERS + "delete_teacher.php";


    public static final String URL_GET_STUDENT_COUNT = BASE_URL + STUDENTS + "get_student_count.php";
    public static final String URL_GET_STUDENT_PER_GRADE = BASE_URL + STUDENTS + "get_students_per_grade.php";
    public static final String URL_GENERATE_STD_ID = BASE_URL + STUDENTS + "generate_student_id.php";
    public static final String URL_ADD_NEW_STUDENT = BASE_URL + STUDENTS + "add_new_student.php";
    public static final String URL_GET_ALL_STUDENTS = BASE_URL + STUDENTS + "get_all_students.php";
    public static final String URL_UPDATE_STUDENT = BASE_URL + STUDENTS + "update_student.php";
    public static final String URL_DELETE_STUDENT = BASE_URL + STUDENTS + "delete_student.php";


    public static final String URL_GET_SUBJECT_COUNT = BASE_URL + SUBJECTS + "get_subject_count.php";
    public static final String URL_GET_ALL_SUBJECTS = BASE_URL + SUBJECTS + "get_all_subjects.php";
    public static final String URL_UPDATE_SUBJECT = BASE_URL + SUBJECTS + "update_subject.php";
    public static final String URL_DELETE_SUBJECT = BASE_URL + SUBJECTS + "delete_subject.php";


    public static final String URL_GET_CLASSROOM_COUNT = BASE_URL + CLASSROOMS + "get_classroom_count.php";
    public static final String URL_GET_ALL_CLASSROOMS = BASE_URL + CLASSROOMS + "get_all_classrooms.php";
    public static final String URL_UPDATE_CLASSROOM = BASE_URL + CLASSROOMS + "update_classroom.php";


    public static final String URL_GET_ALL_GRADES = BASE_URL + GRADES + "get_all_grades.php";

}
