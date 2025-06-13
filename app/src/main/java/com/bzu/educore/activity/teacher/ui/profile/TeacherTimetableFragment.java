package com.bzu.educore.activity.teacher.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class TeacherTimetableFragment extends Fragment {

    private static final String TAG = "TeacherTimetableFragment";
    private static final String ARG_TEACHER_ID = "teacher_id";

    private ProgressBar progressBar;
    private View rootView;
    private int teacherId;

    // Define the fixed order of days and time ranges
    private final List<String> DISPLAY_DAYS = List.of("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday");
    private final List<String> DISPLAY_TIME_SLOTS = List.of(
            "08:00:00 - 09:00:00",
            "09:00:00 - 10:00:00",
            "10:00:00 - 11:00:00",
            "11:00:00 - 11:30:00", // Break time
            "11:30:00 - 12:30:00",
            "12:30:00 - 13:30:00"
    );

    public static TeacherTimetableFragment newInstance(int teacherId) {
        TeacherTimetableFragment fragment = new TeacherTimetableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TEACHER_ID, teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teacherId = getArguments().getInt(ARG_TEACHER_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_view_timetable, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);

        // Initialize day names
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
        for (int i = 0; i < days.length; i++) {
            View row = rootView.findViewById(getResources().getIdentifier(days[i].toLowerCase() + "Row", "id", requireContext().getPackageName()));
            TextView dayName = row.findViewById(R.id.dayName);
            dayName.setText(days[i]);
        }

        fetchTimetable();
    }

    private void displayTimetable(Map<String, List<TeacherTimetableEntry>> organizedTimetable) {
        // Clear all cells first
        for (String day : DISPLAY_DAYS) {
            View row = rootView.findViewById(getResources().getIdentifier(day.toLowerCase() + "Row", "id", requireContext().getPackageName()));

            // Clear all time cells
            for (int i = 1; i <= 6; i++) {
                TextView cell = row.findViewById(getResources().getIdentifier("time" + i + "Cell", "id", requireContext().getPackageName()));
                cell.setText("");

                // Set break time background for the break column (time4)
                if (i == 4) {
                    cell.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.timetable_break_bg));
                    cell.setText("Break");
                    cell.setTextColor(ContextCompat.getColor(requireContext(), R.color.timetable_break_text));
                } else {
                    cell.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.timetable_empty_cell_bg));
                    cell.setTextColor(ContextCompat.getColor(requireContext(), R.color.timetable_empty_cell_text));
                }
            }
        }

        // Fill in the timetable data
        for (String day : DISPLAY_DAYS) {
            List<TeacherTimetableEntry> entriesForDay = organizedTimetable.get(day);
            if (entriesForDay != null) {
                for (TeacherTimetableEntry entry : entriesForDay) {
                    String entryStartTime = entry.getStartTime();
                    String entryEndTime = entry.getEndTime();

                    // Find all time slots that fall within this entry's time range
                    for (int i = 0; i < DISPLAY_TIME_SLOTS.size(); i++) {
                        // Skip break time column
                        if (i == 3) continue;

                        String timeSlot = DISPLAY_TIME_SLOTS.get(i);
                        String[] slotTimes = timeSlot.split(" - ");
                        String slotStartTime = slotTimes[0].trim();
                        String slotEndTime = slotTimes[1].trim();

                        if (isTimeInRange(slotStartTime, entryStartTime, entryEndTime) ||
                                isTimeInRange(slotEndTime, entryStartTime, entryEndTime) ||
                                isTimeInRange(entryStartTime, slotStartTime, slotEndTime)) {

                            View row = rootView.findViewById(getResources().getIdentifier(day.toLowerCase() + "Row", "id", requireContext().getPackageName()));
                            TextView cell = row.findViewById(getResources().getIdentifier("time" + (i + 1) + "Cell", "id", requireContext().getPackageName()));

                            cell.setText(entry.getSubject());
                            cell.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.timetable_subject_bg));
                            cell.setTextColor(ContextCompat.getColor(requireContext(), R.color.timetable_subject_text));

                            final String className = entry.getClassName();
                            cell.setOnClickListener(v -> {
                                Toast.makeText(requireContext(), "Class: " + className, Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }
            }
        }
    }

    private boolean isTimeInRange(String time, String startTime, String endTime) {
        return time.compareTo(startTime) >= 0 && time.compareTo(endTime) <= 0;
    }

    private void fetchTimetable() {
        setLoading(true);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("teacher_id", teacherId);
            Log.d(TAG, "Fetching timetable for teacher ID: " + teacherId);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error (creating request body): " + e.getMessage());
            Toast.makeText(requireContext(), "Error creating request", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                UrlManager.URL_GET_TEACHER_TIMETABLE,
                requestBody,
                response -> {
                    Log.d(TAG, "Response received: " + response.toString());
                    handleTimetableResponse(response);
                },
                error -> {
                    Log.e(TAG, "Error fetching timetable: " + error.getMessage());
                    handleError(error);
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void handleTimetableResponse(JSONObject response) {
        setLoading(false);
        try {
            String status = response.getString("status");
            if (status.equals("success")) {
                JSONObject timetableObj = response.getJSONObject("timetable");
                Map<String, List<TeacherTimetableEntry>> organizedTimetable = new LinkedHashMap<>();

                // Organize timetable data
                for (String day : DISPLAY_DAYS) {
                    List<TeacherTimetableEntry> entriesForDay = new ArrayList<>();
                    if (timetableObj.has(day)) {
                        JSONArray dayEntries = timetableObj.getJSONArray(day);
                        for (int i = 0; i < dayEntries.length(); i++) {
                            JSONObject entryObj = dayEntries.getJSONObject(i);
                            TeacherTimetableEntry entry = new TeacherTimetableEntry(
                                    day,
                                    entryObj.getString("time").split(" - ")[0].trim(),
                                    entryObj.getString("time").split(" - ")[1].trim(),
                                    entryObj.getString("subject"),
                                    entryObj.getString("class")
                            );
                            entriesForDay.add(entry);
                        }
                        Collections.sort(entriesForDay, Comparator.comparing(TeacherTimetableEntry::getStartTime));
                    }
                    organizedTimetable.put(day, entriesForDay);
                }
                displayTimetable(organizedTimetable);
            } else {
                String message = response.optString("message", "No timetable found");
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error parsing timetable data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(VolleyError error) {
        setLoading(false);
        String errorMessage = "Error loading timetable: ";
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            String responseData = new String(error.networkResponse.data);
            errorMessage += "Status Code: " + statusCode + ", Response: " + responseData;
        } else if (error.getMessage() != null) {
            errorMessage += error.getMessage();
        } else {
            errorMessage += "Unknown error";
        }
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        rootView.findViewById(R.id.headerRow).setVisibility(isLoading ? View.GONE : View.VISIBLE);
        for (String day : DISPLAY_DAYS) {
            rootView.findViewById(getResources().getIdentifier(day.toLowerCase() + "Row", "id", requireContext().getPackageName()))
                    .setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    // Inner class for teacher timetable entries
    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class TeacherTimetableEntry {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private String subject;
        private String className;

        public TeacherTimetableEntry(String dayOfWeek, String startTime, String endTime, String subject, String className) {
            this.dayOfWeek = dayOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
            this.subject = subject;
            this.className = className;
        }
    }
}