package com.bzu.educore.activity.teacher.ui.task_management;

import android.app.Activity;
import androidx.fragment.app.Fragment;
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
import com.bzu.educore.util.InputValidator;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;
import com.bzu.educore.util.teacher.FragmentHelper;

import java.time.LocalDate;

public class AssignAssignmentFragment extends Fragment {

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
            FragmentHelper.showToast(this,"File selected");
        }
    }

    private void publishAssignment() {
        String title = titleEditText.getText().toString().trim();
        String description = descEditText.getText().toString().trim();
        String maxScoreStr = maxScoreEditText.getText().toString().trim();

        if (!InputValidator.validateEditTexts(titleEditText, descEditText, maxScoreEditText)) {
            FragmentHelper.showToast(this,"All fields are required");
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
            FragmentHelper.showToast(this,"Please select a file");
            return;
        }

        LocalDate deadlineDate = LocalDate.of(
                deadlinePicker.getYear(),
                deadlinePicker.getMonth() + 1,
                deadlinePicker.getDayOfMonth()
        );

        try {
            JSONObject data = new JSONObject();
            data.put("subject_id", subjectId);
            data.put("class_id", classGradeId);
            data.put("teacher_id", teacherId);
            data.put("type", "assignment"); // Fixed: Pass task type
            data.put("max_score", maxScore);
            data.put("title", title);
            data.put("description", description);
            data.put("question_file_url", uploadedFileUrl);
            data.put("deadline", deadlineDate.toString()); // Fixed: Format deadline properly

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    UrlManager.URL_PUBLISH_ASSIGNMENT,
                    data,
                    response -> {
                        FragmentHelper.showToast(this,"Assignment published successfully!");
                        requireActivity().getSupportFragmentManager().popBackStack();
                    },
                    error -> FragmentHelper.showErrorToast(this,"Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown"))
            );

            VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);

        } catch (JSONException e) {
            FragmentHelper.showErrorToast(this,"Failed to create request: " + e.getMessage());
        }
    }
}