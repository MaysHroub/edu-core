package com.bzu.educore.activity.teacher;

import android.media.MediaCodec;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.bzu.educore.R;
import com.bzu.educore.model.user.TimeTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnnounceExamActivity extends AppCompatActivity {

    Spinner subjectSpinner, gradeSpinner;
    EditText examTitleEditText, examDescriptionEditText;
    DatePicker examDatePicker;
    Button publishAnnouncementButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.announce_exam_activity);

        subjectSpinner = findViewById(R.id.subjectSpinner);
        gradeSpinner = findViewById(R.id.gradeSpinner);
        examTitleEditText = findViewById(R.id.examTitleEditText);
        examDescriptionEditText = findViewById(R.id.examDescriptionEditText);
        examDatePicker = findViewById(R.id.examDatePicker);
        publishAnnouncementButton = findViewById(R.id.publishAnnouncementButton);

        int teacherId = 1;  // Hardcoded for testing

        ApiHelper.getInstance(this).getTeacherTimeTable(teacherId, new ApiHelper.ApiCallback<List<TimeTable>>() {
            @Override
            public void onSuccess(List<TimeTable> timeTableList) {
                // Extract unique subjects and grades
                Set<String> subjects = new HashSet<>();
                Set<String> grades = new HashSet<>();
                for (TimeTable t : timeTableList) {
                    subjects.add(String.valueOf(t.getSubjectId()));
                    grades.add(String.valueOf(t.getClassId()));
                }

                runOnUiThread(() -> {
                    subjectSpinner.setAdapter(new ArrayAdapter<>(AnnounceExamActivity.this,
                            android.R.layout.simple_spinner_item, new ArrayList<>(subjects)));
                    gradeSpinner.setAdapter(new ArrayAdapter<>(AnnounceExamActivity.this,
                            android.R.layout.simple_spinner_item, new ArrayList<>(grades)));
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AnnounceExamActivity.this, "Failed to load timetable: " + error, Toast.LENGTH_LONG).show();

                // Optional fallback: hardcode some default values
                runOnUiThread(() -> {
                    subjectSpinner.setAdapter(new ArrayAdapter<>(AnnounceExamActivity.this,
                            android.R.layout.simple_spinner_item, new String[]{"Math", "Science"}));
                    gradeSpinner.setAdapter(new ArrayAdapter<>(AnnounceExamActivity.this,
                            android.R.layout.simple_spinner_item, new String[]{"Grade 1", "Grade 2"}));
                });
            }
        });

        publishAnnouncementButton.setOnClickListener(v -> publishExam());
    }

    private void publishExam() {
        String subject = subjectSpinner.getSelectedItem().toString();
        String grade = gradeSpinner.getSelectedItem().toString();
        String title = examTitleEditText.getText().toString().trim();
        String description = examDescriptionEditText.getText().toString().trim();

        int day = examDatePicker.getDayOfMonth();
        int month = examDatePicker.getMonth() + 1;
        int year = examDatePicker.getYear();
        String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);

        if (title.isEmpty()) {
            examTitleEditText.setError("Title is required");
            return;
        }

        JSONObject postData = new JSONObject();
        try {
            postData.put("subject", subject);
            postData.put("grade", grade);
            postData.put("title", title);
            postData.put("date", date);
            postData.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create request data", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiHelper.getInstance(this).announceExam(postData, new ApiHelper.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() ->
                        Toast.makeText(AnnounceExamActivity.this, "Exam announced successfully!", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(AnnounceExamActivity.this, "Error: " + error, Toast.LENGTH_LONG).show()
                );
            }
        });
    }
}
