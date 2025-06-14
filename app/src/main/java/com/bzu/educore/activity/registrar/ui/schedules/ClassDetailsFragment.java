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
import android.widget.Spinner;
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
import android.widget.LinearLayout;
import android.widget.CheckBox;
import android.util.Pair;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import androidx.core.content.ContextCompat;

public class ClassDetailsFragment extends Fragment {
    private static final String ARG_CLASS = "classData";
    private static final String TAG = "ClassDetailsFragment";
    private TextView textClassDetails;
    private RecyclerView timetableRecyclerView;
    private TimetableAdapter timetableAdapter;
    private RequestQueue requestQueue;
    private FloatingActionButton fabAddClass;

    // Dialog related views and state
    private LinearLayout layoutStep1;
    private LinearLayout layoutStep2;
    private LinearLayout layoutStep3;
    private LinearLayout layoutStep4;
    private LinearLayout layoutStep5;
    private LinearLayout layoutStep6;
    private LinearLayout layoutStep7;
    private AutoCompleteTextView dropdownTeacher;
    private Spinner dropdownTime;
    private MaterialButton buttonBack;
    private MaterialButton buttonNext;
    private TextView textStepTitle;
    private int currentStep = 1; // Start at step 1
    private String selectedSubjectId = ""; // To store the selected subject ID
    private String selectedTimeSlot = ""; // To store the selected time slot (for single time)
    private String selectedTeacherId = ""; // To store the selected teacher ID
    private List<com.bzu.educore.model.Subject> subjectsList; // Store fetched subjects with IDs
    private List<com.bzu.educore.model.Teacher> teachersList; // Store fetched teachers with IDs
    private String currentClassId; // New: Store the class ID
    private List<String> selectedDays; // New: To store selected days across steps
    private Map<String, String> selectedTimesPerDay; // New: To store time slots for each selected day
    private Map<String, Spinner> dropdownsPerDay; // Changed from AutoCompleteTextView to Spinner
    private LinearLayout confirmationDetailsContainer;
    // Checkboxes for days
    private CheckBox checkboxSunday;
    private CheckBox checkboxMonday;
    private CheckBox checkboxTuesday;
    private CheckBox checkboxWednesday;
    private CheckBox checkboxThursday;
    // Radio buttons for Step 4
    private RadioGroup radioGroupSameTime;
    private RadioButton radioYes;
    private RadioButton radioNo;

    // Define timetable structure
    private final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
    private final String[] timeSlots = {"8:00-9:00", "9:00-10:00", "10:00-11:00", "11:00-11:30", "11:30-12:30", "12:30-13:30"};
    private final int columnCount = timeSlots.length + 1; // +1 for the day column

    // Specific time slots for the dialog dropdown
    private final String[] dialogTimeSlots = {"8:00-8:50", "9:00-9:50", "10:00-10:50", "11:30-12:20", "12:30-1:20"};

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
                
                // Store class ID
                currentClassId = classData.getId();
                
                // Fetch timetable data
                fetchTimetable(classData.getId());
            } else {
                Log.e(TAG, "Class data is null in onViewCreated. currentClassId will not be set.");
            }
        } else {
            Log.e(TAG, "Arguments are null in onViewCreated. currentClassId will not be set.");
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
                            JSONArray timetable = response.getJSONArray("timetable");
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

    private void displayTimetable(JSONArray timetable) {
        Log.d(TAG, "Starting to display timetable");

        new Thread(() -> {
            List<TimetableCell> cells = new ArrayList<>();

            // Group periods by day_of_week
            Map<String, List<JSONObject>> timetableMap = new HashMap<>();
            try {
                for (int i = 0; i < timetable.length(); i++) {
                    JSONObject period = timetable.getJSONObject(i);
                    String day = period.getString("day_of_week");
                    timetableMap.computeIfAbsent(day, k -> new ArrayList<>()).add(period);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error grouping timetable: " + e.getMessage());
            }

            // Add top-left empty header
            cells.add(new TimetableCell("", Color.parseColor("#2196F3"), Color.WHITE, TimetableCell.TYPE_HEADER));

            // Time slot headers
            for (String timeSlot : timeSlots) {
                cells.add(new TimetableCell(timeSlot, Color.parseColor("#2196F3"), Color.WHITE, TimetableCell.TYPE_STANDARD));
            }

            // Fill rows per day
            for (String day : days) {
                Log.d(TAG, "Processing day: " + day);
                cells.add(new TimetableCell(day, Color.parseColor("#E3F2FD"), Color.BLACK, TimetableCell.TYPE_HEADER));

                for (String timeSlot : timeSlots) {
                    String cellText = "";
                    int bgColor = Color.WHITE;
                    int textColor = Color.BLACK;

                    if (timeSlot.equals("11:00-11:30")) {
                        cellText = "Break";
                        bgColor = Color.parseColor("#FFE0B2");
                    } else {
                        String slotHour = timeSlot.split(":")[0];
                        if (slotHour.startsWith("0")) slotHour = slotHour.substring(1);

                        List<JSONObject> daySchedule = timetableMap.get(day);
                        if (daySchedule != null) {
                            for (JSONObject period : daySchedule) {
                                try {
                                    String startTime = period.getString("start_time").substring(0, 5);
                                    String periodHour = startTime.split(":")[0];
                                    if (periodHour.startsWith("0")) periodHour = periodHour.substring(1);

                                    if (slotHour.equals(periodHour)) {
                                        String subject = period.getString("subject");
                                        String teacher = period.getString("teacher_fname") + " " + period.getString("teacher_lname");
                                        cellText = subject + "\n" + teacher;
                                        bgColor = Color.parseColor("#E8F5E9");
                                        break;
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "Error in daySchedule loop: " + e.getMessage());
                                }
                            }
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
        Log.d(TAG, "1. showAddScheduleDialog called. currentClassId: " + currentClassId);
        if (currentClassId == null || currentClassId.isEmpty()) {
            Toast.makeText(requireContext(), "Class details not loaded. Please try again later.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "currentClassId is null or empty when FAB clicked. Dialog not shown.");
            return;
        }

        Dialog dialog = new Dialog(requireContext());
        Log.d(TAG, "2. Dialog instance created.");
        try {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Log.d(TAG, "3. Dialog requested no title.");
            dialog.setContentView(R.layout.dialog_add_schedule);
            Log.d(TAG, "4. Dialog content view set: R.layout.dialog_add_schedule");
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Log.d(TAG, "5. Dialog window layout and background set.");

            // Initialize views
            textStepTitle = dialog.findViewById(R.id.text_step_title);
            layoutStep1 = dialog.findViewById(R.id.layout_step_1);
            layoutStep2 = dialog.findViewById(R.id.layout_step_2);
            layoutStep3 = dialog.findViewById(R.id.layout_step_3);
            layoutStep4 = dialog.findViewById(R.id.layout_step_4);
            layoutStep5 = dialog.findViewById(R.id.layout_step_5_per_day);
            layoutStep6 = dialog.findViewById(R.id.layout_step_6);
            layoutStep7 = dialog.findViewById(R.id.layout_step_7);
            AutoCompleteTextView dropdownSubject = dialog.findViewById(R.id.dropdown_subject);
            dropdownTeacher = dialog.findViewById(R.id.dropdown_teacher);
            dropdownTime = dialog.findViewById(R.id.dropdown_time);
            MaterialButton buttonCancel = dialog.findViewById(R.id.button_cancel);
            buttonBack = dialog.findViewById(R.id.button_back);
            buttonNext = dialog.findViewById(R.id.button_next);
            checkboxSunday = dialog.findViewById(R.id.checkbox_sunday);
            checkboxMonday = dialog.findViewById(R.id.checkbox_monday);
            checkboxTuesday = dialog.findViewById(R.id.checkbox_tuesday);
            checkboxWednesday = dialog.findViewById(R.id.checkbox_wednesday);
            checkboxThursday = dialog.findViewById(R.id.checkbox_thursday);
            radioGroupSameTime = dialog.findViewById(R.id.radio_group_same_time);
            radioYes = dialog.findViewById(R.id.radio_yes);
            radioNo = dialog.findViewById(R.id.radio_no);
            confirmationDetailsContainer = dialog.findViewById(R.id.confirmation_details_container);
            Log.d(TAG, "6. All dialog views initialized via findViewById.");

            // Basic null checks for critical views after findViewById
            if (textStepTitle == null || layoutStep1 == null || dropdownSubject == null || layoutStep7 == null || confirmationDetailsContainer == null) {
                Log.e(TAG, "CRITICAL ERROR: One or more essential dialog views are null.");
                Toast.makeText(requireContext(), "Error initializing schedule dialog. Please check layout file.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                return;
            }

            // Initialize maps for per-day selection
            selectedTimesPerDay = new HashMap<>();
            dropdownsPerDay = new HashMap<>();
            Log.d(TAG, "7. Maps initialized.");

            // Reset current step to 1 when dialog is shown
            currentStep = 1;
            updateDialogUI();
            Log.d(TAG, "8. currentStep reset and updateDialogUI called.");

            // Populate time slots dropdown for the single time selection (Step 6)
            ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                dialogTimeSlots
            );
            timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdownTime.setAdapter(timeAdapter);
            Log.d(TAG, "9. Single time selection spinner adapter set.");

            // Fetch subjects for the class
            Log.d(TAG, "10. Calling fetchSubjectsForClass for dropdown_subject.");
            fetchSubjectsForClass(dialog, dropdownSubject);

            // Set up cancel button
            buttonCancel.setOnClickListener(v -> dialog.dismiss());
            Log.d(TAG, "11. Cancel button listener set.");

            // Set up back button
            buttonBack.setOnClickListener(v -> {
                if (currentStep == 7) {
                    currentStep = 4;
                } else if (currentStep == 5 || currentStep == 6) {
                    currentStep = 4;
                } else if (currentStep > 1) {
                    currentStep--;
                }
                updateDialogUI();
                Log.d(TAG, "Back button clicked. New step: " + currentStep);
            });
            Log.d(TAG, "12. Back button listener set.");

            // Set up next button
            buttonNext.setOnClickListener(v -> {
                Log.d(TAG, "Next button clicked. Current step: " + currentStep);
                if (currentStep == 1) {
                    String selectedSubjectTitle = dropdownSubject.getText().toString();
                    com.bzu.educore.model.Subject selectedSubject = null;
                    for (com.bzu.educore.model.Subject subject : subjectsList) {
                        if (subject.getTitle().equals(selectedSubjectTitle)) {
                            selectedSubject = subject;
                            break;
                        }
                    }

                    if (selectedSubject != null) {
                        selectedSubjectId = selectedSubject.getId();
                        currentStep++;
                        updateDialogUI();
                        fetchTeachersForSubject(dialog, dropdownTeacher, selectedSubjectId);
                    } else {
                        Toast.makeText(requireContext(), "Please select a valid subject", Toast.LENGTH_SHORT).show();
                    }
                } else if (currentStep == 2) {
                    String selectedTeacherName = dropdownTeacher.getText().toString();
                    com.bzu.educore.model.Teacher selectedTeacher = null;
                    for (com.bzu.educore.model.Teacher teacher : teachersList) {
                        if (teacher.toString().equals(selectedTeacherName)) {
                            selectedTeacher = teacher;
                            break;
                        }
                    }
                    
                    if (selectedTeacher != null) {
                        selectedTeacherId = selectedTeacher.getId();
                        currentStep++;
                        updateDialogUI();
                    } else {
                        Toast.makeText(requireContext(), "Please select a valid teacher", Toast.LENGTH_SHORT).show();
                    }
                } else if (currentStep == 3) {
                    selectedDays = new ArrayList<>();
                    if (checkboxSunday.isChecked()) selectedDays.add("Sunday");
                    if (checkboxMonday.isChecked()) selectedDays.add("Monday");
                    if (checkboxTuesday.isChecked()) selectedDays.add("Tuesday");
                    if (checkboxWednesday.isChecked()) selectedDays.add("Wednesday");
                    if (checkboxThursday.isChecked()) selectedDays.add("Thursday");

                    if (!selectedDays.isEmpty()) {
                        if (selectedDays.size() == 1) {
                            currentStep = 6;
                            updateDialogUI();
                            loadSingleDayAvailableTimes(selectedDays.get(0), dropdownTime, dialog);
                        } else {
                            currentStep++;
                            updateDialogUI();
                            radioGroupSameTime.clearCheck();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Please select at least one day", Toast.LENGTH_SHORT).show();
                    }
                } else if (currentStep == 4) {
                    int selectedId = radioGroupSameTime.getCheckedRadioButtonId();
                    if (selectedId == -1) {
                        Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedId == R.id.radio_yes) {
                        currentStep = 6;
                        updateDialogUI();
                        dropdownTime.setSelection(0);
                    } else if (selectedId == R.id.radio_no) {
                        currentStep = 5;
                        updateDialogUI();
                        populatePerDayTimeSelectors(dialog);
                    }
                } else if (currentStep == 5) {
                    boolean allTimesSelected = true;
                    selectedTimesPerDay.clear();

                    for (String day : selectedDays) {
                        Spinner daySpinner = dropdownsPerDay.get(day);
                        if (daySpinner != null) {
                            String selectedTime = daySpinner.getSelectedItem().toString();
                            if (selectedTime.equals("No available time slots")) {
                                allTimesSelected = false;
                                Toast.makeText(requireContext(), "Please select time for " + day, Toast.LENGTH_SHORT).show();
                                break;
                            }
                            selectedTimesPerDay.put(day, selectedTime);
                        } else {
                            allTimesSelected = false;
                            Log.e(TAG, "Spinner for day " + day + " not found!");
                            break;
                        }
                    }

                    if (allTimesSelected) {
                        checkMultipleTimesAvailability(dialog, selectedDays, selectedTimesPerDay, () -> {
                            currentStep = 7;
                            updateDialogUI();
                            populateConfirmationStep();
                        });
                    }
                } else if (currentStep == 6) {
                    String selectedTime = dropdownTime.getSelectedItem().toString();
                    if (!selectedTime.equals("No available time slots")) {
                        selectedTimeSlot = selectedTime;
                        checkTimeAvailability(dialog, selectedDays, () -> {
                            currentStep = 7;
                            updateDialogUI();
                            populateConfirmationStep();
                        });
                    } else {
                        Toast.makeText(requireContext(), "Please select a time slot", Toast.LENGTH_SHORT).show();
                    }
                } else if (currentStep == 7) {
                    addScheduleToTimetable(dialog);
                }
            });
            Log.d(TAG, "13. Next button listener set.");

            dialog.show();
            Log.d(TAG, "14. Dialog.show() called. This should make the dialog visible.");

        } catch (Exception e) {
            Log.e(TAG, "Fatal error during showAddScheduleDialog setup: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "A critical error occurred while opening the schedule dialog. Please try again.", Toast.LENGTH_LONG).show();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void updateDialogUI() {
        // Hide all layouts first
        layoutStep1.setVisibility(View.GONE);
        layoutStep2.setVisibility(View.GONE);
        layoutStep3.setVisibility(View.GONE);
        layoutStep4.setVisibility(View.GONE);
        layoutStep5.setVisibility(View.GONE);
        layoutStep6.setVisibility(View.GONE);
        layoutStep7.setVisibility(View.GONE); // Hide new step 7
        buttonBack.setVisibility(View.VISIBLE);
        buttonNext.setText("Next"); // Reset Next button text

        switch (currentStep) {
            case 1:
                textStepTitle.setText("Select Subject");
                layoutStep1.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.GONE);
                break;
            case 2:
                textStepTitle.setText("Select Teacher");
                layoutStep2.setVisibility(View.VISIBLE);
                break;
            case 3:
                textStepTitle.setText("Select Days");
                layoutStep3.setVisibility(View.VISIBLE);
                break;
            case 4:
                textStepTitle.setText("Same Time for All Days?");
                layoutStep4.setVisibility(View.VISIBLE);
                break;
            case 5:
                textStepTitle.setText("Select Time for Each Day");
                layoutStep5.setVisibility(View.VISIBLE);
                break;
            case 6:
                textStepTitle.setText("Select Time");
                layoutStep6.setVisibility(View.VISIBLE);
                break;
            case 7:
                textStepTitle.setText("Confirm Schedule");
                layoutStep7.setVisibility(View.VISIBLE);
                buttonNext.setText("Confirm Schedule"); // Change button text for final step
                break;
        }
    }

    private void populatePerDayTimeSelectors(Dialog dialog) {
        layoutStep5.removeAllViews();
        dropdownsPerDay.clear();

        TextView instructionText = new TextView(requireContext());
        instructionText.setText("Select time slot for each selected day:");
        instructionText.setTextSize(16);
        instructionText.setTypeface(null, android.graphics.Typeface.BOLD);
        
        int textColorPrimary;
        android.util.TypedValue typedValue = new android.util.TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        textColorPrimary = typedValue.data;
        instructionText.setTextColor(textColorPrimary);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, (int) (16 * getResources().getDisplayMetrics().density));
        instructionText.setLayoutParams(params);
        layoutStep5.addView(instructionText);

        TextView loadingText = new TextView(requireContext());
        loadingText.setText("Loading available time slots...");
        loadingText.setTextSize(14);
        loadingText.setGravity(Gravity.CENTER);
        loadingText.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        layoutStep5.addView(loadingText);

        fetchAllAvailableTimeSlots(dialog, loadingText, textColorPrimary);
    }

    private void fetchAllAvailableTimeSlots(final Dialog dialog, final TextView loadingText, final int textColorPrimary) {
        final Map<String, List<String>> allAvailableSlots = new HashMap<>();
        final int totalDaysToCheck = selectedDays.size();
        final int totalTimeSlotsPerDay = dialogTimeSlots.length;
        final int totalRequests = totalDaysToCheck * totalTimeSlotsPerDay;
        final int[] completedRequests = {0};

        if (totalDaysToCheck == 0) {
            requireActivity().runOnUiThread(() -> {
                layoutStep5.removeView(loadingText);
                TextView noDaysSelectedText = new TextView(requireContext());
                noDaysSelectedText.setText("No days selected to check.");
                noDaysSelectedText.setTextSize(14);
                noDaysSelectedText.setGravity(Gravity.CENTER);
                layoutStep5.addView(noDaysSelectedText);
            });
            return;
        }

        for (final String day : selectedDays) {
            allAvailableSlots.put(day, new ArrayList<>());

            for (final String timeSlot : dialogTimeSlots) {
                Pair<String, String> parsedTime = parseTimeSlotToStartEnd(timeSlot);
                String startTime = parsedTime.first;
                String endTime = parsedTime.second;

                String url = UrlManager.CHECK_TIME_AVAILABILITY_URL;
                Map<String, String> params = new HashMap<>();
                params.put("class_id", currentClassId);
                params.put("subject_id", selectedSubjectId);
                params.put("teacher_id", selectedTeacherId);
                params.put("day_of_week", day);
                params.put("start_time", startTime);
                params.put("end_time", endTime);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("status") && response.getString("status").equals("success")) {
                                    boolean available = response.getBoolean("available");
                                    if (available) {
                                        allAvailableSlots.get(day).add(timeSlot);
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing availability response for " + day + " at " + timeSlot, e);
                            }
                            
                            completedRequests[0]++;
                            if (completedRequests[0] == totalRequests) {
                                renderPerDayTimeSelectors(loadingText, allAvailableSlots, textColorPrimary);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error checking availability for " + day + " at " + timeSlot, error);
                            completedRequests[0]++;
                            if (completedRequests[0] == totalRequests) {
                                renderPerDayTimeSelectors(loadingText, allAvailableSlots, textColorPrimary);
                            }
                        }
                    });
                requestQueue.add(request);
            }
        }
    }

    private void renderPerDayTimeSelectors(final TextView loadingText, final Map<String, List<String>> allAvailableSlots, final int textColorPrimary) {
        requireActivity().runOnUiThread(() -> {
            layoutStep5.removeView(loadingText);

            for (String day : selectedDays) {
                List<String> availableTimeSlots = allAvailableSlots.get(day);
                if (availableTimeSlots == null) {
                    availableTimeSlots = new ArrayList<>();
                }

                LinearLayout dayLayout = new LinearLayout(requireContext());
                dayLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                dayLayout.setOrientation(LinearLayout.HORIZONTAL);
                dayLayout.setGravity(Gravity.CENTER_VERTICAL);
                ((LinearLayout.LayoutParams) dayLayout.getLayoutParams()).setMargins(0, 0, 0, (int) (24 * getResources().getDisplayMetrics().density));

                TextView dayLabel = new TextView(requireContext());
                dayLabel.setText(day + ": ");
                dayLabel.setTextSize(14);
                dayLabel.setTextColor(textColorPrimary);
                LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                );
                labelParams.setMargins(0, 0, (int) (8 * getResources().getDisplayMetrics().density), 0);
                dayLabel.setLayoutParams(labelParams);
                dayLayout.addView(dayLabel);

                Spinner spinner = new Spinner(requireContext());
                spinner.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1.0f
                ));

                if (!availableTimeSlots.isEmpty()) {
                    ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        availableTimeSlots
                    );
                    timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(timeAdapter);
                } else {
                    spinner.setEnabled(false);
                    ArrayAdapter<String> noSlotsAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        new String[]{"No available time slots"}
                    );
                    noSlotsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(noSlotsAdapter);
                }

                dayLayout.addView(spinner);
                layoutStep5.addView(dayLayout);

                dropdownsPerDay.put(day, spinner);
            }
        });
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

        dropdownSubject.setEnabled(false);
        dropdownSubject.setText("Loading subjects...");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "Received response: " + response.toString());
                        
                        new Thread(() -> {
                            try {
                                subjectsList = new ArrayList<>();
                                
                                if (response.has("subjects")) {
                                    JSONArray subjectsArray = response.getJSONArray("subjects");
                                    
                                    for (int i = 0; i < subjectsArray.length(); i++) {
                                        JSONObject subjectJson = subjectsArray.getJSONObject(i);
                                        if (subjectJson.has("id") && subjectJson.has("title")) {
                                            subjectsList.add(new com.bzu.educore.model.Subject(
                                                subjectJson.getString("id"),
                                                subjectJson.getString("title")
                                            ));
                                        }
                                    }
                                }

                                requireActivity().runOnUiThread(() -> {
                                    dropdownSubject.setEnabled(true);
                                    dropdownSubject.setText("");

                                    if (!subjectsList.isEmpty()) {
                                        ArrayAdapter<com.bzu.educore.model.Subject> adapter = new ArrayAdapter<>(
                                            requireContext(),
                                            android.R.layout.simple_dropdown_item_1line,
                                            subjectsList
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

    private void fetchTeachersForSubject(Dialog dialog, AutoCompleteTextView dropdownTeacher, String subjectId) {
        String url = UrlManager.GET_TEACHERS_FOR_SUBJECT_URL;

        Map<String, String> params = new HashMap<>();
        params.put("subject_id", subjectId);

        Log.d(TAG, "Fetching teachers for subject ID: " + subjectId);
        Log.d(TAG, "Request URL: " + url);
        Log.d(TAG, "Request params: " + params.toString());

        dropdownTeacher.setEnabled(false);
        dropdownTeacher.setText("Loading teachers...");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d(TAG, "Received teachers response: " + response.toString());
                        
                        new Thread(() -> {
                            try {
                                teachersList = new ArrayList<>();
                                
                                if (response.has("status") && response.getString("status").equals("success")) {
                                    if (response.has("teachers")) {
                                        JSONArray teachersArray = response.getJSONArray("teachers");
                                        
                                        for (int i = 0; i < teachersArray.length(); i++) {
                                            JSONObject teacher = teachersArray.getJSONObject(i);
                                            teachersList.add(new com.bzu.educore.model.Teacher(
                                                teacher.getString("id"),
                                                teacher.getString("fname"),
                                                teacher.getString("lname")
                                            ));
                                        }
                                    }
                                }

                                requireActivity().runOnUiThread(() -> {
                                    dropdownTeacher.setEnabled(true);
                                    dropdownTeacher.setText("");

                                    if (!teachersList.isEmpty()) {
                                        ArrayAdapter<com.bzu.educore.model.Teacher> adapter = new ArrayAdapter<>(
                                            requireContext(),
                                            android.R.layout.simple_dropdown_item_1line,
                                            teachersList
                                        );
                                        dropdownTeacher.setAdapter(adapter);
                                    } else {
                                        Toast.makeText(requireContext(), "No teachers available for this subject", Toast.LENGTH_SHORT).show();
                                        currentStep--;
                                        updateDialogUI();
                                    }
                                });
                            } catch (JSONException e) {
                                handleError("Error parsing teachers data: " + e.getMessage(), dialog);
                            }
                        }).start();
                    } catch (Exception e) {
                        handleError("Error processing teachers response: " + e.getMessage(), dialog);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    handleError("Error fetching teachers: " + error.toString(), dialog);
                    currentStep--;
                    updateDialogUI();
                }
            });

        requestQueue.add(request);
    }

    private Pair<String, String> parseTimeSlotToStartEnd(String timeSlot) {
        String[] parts = timeSlot.split("-");
        String startTime = parts[0] + ":00";
        String endTime = parts[1] + ":00";

        if (startTime.startsWith("12:") && endTime.startsWith("1:")) {
            endTime = "13" + endTime.substring(1);
        }

        return new Pair<>(startTime, endTime);
    }

    private void checkTimeAvailability(final Dialog dialog, final List<String> selectedDays, final Runnable onComplete) {
        if (selectedDays.isEmpty()) {
            Toast.makeText(requireContext(), "No days selected for availability check.", Toast.LENGTH_SHORT).show();
            onComplete.run();
            return;
        }

        final Pair<String, String> parsedTime = parseTimeSlotToStartEnd(selectedTimeSlot);
        final String startTime = parsedTime.first;
        final String endTime = parsedTime.second;

        final int[] completedRequests = {0};
        final boolean[] allAvailable = {true};

        if (buttonNext != null) {
            buttonNext.setEnabled(false);
            buttonNext.setText("Checking availability...");
        }

        for (final String day : selectedDays) {
            String url = UrlManager.CHECK_TIME_AVAILABILITY_URL;
            Map<String, String> params = new HashMap<>();
            params.put("class_id", currentClassId);
            params.put("subject_id", selectedSubjectId);
            params.put("teacher_id", selectedTeacherId);
            params.put("day_of_week", day);
            params.put("start_time", startTime);
            params.put("end_time", endTime);

            Log.d(TAG, "Checking availability for " + day + ": " + params.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Availability response for " + day + ": " + response.toString());
                        try {
                            if (response.has("status") && response.getString("status").equals("success")) {
                                boolean available = response.getBoolean("available");
                                if (!available) {
                                    allAvailable[0] = false;
                                    Toast.makeText(requireContext(), "Time slot is NOT available for " + day, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                allAvailable[0] = false;
                                Toast.makeText(requireContext(), "Error in availability response for " + day + ": " + response.toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            allAvailable[0] = false;
                            Toast.makeText(requireContext(), "Error parsing availability data for " + day + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Error parsing availability data for " + day, e);
                        }
                        
                        completedRequests[0]++;
                        if (completedRequests[0] == selectedDays.size()) {
                            if (buttonNext != null) {
                                buttonNext.setEnabled(true);
                                buttonNext.setText("Next");
                            }
                            if (allAvailable[0]) {
                                Toast.makeText(requireContext(), "All selected times are available! Schedule can be added.", Toast.LENGTH_LONG).show();
                                onComplete.run();
                            } else {
                                Toast.makeText(requireContext(), "Please adjust your selections for unavailable times.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        allAvailable[0] = false;
                        Log.e(TAG, "Error checking availability for " + day + ": " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Error response code: " + error.networkResponse.statusCode);
                            Log.e(TAG, "Error response data: " + new String(error.networkResponse.data));
                        }
                        Toast.makeText(requireContext(), "Network error checking availability for " + day, Toast.LENGTH_LONG).show();

                        completedRequests[0]++;
                        if (completedRequests[0] == selectedDays.size()) {
                            if (buttonNext != null) {
                                buttonNext.setEnabled(true);
                                buttonNext.setText("Next");
                            }
                            Toast.makeText(requireContext(), "Please adjust your selections for unavailable times.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            requestQueue.add(request);
        }
    }

    private void checkMultipleTimesAvailability(final Dialog dialog, final List<String> selectedDays, final Map<String, String> selectedTimes, final Runnable onComplete) {
        if (selectedDays.isEmpty()) {
            Toast.makeText(requireContext(), "No days selected for availability check.", Toast.LENGTH_SHORT).show();
            onComplete.run();
            return;
        }

        final int[] completedRequests = {0};
        final boolean[] allAvailable = {true};

        if (buttonNext != null) {
            buttonNext.setEnabled(false);
            buttonNext.setText("Checking availability...");
        }

        for (final String day : selectedDays) {
            final String timeSlot = selectedTimes.get(day);
            if (timeSlot == null || timeSlot.isEmpty()) {
                Log.e(TAG, "No time slot selected for day: " + day);
                completedRequests[0]++;
                allAvailable[0] = false;
                continue;
            }

            final Pair<String, String> parsedTime = parseTimeSlotToStartEnd(timeSlot);
            final String startTime = parsedTime.first;
            final String endTime = parsedTime.second;

            String url = UrlManager.CHECK_TIME_AVAILABILITY_URL;
            Map<String, String> params = new HashMap<>();
            params.put("class_id", currentClassId);
            params.put("subject_id", selectedSubjectId);
            params.put("teacher_id", selectedTeacherId);
            params.put("day_of_week", day);
            params.put("start_time", startTime);
            params.put("end_time", endTime);

            Log.d(TAG, "Checking availability for " + day + " at " + timeSlot + ": " + params.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Availability response for " + day + " at " + timeSlot + ": " + response.toString());
                        try {
                            if (response.has("status") && response.getString("status").equals("success")) {
                                boolean available = response.getBoolean("available");
                                if (!available) {
                                    allAvailable[0] = false;
                                    Toast.makeText(requireContext(), "Time slot is NOT available for " + day + " at " + timeSlot, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                allAvailable[0] = false;
                                Toast.makeText(requireContext(), "Error in availability response for " + day + " at " + timeSlot + ": " + response.toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            allAvailable[0] = false;
                            Toast.makeText(requireContext(), "Error parsing availability data for " + day + " at " + timeSlot + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Error parsing availability data for " + day, e);
                        }
                        
                        completedRequests[0]++;
                        if (completedRequests[0] == selectedDays.size()) {
                            if (buttonNext != null) {
                                buttonNext.setEnabled(true);
                                buttonNext.setText("Next");
                            }
                            if (allAvailable[0]) {
                                Toast.makeText(requireContext(), "All selected times are available! Schedule can be added.", Toast.LENGTH_LONG).show();
                                onComplete.run();
                            } else {
                                Toast.makeText(requireContext(), "Please adjust your selections for unavailable times.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        allAvailable[0] = false;
                        Log.e(TAG, "Error checking availability for " + day + " at " + timeSlot + ": " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Error response code: " + error.networkResponse.statusCode);
                            Log.e(TAG, "Error response data: " + new String(error.networkResponse.data));
                        }
                        Toast.makeText(requireContext(), "Network error checking availability for " + day + " at " + timeSlot, Toast.LENGTH_LONG).show();

                        completedRequests[0]++;
                        if (completedRequests[0] == selectedDays.size()) {
                            if (buttonNext != null) {
                                buttonNext.setEnabled(true);
                                buttonNext.setText("Next");
                            }
                            Toast.makeText(requireContext(), "Please adjust your selections for unavailable times.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            requestQueue.add(request);
        }
    }

    private void populateConfirmationStep() {
        confirmationDetailsContainer.removeAllViews();

        TextView header = new TextView(requireContext());
        header.setText("Selected Schedule Details:");
        header.setTextSize(16);
        header.setTypeface(null, android.graphics.Typeface.BOLD);
        header.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerParams.setMargins(0, 0, 0, (int) (8 * getResources().getDisplayMetrics().density));
        header.setLayoutParams(headerParams);
        confirmationDetailsContainer.addView(header);

        if (radioYes.isChecked()) {
            TextView singleTimeSummary = new TextView(requireContext());
            singleTimeSummary.setText(String.format("Subject: %s\nTeacher: %s\nTime: %s",
                (subjectsList != null && !subjectsList.isEmpty() && !selectedSubjectId.isEmpty()) ? getSubjectTitleById(selectedSubjectId) : "N/A",
                (teachersList != null && !teachersList.isEmpty() && !selectedTeacherId.isEmpty()) ? getTeacherNameById(selectedTeacherId) : "N/A",
                selectedTimeSlot));
            singleTimeSummary.setTextSize(14);
            singleTimeSummary.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            confirmationDetailsContainer.addView(singleTimeSummary);

            TextView daysHeader = new TextView(requireContext());
            daysHeader.setText("Days:");
            daysHeader.setTextSize(14);
            daysHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            daysHeader.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            confirmationDetailsContainer.addView(daysHeader);

            for (String day : selectedDays) {
                TextView dayText = new TextView(requireContext());
                dayText.setText(String.format("- %s", day));
                dayText.setTextSize(14);
                dayText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
                confirmationDetailsContainer.addView(dayText);
            }

        } else {
            TextView multiTimeSummary = new TextView(requireContext());
            multiTimeSummary.setText(String.format("Subject: %s\nTeacher: %s",
                (subjectsList != null && !subjectsList.isEmpty() && !selectedSubjectId.isEmpty()) ? getSubjectTitleById(selectedSubjectId) : "N/A",
                (teachersList != null && !teachersList.isEmpty() && !selectedTeacherId.isEmpty()) ? getTeacherNameById(selectedTeacherId) : "N/A"));
            multiTimeSummary.setTextSize(14);
            multiTimeSummary.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            multiTimeSummary.setPadding(0, 0, 0, (int) (8 * getResources().getDisplayMetrics().density));
            confirmationDetailsContainer.addView(multiTimeSummary);

            for (String day : selectedDays) {
                String time = selectedTimesPerDay.get(day);
                TextView dayTimeText = new TextView(requireContext());
                dayTimeText.setText(String.format("- %s: %s", day, time != null ? time : "No time selected"));
                dayTimeText.setTextSize(14);
                dayTimeText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
                confirmationDetailsContainer.addView(dayTimeText);
            }
        }
    }

    private String getSubjectTitleById(String subjectId) {
        if (subjectsList != null) {
            for (com.bzu.educore.model.Subject subject : subjectsList) {
                if (subject.getId().equals(subjectId)) {
                    return subject.getTitle();
                }
            }
        }
        return "Unknown Subject";
    }

    private String getTeacherNameById(String teacherId) {
        if (teachersList != null) {
            for (com.bzu.educore.model.Teacher teacher : teachersList) {
                if (teacher.getId().equals(teacherId)) {
                    return teacher.getFname() + " " + teacher.getLname();
                }
            }
        }
        return "Unknown Teacher";
    }

    private void loadSingleDayAvailableTimes(final String day, final Spinner targetSpinner, final Dialog dialog) {
        targetSpinner.setEnabled(false);
        ArrayAdapter<String> loadingAdapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            new String[]{"Loading available times..."}
        );
        loadingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetSpinner.setAdapter(loadingAdapter);

        final List<String> availableTimeSlots = new ArrayList<>();
        final int[] completedChecks = {0};
        final int totalTimeSlots = dialogTimeSlots.length;

        for (String timeSlot : dialogTimeSlots) {
            Pair<String, String> parsedTime = parseTimeSlotToStartEnd(timeSlot);
            String startTime = parsedTime.first;
            String endTime = parsedTime.second;

            String url = UrlManager.CHECK_TIME_AVAILABILITY_URL;
            Map<String, String> params = new HashMap<>();
            params.put("class_id", currentClassId);
            params.put("subject_id", selectedSubjectId);
            params.put("teacher_id", selectedTeacherId);
            params.put("day_of_week", day);
            params.put("start_time", startTime);
            params.put("end_time", endTime);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("status") && response.getString("status").equals("success")) {
                                boolean available = response.getBoolean("available");
                                if (available) {
                                    availableTimeSlots.add(timeSlot);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing availability response for " + day + " at " + timeSlot, e);
                        }

                        completedChecks[0]++;
                        if (completedChecks[0] == totalTimeSlots) {
                            requireActivity().runOnUiThread(() -> {
                                targetSpinner.setEnabled(true);
                                if (!availableTimeSlots.isEmpty()) {
                                    ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        availableTimeSlots
                                    );
                                    timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    targetSpinner.setAdapter(timeAdapter);
                                } else {
                                    ArrayAdapter<String> noSlotsAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        new String[]{"No available time slots"}
                                    );
                                    noSlotsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    targetSpinner.setAdapter(noSlotsAdapter);
                                    targetSpinner.setEnabled(false);
                                }
                            });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error checking availability for " + day + " at " + timeSlot, error);
                        completedChecks[0]++;
                        if (completedChecks[0] == totalTimeSlots) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Error checking availability for " + day, Toast.LENGTH_SHORT).show();
                                targetSpinner.setEnabled(false);
                                ArrayAdapter<String> errorAdapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    new String[]{"Error loading times"}
                                );
                                errorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                targetSpinner.setAdapter(errorAdapter);
                            });
                        }
                    }
                });
            requestQueue.add(request);
        }
    }

    private void addScheduleToTimetable(final Dialog dialog) {
        // Determine the list of schedules to add
        final List<JSONObject> schedulesToAdd = new ArrayList<>();
        try {
            if (radioYes.isChecked()) {
                // Same time for all days
                Pair<String, String> parsedTime = parseTimeSlotToStartEnd(selectedTimeSlot);
                for (String day : selectedDays) {
                    JSONObject schedule = new JSONObject();
                    schedule.put("class_id", currentClassId);
                    schedule.put("subject_id", selectedSubjectId);
                    schedule.put("teacher_id", selectedTeacherId);
                    schedule.put("day_of_week", day);
                    schedule.put("start_time", parsedTime.first);
                    schedule.put("end_time", parsedTime.second);
                    schedulesToAdd.add(schedule);
                }
            } else {
                // Different times per day
                for (String day : selectedDays) {
                    JSONObject schedule = new JSONObject();
                    schedule.put("class_id", currentClassId);
                    schedule.put("subject_id", selectedSubjectId);
                    schedule.put("teacher_id", selectedTeacherId);
                    schedule.put("day_of_week", day);
                    String timeSlot = selectedTimesPerDay.get(day);
                    if (timeSlot == null || timeSlot.isEmpty()) {
                        Log.e(TAG, "No time slot selected for day: " + day + " during schedule addition. Skipping.");
                        Toast.makeText(requireContext(), "Missing time for " + day + ". Cannot add schedule.", Toast.LENGTH_SHORT).show();
                        continue;
                    }
                    Pair<String, String> parsedTime = parseTimeSlotToStartEnd(timeSlot);
                    schedule.put("start_time", parsedTime.first);
                    schedule.put("end_time", parsedTime.second);
                    schedulesToAdd.add(schedule);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating schedule JSON for addition: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error preparing schedule data for addition.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (schedulesToAdd.isEmpty()) {
            Toast.makeText(requireContext(), "No schedule data to add.", Toast.LENGTH_SHORT).show();
            return;
        }

        final int totalRequests = schedulesToAdd.size();
        final int[] completedRequests = {0};
        final boolean[] allSuccessful = {true};

        // Disable button and show loading
        if (buttonNext != null) {
            buttonNext.setEnabled(false);
            buttonNext.setText("Adding Schedule...");
        }

        String addUrl = UrlManager.ADD_TO_TIMETABLE_URL;

        for (final JSONObject scheduleBody : schedulesToAdd) {
            final String dayForLog = scheduleBody.optString("day_of_week", "Unknown Day");
            final String startTimeForLog = scheduleBody.optString("start_time", "Unknown Time");
            Log.d(TAG, "Attempting to add schedule for " + dayForLog + " at " + startTimeForLog + ". Payload: " + scheduleBody.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, addUrl, scheduleBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response for adding schedule for " + dayForLog + ": " + response.toString());
                        try {
                            if (response.has("status") && response.getString("status").equals("success")) {
                                Toast.makeText(requireContext(), "Schedule added for " + dayForLog + ".", Toast.LENGTH_SHORT).show();
                            } else {
                                allSuccessful[0] = false;
                                String message = response.has("message") ? response.getString("message") : "Failed to add schedule for " + dayForLog + ".";
                                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                Log.e(TAG, "Server reported failure for " + dayForLog + ": " + message);
                            }
                        } catch (JSONException e) {
                            allSuccessful[0] = false;
                            Log.e(TAG, "Error parsing add schedule response for " + dayForLog + ": " + e.getMessage(), e);
                            Toast.makeText(requireContext(), "Error processing server response for " + dayForLog + ".", Toast.LENGTH_SHORT).show();
                        }

                        completedRequests[0]++;
                        if (completedRequests[0] == totalRequests) {
                            // All requests completed
                            if (buttonNext != null) {
                                buttonNext.setEnabled(true);
                                buttonNext.setText("Confirm Schedule");
                            }
                            dialog.dismiss();
                            fetchTimetable(currentClassId); // Refresh the timetable after all additions/failures
                            if (allSuccessful[0]) {
                                Toast.makeText(requireContext(), "All selected schedules processed.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(requireContext(), "Some schedules could not be added. Please review.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        allSuccessful[0] = false;
                        Log.e(TAG, "Network error adding schedule for " + dayForLog + ": " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Error response code: " + error.networkResponse.statusCode);
                            Log.e(TAG, "Error response data: " + new String(error.networkResponse.data));
                        }
                        Toast.makeText(requireContext(), "Network error for " + dayForLog + ".", Toast.LENGTH_LONG).show();

                        completedRequests[0]++;
                        if (completedRequests[0] == totalRequests) {
                            // All requests completed (even with errors)
                            if (buttonNext != null) {
                                buttonNext.setEnabled(true);
                                buttonNext.setText("Confirm Schedule");
                            }
                            dialog.dismiss();
                            fetchTimetable(currentClassId); // Refresh the timetable
                            Toast.makeText(requireContext(), "Some schedules could not be added. Please review.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            requestQueue.add(request);
        }
    }

    private void handleError(String errorMessage, Dialog dialog) {
        Log.e(TAG, errorMessage);
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
} 