package com.bzu.educore.util;

public class UrlManager {
    private static final String BASE_URL = "http://10.0.2.2/edu-core/";

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
    public static final String URL_ADD_NEW_SUBJECT = BASE_URL + SUBJECTS + "add_new_subject.php";



    public static final String URL_GET_CLASSROOM_COUNT = BASE_URL + CLASSROOMS + "get_classroom_count.php";
    public static final String URL_GET_ALL_CLASSROOMS = BASE_URL + CLASSROOMS + "get_all_classrooms.php";
    public static final String URL_UPDATE_CLASSROOM = BASE_URL + CLASSROOMS + "update_classroom.php";


    public static final String URL_GET_ALL_GRADES = BASE_URL + GRADES + "get_all_grades.php";

    // ---- Teacher URLS -----
    public static final String URL_GET_ASSIGNMENT_STUDENTS = BASE_URL + "get_assignment_students.php";
    public static final String URL_GET_CLASS_STUDENTS = BASE_URL + "get_class_students.php";
    public static final String URL_GET_EXAM_STUDENTS = BASE_URL + "get_exam_students.php";
    public static final String URL_GET_GRADES = BASE_URL + "get_grades.php";
    public static final String URL_GET_SUBJECTS = BASE_URL + "get_subjects.php";
    public static final String URL_GET_TIMETABLE_BY_TEACHER = BASE_URL + "get_timetable_by_teacher.php";
    public static final String URL_PUBLISH_ASSIGNMENT = BASE_URL + "publish_assignment.php";
    public static final String URL_PUBLISH_EXAM = BASE_URL + "publish_exam.php";
    public static final String URL_PUBLISH_MARKS = BASE_URL + "publish_marks.php";
    public static final String URL_SEARCH_TASKS = BASE_URL + "search_tasks.php";
    public static final String URL_SUBMIT_ABSENCE = BASE_URL + "submit_absence.php";
    public static final String URL_UPLOAD_FILE = BASE_URL + "upload_file.php";
    public static final String URL_VIEW_SUBMISSION = BASE_URL + "view_submission.php";
    public static final String URL_GET_HOMEROOM_CLASS = BASE_URL + "get_homeroom_class.php";
    public static final String URL_GET_TEACHER_TIMETABLE = BASE_URL + "get_teacher_timetable.php";


    public static final String URL_LOGIN = BASE_URL + "login.php";

    // Student URLs
    public static final String URL_GET_EVENTS = BASE_URL + "get_events.php";
    public static final String URL_GET_MARKS = BASE_URL + "get_marks.php";
    public static final String URL_SUBMIT_ASSIGNMENT = BASE_URL + "submit_assignment.php";
    public static final String URL_GET_TIMETABLE_BY_STUDENT = BASE_URL + "get_timetable_by_student.php";

    // Student Profile URLs
    public static final String URL_GET_STUDENT_DATA = BASE_URL + "get_student_data.php";
    public static final String URL_EDIT_STUDENT_DATA = BASE_URL + "edit_student_data.php";

    // Registrar Profile URLs
    public static final String URL_GET_REGISTRAR_DATA = BASE_URL + "get_registrar_data.php";

    // Authentication URLs
    public static final String LOGIN_URL = BASE_URL + "login.php";
    public static final String REGISTER_URL = BASE_URL + "register.php";
    public static final String GET_TEACHER_DATA_URL = BASE_URL + "get_teacher_data.php";
    public static final String GET_REGISTRAR_DATA_URL = BASE_URL + "get_registrar_data.php";

    // Timetable Management URLs
    public static final String GET_ALL_CLASSES_URL = BASE_URL + "get_all_classes.php";
    public static final String GET_CLASS_SUBJECTS_URL = BASE_URL + "get_class_subjects.php";
    public static final String GET_TEACHERS_FOR_SUBJECT_URL = BASE_URL + "get_teachers_for_subject.php";
    public static final String CHECK_TIME_AVAILABILITY_URL = BASE_URL + "check_time_availability.php";
    public static final String ADD_TO_TIMETABLE_URL = BASE_URL + "add_to_timetable.php";

    public static final String GET_CLASS_TIMETABLE_URL = BASE_URL + "get_class_timetable.php";
}
