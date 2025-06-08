package com.bzu.educore.activity.teacher.fragment.task_management;

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
import com.bzu.educore.activity.teacher.fragment.BaseFragment;
import com.bzu.educore.model.task.Assignment;
import com.bzu.educore.util.InputValidator;
import com.bzu.educore.util.VolleySingleton;
import com.bzu.educore.util.teacher.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

public class AssignAssignmentFragment extends BaseFragment {

    private static final int FILE_PICKER_REQUEST = 100;

    private EditText titleEditText, descEditText, maxScoreEditText;
    private TextView subjectTextView, classGradeTextView;
    private DatePicker deadlinePicker;
    private Button uploadButton, publishButton;
    private String uploadedFileUrl = "";

    private int subjectId, classGradeId, teacherId;
    private String subjectName, classGradeName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
        maxScoreEditText = view.findViewById(R.id.etMaxScore);
        deadlinePicker = view.findViewById(R.id.datepickerDeadline);
        uploadButton = view.findViewById(R.id.btnUploadQuestion);
        publishButton = view.findViewById(R.id.btnPublishAssignment);

        uploadButton.setOnClickListener(v -> openFilePicker());
        publishButton.setOnClickListener(v -> publishAssignment());
    }

    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            subjectId = args.getInt("subject_id", -1);
            classGradeId = args.getInt("class_grade_id", -1);
            teacherId = args.getInt("teacher_id", -1);
            subjectName = args.getString("subject_name", "Unknown Subject");
            classGradeName = args.getString("class_grade_name", "Unknown Class");
        }
    }

    private void updateUI() {
        subjectTextView.setText(subjectName);
        classGradeTextView.setText(classGradeName);
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

        if (requestCode == FILE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK
                && data != null) {
            Uri fileUri = data.getData();
            uploadedFileUrl = fileUri.toString();
            uploadButton.setText("File Selected ✓");
            showToast("File selected");
        }
    }

    private void publishAssignment() {
        String title = titleEditText.getText().toString().trim();
        String description = descEditText.getText().toString().trim();
        String maxScoreStr = maxScoreEditText.getText().toString().trim();

        if (!InputValidator.validateEditTexts(titleEditText, descEditText, maxScoreEditText)) {
            showToast("All fields are required");
            return;
        }

        double maxScore;
        try {
            maxScore = Double.parseDouble(maxScoreStr);
            if (maxScore <= 0) {
                maxScoreEditText.setError("Must be > 0");
                return;
            }
        } catch (NumberFormatException e) {
            maxScoreEditText.setError("Invalid number");
            return;
        }

        if (uploadedFileUrl.isEmpty()) {
            showToast("Please select a file");
            return;
        }

        LocalDate deadlineDate = LocalDate.of(
                deadlinePicker.getYear(),
                deadlinePicker.getMonth() + 1,
                deadlinePicker.getDayOfMonth()
        );

        Assignment assignment = new Assignment(
                null,
                subjectId,
                classGradeId,
                null,
                teacherId,
                "assignment",
                uploadedFileUrl,
                deadlineDate,
                null,
                maxScore
        );

        try {
            JSONObject data = assignment.toJson();
            data.put("title", title);
            data.put("description", description);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Constants.BASE_URL + "assignment.php",
                    data,
                    response -> {
                        showToast("Assignment published successfully!");
                        requireActivity().getSupportFragmentManager().popBackStack();
                    },
                    error -> showErrorToast("Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown"))
            );

            VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);

        } catch (JSONException e) {
            showErrorToast("Failed to create request: " + e.getMessage());
        }
    }
}
