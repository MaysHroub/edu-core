package com.bzu.educore.activity.teacher.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.adapter.AssignmentSearchAdapter;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AssignmentSearchFragment extends BaseFragment implements AssignmentSearchAdapter.OnAssignmentClickListener {

    private Spinner subjectFilterSpinner, gradeFilterSpinner;
    private RecyclerView assignmentsRecyclerView;
    private AssignmentSearchAdapter adapter;
    private List<AssignmentSearchAdapter.AssignmentItem> assignmentItems = new ArrayList<>();

    // Keep track of IDs for selected spinner items
    protected List<Integer> subjectIds = new ArrayList<>();
    protected List<Integer> classIds = new ArrayList<>(); // Represents grades

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_submissions_fragment, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupSpinnerListeners();
        loadTimetableAndFilters();

        return view;
    }

    private void initializeViews(View view) {
        subjectFilterSpinner = view.findViewById(R.id.subjectFilterSpinner);
        gradeFilterSpinner = view.findViewById(R.id.gradeFilterSpinner);
        assignmentsRecyclerView = view.findViewById(R.id.assignmentsRecyclerView);
    }

    private void setupRecyclerView() {
        assignmentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AssignmentSearchAdapter(assignmentItems, this); // 'this' refers to OnAssignmentClickListener
        assignmentsRecyclerView.setAdapter(adapter);
    }

    private void setupSpinnerListeners() {
        // When subject or grade selection changes, reload assignments
        subjectFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAssignments();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        gradeFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAssignments();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadTimetableAndFilters() {
        // This method fetches the teacher's timetable to populate subject and grade spinners.
        // It's similar to the BaseFragment's loadTimetable but needs to populate the filter spinners.
        String url = "http://10.0.2.2/android/teacher_timetable.php?teacher_id=" + 1;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<String> subjects = new ArrayList<>();
                        List<String> grades = new ArrayList<>();
                        subjectIds.clear();
                        classIds.clear(); // Clear existing IDs

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            int subjectId = item.getInt("subject_id");
                            String subjectTitle = item.getString("subject_title");
                            int gradeNumber = item.getInt("grade_number");
                            int classId = item.getInt("class_id"); // Assuming class_id is needed for assignments

                            if (!subjectIds.contains(subjectId)) {
                                subjectIds.add(subjectId);
                                subjects.add(subjectTitle);
                            }
                            if (!classIds.contains(classId)) {
                                classIds.add(classId);
                                grades.add("Grade " + gradeNumber + " (Class " + classId + ")");
                            }
                        }

                        // Add "All" option
                        subjects.add(0, "All Subjects");
                        subjectIds.add(0, 0); // 0 or -1 to indicate "all"

                        grades.add(0, "All Grades");
                        classIds.add(0, 0); // 0 or -1 to indicate "all"

                        subjectFilterSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjects));
                        gradeFilterSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, grades));

                        // Load assignments after spinners are populated
                        loadAssignments();

                    } catch (Exception e) {
                        showErrorToast("Error parsing timetable: " + e.getMessage());
                        showFallbackFilters();
                    }
                },
                error -> {
                    showErrorToast("Failed to load timetable: " + error.getMessage());
                    showFallbackFilters();
                }
        );
        requestQueue.add(request);
    }

    private void showFallbackFilters() {
        List<String> fallbackSubjects = List.of("All Subjects", "Mathematics", "Science");
        List<Integer> fallbackSubjectIds = List.of(0, 1, 2); // Example IDs
        List<String> fallbackGrades = List.of("All Grades", "Grade 10 (Class 1)", "Grade 11 (Class 2)");
        List<Integer> fallbackClassIds = List.of(0, 101, 102); // Example IDs

        subjectFilterSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, fallbackSubjects));
        gradeFilterSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, fallbackGrades));

        this.subjectIds = fallbackSubjectIds;
        this.classIds = fallbackClassIds;

        loadAssignments(); // Still try to load assignments with fallback
    }


    private void loadAssignments() {
        int selectedSubjectId = subjectIds.get(subjectFilterSpinner.getSelectedItemPosition());
        int selectedClassId = classIds.get(gradeFilterSpinner.getSelectedItemPosition());

        String url = "http://10.0.2.2/android/search_assignments.php";
        String params = "?teacher_id=" + 1;

        if (selectedSubjectId != 0) { // If "All Subjects" is not selected
            params += "&subject_id=" + selectedSubjectId;
        }
        if (selectedClassId != 0) { // If "All Grades" is not selected
            params += "&class_id=" + selectedClassId;
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url + params, null,
                response -> {
                    try {
                        assignmentItems.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject assignment = response.getJSONObject(i);
                            assignmentItems.add(new AssignmentSearchAdapter.AssignmentItem(
                                    assignment.getInt("id"),
                                    assignment.getString("assignment_title"),
                                    assignment.getString("subject_title"),
                                    assignment.getInt("grade_number"),
                                    assignment.optString("deadline"),
                                    assignment.getInt("submitted_count"),
                                    assignment.getInt("total_students")
                            ));
                        }
                        adapter.updateAssignments(assignmentItems);
                        if (assignmentItems.isEmpty()) {
                            showToast("No assignments found for the selected filters.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showErrorToast("Error parsing assignments: " + e.getMessage());
                        assignmentItems.clear();
                        adapter.updateAssignments(assignmentItems); // Clear list on error
                    }
                },
                error -> {
                    error.printStackTrace();
                    showErrorToast("Failed to load assignments: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    assignmentItems.clear();
                    adapter.updateAssignments(assignmentItems); // Clear list on error
                }
        );
        requestQueue.add(request);
    }

    @Override
    public void onAssignmentClick(int assignmentId) {
        // Navigate to AssignmentSubmissionsFragment when an assignment card is clicked
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AssignmentSubmissionsFragment.newInstance(assignmentId))
                    .addToBackStack(null)
                    .commit();
        }
    }
}