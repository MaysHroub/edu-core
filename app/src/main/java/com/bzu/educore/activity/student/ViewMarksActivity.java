package com.bzu.educore.activity.student;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView; //for ui
import android.widget.Toast; // for debugging
import android.util.Log; // for debugging

import com.android.volley.Request; //for http requests
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.R;
import com.bzu.educore.model.MarkData;
import com.google.gson.Gson; // to parse JSON JSON responses into Java objects


import java.util.ArrayList;

public class ViewMarksActivity extends AppCompatActivity {

    private ListView lstMarks; // the list that contains the marks meant to be shown
    private RequestQueue queue; // manages volley network requests

    // the url to get the marks from the database (get_marks script's path followed by the get method content)
    private static final String URL = "http://192.168.172.3/school_system/student/get_marks.php?student_id=20230001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_marks);
        lstMarks = findViewById(R.id.lstMarks);
        queue = Volley.newRequestQueue(this); // initializes the Volley request queue for making HTTP calls
        loadMarks();
    }

    // retrieves and displays the marks from the database
    private void loadMarks() {
        // im creating a http get request using JsonArrayRequest since the server returns a JSON array
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> { // runs if the the server does respond (for debugging)
                    Toast.makeText(this, "Response received: " + response.toString(), Toast.LENGTH_LONG).show();

                    Gson gson = new Gson();
                    MarkData[] results = gson.fromJson(response.toString(), MarkData[].class);

                    ArrayList<String> marksList = new ArrayList<>();
                    for (MarkData r : results) {
                        marksList.add(r.toString());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewMarksActivity.this,
                            android.R.layout.simple_list_item_1, marksList);
                    lstMarks.setAdapter(adapter);
                },
                error -> Log.d("marks_error", error.toString()) // runs if the server does not respond (for debugging)
        );

        queue.add(request);
    }


}


