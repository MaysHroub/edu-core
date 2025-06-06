package com.bzu.educore.util.teacher;

public class Constants {
    // API URLs
    public static final String BASE_URL = "http://10.0.2.2/android/";
    public static final String SEARCH_TASKS_URL = BASE_URL + "search_tasks.php";
    public static final String GET_SUBJECTS_URL = BASE_URL + "get_subjects.php";
    public static final String GET_GRADES_URL = BASE_URL + "get_grades.php";

    // Grading
    public static final double MIN_MARK = 0.0;
    public static final double MAX_MARK = 100.0;

    // Status
    public static final String STATUS_SUBMITTED = "submitted";
    public static final String STATUS_OVERDUE = "overdue";
    public static final String STATUS_PENDING = "pending";

    // Spinner defaults
    public static final String ALL_SUBJECTS = "All Subjects";
    public static final String ALL_GRADES = "All Grades";
    public static final String ALL_TYPES = "All";
}
