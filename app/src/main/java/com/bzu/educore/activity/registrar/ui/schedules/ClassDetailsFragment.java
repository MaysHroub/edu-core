package com.bzu.educore.activity.registrar.ui.schedules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.widget.Toast;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.R;
import com.bzu.educore.model.Class;
import com.bzu.educore.util.UrlManager;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ClassDetailsFragment extends Fragment {
    private static final String ARG_CLASS = "classData";
    private static final String TAG = "ClassDetailsFragment";
    private TextView textClassDetails;
    private TableLayout timetableTable;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textClassDetails = view.findViewById(R.id.text_class_details);
        timetableTable = view.findViewById(R.id.timetable_table);
        requestQueue = Volley.newRequestQueue(requireContext());

        // Set TableLayout to stretch all columns
        timetableTable.setStretchAllColumns(true);

        // Get class data from arguments
        Bundle args = getArguments();
        if (args != null) {
            Class classData = (Class) args.getSerializable(ARG_CLASS);
            if (classData != null) {
                textClassDetails.setText(String.format("Grade %s Section %s", 
                    classData.getGradeNumber(), classData.getSection()));
                
                // Fetch timetable data
                fetchTimetable(classData.getId());
            }
        }
    }

    private void fetchTimetable(String classId) {
        String url = UrlManager.GET_CLASS_TIMETABLE_URL;
        
        Map<String, String> params = new HashMap<>();
        params.put("class_id", classId);

        Log.d(TAG, "Fetching timetable for class ID: " + classId);
        Log.d(TAG, "Request URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "Received response: " + response.toString());
                        if (response.getString("status").equals("success")) {
                            JSONObject timetable = response.getJSONObject("timetable");
                            Log.d(TAG, "Timetable data: " + timetable.toString());
                            displayTimetable(timetable);
                        } else {
                            Log.e(TAG, "Error in response: " + response.toString());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing timetable data", e);
                        Toast.makeText(requireContext(), "Error parsing timetable data", Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error fetching timetable: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error response code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Error response data: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(requireContext(), "Error fetching timetable", Toast.LENGTH_SHORT).show();
                }
            });

        requestQueue.add(request);
    }

    private void displayTimetable(JSONObject timetable) throws JSONException {
        Log.d(TAG, "Starting to display timetable");
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
        String[] timeSlots = {"8:00-9:00", "9:00-10:00", "10:00-11:00", "11:00-11:30", "11:30-12:30", "12:30-13:30"};

        // Set fixed height for all rows
        int rowHeight = 200; // A more reasonable height

        // Create header row
        TableRow headerRow = new TableRow(requireContext());
        headerRow.setBackgroundColor(Color.parseColor("#2196F3"));
        headerRow.setLayoutParams(new TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            rowHeight
        ));
        
        // Add empty cell for time column
        TextView emptyCell = new TextView(requireContext());
        emptyCell.setText("");
        emptyCell.setPadding(15, 15, 15, 15); // Consistent padding
        emptyCell.setTextColor(Color.WHITE);
        emptyCell.setTextSize(14);
        emptyCell.setMinWidth(180); // Adjusted min width
        emptyCell.setGravity(Gravity.CENTER);
        emptyCell.setSingleLine(false); // Ensure multi-line text
        emptyCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        headerRow.addView(emptyCell);

        // Add day headers
        for (String day : days) {
            TextView dayHeader = new TextView(requireContext());
            dayHeader.setText(day);
            dayHeader.setPadding(15, 15, 15, 15); // Consistent padding
            dayHeader.setTextColor(Color.WHITE);
            dayHeader.setTextSize(14);
            dayHeader.setGravity(Gravity.CENTER);
            dayHeader.setSingleLine(false); // Ensure multi-line text
            dayHeader.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            headerRow.addView(dayHeader);
        }
        timetableTable.addView(headerRow);

        // Create rows for each time slot
        for (String timeSlot : timeSlots) {
            TableRow row = new TableRow(requireContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                rowHeight
            ));
            row.setBackgroundColor(Color.parseColor("#DDDDDD")); // Light grey for row background (border effect)

            // Add time slot cell
            TextView timeCell = new TextView(requireContext());
            timeCell.setText(timeSlot);
            timeCell.setPadding(15, 15, 15, 15); // Consistent padding
            timeCell.setMinWidth(180); // Adjusted min width
            timeCell.setGravity(Gravity.CENTER);
            timeCell.setSingleLine(false); // Ensure multi-line text
            timeCell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            
            // Set different color for break time
            if (timeSlot.equals("11:00-11:30")) {
                timeCell.setBackgroundColor(Color.parseColor("#FFE0B2")); // Light orange for break
            } else {
                timeCell.setBackgroundColor(Color.parseColor("#E3F2FD"));
            }
            timeCell.setTextSize(14);
            row.addView(timeCell);

            // Add cells for each day
            for (String day : days) {
                TextView cell = new TextView(requireContext());
                cell.setPadding(15, 15, 15, 15); // Consistent padding
                cell.setGravity(Gravity.CENTER);
                cell.setSingleLine(false); // Ensure multi-line text
                cell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                
                // Set different color for break time
                if (timeSlot.equals("11:00-11:30")) {
                    cell.setBackgroundColor(Color.parseColor("#FFE0B2")); // Light orange for break
                    cell.setText("Break");
                } else {
                    cell.setBackgroundColor(Color.WHITE);
                }
                cell.setTextSize(14);
                
                if (timetable.has(day) && !timeSlot.equals("11:00-11:30")) {
                    JSONArray daySchedule = timetable.getJSONArray(day);
                    Log.d(TAG, "Processing " + day + " schedule: " + daySchedule.toString());
                    
                    boolean foundMatch = false;
                    for (int i = 0; i < daySchedule.length(); i++) {
                        JSONObject period = daySchedule.getJSONObject(i);
                        String startTime = period.getString("start_time").substring(0, 5);
                        String endTime = period.getString("end_time").substring(0, 5);
                        
                        // Extract hour from time slot (e.g., "8:00-9:00" -> "8")
                        String slotHour = timeSlot.split(":")[0];
                        String periodHour = startTime.split(":")[0];
                        
                        // Remove leading zero if present
                        if (periodHour.startsWith("0")) {
                            periodHour = periodHour.substring(1);
                        }
                        
                        Log.d(TAG, String.format("Comparing - Time Slot: %s (hour: %s) with Period: %s-%s (hour: %s)", 
                            timeSlot, slotHour, startTime, endTime, periodHour));
                        
                        if (slotHour.equals(periodHour)) {
                            String subject = period.getString("subject");
                            String teacher = period.getString("teacher");
                            cell.setText(subject + "\n" + teacher);
                            cell.setBackgroundColor(Color.parseColor("#E8F5E9"));
                            Log.d(TAG, "MATCH FOUND! Adding subject: " + subject + " with teacher: " + teacher);
                            foundMatch = true;
                            break;
                        }
                    }
                    
                    if (!foundMatch) {
                        Log.d(TAG, "No match found for " + day + " at time slot " + timeSlot);
                    }
                } else {
                    Log.d(TAG, "No schedule found for " + day);
                }
                row.addView(cell);
            }
            timetableTable.addView(row);
        }
        Log.d(TAG, "Finished displaying timetable");
    }
}