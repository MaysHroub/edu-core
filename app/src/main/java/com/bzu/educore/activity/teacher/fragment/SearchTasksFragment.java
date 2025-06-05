package com.bzu.educore.activity.teacher.fragment;

import android.os.Bundle;
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
import com.bzu.educore.adapter.teacher.TaskAdapter;
import com.bzu.educore.model.task.Task;
import com.bzu.educore.util.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to search and display both assignments and exams with optional text, subject, grade, and type filters.
 */
public class SearchTasksFragment extends Fragment {

    private static final String BASE_URL = "http://10.0.2.2/android/search_tasks.php";

    private EditText etSearch;
    private Button btnClearSearch;
    private Spinner spinnerSubject, spinnerGrade, spinnerType;
    private Button btnSearch, btnClearFilters;
    private RecyclerView recyclerViewTasks;
    private TextView tvResultCount;
    private LinearLayout layoutEmptyState;

    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private List<String> subjectOptions;
    private List<String> gradeOptions;
    private List<String> typeOptions;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable  ViewGroup container,
            @Nullable  Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_search_task, container, false);

        initViews(view);
        setupRecyclerView();
        populateSpinners();
        setupListeners();

        updateEmptyState();
        updateResultCount();

        return view;
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);
        spinnerSubject = view.findViewById(R.id.spinner_subject);
        spinnerGrade = view.findViewById(R.id.spinner_grade);
        spinnerType = view.findViewById(R.id.spinner_type);
        btnSearch = view.findViewById(R.id.btn_search);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);
        recyclerViewTasks = view.findViewById(R.id.recycler_view_tasks);
        tvResultCount = view.findViewById(R.id.tv_result_count);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);

        taskList = new ArrayList<>();
        subjectOptions = new ArrayList<>();
        gradeOptions = new ArrayList<>();
        typeOptions = new ArrayList<>();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(taskList, requireActivity());
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewTasks.setAdapter(taskAdapter);
    }

    private void populateSpinners() {
        loadSubjects();
        loadGrades();
        loadTypes();
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

    private void loadTypes() {
        // We manually set the types: All, Assignment, Exam
        typeOptions.clear();
        typeOptions.add("All");
        typeOptions.add("Assignment");
        typeOptions.add("Exam");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                typeOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void setupListeners() {
        // Search text change: show/hide clear button
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // Search on IME action or Enter key
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
            spinnerType.setSelection(0);
            btnClearSearch.setVisibility(View.GONE);
            taskList.clear();
            taskAdapter.notifyDataSetChanged();
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

        String selectedType = (String) spinnerType.getSelectedItem();
        if (selectedType != null && !selectedType.equals("All")) {
            urlBuilder.append("type=").append(selectedType.toLowerCase()).append("&");
        }

        // Trim trailing '&' or '?' if present
        String finalUrl = urlBuilder.toString();
        if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        fetchTasks(finalUrl);
    }

    private void fetchTasks(String url) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    taskList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            // Construct a Task object from JSON
                            Task task = new Task(
                                    obj.getInt("id"),
                                    obj.getString("subject_title"),
                                    obj.getInt("grade_number"),
                                    obj.getString("teacher_name"),
                                    obj.getDouble("max_score"),
                                    obj.getString("date"),
                                    obj.optString("deadline", ""),       // exams might not have a “deadline”, but keep the field
                                    obj.optString("question_file_url", ""),
                                    obj.getString("type")               // "assignment" or "exam"
                            );
                            taskList.add(task);
                        }
                        taskAdapter.notifyDataSetChanged();
                        updateEmptyState();
                        updateResultCount();
                    } catch (JSONException e) {
                        showToast("Error parsing task data");
                        updateEmptyState();
                        updateResultCount();
                    }
                },
                error -> {
                    showToast("Failed to retrieve tasks");
                    taskList.clear();
                    taskAdapter.notifyDataSetChanged();
                    updateEmptyState();
                    updateResultCount();
                }
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void updateResultCount() {
        int count = taskList.size();
        tvResultCount.setText(count + (count == 1 ? " item" : " items"));
    }

    private void updateEmptyState() {
        boolean isEmpty = taskList.isEmpty();
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerViewTasks.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvResultCount.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
