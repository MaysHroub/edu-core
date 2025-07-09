package com.bzu.educore.activity.student.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.util.SharedPreferencesManager;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.example.studentsection.adapter.EventAdapter;
import com.bzu.educore.model.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewEventsFragment extends Fragment {

    private RecyclerView recyclerEvents;
    private List<Event> events;
    private Integer studentId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerEvents = view.findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        events = new ArrayList<>();

        fetchStudentId();
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
                        loadEvents();
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

    private void loadEvents() {
        String urlWithId = UrlManager.URL_GET_EVENTS + "?student_id=" + studentId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                urlWithId,
                null,
                this::handleEventsResponse,
                this::handleError
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }


    private void handleEventsResponse(JSONArray response) {
        try {
            events.clear();
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);

                String title = obj.getString("subject_title") + " (" + obj.getString("type") + ")";
                String description = obj.optString("question_file_url", "No file");
                String deadline = obj.getString("deadline");
                String date = obj.getString("date");
                String location = "School Campus"; // Static or optional info

                if (deadline.equals("null"))
                    events.add(new Event(title, description, date, location));
                else
                    events.add(new Event(title, description, deadline, location));
            }

            recyclerEvents.setAdapter(new EventAdapter(events));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error parsing events data", Toast.LENGTH_SHORT).show();
        }
    }


    private void handleError(VolleyError error) {
        Toast.makeText(requireContext(), "Error loading events: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
