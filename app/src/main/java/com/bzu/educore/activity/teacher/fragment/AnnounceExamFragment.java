package com.bzu.educore.activity.teacher.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;

import org.json.JSONObject;

public class AnnounceExamFragment extends BaseFragment {

    private Spinner subjectSpinner, gradeSpinner;
    private EditText examTitleEditText, examDescriptionEditText;
    private DatePicker examDatePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.announce_exam_fragment, container, false);

        initializeViews(view);
        loadTimetable(new TimetableCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    setupSpinners(response, subjectSpinner, gradeSpinner);
                    showToast("Timetable loaded!");
                } catch (Exception e) {
                    showFallbackSpinners(subjectSpinner, gradeSpinner);
                }
            }

            @Override
            public void onError(String error) {
                showFallbackSpinners(subjectSpinner, gradeSpinner);
                showToast("Using fallback data");
            }
        });

        return view;
    }

    private void initializeViews(View view) {
        subjectSpinner = view.findViewById(R.id.subjectSpinner);
        gradeSpinner = view.findViewById(R.id.gradeSpinner);
        examTitleEditText = view.findViewById(R.id.examTitleEditText);
        examDescriptionEditText = view.findViewById(R.id.examDescriptionEditText);
        examDatePicker = view.findViewById(R.id.examDatePicker);
        Button publishButton = view.findViewById(R.id.publishAnnouncementButton);

        publishButton.setOnClickListener(v -> publishExam());
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
            showToast("Please select subject and grade");
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
