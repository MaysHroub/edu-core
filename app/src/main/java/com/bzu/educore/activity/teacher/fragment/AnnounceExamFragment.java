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
import com.bzu.educore.model.task.Task;
import com.bzu.educore.util.VolleySingleton;

import org.json.JSONObject;

import java.time.LocalDate;

public class AnnounceExamFragment extends BaseFragment {

    private EditText examTitleEditText, examDescriptionEditText;
    private DatePicker examDatePicker;
    private TextView subjectTextView, classGradeTextView;

    private int subjectId, classGradeId, teacherId;
    private String subjectName, classGradeName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

    private void publishExam() {
        // 1. Read UI values
        String titleStr = examTitleEditText.getText().toString().trim();
        String descStr = examDescriptionEditText.getText().toString().trim();

        if (titleStr.isEmpty()) {
            examTitleEditText.setError("Title required");
            return;
        }

        LocalDate selectedDate = LocalDate.of(
                examDatePicker.getYear(),
                examDatePicker.getMonth() + 1,
                examDatePicker.getDayOfMonth()
        );

        // 2. Build Task POJO (ID is null because this is a new record)
        Task newTask = new Task(
                null,               // id
                subjectId,          // subjectId
                classGradeId,       // sectionId
                selectedDate,       // date
                teacherId,          // teacherId
                "exam",
                100                 // maxScore
        );
        try {
            JSONObject data = newTask.toJson();
            data.put("title", titleStr);
            data.put("description", descStr);
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

            VolleySingleton.getInstance(requireContext())
                    .addToRequestQueue(request);

        } catch (Exception e) {
            showErrorToast("Failed to create request");
        }
    }
}
