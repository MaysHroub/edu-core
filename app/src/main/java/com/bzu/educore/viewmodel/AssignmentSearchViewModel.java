package com.bzu.educore.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.model.Assignment;
import com.bzu.educore.model.FilterOption;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AssignmentSearchViewModel extends AndroidViewModel {
    private final RequestQueue requestQueue;
    private final MutableLiveData<List<Assignment>> assignments = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FilterOption>> subjects = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<FilterOption>> grades = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    
    private int selectedSubjectId = 0;
    private int selectedClassId = 0;
    private String searchQuery = "";
    private final int teacherId;

    public AssignmentSearchViewModel(Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
        teacherId = 1; // In practice, inject this or get from preferences
    }

    public LiveData<List<Assignment>> getAssignments() { return assignments; }
    public LiveData<List<FilterOption>> getSubjects() { return subjects; }
    public LiveData<List<FilterOption>> getGrades() { return grades; }
    public LiveData<Boolean> isLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }

    public void loadFilters() {
        isLoading.setValue(true);
        String url = "http://10.0.2.2/android/teacher_timetable.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    List<FilterOption> subjectList = new ArrayList<>();
                    List<FilterOption> gradeList = new ArrayList<>();
                    
                    subjectList.add(new FilterOption(0, "All Subjects"));
                    gradeList.add(new FilterOption(0, "All Grades"));

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);
                        int subjectId = item.getInt("subject_id");
                        String subjectTitle = item.getString("subject_title");
                        int gradeNumber = item.getInt("grade_number");
                        int classId = item.getInt("class_id");

                        subjectList.add(new FilterOption(subjectId, subjectTitle));
                        gradeList.add(new FilterOption(classId, "Grade " + gradeNumber + " (Class " + classId + ")"));
                    }

                    subjects.setValue(subjectList);
                    grades.setValue(gradeList);
                    isLoading.setValue(false);
                    loadAssignments();
                } catch (Exception e) {
                    error.setValue("Error loading filters: " + e.getMessage());
                    isLoading.setValue(false);
                    loadFallbackFilters();
                }
            },
            err -> {
                error.setValue("Network error loading filters");
                isLoading.setValue(false);
                loadFallbackFilters();
            }
        );

        requestQueue.add(request);
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        loadAssignments();
    }

    public void setSelectedSubject(int position) {
        List<FilterOption> currentSubjects = subjects.getValue();
        if (currentSubjects != null && position >= 0 && position < currentSubjects.size()) {
            this.selectedSubjectId = currentSubjects.get(position).getId();
            loadAssignments();
        }
    }

    public void setSelectedGrade(int position) {
        List<FilterOption> currentGrades = grades.getValue();
        if (currentGrades != null && position >= 0 && position < currentGrades.size()) {
            this.selectedClassId = currentGrades.get(position).getId();
            loadAssignments();
        }
    }

    private void loadAssignments() {
        isLoading.setValue(true);

        String url = "http://10.0.2.2/android/search_assignments.php?teacher_id=" + teacherId;
        if (!searchQuery.isEmpty()) url += "&search=" + searchQuery;
        if (selectedSubjectId != 0) url += "&subject_id=" + selectedSubjectId;
        if (selectedClassId != 0) url += "&class_id=" + selectedClassId;

        JsonArrayRequest request = new JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            response -> {
                try {
                    List<Assignment> assignmentList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        assignmentList.add(new Assignment(
                            obj.getInt("id"),
                            obj.optString("assignment_title", "Untitled Assignment"),
                            obj.optString("subject_title", "Unknown Subject"),
                            obj.optInt("grade_number", 0),
                            obj.optString("deadline", "No deadline"),
                            obj.optInt("submitted_count", 0),
                            obj.optInt("total_students", 0)
                        ));
                    }
                    assignments.setValue(assignmentList);
                    isLoading.setValue(false);
                } catch (Exception e) {
                    error.setValue("Error loading assignments: " + e.getMessage());
                    assignments.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                }
            },
            err -> {
                error.setValue("Network error loading assignments");
                assignments.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        );

        requestQueue.add(request);
    }

    private void loadFallbackFilters() {
        List<FilterOption> fallbackSubjects = new ArrayList<>();
        fallbackSubjects.add(new FilterOption(0, "All Subjects"));
        fallbackSubjects.add(new FilterOption(1, "Mathematics"));
        fallbackSubjects.add(new FilterOption(2, "Science"));

        List<FilterOption> fallbackGrades = new ArrayList<>();
        fallbackGrades.add(new FilterOption(0, "All Grades"));
        fallbackGrades.add(new FilterOption(101, "Grade 10"));
        fallbackGrades.add(new FilterOption(102, "Grade 11"));

        subjects.setValue(fallbackSubjects);
        grades.setValue(fallbackGrades);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        requestQueue.cancelAll(getClass().getName());
    }
} 