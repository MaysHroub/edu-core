package com.bzu.educore.activity.teacher.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.adapter.teacher.SubmissionsAdapter;
import com.bzu.educore.util.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentSubmissionsFragment extends BaseFragment {

    private RecyclerView submissionsRecyclerView;
    private TextView tvAssignmentTitle, tvAssignmentDeadline, tvAssignmentMaxScore;
    private Button saveMarksButton;
    private SubmissionsAdapter adapter;
    private int assignmentId;
    private Map<String, Double> studentMarks = new HashMap<>();

    public static AssignmentSubmissionsFragment newInstance(int assignmentId) {
        AssignmentSubmissionsFragment fragment = new AssignmentSubmissionsFragment();
        Bundle args = new Bundle();
        args.putInt("assignment_id", assignmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            assignmentId = getArguments().getInt("assignment_id", -1);
        }

        if (assignmentId == -1) {
            showErrorToast("Invalid assignment ID provided for marking.");
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.assignment_submissions_fragment, container, false);

        initializeViews(view);
        loadAssignmentSubmissions();

        return view;
    }

    private void initializeViews(View view) {
        tvAssignmentTitle = view.findViewById(R.id.tvAssignmentTitle);
        tvAssignmentDeadline = view.findViewById(R.id.tvAssignmentDeadline);
        tvAssignmentMaxScore = view.findViewById(R.id.tvAssignmentMaxScore);
        submissionsRecyclerView = view.findViewById(R.id.submissionsRecyclerView);
        saveMarksButton = view.findViewById(R.id.saveMarksButton);

        submissionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        saveMarksButton.setOnClickListener(v -> saveMarks());
    }

    private void loadAssignmentSubmissions() {
        String url = "http://10.0.2.2/android/assignment_submissions.php?assignment_id=" + assignmentId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        setupAssignmentInfo(response.getJSONObject("assignment"));
                        setupSubmissionsList(response.getJSONArray("students"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        showErrorToast("Error loading submissions: " + e.getMessage());
                        showFallbackData();
                    }
                },
                error -> {
                    error.printStackTrace();
                    showErrorToast("Failed to load submissions: " +
                            (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    showFallbackData();
                }
        );

        VolleySingleton.getInstance(requireContext())
                .addToRequestQueue(request);
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
        studentMarks.clear();

        for (int i = 0; i < students.length(); i++) {
            JSONObject student = students.getJSONObject(i);
            String studentId = student.getString("id");
            Double mark = student.has("mark") && !student.isNull("mark")
                    ? student.getDouble("mark")
                    : null;

            submissions.add(new SubmissionsAdapter.StudentSubmission(
                    studentId,
                    student.getString("name"),
                    student.optString("submission_date", null),
                    student.optString("submission_file_url", null),
                    mark,
                    student.optString("feedback", null),
                    student.getString("status")
            ));

            if (mark != null) {
                studentMarks.put(studentId, mark);
            }
        }

        adapter = new SubmissionsAdapter(
                submissions,
                this::viewSubmission,
                (studentId, mark) -> {
                    if (mark != null) {
                        studentMarks.put(studentId, mark);
                    } else {
                        studentMarks.remove(studentId);
                    }
                }
        );
        submissionsRecyclerView.setAdapter(adapter);
    }

    private void showFallbackData() {
        tvAssignmentTitle.setText("Math - Assignment (Fallback)");
        tvAssignmentDeadline.setText("Deadline: N/A");
        tvAssignmentMaxScore.setText("Max Score: 100.0");

        List<SubmissionsAdapter.StudentSubmission> submissions = List.of(
                new SubmissionsAdapter.StudentSubmission(
                        "1",
                        "John Doe (Fallback)",
                        "2024-12-25",
                        "http://example.com/submission1.pdf",
                        85.0,
                        "Good work",
                        "submitted"
                ),
                new SubmissionsAdapter.StudentSubmission(
                        "2",
                        "Jane Smith (Fallback)",
                        null,
                        null,
                        null,
                        null,
                        "pending"
                )
        );
        studentMarks.clear();
        studentMarks.put("1", 85.0);

        adapter = new SubmissionsAdapter(
                submissions,
                this::viewSubmission,
                (studentId, mark) -> {
                    if (mark != null) {
                        studentMarks.put(studentId, mark);
                    } else {
                        studentMarks.remove(studentId);
                    }
                }
        );
        submissionsRecyclerView.setAdapter(adapter);
    }

    private void viewSubmission(SubmissionsAdapter.StudentSubmission submission) {
        String fileUrl = submission.submissionFileUrl;
        if (fileUrl != null && !fileUrl.isEmpty() && !"null".equals(fileUrl)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
            try {
                startActivity(intent);
            } catch (Exception e) {
                showToast("Cannot open submission file. Ensure a browser or file viewer is installed.");
            }
        } else {
            showToast("No submission file available for this student.");
        }
    }

    private void saveMarks() {
        if (studentMarks.isEmpty()) {
            showToast("No marks to save or marks are empty.");
            return;
        }

        try {
            JSONArray marksArray = new JSONArray();
            for (Map.Entry<String, Double> entry : studentMarks.entrySet()) {
                JSONObject markData = new JSONObject();
                markData.put("task_id", assignmentId);
                markData.put("student_id", entry.getKey());
                if (entry.getValue() != null) {
                    markData.put("mark", entry.getValue());
                } else {
                    markData.put("mark", JSONObject.NULL);
                }
                marksArray.put(markData);
            }

            JSONObject data = new JSONObject();
            data.put("marks", marksArray);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://10.0.2.2/android/marks.php",
                    data,
                    response -> {
                        if (response.optBoolean("success", false)) {
                            showToast("Marks saved successfully!");
                        } else {
                            showErrorToast("Failed to save marks: " +
                                    response.optString("message", "Unknown error"));
                        }
                    },
                    error -> showErrorToast("Error saving marks: " +
                            (error.getMessage() != null ? error.getMessage() : "Unknown error"))
            );

            VolleySingleton.getInstance(requireContext())
                    .addToRequestQueue(request);

        } catch (Exception e) {
            showErrorToast("Failed to prepare mark saving request: " + e.getMessage());
        }
    }
}
