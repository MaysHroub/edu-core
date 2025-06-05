package com.bzu.educore.activity.teacher.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.bzu.educore.model.Assignment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchAssignmentsFragment extends Fragment {

    private EditText etSearch;
    private Button btnClearSearch;
    private Spinner spinnerSubject, spinnerGrade;
    private Button btnSearch, btnClearFilters;
    private RecyclerView recyclerViewAssignments;
    private TextView tvResultCount;
    private LinearLayout layoutEmptyState;

    private AssignmentAdapter assignmentAdapter;
    private List<Assignment> assignmentList;
    private List<Assignment> allAssignmentsList; // For local filtering
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
        setupSearchFunctionality();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);
        spinnerSubject = view.findViewById(R.id.spinner_subject);
        spinnerGrade = view.findViewById(R.id.spinner_grade);
        btnSearch = view.findViewById(R.id.btn_search);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);
        recyclerViewAssignments = view.findViewById(R.id.recycler_view_assignments);
        tvResultCount = view.findViewById(R.id.tv_result_count);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);

        requestQueue = Volley.newRequestQueue(requireContext());
        assignmentList = new ArrayList<>();
        allAssignmentsList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        assignmentAdapter = new AssignmentAdapter(assignmentList, getActivity());
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAssignments.setAdapter(assignmentAdapter);
    }

    private void setupSearchFunctionality() {
        // Text change listener for search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show/hide clear button
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });

        // Search on enter key
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch();
                return true;
            }
            return false;
        });

        // Clear search button
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
        });
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

    private void performSearch() {
        String searchQuery = etSearch.getText().toString().trim();

        if (searchQuery.isEmpty()) {
            searchAssignments();
        } else {
            searchAssignmentsWithText(searchQuery);
        }
    }

    private void searchAssignmentsWithText(String searchQuery) {
        Object selectedSubjectObj = spinnerSubject.getSelectedItem();
        String selectedSubject = (selectedSubjectObj != null) ? selectedSubjectObj.toString() : "";

        Object selectedGradeObj = spinnerGrade.getSelectedItem();
        String selectedGrade = (selectedGradeObj != null) ? selectedGradeObj.toString() : "";

        String url = BASE_URL + "search_assignments.php";

        StringBuilder urlBuilder = new StringBuilder(url + "?");

        urlBuilder.append("search=").append(searchQuery).append("&");

        if (!selectedSubject.equals("All Subjects") && !selectedSubject.isEmpty()) {
            urlBuilder.append("subject=").append(selectedSubject).append("&");
        }

        if (!selectedGrade.equals("All Grades") && !selectedGrade.isEmpty()) {
            String gradeNumber = selectedGrade.replace("Grade ", "");
            urlBuilder.append("grade=").append(gradeNumber).append("&");
        }

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        performSearchRequest(finalUrl);
    }

    private void searchAssignments() {
        Object selectedSubjectObj = spinnerSubject.getSelectedItem();
        String selectedSubject = (selectedSubjectObj != null) ? selectedSubjectObj.toString() : "";

        Object selectedGradeObj = spinnerGrade.getSelectedItem();
        String selectedGrade = (selectedGradeObj != null) ? selectedGradeObj.toString() : "";

        String searchQuery = etSearch.getText().toString().trim();

        String url = BASE_URL + "search_assignments.php";

        StringBuilder urlBuilder = new StringBuilder(url + "?");

        if (!searchQuery.isEmpty()) {
            urlBuilder.append("search=").append(searchQuery).append("&");
        }

        if (!selectedSubject.equals("All Subjects") && !selectedSubject.isEmpty()) {
            urlBuilder.append("subject=").append(selectedSubject).append("&");
        }

        if (!selectedGrade.equals("All Grades") && !selectedGrade.isEmpty()) {
            String gradeNumber = selectedGrade.replace("Grade ", "");
            urlBuilder.append("grade=").append(gradeNumber).append("&");
        }

        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        performSearchRequest(finalUrl);
    }

    private void performSearchRequest(String url) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    assignmentList.clear();
                    allAssignmentsList.clear();

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
                            allAssignmentsList.add(assignment);
                        }

                        assignmentAdapter.notifyDataSetChanged();
                        updateResultCount();
                        updateEmptyState();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Error parsing assignment data");
                    }
                },
                error -> {
                    showToast("Failed to search assignments");
                    updateEmptyState();
                });

        requestQueue.add(request);
    }

    private void clearFilters() {
        etSearch.setText("");
        spinnerSubject.setSelection(0);
        spinnerGrade.setSelection(0);
        btnClearSearch.setVisibility(View.GONE);
        assignmentList.clear();
        allAssignmentsList.clear();
        assignmentAdapter.notifyDataSetChanged();
        updateResultCount();
        updateEmptyState();
    }

    private void updateResultCount() {
        int count = assignmentList.size();
        String countText = count + (count == 1 ? " assignment" : " assignments");
        tvResultCount.setText(countText);
    }

    private void updateEmptyState() {
        boolean isEmpty = assignmentList.isEmpty();
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerViewAssignments.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvResultCount.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
