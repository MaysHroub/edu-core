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
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.model.MarkData;
import com.bzu.educore.util.SharedPreferencesManager;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewMarksFragment extends Fragment {

    private ListView lstMarks;
    private Integer studentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_marks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lstMarks = view.findViewById(R.id.lstMarks);
        fetchStudentId();
    }

    private void loadMarks() {
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

    private void fetchStudentId() {
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(requireContext());
        String email = prefsManager.getUserEmail();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                UrlManager.URL_GET_STUDENT_DATA,
                requestBody,
                response -> {
                    try {
                        JSONObject student = response.getJSONObject("student");
                        studentId = student.getInt("id");
                        loadMarks();
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), "Couldn't load registrar's data", Toast.LENGTH_SHORT).show();
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
