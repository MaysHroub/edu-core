package com.bzu.educore.activity.teacher.fragment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragment extends Fragment {

    protected RequestQueue requestQueue;
    protected List<Integer> subjectIds = new ArrayList<>();
    protected List<Integer> classIds = new ArrayList<>();
    protected static final String BASE_URL = "http://10.0.2.2/android/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
    }

    protected void setupSpinners(JSONObject response, Spinner subjectSpinner, Spinner gradeSpinner) throws Exception {
        JSONArray subjects = response.getJSONArray("subjects");
        JSONArray classes = response.getJSONArray("classes");

        List<String> subjectTitles = new ArrayList<>();
        List<String> gradeNumbers = new ArrayList<>();

        subjectIds.clear();
        classIds.clear();

        // Add "All" options
        subjectTitles.add("All Subjects");
        gradeNumbers.add("All Classes");
        subjectIds.add(0);
        classIds.add(0);

        for (int i = 0; i < subjects.length(); i++) {
            JSONObject subject = subjects.getJSONObject(i);
            subjectTitles.add(subject.getString("title"));
            subjectIds.add(subject.getInt("id"));
        }

        for (int i = 0; i < classes.length(); i++) {
            JSONObject cls = classes.getJSONObject(i);
            gradeNumbers.add("Grade " + cls.getInt("grade_number") + " - Class " + cls.getInt("id"));
            classIds.add(cls.getInt("id"));
        }

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            subjectTitles
        );
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);

        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            gradeNumbers
        );
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);
    }

    protected void showFallbackSpinners(Spinner subjectSpinner, Spinner gradeSpinner) {
        List<String> subjects = new ArrayList<>();
        List<String> grades = new ArrayList<>();

        // Add "All" options
        subjects.add("All Subjects");
        grades.add("All Classes");

        // Add fallback data
        subjects.add("Math");
        subjects.add("Science");
        grades.add("Grade 10 - Class 101");
        grades.add("Grade 11 - Class 102");

        subjectIds.clear();
        subjectIds.addAll(List.of(0, 1, 2));
        classIds.clear();
        classIds.addAll(List.of(0, 101, 102));

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            subjects
        );
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);

        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            grades
        );
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);
    }

    protected void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    protected void showErrorToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    interface TimetableCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}
