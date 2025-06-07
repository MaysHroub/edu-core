package com.bzu.educore.util.teacher;

public class Constants {
    // API URLs
    public static final String BASE_URL = "http://10.0.2.2/android/";
    public static final String SEARCH_TASKS_URL = BASE_URL + "search_tasks.php";
    public static final String GET_SUBJECTS_URL = BASE_URL + "get_subjects.php";
    public static final String GET_GRADES_URL = BASE_URL + "get_grades.php";
    public static final String PUBLISH_MARKS_URL = BASE_URL + "publish_marks.php";
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
    // JSON Keys
    public static final String JSON_TITLE = "title";
    public static final String JSON_GRADE_NUMBER = "grade_number";

    // Prefixes
    public static final String GRADE_PREFIX = "Grade ";

    // Task Types
    public static final String TYPE_ASSIGNMENT = "Assignment";
    public static final String TYPE_EXAM = "Exam";

    // UI Texts
    public static final String TEXT_SEARCH = "Search";
    public static final String TEXT_SEARCHING = "Searching...";
    public static final String TEXT_ITEM_SINGULAR = " item";
    public static final String TEXT_ITEM_PLURAL = " items";

    // Error Messages
    public static final String ERROR_PARSE_SPINNER = "Error parsing spinner data";
    public static final String ERROR_LOAD_SPINNER = "Failed to load spinner data";
    public static final String ERROR_SEARCH_FAILED = "Search failed";
    public static final String ERROR_PARSE_TASKS = "Error parsing tasks";

}
