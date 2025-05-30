package com.bzu.educore.activity.teacher;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssignAssignmentActivity extends AppCompatActivity {

    private static final int FILE_PICKER_REQUEST = 100;

    private RequestQueue requestQueue;
    private Spinner sectionSpinner;
    private EditText titleEditText, descEditText;
    private DatePicker deadlinePicker;
    private Button uploadButton, publishButton;

    private List<Integer> classIds = new ArrayList<>();
    private String uploadedFileUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_assignment_activity);

        requestQueue = Volley.newRequestQueue(this);

        sectionSpinner = findViewById(R.id.spinnerSectionAssign);
        titleEditText = findViewById(R.id.etAssignmentTitle);
        descEditText = findViewById(R.id.etAssignmentDesc);
        deadlinePicker = findViewById(R.id.datepickerDeadline);
        uploadButton = findViewById(R.id.btnUploadQuestion);
        publishButton = findViewById(R.id.btnPublishAssignment);

        loadSections();

        uploadButton.setOnClickListener(v -> openFilePicker());
        publishButton.setOnClickListener(v -> publishAssignment());
    }

    private void loadSections() {
        String url = "http://10.0.2.2/android/timetable.php?teacher_id=1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        setupSectionSpinner(response);
                    } catch (Exception e) {
                        showFallbackSections();
                    }
                },
                error -> showFallbackSections()
        );

        requestQueue.add(request);
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

        sectionSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sectionNames));
    }

    private void showFallbackSections() {
        List<String> sections = List.of("Grade 1 - Class 101", "Grade 2 - Class 102");
        classIds.clear();
        classIds.addAll(List.of(101, 102));

        sectionSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sections));
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            uploadedFileUrl = fileUri.toString(); // In real app, upload to server and get URL
            uploadButton.setText("File Selected ✓");
            Toast.makeText(this, "File selected", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
            return;
        }

        int sectionPos = sectionSpinner.getSelectedItemPosition();
        if (sectionPos < 0) {
            Toast.makeText(this, "Please select a section", Toast.LENGTH_SHORT).show();
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
            data.put("subject_id", 1); // You may want to add subject selection
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
                        Toast.makeText(this, "Assignment published successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
            );

            requestQueue.add(request);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to create request", Toast.LENGTH_SHORT).show();
        }
    }
}
