package com.bzu.educore.activity.teacher.ui.student_management;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
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
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import lombok.Getter;

public class SearchTasksFragment extends Fragment {

    // Add teacher ID support
    private static final String ARG_TEACHER_ID = "teacher_id";

    // Getter method to access teacher ID if needed by other methods
    @Getter
    private int teacherId;

    private EditText etSearch;
    private Button btnClearSearch, btnSearch, btnClearFilters;
    private Spinner spinnerSubject, spinnerGrade, spinnerType;
    private RecyclerView recyclerViewTasks;
    private TextView tvResultCount;
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBar;
    private TaskAdapter taskAdapter;
    private final List<Task> taskList = new ArrayList<>();

    // Factory method to create fragment with teacher ID
    public static SearchTasksFragment newInstance(int teacherId) {
        SearchTasksFragment fragment = new SearchTasksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TEACHER_ID, teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teacherId = getArguments().getInt(ARG_TEACHER_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_task, container, false);
        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadSpinners();
        updateUI();
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
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(taskList, requireActivity());
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewTasks.setAdapter(taskAdapter);
    }

    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch();
                return true;
            }
            return false;
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            btnClearSearch.setVisibility(View.GONE);
            clearResults();
        });

        btnSearch.setOnClickListener(v -> performSearch());

        btnClearFilters.setOnClickListener(v -> {
            etSearch.setText("");
            spinnerSubject.setSelection(0);
            spinnerGrade.setSelection(0);
            spinnerType.setSelection(0);
            btnClearSearch.setVisibility(View.GONE);
            clearResults();
        });
    }

    private void loadSpinners() {
        // Load spinners with teacher-specific data if needed
        loadSpinner(spinnerSubject, UrlManager.URL_GET_SUBJECTS, "All Subjects", "title", false);
        loadSpinner(spinnerGrade, UrlManager.URL_GET_GRADES, "All Grades", "grade_number", true);

        List<String> types = Arrays.asList("All Types", "Assignment","Quiz","First","Second","Midterm","Final");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void loadSpinner(Spinner spinner, String url, String defaultItem, String jsonKey, boolean prefixGrade) {
        // If you need teacher-specific data for spinners, modify the URL here
        String finalUrl = url;
        if (teacherId != -1) {
            // Example: Add teacher ID as parameter if your API supports it
            // finalUrl = url + (url.contains("?") ? "&" : "?") + "teacher_id=" + teacherId;
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, finalUrl, null,
                response -> {
                    List<String> items = new ArrayList<>();
                    items.add(defaultItem);
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String item = obj.getString(jsonKey);
                            if (prefixGrade) item = "Grade" + item;
                            items.add(item);
                        } catch (JSONException ignored) {
                            // silently skip invalid item
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                },
                error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void performSearch() {
        String url = buildSearchUrl();
        showLoading(true);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    handleSearchResults(response);
                    showLoading(false);
                },
                error -> {
                    Toast.makeText(getContext(), "Search has failed", Toast.LENGTH_SHORT).show();
                    clearResults();
                    showLoading(false);
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private String buildSearchUrl() {
        StringBuilder url = new StringBuilder(UrlManager.URL_SEARCH_TASKS);
        List<String> params = new ArrayList<>();

        String text = etSearch.getText().toString().trim();
        if (!text.isEmpty()) params.add("search=" + text);

        String subject = getSelectedValue(spinnerSubject, "All Subjects");
        if (subject != null) params.add("subject=" + subject);

        String grade = getSelectedValue(spinnerGrade, "All Grades");
        if (grade != null) params.add("grade=" + grade.replace("Grade", ""));

        String type = getSelectedValue(spinnerType, "All Types");
        if (type != null) params.add("type=" + type.toLowerCase());

        // Add teacher ID to search parameters
        if (teacherId != -1) {
            params.add("teacher_id=" + teacherId);
        }

        if (!params.isEmpty()) url.append("?").append(TextUtils.join("&", params));
        return url.toString();
    }

    private String getSelectedValue(Spinner spinner, String defaultValue) {
        String selected = (String) spinner.getSelectedItem();
        return (selected != null && !selected.equals(defaultValue)) ? selected : null;
    }

    private void handleSearchResults(JSONArray response) {
        taskList.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                Task task = new Task(
                        obj.getInt("id"),
                        obj.getString("subject_title"),
                        obj.getInt("grade_number"),
                        obj.getString("teacher_name"),
                        obj.getDouble("max_score"),
                        obj.getString("date"),
                        obj.optString("deadline", ""),
                        obj.optString("question_file_url", ""),
                        obj.getString("type")
                );
                taskList.add(task);
            } catch (JSONException ignored) {
                // skip malformed task silently
            }
        }
        taskAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void clearResults() {
        taskList.clear();
        taskAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSearch.setEnabled(!show);
        btnSearch.setText(show ? "Searching..." : "Search");
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        int count = taskList.size();
        tvResultCount.setText(count + (count == 1 ? "Item" : "Items"));

        boolean isEmpty = taskList.isEmpty();
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerViewTasks.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvResultCount.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

}