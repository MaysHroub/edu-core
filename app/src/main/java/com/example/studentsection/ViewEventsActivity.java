package com.example.studentsection;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.R;
import com.example.studentsection.model.EventData;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;

public class ViewEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerEvents;
    private RequestQueue queue;
    private static final String URL = "http://192.168.172.3/school_system/student/get_events.php?student_id=20230001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);
        recyclerEvents = findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        queue = Volley.newRequestQueue(this);
        loadEvents();
    }

    private void loadEvents() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL,
                null,
                response -> {
                    try {
                        Gson gson = new Gson();
                        EventData[] data = gson.fromJson(response.toString(), EventData[].class);
                        List<EventData> events = Arrays.asList(data);

                        if (events.isEmpty()) {
                            Toast.makeText(this, "No upcoming events", Toast.LENGTH_SHORT).show();
                        }

                        recyclerEvents.setAdapter(new EventAdapter(events));

                    } catch (Exception e) {
                        Log.e("gson_error", e.toString());
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("events_error", error.toString());
                    Toast.makeText(this, "Connection error", Toast.LENGTH_LONG).show();
                });
        queue.add(request);
    }
}
