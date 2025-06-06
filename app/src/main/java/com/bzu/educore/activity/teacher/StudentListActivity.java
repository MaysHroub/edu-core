package com.bzu.educore.activity.teacher;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.adapter.teacher.UnifiedStudentAdapter;
import com.bzu.educore.model.task.StudentSubmission;
import com.bzu.educore.util.Constants;
import com.bzu.educore.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity
        implements UnifiedStudentAdapter.OnViewSubmissionClickListener,
        UnifiedStudentAdapter.OnMarkChangedListener {

    private RecyclerView recyclerViewStudents;
    private Button btnPublishMarks;
    private EditText etSearchStudent;
    private int taskId;
    private String type;
    private double maxMark;

    private UnifiedStudentAdapter studentAdapter;
    private List<StudentSubmission> studentList;
    private List<StudentSubmission> filteredStudentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        initViews();
        getIntentData();
        setupAdapter();
        setupListeners();
        setupRecyclerView();
        fetchStudentList();
    }

    private void initViews() {
        recyclerViewStudents = findViewById(R.id.recycler_view_students);
        btnPublishMarks = findViewById(R.id.btn_publish_marks);
        etSearchStudent = findViewById(R.id.et_search_student);

        studentList = new ArrayList<>();
        filteredStudentList = new ArrayList<>();
    }

    private void getIntentData() {
        taskId = getIntent().getIntExtra("taskId", -1);
        type = getIntent().getStringExtra("type");
        maxMark = getIntent().getDoubleExtra("maxMark", Constants.MAX_MARK);

        if (type == null || taskId == -1) {
            Toast.makeText(this, "Invalid data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void setupAdapter() {
        UnifiedStudentAdapter.DisplayMode displayMode = "assignment".equalsIgnoreCase(type) ?
                UnifiedStudentAdapter.DisplayMode.ASSIGNMENT_MODE :
                UnifiedStudentAdapter.DisplayMode.EXAM_MODE;

        studentAdapter = new UnifiedStudentAdapter(
                filteredStudentList,
                displayMode,
                this, // OnViewSubmissionClickListener
                this, // OnMarkChangedListener
                maxMark
        );
    }

    private void setupListeners() {
        btnPublishMarks.setOnClickListener(v -> publishMarks());
        etSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStudents.setAdapter(studentAdapter);
    }

    private void filterStudents(String searchText) {
        filteredStudentList.clear();

        if (searchText.isEmpty()) {
            // Show all students if search is empty
            filteredStudentList.addAll(studentList);
        } else {
            // Filter students by name (case-insensitive)
            String searchLower = searchText.toLowerCase().trim();
            for (StudentSubmission student : studentList) {
                if (student.getStudentName().toLowerCase().contains(searchLower)) {
                    filteredStudentList.add(student);
                }
            }
        }

        // Notify adapter of data change
        studentAdapter.notifyDataSetChanged();

        // Update publish button state based on filtered results
        updatePublishButtonState();
    }

    private void updatePublishButtonState() {
        // Enable publish button only if there are students in the filtered list
        boolean hasStudents = !filteredStudentList.isEmpty();
        btnPublishMarks.setEnabled(hasStudents);
        btnPublishMarks.setAlpha(hasStudents ? 1.0f : 0.5f);
    }

    private void publishMarks() {
        // Collect all marks from the current student list (use original list, not filtered)
        // This ensures we publish marks for all students, not just the filtered ones
        JSONArray marksArray = new JSONArray();
        boolean hasValidMarks = false;

        for (StudentSubmission student : studentList) {
            if (student.getMark() != null) {
                try {
                    JSONObject markObject = new JSONObject();
                    markObject.put("task_id", taskId);
                    markObject.put("student_id", student.getStudentId());
                    markObject.put("mark", student.getMark());
                    markObject.put("feedback", student.getFeedback() != null ? student.getFeedback() : "");

                    marksArray.put(markObject);
                    hasValidMarks = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!hasValidMarks) {
            Toast.makeText(this, "No marks to publish. Please enter marks first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the main JSON object
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("marks", marksArray);
        } catch (JSONException e) {
            Toast.makeText(this, "Error preparing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable the button to prevent multiple submissions
        btnPublishMarks.setEnabled(false);
        btnPublishMarks.setText("Publishing...");

        // Make the API call
        String url = "http://10.0.2.2/android/publish_marks.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    // Re-enable the button
                    btnPublishMarks.setEnabled(true);
                    btnPublishMarks.setText("Publish Marks");

                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");

                        if (success) {
                            Toast.makeText(this, "Marks published successfully!", Toast.LENGTH_LONG).show();
                            // Optionally refresh the data
                            fetchStudentList();
                        } else {
                            Toast.makeText(this, "Failed to publish marks: " + message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Re-enable the button
                    btnPublishMarks.setEnabled(true);
                    btnPublishMarks.setText("Publish Marks");

                    String errorMessage = "Network error";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject errorJson = new JSONObject(responseBody);
                            errorMessage = errorJson.optString("message", errorMessage);
                        } catch (Exception e) {
                            // Use default error message
                        }
                    }
                    Toast.makeText(this, "Failed to publish marks: " + errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onViewSubmissionClick(StudentSubmission submission) {
        if (submission.getSubmissionFileUrl() != null && !submission.getSubmissionFileUrl().isEmpty()) {
            Toast.makeText(this, "View URL: " + submission.getSubmissionFileUrl(), Toast.LENGTH_SHORT).show();
            // TODO: Implement file viewing logic
        } else {
            Toast.makeText(this, "No submission file available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMarkChanged(String studentId, Double newMark) {
        updateStudentMark(studentId, newMark);
    }

    private void updateStudentMark(String studentId, Double newMark) {
        // Update the mark in the local list
        for (StudentSubmission student : studentList) {
            if (student.getStudentId().equals(studentId)) {
                student.setMark(newMark);
                break;
            }
        }
    }

    private void fetchStudentList() {
        String url = type.equalsIgnoreCase("assignment")
                ? "http://10.0.2.2/android/get_assignment_students.php?taskId=" + taskId
                : "http://10.0.2.2/android/get_exam_students.php?taskId=" + taskId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    studentList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            String studentId = obj.getString("student_id");
                            String studentName = obj.getString("student_name");
                            String submissionDate = obj.optString("submission_date", "");
                            String submissionFileUrl = obj.optString("submission_file_url", "");
                            Double mark = obj.has("mark") && !obj.isNull("mark") ? obj.getDouble("mark") : null;
                            String status = obj.optString("status", "");
                            String feedback = obj.optString("feedback", "");

                            StudentSubmission item = new StudentSubmission(
                                    studentId,
                                    studentName,
                                    submissionDate,
                                    submissionFileUrl,
                                    mark,
                                    feedback,
                                    status
                            );

                            studentList.add(item);
                        }

                        // Initialize filtered list with all students
                        filteredStudentList.clear();
                        filteredStudentList.addAll(studentList);

                        studentAdapter.notifyDataSetChanged();
                        updatePublishButtonState();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing student data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to load students: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}