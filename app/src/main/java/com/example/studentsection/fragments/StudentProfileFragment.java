package com.example.studentsection.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.activity.LoginActivity;
import com.bzu.educore.util.SharedPreferencesManager;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StudentProfileFragment extends Fragment {

    private static final String TAG = "StudentProfileFragment";
    private ProgressBar progressBar;
    private SharedPreferencesManager prefsManager;
    private View rootView;
    private TextView tvStudentName, tvStudentEmail, tvStudentGrade, tvStudentSection, tvStudentDOB;
    private MaterialButton btnEditProfile, btnLogout;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_student_profile, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews();
        setupClickListeners();
        fetchStudentData();
    }

    private void initializeViews() {
        progressBar = rootView.findViewById(R.id.progressBar);
        prefsManager = new SharedPreferencesManager(requireContext());
        
        tvStudentName = rootView.findViewById(R.id.tvStudentName);
        tvStudentEmail = rootView.findViewById(R.id.tvStudentEmail);
        tvStudentGrade = rootView.findViewById(R.id.tvStudentGrade);
        tvStudentSection = rootView.findViewById(R.id.tvStudentSection);
        tvStudentDOB = rootView.findViewById(R.id.tvStudentDOB);
        btnEditProfile = rootView.findViewById(R.id.btnEditProfile);
        btnLogout = rootView.findViewById(R.id.btnLogout);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> showEditDialog());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void showEditDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_student_profile, null);
        TextInputEditText fnameInput = dialogView.findViewById(R.id.fnameInput);
        TextInputEditText lnameInput = dialogView.findViewById(R.id.lnameInput);
        TextInputEditText dobInput = dialogView.findViewById(R.id.dobInput);

        // Set current values
        String[] nameParts = tvStudentName.getText().toString().split(" ");
        fnameInput.setText(nameParts[0]);
        lnameInput.setText(nameParts.length > 1 ? nameParts[1] : "");
        dobInput.setText(tvStudentDOB.getText().toString());

        // Setup date picker
        dobInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dobInput.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save", (dialog, which) -> {
                String fname = fnameInput.getText().toString().trim();
                String lname = lnameInput.getText().toString().trim();
                String dob = dobInput.getText().toString().trim();

                if (fname.isEmpty() || lname.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveStudentData(fname, lname, dob);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void fetchStudentData() {
        setLoading(true);
        String email = prefsManager.getUserEmail();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
            Toast.makeText(requireContext(), "Error creating request", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            UrlManager.URL_GET_STUDENT_DATA,
            requestBody,
            response -> {
                Log.d(TAG, "Response received: " + response.toString());
                handleStudentDataResponse(response);
            },
            error -> {
                Log.e(TAG, "Error fetching student data: " + error.getMessage());
                handleError(error);
            }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void handleStudentDataResponse(JSONObject response) {
        setLoading(false);
        try {
            String status = response.getString("status");
            if (status.equals("success")) {
                JSONObject student = response.getJSONObject("student");
                
                tvStudentName.setText(student.getString("fname") + " " + student.getString("lname"));
                tvStudentEmail.setText(student.getString("email"));
                tvStudentDOB.setText(student.getString("date_of_birth"));
                tvStudentGrade.setText("Grade: " + student.getInt("grade_number"));
                tvStudentSection.setText("Section: " + student.getString("section"));
            } else {
                String message = response.optString("message", "Failed to load student data");
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error: " + e.getMessage());
            Toast.makeText(requireContext(), "Error parsing student data", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveStudentData(String fname, String lname, String dateOfBirth) {
        setLoading(true);
        String email = prefsManager.getUserEmail();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("fname", fname);
            requestBody.put("lname", lname);
            requestBody.put("date_of_birth", dateOfBirth);
            requestBody.put("class_id", 1); // You might want to get this from somewhere
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
            Toast.makeText(requireContext(), "Error creating request", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            UrlManager.URL_EDIT_STUDENT_DATA,
            requestBody,
            response -> {
                Log.d(TAG, "Response received: " + response.toString());
                handleSaveResponse(response);
            },
            error -> {
                Log.e(TAG, "Error saving student data: " + error.getMessage());
                handleError(error);
            }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void handleSaveResponse(JSONObject response) {
        setLoading(false);
        try {
            String status = response.getString("status");
            String message = response.getString("message");
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            
            if (status.equals("success")) {
                // Refresh the data
                fetchStudentData();
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error: " + e.getMessage());
            Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        prefsManager.clearUserData();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void handleError(VolleyError error) {
        setLoading(false);
        String errorMessage = "Error: ";
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
        btnEditProfile.setEnabled(!isLoading);
    }
} 