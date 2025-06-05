
package com.bzu.educore.activity.teacher.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.adapter.AssignmentAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.bzu.educore.model.Assignment;

public class SearchAssignmentsFragment extends Fragment {

    private Spinner spinnerSubject, spinnerGrade;
    private Button btnSearch, btnClearFilters;
    private RecyclerView recyclerViewAssignments;
    private AssignmentAdapter assignmentAdapter;
    private List<Assignment> assignmentList;
    private RequestQueue requestQueue;

    private static final String BASE_URL = "http://10.0.2.2/android/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_assignment, container, false);

        initViews(view);
        setupRecyclerView();
        loadSpinnerData();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        spinnerSubject = view.findViewById(R.id.spinner_subject);
        spinnerGrade = view.findViewById(R.id.spinner_grade);
        btnSearch = view.findViewById(R.id.btn_search);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);
        recyclerViewAssignments = view.findViewById(R.id.recycler_view_assignments);

        requestQueue = Volley.newRequestQueue(requireContext());
        assignmentList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        assignmentAdapter = new AssignmentAdapter(assignmentList);
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAssignments.setAdapter(assignmentAdapter);
    }

    private void loadSpinnerData() {
        loadSubjects();
        loadGrades();
    }

    private void loadSubjects() {
        String url = BASE_URL + "get_subjects.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> subjects = new ArrayList<>();
                    subjects.add("All Subjects");

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject subject = response.getJSONObject(i);
                            subjects.add(subject.getString("title"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_item, subjects);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSubject.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Error loading subjects");
                    }
                },
                error -> showToast("Failed to load subjects"));

        requestQueue.add(request);
    }

    private void loadGrades() {
        String url = BASE_URL + "get_grades.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> grades = new ArrayList<>();
                    grades.add("All Grades");

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject grade = response.getJSONObject(i);
                            grades.add("Grade " + grade.getString("grade_number"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_item, grades);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerGrade.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Error loading grades");
                    }
                },
                error -> showToast("Failed to load grades"));

        requestQueue.add(request);
    }

    private void setupClickListeners() {
        btnSearch.setOnClickListener(v -> searchAssignments());
        btnClearFilters.setOnClickListener(v -> clearFilters());
    }

    private void searchAssignments() {
        String selectedSubject = spinnerSubject.getSelectedItem().toString();
        String selectedGrade = spinnerGrade.getSelectedItem().toString();

        String url = BASE_URL + "search_assignments.php";

        // Build query parameters
        StringBuilder urlBuilder = new StringBuilder(url + "?");

        if (!selectedSubject.equals("All Subjects")) {
            urlBuilder.append("subject=").append(selectedSubject).append("&");
        }

        if (!selectedGrade.equals("All Grades")) {
            String gradeNumber = selectedGrade.replace("Grade ", "");
            urlBuilder.append("grade=").append(gradeNumber).append("&");
        }

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, finalUrl, null,
                response -> {
                    assignmentList.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject assignmentObj = response.getJSONObject(i);
                            Assignment assignment = new Assignment(
                                    assignmentObj.getInt("id"),
                                    assignmentObj.getString("subject_title"),
                                    assignmentObj.getInt("grade_number"),
                                    assignmentObj.getString("teacher_name"),
                                    assignmentObj.getDouble("max_score"),
                                    assignmentObj.getString("date"),
                                    assignmentObj.getString("deadline"),
                                    assignmentObj.getString("question_file_url")
                            );
                            assignmentList.add(assignment);
                        }

                        assignmentAdapter.notifyDataSetChanged();

                        if (assignmentList.isEmpty()) {
                            showToast("No assignments found with the selected filters");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Error parsing assignment data");
                    }
                },
                error -> showToast("Failed to search assignments"));

        requestQueue.add(request);
    }

    private void clearFilters() {
        spinnerSubject.setSelection(0);
        spinnerGrade.setSelection(0);
        assignmentList.clear();
        assignmentAdapter.notifyDataSetChanged();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}