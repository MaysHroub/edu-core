package com.bzu.educore.activity.teacher.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;

import org.json.JSONObject;

public class AssignAssignmentFragment extends BaseFragment {

    private static final int FILE_PICKER_REQUEST = 100;

    private EditText titleEditText, descEditText;
    private TextView subjectTextView, classGradeTextView;
    private DatePicker deadlinePicker;
    private Button uploadButton, publishButton;
    private String uploadedFileUrl = "";

    private int subjectId, classGradeId, teacherId;
    private String subjectName, classGradeName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assign_assignment_fragment, container, false);

        initializeViews(view);
        getArgumentsData();
        updateUI();

        return view;
    }

    private void initializeViews(View view) {
        subjectTextView = view.findViewById(R.id.tvSubjectName);
        classGradeTextView = view.findViewById(R.id.tvClassGradeName);
        titleEditText = view.findViewById(R.id.etAssignmentTitle);
        descEditText = view.findViewById(R.id.etAssignmentDesc);
        deadlinePicker = view.findViewById(R.id.datepickerDeadline);
        uploadButton = view.findViewById(R.id.btnUploadQuestion);
        publishButton = view.findViewById(R.id.btnPublishAssignment);

        uploadButton.setOnClickListener(v -> openFilePicker());
        publishButton.setOnClickListener(v -> publishAssignment());
    }

    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            subjectId = args.getInt("subject_id");
            classGradeId = args.getInt("class_grade_id");
            teacherId = args.getInt("teacher_id");
            subjectName = args.getString("subject_name", "Unknown Subject");
            classGradeName = args.getString("class_grade_name", "Unknown Class");
        }
    }

    private void updateUI() {
        subjectTextView.setText("Subject: " + subjectName);
        classGradeTextView.setText("Class: " + classGradeName);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_PICKER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            uploadedFileUrl = fileUri.toString();
            uploadButton.setText("File Selected ✓");
            showToast("File selected");
        }
    }

    private void publishAssignment() {
        String title = titleEditText.getText().toString().trim();
        String description = descEditText.getText().toString().trim();

        if (title.isEmpty()) {
            titleEditText.setError("Title required");
            return;
        }

        if (uploadedFileUrl.isEmpty()) {
            showToast("Please select a file");
            return;
        }

        String deadline = String.format("%04d-%02d-%02d",
                deadlinePicker.getYear(),
                deadlinePicker.getMonth() + 1,
                deadlinePicker.getDayOfMonth());

        try {
            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("description", description);
            data.put("subject_id", subjectId);
            data.put("class_id", classGradeId);
            data.put("teacher_id", teacherId);
            data.put("max_score", 100);  // Optional: Make dynamic if needed
            data.put("deadline", deadline);
            data.put("question_file_url", uploadedFileUrl);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://10.0.2.2/android/assignment.php",
                    data,
                    response -> {
                        showToast("Assignment published successfully!");
                        requireActivity().getSupportFragmentManager().popBackStack();
                    },
                    error -> showErrorToast("Error: " + error.getMessage())
            );

            requestQueue.add(request);

        } catch (Exception e) {
            showErrorToast("Failed to create request");
        }
    }
}
