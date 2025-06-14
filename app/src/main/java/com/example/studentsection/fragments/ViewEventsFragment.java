package com.example.studentsection.fragments;

import android.os.Bundle;
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
import com.bzu.educore.R;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.example.studentsection.adapter.EventAdapter;
import com.example.studentsection.model.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewEventsFragment extends Fragment {

    private RecyclerView recyclerEvents;
    private List<Event> events;

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

        loadEvents();
    }

    private void loadEvents() {
        String studentId = "S001"; // Replace with actual logged-in student ID
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
                String date = obj.has("deadline") ? obj.getString("deadline") : obj.getString("date");
                String location = "School Campus"; // Static or optional info

                events.add(new Event(title, description, date, location));
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
