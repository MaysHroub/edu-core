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
import com.bzu.educore.util.VolleySingleton;
import com.bzu.educore.util.teacher.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class StudentListActivity extends AppCompatActivity
        implements UnifiedStudentAdapter.OnViewSubmissionClickListener,
        UnifiedStudentAdapter.OnMarkChangedListener {

    private RecyclerView recyclerViewStudents;
    private Button btnPublishMarks;
    private EditText etSearchStudent;

    private UnifiedStudentAdapter studentAdapter;
    private final List<StudentSubmission> studentList = new ArrayList<>();
    private final List<StudentSubmission> filteredStudentList = new ArrayList<>();

    private int taskId;
    private String type;
    private double maxMark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        initViews();
        getIntentData();
        setupAdapter();
        setupRecyclerView();
        setupListeners();
        fetchStudentList();
    }

    private void initViews() {
        recyclerViewStudents = findViewById(R.id.recycler_view_students);
        btnPublishMarks = findViewById(R.id.btn_publish_marks);
        etSearchStudent = findViewById(R.id.et_search_student);
    }

    private void getIntentData() {
        taskId = getIntent().getIntExtra("taskId", -1);
        type = getIntent().getStringExtra("type");
        maxMark = getIntent().getDoubleExtra("maxMark", Constants.MAX_MARK);

        if (type == null || taskId == -1) {
            Toast.makeText(this, "Invalid task data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupAdapter() {
        UnifiedStudentAdapter.DisplayMode mode = "assignment".equalsIgnoreCase(type)
                ? UnifiedStudentAdapter.DisplayMode.ASSIGNMENT_MODE
                : UnifiedStudentAdapter.DisplayMode.EXAM_MODE;

        studentAdapter = new UnifiedStudentAdapter(
                filteredStudentList,
                mode,
                this,
                this,
                maxMark
        );
    }

    private void setupRecyclerView() {
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStudents.setAdapter(studentAdapter);
    }

    private void setupListeners() {
        btnPublishMarks.setOnClickListener(v -> publishMarks());

        etSearchStudent.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }
        });
    }

    private void filterStudents(String text) {
        filteredStudentList.clear();
        String search = text.trim().toLowerCase();

        if (search.isEmpty()) {
            filteredStudentList.addAll(studentList);
        } else {
            for (StudentSubmission student : studentList) {
                if (student.getStudentName().toLowerCase().contains(search)) {
                    filteredStudentList.add(student);
                }
            }
        }

        studentAdapter.notifyDataSetChanged();
        updatePublishButtonState();
    }

    private void updatePublishButtonState() {
        boolean enabled = !filteredStudentList.isEmpty();
        btnPublishMarks.setEnabled(enabled);
        btnPublishMarks.setAlpha(enabled ? 1f : 0.5f);
    }

    private void publishMarks() {
        JSONArray marksArray = new JSONArray();
        boolean hasMarks = false;

        for (StudentSubmission student : studentList) {
            if (student.getMark() != null) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("task_id", taskId);
                    obj.put("student_id", student.getStudentId());
                    obj.put("mark", student.getMark());
                    obj.put("feedback", student.getFeedback() != null ? student.getFeedback() : "");
                    marksArray.put(obj);
                    hasMarks = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!hasMarks) {
            Toast.makeText(this, "No marks to publish.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("marks", marksArray);
        } catch (JSONException e) {
            Toast.makeText(this, "Error preparing data", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPublishMarks.setEnabled(false);
        btnPublishMarks.setText("Publishing...");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "http://10.0.2.2/android/publish_marks.php",
                requestBody,
                response -> {
                    btnPublishMarks.setEnabled(true);
                    btnPublishMarks.setText("Publish Marks");

                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                        if (success) fetchStudentList();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Response error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnPublishMarks.setEnabled(true);
                    btnPublishMarks.setText("Publish Marks");

                    String message = "Network error";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String body = new String(error.networkResponse.data, "utf-8");
                            JSONObject errorJson = new JSONObject(body);
                            message = errorJson.optString("message", message);
                        } catch (Exception ignored) {}
                    }

                    Toast.makeText(this, "Failed to publish marks: " + message, Toast.LENGTH_LONG).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
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
                            StudentSubmission student = new StudentSubmission(
                                    obj.getString("student_id"),
                                    obj.getString("student_name"),
                                    obj.optString("submission_date", ""),
                                    obj.optString("submission_file_url", ""),
                                    obj.has("mark") && !obj.isNull("mark") ? obj.getDouble("mark") : null,
                                    obj.optString("feedback", ""),
                                    obj.optString("status", "")
                            );
                            studentList.add(student);
                        }

                        filteredStudentList.clear();
                        filteredStudentList.addAll(studentList);
                        studentAdapter.notifyDataSetChanged();
                        updatePublishButtonState();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public void onViewSubmissionClick(StudentSubmission submission) {
        if (submission.getSubmissionFileUrl() != null && !submission.getSubmissionFileUrl().isEmpty()) {
            Toast.makeText(this, "View URL: " + submission.getSubmissionFileUrl(), Toast.LENGTH_SHORT).show();
            // TODO: Open file viewer or browser
        } else {
            Toast.makeText(this, "No submission file available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMarkChanged(String studentId, Double newMark) {
        for (StudentSubmission student : studentList) {
            if (student.getStudentId().equals(studentId)) {
                student.setMark(newMark);
                break;
            }
        }
    }
}
