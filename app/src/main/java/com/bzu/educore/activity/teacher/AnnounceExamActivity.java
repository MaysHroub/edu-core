package com.bzu.educore.activity.teacher;

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

public class AnnounceExamActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Spinner subjectSpinner, gradeSpinner;
    private EditText examTitleEditText, examDescriptionEditText;
    private DatePicker examDatePicker;

    private List<Integer> subjectIds = new ArrayList<>();
    private List<Integer> classIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announce_exam_activity);

        requestQueue = Volley.newRequestQueue(this);

        subjectSpinner = findViewById(R.id.subjectSpinner);
        gradeSpinner = findViewById(R.id.gradeSpinner);
        examTitleEditText = findViewById(R.id.examTitleEditText);
        examDescriptionEditText = findViewById(R.id.examDescriptionEditText);
        examDatePicker = findViewById(R.id.examDatePicker);
        Button publishButton = findViewById(R.id.publishAnnouncementButton);

        loadTimetable();
        publishButton.setOnClickListener(v -> publishExam());
    }

    private void loadTimetable() {
        String url = "http://10.0.2.2/android/timetable.php?teacher_id=1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        setupSpinners(response);
                        Toast.makeText(this, "Timetable loaded!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        showFallbackData();
                    }
                },
                error -> showFallbackData()
        );

        requestQueue.add(request);
    }

    private void setupSpinners(JSONObject response) throws Exception {
        JSONArray subjects = response.getJSONArray("subjects");
        JSONArray classes = response.getJSONArray("classes");

        List<String> subjectTitles = new ArrayList<>();
        List<String> gradeNumbers = new ArrayList<>();

        subjectIds.clear();
        classIds.clear();

        for (int i = 0; i < subjects.length(); i++) {
            JSONObject subject = subjects.getJSONObject(i);
            subjectTitles.add(subject.getString("title"));
            subjectIds.add(subject.getInt("id"));
        }

        for (int i = 0; i < classes.length(); i++) {
            JSONObject cls = classes.getJSONObject(i);
            gradeNumbers.add("Grade " + cls.getInt("grade_number"));
            classIds.add(cls.getInt("id"));
        }

        subjectSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectTitles));
        gradeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gradeNumbers));
    }

    private void showFallbackData() {
        List<String> subjects = List.of("Math", "Science");
        List<String> grades = List.of("Grade 1", "Grade 2");

        subjectIds.clear();
        subjectIds.addAll(List.of(1, 2));
        classIds.clear();
        classIds.addAll(List.of(101, 102));

        subjectSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects));
        gradeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grades));

        Toast.makeText(this, "Using fallback data", Toast.LENGTH_SHORT).show();
    }

    private void publishExam() {
        String title = examTitleEditText.getText().toString().trim();
        String description = examDescriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            examTitleEditText.setError("Title required");
            return;
        }

        int subjectPos = subjectSpinner.getSelectedItemPosition();
        int gradePos = gradeSpinner.getSelectedItemPosition();

        if (subjectPos < 0 || gradePos < 0) {
            Toast.makeText(this, "Please select subject and grade", Toast.LENGTH_SHORT).show();
            return;
        }

        int subjectId = subjectIds.get(subjectPos);
        int classId = classIds.get(gradePos);

        String date = String.format("%04d-%02d-%02d",
                examDatePicker.getYear(),
                examDatePicker.getMonth() + 1,
                examDatePicker.getDayOfMonth());

        try {
            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("description", description);
            data.put("subject_id", subjectId);
            data.put("class_id", classId);
            data.put("teacher_id", 1);
            data.put("max_score", 100);
            data.put("exam_date", date);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://10.0.2.2/android/exam.php",
                    data,
                    response -> Toast.makeText(this, "Exam announced successfully!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
            );

            requestQueue.add(request);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to create request", Toast.LENGTH_SHORT).show();
        }
    }
}