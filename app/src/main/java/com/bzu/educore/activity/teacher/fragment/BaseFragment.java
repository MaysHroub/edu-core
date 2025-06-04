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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(requireContext());
    }

    protected void loadTimetable(TimetableCallback callback) {
        String url = "http://10.0.2.2/android/timetable.php?teacher_id=1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        callback.onSuccess(response);
                    } catch (Exception e) {
                        callback.onError("Failed to parse timetable data");
                    }
                },
                error -> callback.onError("Failed to load timetable")
        );

        requestQueue.add(request);
    }

    protected void setupSpinners(JSONObject response, Spinner subjectSpinner, Spinner gradeSpinner) throws Exception {
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

        subjectSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjectTitles));
        gradeSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, gradeNumbers));
    }

    protected void showFallbackSpinners(Spinner subjectSpinner, Spinner gradeSpinner) {
        List<String> subjects = List.of("Math", "Science");
        List<String> grades = List.of("Grade 1", "Grade 2");

        subjectIds.clear();
        subjectIds.addAll(List.of(1, 2));
        classIds.clear();
        classIds.addAll(List.of(101, 102));

        subjectSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjects));
        gradeSpinner.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, grades));
    }

    protected void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showErrorToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    interface TimetableCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }
}
