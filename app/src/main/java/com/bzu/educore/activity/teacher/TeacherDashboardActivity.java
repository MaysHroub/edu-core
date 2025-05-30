package com.bzu.educore.activity.teacher;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bzu.educore.R;

public class TeacherDashboardActivity extends AppCompatActivity {

    private CardView cardCreateAssignment;
    private CardView cardAnnounceExam;
    private CardView cardPublishMarks;
    private CardView cardSubmissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_dashboard);

        // 1. Find views
        cardCreateAssignment = findViewById(R.id.cardCreateAssignment);
        cardAnnounceExam     = findViewById(R.id.cardAnnounceExam);
        cardPublishMarks     = findViewById(R.id.cardPublishMarks);
        cardSubmissions = findViewById(R.id.cardViewSubmissions);

        // 2. Set up click listeners
        cardCreateAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, AssignAssignmentActivity.class);
            startActivity(intent);
        });

        cardAnnounceExam.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, AnnounceExamActivity.class);
            startActivity(intent);
        });

        cardPublishMarks.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, PublishMarksActivity.class);
            startActivity(intent);
        });
        cardSubmissions.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashboardActivity.this, AssignmentSubmissionsActivity.class);
            startActivity(intent);
        });
    }
}
