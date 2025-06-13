package com.bzu.educore.activity.registrar.ui.schedules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.Toast;
import android.util.Log;
import android.view.Gravity;
import android.app.Dialog;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bzu.educore.R;
import com.bzu.educore.model.Class;
import com.bzu.educore.util.UrlManager;

import java.util.HashMap;
import java.util.Map;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ClassDetailsFragment extends Fragment {
    private static final String ARG_CLASS = "classData";
    private static final String TAG = "ClassDetailsFragment";
    private TextView textClassDetails;
    private RecyclerView timetableRecyclerView;
    private TimetableAdapter timetableAdapter;
    private RequestQueue requestQueue;
    private FloatingActionButton fabAddClass;

    // Define timetable structure
    private final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    private final String[] timeSlots = {"8:00-9:00", "9:00-10:00", "10:00-11:00", "11:00-11:30", "11:30-12:30", "12:30-13:30"};
    private final int columnCount = timeSlots.length + 1; // +1 for the day column

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textClassDetails = view.findViewById(R.id.text_class_details);
        timetableRecyclerView = view.findViewById(R.id.timetable_table);
        requestQueue = Volley.newRequestQueue(requireContext());
        fabAddClass = view.findViewById(R.id.fab_add_class);

        // Set up RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), columnCount);
        timetableRecyclerView.setLayoutManager(layoutManager);
        timetableAdapter = new TimetableAdapter(new ArrayList<>(), columnCount);
        timetableRecyclerView.setAdapter(timetableAdapter);

        // Set OnClickListener for the FloatingActionButton
        fabAddClass.setOnClickListener(v -> showAddScheduleDialog());

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

    private void displayTimetable(JSONObject timetable) {
        Log.d(TAG, "Starting to display timetable");

        new Thread(() -> {
            List<TimetableCell> cells = new ArrayList<>();

            // Add empty cell for the top-left corner
            cells.add(new TimetableCell("", Color.parseColor("#2196F3"), Color.WHITE, TimetableCell.TYPE_HEADER));

            // Add time slot headers
            for (String timeSlot : timeSlots) {
                cells.add(new TimetableCell(timeSlot, Color.parseColor("#2196F3"), Color.WHITE, TimetableCell.TYPE_STANDARD));
            }

            // Add rows for each day
            for (String day : days) {
                // Add day header cell
                cells.add(new TimetableCell(day, Color.parseColor("#E3F2FD"), Color.BLACK, TimetableCell.TYPE_HEADER));

                // Add cells for each time slot
                for (String timeSlot : timeSlots) {
                    String cellText = "";
                    int bgColor = Color.WHITE;
                    int textColor = Color.BLACK;

                    if (timeSlot.equals("11:00-11:30")) {
                        cellText = "Break";
                        bgColor = Color.parseColor("#FFE0B2"); // Light orange for break
                    } else {
                        try {
                            if (timetable.has(day)) {
                                JSONArray daySchedule = timetable.getJSONArray(day);
                                String slotHour = timeSlot.split(":")[0];

                                for (int i = 0; i < daySchedule.length(); i++) {
                                    JSONObject period = daySchedule.getJSONObject(i);
                                    String startTime = period.getString("start_time").substring(0, 5);
                                    String periodHour = startTime.split(":")[0];

                                    if (periodHour.startsWith("0")) {
                                        periodHour = periodHour.substring(1);
                                    }

                                    if (slotHour.equals(periodHour)) {
                                        String subject = period.getString("subject");
                                        String teacher = period.getString("teacher");
                                        cellText = subject + "\n" + teacher;
                                        bgColor = Color.parseColor("#E8F5E9"); // Light green for class cells
                                        break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing timetable data for cell: " + e.getMessage());
                        }
                    }
                    cells.add(new TimetableCell(cellText, bgColor, textColor, TimetableCell.TYPE_STANDARD));
                }
            }

            requireActivity().runOnUiThread(() -> {
                timetableAdapter.updateData(cells);
                Log.d(TAG, "Finished displaying timetable with RecyclerView");
            });
        }).start();
    }

    private void showAddScheduleDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_schedule);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Initialize views
        AutoCompleteTextView dropdownSubject = dialog.findViewById(R.id.dropdown_subject);
        MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel);
        MaterialButton buttonNext = dialog.findViewById(R.id.button_next);

        // Fetch subjects for the class
        fetchSubjectsForClass(dialog, dropdownSubject);

        // Set up cancel button
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        // Set up next button
        buttonNext.setOnClickListener(v -> {
            String selectedSubject = dropdownSubject.getText().toString();
            if (!selectedSubject.isEmpty()) {
                // TODO: Move to next step
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Please select a subject", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void fetchSubjectsForClass(Dialog dialog, AutoCompleteTextView dropdownSubject) {
        String url = UrlManager.GET_CLASS_SUBJECTS_URL;
        
        Map<String, String> params = new HashMap<>();
        Bundle args = getArguments();
        if (args != null) {
            Class classData = (Class) args.getSerializable(ARG_CLASS);
            if (classData != null) {
                params.put("grade_number", classData.getGradeNumber());
            }
        }

        Log.d(TAG, "Fetching subjects for class. URL: " + url);
        Log.d(TAG, "Request params: " + params.toString());

        // Show loading state
        dropdownSubject.setEnabled(false);
        dropdownSubject.setText("Loading subjects...");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "Received response: " + response.toString());
                        
                        // Process response in background
                        new Thread(() -> {
                            try {
                                List<String> subjectList = new ArrayList<>();
                                
                                if (response.has("subjects")) {
                                    JSONArray subjectsArray = response.getJSONArray("subjects");
                                    
                                    for (int i = 0; i < subjectsArray.length(); i++) {
                                        JSONObject subject = subjectsArray.getJSONObject(i);
                                        if (subject.has("title")) {
                                            subjectList.add(subject.getString("title"));
                                        }
                                    }
                                }

                                // Update UI on main thread
                                requireActivity().runOnUiThread(() -> {
                                    dropdownSubject.setEnabled(true);
                                    dropdownSubject.setText("");

                                    if (!subjectList.isEmpty()) {
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                            requireContext(),
                                            android.R.layout.simple_dropdown_item_1line,
                                            subjectList
                                        );
                                        dropdownSubject.setAdapter(adapter);
                                    } else {
                                        Toast.makeText(requireContext(), "No subjects available", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            } catch (JSONException e) {
                                handleError("Error parsing subjects data: " + e.getMessage(), dialog);
                            }
                        }).start();
                    } catch (Exception e) {
                        handleError("Error processing response: " + e.getMessage(), dialog);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    handleError("Error fetching subjects: " + error.toString(), dialog);
                }
            });

        requestQueue.add(request);
    }

    private void handleError(String errorMessage, Dialog dialog) {
        Log.e(TAG, errorMessage);
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}