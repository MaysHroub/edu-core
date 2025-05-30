package com.bzu.educore.activity.teacher;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PublishMarksActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Spinner subjectSpinner, gradeSpinner, taskSpinner; // Changed from examSpinner to taskSpinner
    private RecyclerView studentsRecyclerView;
    private Button publishButton;

    private List<Integer> subjectIds = new ArrayList<>();
    private List<Integer> classIds = new ArrayList<>();
    private List<Integer> taskIds = new ArrayList<>(); // Changed from examIds to taskIds
    private StudentMarksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_marks_activity);

        requestQueue = Volley.newRequestQueue(this);

        subjectSpinner = findViewById(R.id.subjectSpinner);
        gradeSpinner = findViewById(R.id.gradeSpinner);
        taskSpinner = findViewById(R.id.examSpinner); // Keep the same ID for layout compatibility
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        publishButton = findViewById(R.id.publishButton);

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTimetable();

        // Load tasks when subject/grade changes
        subjectSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadTasks(); // Changed from loadExams to loadTasks
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        gradeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                loadTasks(); // Changed from loadExams to loadTasks
                loadStudents();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        publishButton.setOnClickListener(v -> publishMarks());
    }

    private void loadTimetable() {
        String url = "http://10.0.2.2/android/timetable.php?teacher_id=1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        setupSpinners(response);
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
    }

    private void loadTasks() { // Renamed from loadExams
        int subjectPos = subjectSpinner.getSelectedItemPosition();
        int gradePos = gradeSpinner.getSelectedItemPosition();

        if (subjectPos < 0 || gradePos < 0 ||
                subjectPos >= subjectIds.size() || gradePos >= classIds.size()) {
            return;
        }

        int subjectId = subjectIds.get(subjectPos);
        int classId = classIds.get(gradePos);

        // Changed URL from exams.php to tasks.php
        String url = "http://10.0.2.2/android/tasks.php?subject_id=" + subjectId + "&class_id=" + classId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<String> taskTitles = new ArrayList<>();
                        taskIds.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject task = response.getJSONObject(i);
                            taskTitles.add(task.getString("title"));
                            taskIds.add(task.getInt("id"));
                        }

                        if (taskTitles.isEmpty()) {
                            taskTitles.add("No tasks available");
                            taskIds.add(-1); // Invalid ID to prevent selection
                        }

                        taskSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, taskTitles));
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFallbackTasks();
                    }
                },
                error -> {
                    error.printStackTrace();
                    showFallbackTasks();
                }
        );

        requestQueue.add(request);
    }

    private void showFallbackTasks() { // Renamed from showFallbackExams
        List<String> tasks = List.of("Midterm Exam", "Final Exam", "Assignment 1");
        taskIds.clear();
        taskIds.addAll(List.of(1, 2, 3));

        taskSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tasks));
    }

    private void loadStudents() {
        int gradePos = gradeSpinner.getSelectedItemPosition();
        if (gradePos < 0 || gradePos >= classIds.size()) return;

        int classId = classIds.get(gradePos);
        String url = "http://10.0.2.2/android/students.php?class_id=" + classId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<StudentMarksAdapter.Student> students = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject student = response.getJSONObject(i);
                            students.add(new StudentMarksAdapter.Student(
                                    student.getString("id"),
                                    student.getString("name")
                            ));
                        }

                        adapter = new StudentMarksAdapter(students);
                        studentsRecyclerView.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showFallbackStudents();
                    }
                },
                error -> {
                    error.printStackTrace();
                    showFallbackStudents();
                }
        );

        requestQueue.add(request);
    }

    private void showFallbackStudents() {
        List<StudentMarksAdapter.Student> students = List.of(
                new StudentMarksAdapter.Student("1", "John Doe"),
                new StudentMarksAdapter.Student("2", "Jane Smith"),
                new StudentMarksAdapter.Student("3", "Bob Johnson")
        );

        adapter = new StudentMarksAdapter(students);
        studentsRecyclerView.setAdapter(adapter);
    }

    private void publishMarks() {
        if (adapter == null) {
            Toast.makeText(this, "No students loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        int taskPos = taskSpinner.getSelectedItemPosition(); // Changed from examPos
        if (taskPos < 0 || taskPos >= taskIds.size()) {
            Toast.makeText(this, "Please select a task", Toast.LENGTH_SHORT).show();
            return;
        }

        int taskId = taskIds.get(taskPos);

        if (taskId == -1) {
            Toast.makeText(this, "No valid task selected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONArray marksArray = new JSONArray();

            for (StudentMarksAdapter.Student student : adapter.getStudents()) {
                JSONObject markData = new JSONObject();
                markData.put("task_id", taskId); // Using taskId instead of examId
                markData.put("student_id", student.id);
                markData.put("mark", student.mark);
                marksArray.put(markData);
            }

            JSONObject data = new JSONObject();
            data.put("marks", marksArray);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://10.0.2.2/android/publish_marks.php",
                    data,
                    response -> Toast.makeText(this, "Marks published successfully!", Toast.LENGTH_SHORT).show(),
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
            );

            requestQueue.add(request);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create request", Toast.LENGTH_SHORT).show();
        }
    }
}