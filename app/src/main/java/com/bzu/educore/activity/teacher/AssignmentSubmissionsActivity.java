package com.bzu.educore.activity.teacher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssignmentSubmissionsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private RecyclerView submissionsRecyclerView;
    private TextView tvAssignmentTitle, tvAssignmentDeadline, tvAssignmentMaxScore;
    private SubmissionsAdapter adapter;

    private int assignmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_submissions);

        requestQueue = Volley.newRequestQueue(this);

        // Get assignment ID from intent
        assignmentId = getIntent().getIntExtra("assignment_id", -1);
        if (assignmentId == -1) {
            Toast.makeText(this, "Invalid assignment ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadAssignmentSubmissions();
    }

    private void initializeViews() {
        tvAssignmentTitle = findViewById(R.id.tvAssignmentTitle);
        tvAssignmentDeadline = findViewById(R.id.tvAssignmentDeadline);
        tvAssignmentMaxScore = findViewById(R.id.tvAssignmentMaxScore);
        submissionsRecyclerView = findViewById(R.id.submissionsRecyclerView);

        submissionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadAssignmentSubmissions() {
        String url = "http://10.0.2.2/android/assignment_submissions.php?assignment_id=" + assignmentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        setupAssignmentInfo(response.getJSONObject("assignment"));
                        setupSubmissionsList(response.getJSONArray("students"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFallbackData();
                    }
                },
                error -> {
                    error.printStackTrace();
                    showFallbackData();
                }
        );

        requestQueue.add(request);
    }

    private void setupAssignmentInfo(JSONObject assignment) throws Exception {
        String title = assignment.getString("subject_title") + " - " + assignment.getString("type");
        String deadline = "Deadline: " + assignment.getString("deadline");
        String maxScore = "Max Score: " + assignment.getDouble("max_score");

        tvAssignmentTitle.setText(title);
        tvAssignmentDeadline.setText(deadline);
        tvAssignmentMaxScore.setText(maxScore);
    }

    private void setupSubmissionsList(JSONArray students) throws Exception {
        List<SubmissionsAdapter.StudentSubmission> submissions = new ArrayList<>();

        for (int i = 0; i < students.length(); i++) {
            JSONObject student = students.getJSONObject(i);

            submissions.add(new SubmissionsAdapter.StudentSubmission(
                    student.getString("id"),
                    student.getString("name"),
                    student.optString("submission_date", null),
                    student.optString("submission_file_url", null),
                    student.has("mark") && !student.isNull("mark") ? student.getDouble("mark") : null,
                    student.optString("feedback", null),
                    student.getString("status")
            ));
        }

        adapter = new SubmissionsAdapter(submissions, this::viewSubmission);
        submissionsRecyclerView.setAdapter(adapter);
    }

    private void showFallbackData() {
        tvAssignmentTitle.setText("Math - Assignment");
        tvAssignmentDeadline.setText("Deadline: 2024-12-31");
        tvAssignmentMaxScore.setText("Max Score: 100.0");

        List<SubmissionsAdapter.StudentSubmission> submissions = List.of(
                new SubmissionsAdapter.StudentSubmission("1", "John Doe", "2024-12-25",
                        "http://example.com/submission1.pdf", 85.0, "Good work", "submitted"),
                new SubmissionsAdapter.StudentSubmission("2", "Jane Smith", null,
                        null, null, null, "pending"),
                new SubmissionsAdapter.StudentSubmission("3", "Bob Johnson", "2025-01-02",
                        "http://example.com/submission3.pdf", null, null, "overdue")
        );

        adapter = new SubmissionsAdapter(submissions, this::viewSubmission);
        submissionsRecyclerView.setAdapter(adapter);
    }

    private void viewSubmission(SubmissionsAdapter.StudentSubmission submission) {
        if (submission.submissionFileUrl != null && !submission.submissionFileUrl.isEmpty()) {
            // Open the submission file in a browser or file viewer
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(submission.submissionFileUrl));

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Cannot open submission file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No submission file available", Toast.LENGTH_SHORT).show();
        }
    }
}