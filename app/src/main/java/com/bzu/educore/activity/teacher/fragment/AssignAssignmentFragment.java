package com.bzu.educore.activity.teacher.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AssignAssignmentFragment extends BaseFragment {

    private static final int FILE_PICKER_REQUEST = 100;

    private Spinner sectionSpinner;
    private EditText titleEditText, descEditText;
    private DatePicker deadlinePicker;
    private Button uploadButton, publishButton;
    private String uploadedFileUrl = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assign_assignment_fragment, container, false);

        initializeViews(view);
        loadSections();

        return view;
    }

    private void initializeViews(View view) {
        sectionSpinner = view.findViewById(R.id.spinnerSectionAssign);
        titleEditText = view.findViewById(R.id.etAssignmentTitle);
        descEditText = view.findViewById(R.id.etAssignmentDesc);
        deadlinePicker = view.findViewById(R.id.datepickerDeadline);
        uploadButton = view.findViewById(R.id.btnUploadQuestion);
        publishButton = view.findViewById(R.id.btnPublishAssignment);

        uploadButton.setOnClickListener(v -> openFilePicker());
        publishButton.setOnClickListener(v -> publishAssignment());
    }

    private void loadSections() {
        loadTimetable(new TimetableCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    setupSectionSpinner(response);
                } catch (Exception e) {
                    showFallbackSections();
                }
            }

            @Override
            public void onError(String error) {
                showFallbackSections();
            }
        });
    }

    private void setupSectionSpinner(JSONObject response) throws Exception {
        JSONArray classes = response.getJSONArray("classes");

        List<String> sectionNames = new ArrayList<>();
        classIds.clear();

        for (int i = 0; i < classes.length(); i++) {
            JSONObject cls = classes.getJSONObject(i);
            sectionNames.add("Grade " + cls.getInt("grade_number") + " - Class " + cls.getInt("id"));
            classIds.add(cls.getInt("id"));
        }

        sectionSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sectionNames));
    }

    private void showFallbackSections() {
        List<String> sections = List.of("Grade 1 - Class 101", "Grade 2 - Class 102");
        classIds.clear();
        classIds.addAll(List.of(101, 102));

        sectionSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sections));
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

        int sectionPos = sectionSpinner.getSelectedItemPosition();
        if (sectionPos < 0) {
            showToast("Please select a section");
            return;
        }

        int classId = classIds.get(sectionPos);

        String deadline = String.format("%04d-%02d-%02d",
                deadlinePicker.getYear(),
                deadlinePicker.getMonth() + 1,
                deadlinePicker.getDayOfMonth());

        try {
            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("description", description);
            data.put("subject_id", 1);
            data.put("class_id", classId);
            data.put("teacher_id", 1);
            data.put("max_score", 100);
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
