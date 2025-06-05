package com.bzu.educore.activity.teacher.fragment;

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

public class AnnounceExamFragment extends BaseFragment {

    private EditText examTitleEditText, examDescriptionEditText;
    private DatePicker examDatePicker;
    private TextView subjectTextView, classGradeTextView;

    private int subjectId, classGradeId, teacherId;
    private String subjectName, classGradeName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.announce_exam_fragment, container, false);

        initializeViews(view);
        getArgumentsData();
        updateUI();

        return view;
    }

    private void initializeViews(View view) {
        subjectTextView = view.findViewById(R.id.tvSubjectName);
        classGradeTextView = view.findViewById(R.id.tvClassGradeName);
        examTitleEditText = view.findViewById(R.id.examTitleEditText);
        examDescriptionEditText = view.findViewById(R.id.examDescriptionEditText);
        examDatePicker = view.findViewById(R.id.examDatePicker);
        Button publishButton = view.findViewById(R.id.publishAnnouncementButton);

        publishButton.setOnClickListener(v -> publishExam());
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
        subjectTextView.setText(subjectName);
        classGradeTextView.setText(classGradeName);
    }

    private void publishExam() {
        String title = examTitleEditText.getText().toString().trim();
        String description = examDescriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            examTitleEditText.setError("Title required");
            return;
        }

        String date = String.format("%04d-%02d-%02d",
                examDatePicker.getYear(),
                examDatePicker.getMonth() + 1,
                examDatePicker.getDayOfMonth());

        try {
            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("description", description);
            data.put("subject_id", subjectId);
            data.put("class_id", classGradeId);
            data.put("teacher_id", teacherId);
            data.put("max_score", 100);
            data.put("exam_date", date);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://10.0.2.2/android/exam.php",
                    data,
                    response -> {
                        showToast("Exam announced successfully!");
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
