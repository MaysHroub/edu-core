package com.bzu.educore.activity.teacher.ui.task_management;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.model.task.Task;
import com.bzu.educore.util.teacher.FragmentHelper;
import com.bzu.educore.util.VolleySingleton;
import com.bzu.educore.util.teacher.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDate;
import java.util.Calendar;

public class AnnounceExamFragment extends Fragment {

    private EditText examTitleEditText;
    private EditText examDescriptionEditText;
    private EditText examMaxScoreEditText;
    private Button btnSelectDate;
    private TextView subjectTextView;
    private TextView classGradeTextView;
    private Button publishButton;
    private Spinner examTypeSpinner;

    private int subjectId;
    private int classGradeId;
    private int teacherId;
    private String subjectName;
    private String classGradeName;
    private LocalDate selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
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
        examMaxScoreEditText = view.findViewById(R.id.etMaxScore);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        publishButton = view.findViewById(R.id.publishAnnouncementButton);
        examTypeSpinner = view.findViewById(R.id.examTypeSpinner);

        publishButton.setOnClickListener(v -> publishExam());
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Initialize selected date to today
        selectedDate = LocalDate.now();
        updateDateButton();

        String[] examTypes = {"Quiz","First","Second","Midterm","Final"};
        ArrayAdapter<String> examAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, examTypes);
        examAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        examTypeSpinner.setAdapter(examAdapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth());

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    updateDateButton();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateDateButton() {
        btnSelectDate.setText(selectedDate.toString());
    }

    private void getArgumentsData() {
        // Using FragmentHelper instead of BaseFragment methods
        subjectId = FragmentHelper.getIntArgument(this, "subject_id", -1);
        classGradeId = FragmentHelper.getIntArgument(this, "class_grade_id", -1);
        teacherId = FragmentHelper.getIntArgument(this, "teacher_id", -1);
        subjectName = FragmentHelper.getStringArgument(this, "subject_name", "Unknown Subject");
        classGradeName = FragmentHelper.getStringArgument(this, "class_grade_name", "Unknown Class");
    }

    private void updateUI() {
        subjectTextView.setText(subjectName);
        classGradeTextView.setText(classGradeName);
    }

    private void publishExam() {
        String titleStr = examTitleEditText.getText().toString().trim();
        String descStr = examDescriptionEditText.getText().toString().trim();
        String maxScoreStr = examMaxScoreEditText.getText().toString().trim();
        String examType = examTypeSpinner.getSelectedItem().toString();

        // Using FragmentHelper validation
        if (!FragmentHelper.validateRequiredFields(this, titleStr, descStr, maxScoreStr, examType)) {
            return;
        }

        if (!FragmentHelper.validatePositiveNumber(this, maxScoreStr, "Max score")) {
            return;
        }

        double maxScore = Double.parseDouble(maxScoreStr);

        try {
            JSONObject data = new JSONObject();
            data.put("subject_id", subjectId);
            data.put("class_id", classGradeId);
            data.put("teacher_id", teacherId);
            data.put("max_score", maxScore);
            data.put("date", selectedDate.toString());
            data.put("title", titleStr);
            data.put("exam_type", examType);
            data.put("description", descStr);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Constants.BASE_URL + "publish_exam.php",
                    data,
                    response -> {
                        FragmentHelper.showToast(this, "Exam announced successfully!");
                        FragmentHelper.navigateBack(this);
                    },
                    error -> FragmentHelper.handleNetworkError(this, error, "Failed to announce exam")
            );

            VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);

        } catch (JSONException e) {
            FragmentHelper.handleError(this, e, "Failed to create request");
        }
    }
}