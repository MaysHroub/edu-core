package com.example.studentsection;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.RequestQueue;
import com.bzu.educore.R;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.example.studentsection.model.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewEventsActivity extends AppCompatActivity {
    private RecyclerView recyclerEvents;
    private List<Event> events;
    private RequestQueue queue;
    private static final String URL = "http://10.0.2.2/edu-core/get_events.php?student_id=S001";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        recyclerEvents = findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        events = new ArrayList<>();

        loadEvents();
    }

    private void loadEvents() {
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                UrlManager.URL_GET_EVENTS,
                null,
                this::handleEventsResponse,
                this::handleError
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void handleEventsResponse(JSONArray response) {
        try {
            events.clear();
            for (int i = 0; i < response.length(); i++) {
                JSONObject eventObj = response.getJSONObject(i);
                Event event = new Event(
                        eventObj.getString("title"),
                        eventObj.getString("description"),
                        eventObj.getString("date"),
                        eventObj.getString("location")
                );
                events.add(event);
            }
            recyclerEvents.setAdapter(new EventAdapter(events));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing events data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(VolleyError error) {
        Toast.makeText(this, "Error loading events: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
