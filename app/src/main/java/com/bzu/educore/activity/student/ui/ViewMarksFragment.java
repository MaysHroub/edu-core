package com.bzu.educore.activity.student.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bzu.educore.R;
import com.example.studentsection.model.MarkData;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

public class ViewMarksFragment extends Fragment {

    private ListView lstMarks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_marks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lstMarks = view.findViewById(R.id.lstMarks);
        loadMarks();
    }

    private void loadMarks() {
        String studentId = "S001"; // TODO: Replace with SharedPreferences later if needed
        String url = UrlManager.URL_GET_MARKS + "?student_id=" + studentId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                this::handleMarksResponse,
                error -> {
                    Toast.makeText(requireContext(), "Error loading marks: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("marks_error", "Volley error: " + error.toString());
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }


    private void handleMarksResponse(JSONArray response) {
        try {
            Log.d("MarksResponse", "Raw JSON: " + response.toString());
            Toast.makeText(requireContext(), "Marks data loaded", Toast.LENGTH_SHORT).show();

            Gson gson = new Gson();
            MarkData[] results = gson.fromJson(response.toString(), MarkData[].class);

            ArrayList<String> marksList = new ArrayList<>();
            for (MarkData r : results) {
                marksList.add(r.toString());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1, marksList);
            lstMarks.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error parsing marks", Toast.LENGTH_SHORT).show();
            Log.e("marks_error", "Parsing error: " + e.getMessage());
        }
    }

}
