package com.bzu.educore.activity.registrar.ui.schedules;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.adapter.ClassAdapter;
import com.bzu.educore.model.Class;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SchedulesFragment extends Fragment implements ClassAdapter.OnClassClickListener {
    private static final String TAG = "SchedulesFragment";
    private RecyclerView recyclerView;
    private ClassAdapter adapter;
    private List<Class> classes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedules, container, false);
        
        recyclerView = root.findViewById(R.id.recycler_classes);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        classes = new ArrayList<>();
        adapter = new ClassAdapter(classes, this);
        recyclerView.setAdapter(adapter);
        
        Log.d(TAG, "Fragment created, fetching classes...");
        fetchClasses();
        
        return root;
    }

    private void fetchClasses() {
        String url = UrlManager.GET_ALL_CLASSES_URL;
        Log.d(TAG, "Fetching classes from URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, "Received response: " + response.toString());
                    try {
                        JSONArray classesArray = response.getJSONArray("classes");
                        handleResponse(classesArray);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error extracting classes array", e);
                        Toast.makeText(requireContext(), "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching classes", error);
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error status code: " + error.networkResponse.statusCode);
                    }
                    Toast.makeText(requireContext(), "Error loading classes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
        Log.d(TAG, "Request added to queue");
    }

    private void handleResponse(JSONArray response) {
        try {
            Log.d(TAG, "Handling response...");
            List<Class> newClasses = new ArrayList<>();

            for (int i = 0; i < response.length(); i++) {
                JSONObject classObj = response.getJSONObject(i);
                Class classItem = new Class(
                        classObj.getString("id"),
                        classObj.getString("grade_number"),
                        classObj.getString("section"),
                        classObj.optString("homeroom_teacher_fname", ""),
                        classObj.optString("homeroom_teacher_lname", "")
                );
                newClasses.add(classItem);
                Log.d(TAG, "Added class: " + classItem.getGradeSection());
            }

            adapter.updateClasses(newClasses);
            Log.d(TAG, "Adapter updated with " + newClasses.size() + " classes");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing response", e);
            Toast.makeText(requireContext(), "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClassClick(Class classItem) {
        // TODO: Show timetable for the selected class
        Toast.makeText(requireContext(), "Selected: " + classItem.getGradeSection(), Toast.LENGTH_SHORT).show();
    }
} 