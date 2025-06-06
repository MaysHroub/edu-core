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

import android.text.TextUtils;
import com.bzu.educore.util.teacher.Constants;
import com.bzu.educore.util.teacher.ErrorHandler;
import java.util.Arrays;

public class SearchTasksFragment extends Fragment {

    // UI Components
    private EditText etSearch;
    private Button btnClearSearch;
    private Spinner spinnerSubject, spinnerGrade, spinnerType;
    private Button btnSearch, btnClearFilters;
    private RecyclerView recyclerViewTasks;
    private TextView tvResultCount;
    private LinearLayout layoutEmptyState;

    // Data
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private List<String> subjectOptions;
    private List<String> gradeOptions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_task, container, false);

        initViews(view);
        setupRecyclerView();
        populateSpinners();
        setupListeners();
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

        taskList = new ArrayList<>();
        subjectOptions = new ArrayList<>();
        gradeOptions = new ArrayList<>();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(taskList, requireActivity());
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewTasks.setAdapter(taskAdapter);
    }

    private void populateSpinners() {
        loadSpinnerData(Constants.GET_SUBJECTS_URL, spinnerSubject, Constants.ALL_SUBJECTS,
                subjectOptions, obj -> obj.getString("title"));
        loadSpinnerData(Constants.GET_GRADES_URL, spinnerGrade, Constants.ALL_GRADES,
                gradeOptions, obj -> "Grade " + obj.getString("grade_number"));
        setupTypeSpinner();
    }

    private void loadSpinnerData(String url, Spinner spinner, String defaultItem,
                                 List<String> options, JsonExtractor extractor) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    options.clear();
                    options.add(defaultItem);
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            options.add(extractor.extract(obj));
                        }
                        setupSpinnerAdapter(spinner, options);
                    } catch (JSONException e) {
                        ErrorHandler.handleParsingError(requireContext());
                    }
                },
                error -> ErrorHandler.handleNetworkError(requireContext(), error)
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void setupTypeSpinner() {
        List<String> typeOptions = Arrays.asList(Constants.ALL_TYPES, "Assignment", "Exam");
        setupSpinnerAdapter(spinnerType, typeOptions);
    }

    private void setupSpinnerAdapter(Spinner spinner, List<String> options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupListeners() {
        // Search text change
        etSearch.addTextChangedListener(new SimpleTextWatcher(
                text -> btnClearSearch.setVisibility(text.length() > 0 ? View.VISIBLE : View.GONE)
        ));

        // Search on enter/IME action
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch();
                return true;
            }
            return false;
        });

        // Button listeners
        btnClearSearch.setOnClickListener(v -> clearSearchText());
        btnSearch.setOnClickListener(v -> performSearch());
        btnClearFilters.setOnClickListener(v -> clearAllFilters());
    }

    private void clearSearchText() {
        etSearch.setText("");
        btnClearSearch.setVisibility(View.GONE);
        performSearch();
    }

    private void clearAllFilters() {
        etSearch.setText("");
        resetSpinners();
        btnClearSearch.setVisibility(View.GONE);
        clearResults();
    }

    private void resetSpinners() {
        spinnerSubject.setSelection(0);
        spinnerGrade.setSelection(0);
        spinnerType.setSelection(0);
    }

    private void clearResults() {
        taskList.clear();
        taskAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void performSearch() {
        FilterParams params = buildFilterParams();
        String url = buildSearchUrl(params);
        fetchTasks(url);
    }

    private FilterParams buildFilterParams() {
        return new FilterParams(
                etSearch.getText().toString().trim(),
                getSelectedSpinnerValue(spinnerSubject, Constants.ALL_SUBJECTS),
                getSelectedSpinnerValue(spinnerGrade, Constants.ALL_GRADES),
                getSelectedSpinnerValue(spinnerType, Constants.ALL_TYPES)
        );
    }

    private String getSelectedSpinnerValue(Spinner spinner, String defaultValue) {
        String selected = (String) spinner.getSelectedItem();
        return (selected != null && !selected.equals(defaultValue)) ? selected : null;
    }

    private String buildSearchUrl(FilterParams params) {
        StringBuilder url = new StringBuilder(Constants.SEARCH_TASKS_URL);
        List<String> queryParams = new ArrayList<>();

        if (!TextUtils.isEmpty(params.searchQuery)) {
            queryParams.add("search=" + params.searchQuery);
        }
        if (params.selectedSubject != null) {
            queryParams.add("subject=" + params.selectedSubject);
        }
        if (params.selectedGrade != null) {
            String gradeNumber = params.selectedGrade.replace("Grade ", "");
            queryParams.add("grade=" + gradeNumber);
        }
        if (params.selectedType != null) {
            queryParams.add("type=" + params.selectedType.toLowerCase());
        }

        if (!queryParams.isEmpty()) {
            url.append("?").append(TextUtils.join("&", queryParams));
        }

        return url.toString();
    }

    private void fetchTasks(String url) {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    taskList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Task task = createTaskFromJson(obj);
                            taskList.add(task);
                        }
                        taskAdapter.notifyDataSetChanged();
                        updateUI();
                    } catch (JSONException e) {
                        ErrorHandler.handleParsingError(requireContext());
                        updateUI();
                    }
                },
                error -> {
                    ErrorHandler.handleNetworkError(requireContext(), error);
                    clearResults();
                }
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private Task createTaskFromJson(JSONObject obj) throws JSONException {
        return new Task(
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
    }

    private void updateUI() {
        updateResultCount();
        updateEmptyState();
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

    // Helper classes and interfaces
    private static class FilterParams {
        final String searchQuery;
        final String selectedSubject;
        final String selectedGrade;
        final String selectedType;

        FilterParams(String searchQuery, String selectedSubject, String selectedGrade, String selectedType) {
            this.searchQuery = searchQuery;
            this.selectedSubject = selectedSubject;
            this.selectedGrade = selectedGrade;
            this.selectedType = selectedType;
        }
    }

    private interface JsonExtractor {
        String extract(JSONObject obj) throws JSONException;
    }

    private static class SimpleTextWatcher implements android.text.TextWatcher {
        private final TextChangeListener listener;

        SimpleTextWatcher(TextChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            listener.onTextChanged(s);
        }

        @Override
        public void afterTextChanged(android.text.Editable s) {}

        interface TextChangeListener {
            void onTextChanged(CharSequence text);
        }
    }
}
