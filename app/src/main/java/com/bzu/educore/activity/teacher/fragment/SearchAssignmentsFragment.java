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
import com.android.volley.toolbox.JsonArrayRequest;
import com.bzu.educore.R;
import com.bzu.educore.adapter.teacher.AssignmentAdapter;
import com.bzu.educore.model.task.Assignment;
import com.bzu.educore.util.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
/**
 * Fragment to search and display assignments with optional text, subject, and grade filters.
 */
public class SearchAssignmentsFragment extends Fragment {

    private static final String BASE_URL = "http://10.0.2.2/android/search_assignments.php";

    private EditText etSearch;
    private Button btnClearSearch;
    private Spinner spinnerSubject, spinnerGrade;
    private Button btnSearch, btnClearFilters;
    private RecyclerView recyclerViewAssignments;
    private TextView tvResultCount;
    private LinearLayout layoutEmptyState;

    private AssignmentAdapter assignmentAdapter;
    private List<Assignment> assignmentList;
    private List<String> subjectOptions;
    private List<String> gradeOptions;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable  ViewGroup container,
            @Nullable  Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_search_assignment, container, false);

        initViews(view);
        setupRecyclerView();
        populateSpinners();
        setupListeners();

        // Initial state: show empty list until user searches or applies filters
        updateEmptyState();
        updateResultCount();

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

        assignmentList = new ArrayList<>();
        subjectOptions = new ArrayList<>();
        gradeOptions = new ArrayList<>();
    }

    private void setupRecyclerView() {
        assignmentAdapter = new AssignmentAdapter(assignmentList, requireActivity());
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewAssignments.setAdapter(assignmentAdapter);
    }

    private void populateSpinners() {
        loadSubjects();
        loadGrades();
    }

    private void loadSubjects() {
        String url = "http://10.0.2.2/android/get_subjects.php";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    subjectOptions.clear();
                    subjectOptions.add("All Subjects");
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject subject = response.getJSONObject(i);
                            subjectOptions.add(subject.getString("title"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                subjectOptions
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSubject.setAdapter(adapter);
                    } catch (JSONException e) {
                        showToast("Error loading subjects");
                    }
                },
                error -> showToast("Failed to load subjects")
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void loadGrades() {
        String url = "http://10.0.2.2/android/get_grades.php";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    gradeOptions.clear();
                    gradeOptions.add("All Grades");
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject grade = response.getJSONObject(i);
                            gradeOptions.add("Grade " + grade.getString("grade_number"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                gradeOptions
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerGrade.setAdapter(adapter);
                    } catch (JSONException e) {
                        showToast("Error loading grades");
                    }
                },
                error -> showToast("Failed to load grades")
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void setupListeners() {
        // Search text change: show/hide clear button
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Search on IME search or Enter key
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                applyFilters();
                return true;
            }
            return false;
        });

        // Clear search text
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
            applyFilters();
        });

        // Search button
        btnSearch.setOnClickListener(v -> applyFilters());

        // Clear all filters
        btnClearFilters.setOnClickListener(v -> {
            etSearch.setText("");
            spinnerSubject.setSelection(0);
            spinnerGrade.setSelection(0);
            btnClearSearch.setVisibility(View.GONE);
            assignmentList.clear();
            assignmentAdapter.notifyDataSetChanged();
            updateEmptyState();
            updateResultCount();
        });
    }
    private void applyFilters() {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL).append("?");

        String searchQuery = etSearch.getText().toString().trim();
        if (!searchQuery.isEmpty()) {
            urlBuilder.append("search=").append(searchQuery).append("&");
        }

        String selectedSubject = (String) spinnerSubject.getSelectedItem();
        if (selectedSubject != null && !selectedSubject.equals("All Subjects")) {
            urlBuilder.append("subject=").append(selectedSubject).append("&");
        }

        String selectedGrade = (String) spinnerGrade.getSelectedItem();
        if (selectedGrade != null && !selectedGrade.equals("All Grades")) {
            String gradeNumber = selectedGrade.replace("Grade ", "");
            urlBuilder.append("grade=").append(gradeNumber).append("&");
        }

        // Trim trailing '&' or '?' if present
        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        fetchAssignments(finalUrl);
    }

    private void fetchAssignments(String url) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    assignmentList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Assignment assignment = new Assignment(
                                    obj.getInt("id"),
                                    obj.getString("subject_title"),
                                    obj.getInt("grade_number"),
                                    obj.getString("teacher_name"),
                                    obj.getDouble("max_score"),
                                    obj.getString("date"),
                                    obj.getString("deadline"),
                                    obj.getString("question_file_url")
                            );
                            assignmentList.add(assignment);
                        }
                        assignmentAdapter.notifyDataSetChanged();
                        updateEmptyState();
                        updateResultCount();
                    } catch (JSONException e) {
                        showToast("Error parsing assignment data");
                        updateEmptyState();
                        updateResultCount();
                    }
                },
                error -> {
                    showToast("Failed to retrieve assignments");
                    assignmentList.clear();
                    assignmentAdapter.notifyDataSetChanged();
                    updateEmptyState();
                    updateResultCount();
                }
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }
    private void updateResultCount() {
        int count = assignmentList.size();
        tvResultCount.setText(count + (count == 1 ? " assignment" : " assignments"));
    }
    private void updateEmptyState() {
        boolean isEmpty = assignmentList.isEmpty();
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerViewAssignments.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvResultCount.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
