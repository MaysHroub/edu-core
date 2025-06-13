package com.bzu.educore.activity.teacher.ui.profile;

import android.app.DatePickerDialog;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.bzu.educore.util.VolleySingleton;
import com.bzu.educore.util.teacher.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;

public class TeacherProfileFragment extends Fragment {
    private static final String TAG = "TeacherProfileFragment";

    private static final String ARG_TEACHER_ID = "teacher_id";
    @Getter
    private int teacherId;

    private TextView tvTeacherName, tvTeacherEmail, tvPhoneNumber, tvDateOfBirth, tvSubject;
    private MaterialButton btnEditProfile, btnChangePassword, btnLogout;
    private SharedPreferencesManager prefsManager;
    private String currentEmail;

    public static TeacherProfileFragment newInstance(int teacherId) {
        TeacherProfileFragment fragment = new TeacherProfileFragment();
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
            Log.d(TAG, "Teacher ID received: " + teacherId);
        } else {
            Log.e(TAG, "No arguments received in onCreate");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        prefsManager = new SharedPreferencesManager(requireContext());
        currentEmail = prefsManager.getUserEmail();
        Log.d(TAG, "Current email from SharedPreferences: " + currentEmail);
        
        initializeViews(view);
        setupClickListeners();
        loadTeacherProfile();
    }

    private void initializeViews(View view) {
        tvTeacherName = view.findViewById(R.id.tvTeacherName);
        tvTeacherEmail = view.findViewById(R.id.tvTeacherEmail);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvDateOfBirth = view.findViewById(R.id.tvDateOfBirth);
        tvSubject = view.findViewById(R.id.tvSubject);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        tvTeacherEmail.setText(currentEmail);
        Log.d(TAG, "Views initialized");
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        Log.d(TAG, "Click listeners setup completed");
    }

    private void loadTeacherProfile() {
        if (currentEmail == null || currentEmail.isEmpty()) {
            Log.e(TAG, "Email is null or empty");
            Toast.makeText(requireContext(), "Email not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Loading profile for email: " + currentEmail + ", teacherId: " + teacherId);
        Log.d(TAG, "API URL: " + Constants.GET_TEACHER_PROFILE);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", currentEmail);
            requestBody.put("teacher_id", teacherId);
            Log.d(TAG, "Request body: " + requestBody.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error preparing request body", e);
            Toast.makeText(requireContext(), "Error preparing request", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Constants.GET_TEACHER_PROFILE,
                requestBody,
                response -> {
                    Log.d(TAG, "Response received: " + response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject teacherData = response.getJSONObject("teacher");
                            Log.d(TAG, "Teacher data: " + teacherData.toString());
                            updateUI(teacherData);
                        } else {
                            String message = response.getString("message");
                            Log.e(TAG, "API returned error: " + message);
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error", error);
                    String errorMessage = error.getMessage();
                    if (errorMessage != null) {
                        Log.e(TAG, "Error message: " + errorMessage);
                        if (errorMessage.contains("<!doctype")) {
                            Log.e(TAG, "Server returned HTML instead of JSON. Check API endpoint configuration.");
                            Toast.makeText(requireContext(), "Server configuration error. Please contact support.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to load profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "No error message available");
                        Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
        Log.d(TAG, "Request added to queue");
    }

    private void updateUI(JSONObject teacherData) throws JSONException {
        try {
            String fullName = teacherData.getString("fname") + " " + teacherData.getString("lname");
            Log.d(TAG, "Updating UI with name: " + fullName);
            
            tvTeacherName.setText(fullName);
            tvPhoneNumber.setText(teacherData.getString("phone_number"));
            tvDateOfBirth.setText(teacherData.getString("date_of_birth"));
            tvSubject.setText(teacherData.getString("subject_title"));
            
            Log.d(TAG, "UI update completed successfully");
        } catch (JSONException e) {
            Log.e(TAG, "Error updating UI", e);
            Toast.makeText(requireContext(), "Error displaying profile data", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditProfileDialog() {
        Log.d(TAG, "Showing edit profile dialog");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText etFirstName = dialogView.findViewById(R.id.etFirstName);
        TextInputEditText etLastName = dialogView.findViewById(R.id.etLastName);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etPhone);
        Button btnDate = dialogView.findViewById(R.id.btnDate);

        // Set current values with proper error handling
        String fullName = tvTeacherName.getText().toString().trim();
        Log.d(TAG, "Current full name: " + fullName);
        
        String[] nameParts = fullName.split("\\s+");
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        Log.d(TAG, "Split name - First: " + firstName + ", Last: " + lastName);
        
        etFirstName.setText(firstName);
        etLastName.setText(lastName);
        etPhone.setText(tvPhoneNumber.getText());
        btnDate.setText(tvDateOfBirth.getText());

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDate = sdf.format(calendar.getTime());
            Log.d(TAG, "Date selected: " + selectedDate);
            btnDate.setText(selectedDate);
        };

        btnDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String fname = etFirstName.getText().toString().trim();
                    String lname = etLastName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String dob = btnDate.getText().toString();

                    Log.d(TAG, "Saving profile - First: " + fname + ", Last: " + lname + ", Phone: " + phone + ", DOB: " + dob);

                    if (fname.isEmpty() || lname.isEmpty() || phone.isEmpty()) {
                        Log.e(TAG, "Validation failed - empty fields");
                        Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    updateProfile(fname, lname, phone, dob);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateProfile(String fname, String lname, String phone, String dob) {
        Log.d(TAG, "Updating profile with new data");
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", currentEmail);
            requestBody.put("fname", fname);
            requestBody.put("lname", lname);
            requestBody.put("phone_number", phone);
            requestBody.put("date_of_birth", dob);
            Log.d(TAG, "Update request body: " + requestBody.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error preparing update request", e);
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                Constants.UPDATE_TEACHER_PROFILE,
                requestBody,
                response -> {
                    Log.d(TAG, "Update response: " + response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            Log.d(TAG, "Profile updated successfully");
                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            loadTeacherProfile(); // Reload profile data
                        } else {
                            String message = response.getString("message");
                            Log.e(TAG, "Update failed: " + message);
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing update response", e);
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error during update", error);
                    String errorMessage = error.getMessage();
                    if (errorMessage != null) {
                        Log.e(TAG, "Error message: " + errorMessage);
                        Toast.makeText(requireContext(), "Failed to update profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
        Log.d(TAG, "Update request added to queue");
    }

    private void showChangePasswordDialog() {
        Log.d(TAG, "Showing change password dialog");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        
        // Find the password input fields
        TextInputEditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);

        // Create and show the dialog
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    // Get the input values
                    String currentPassword = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString().trim() : "";
                    String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
                    String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

                    // Log the input lengths for debugging
                    Log.d(TAG, "Password lengths - Current: " + currentPassword.length() + 
                              ", New: " + newPassword.length() + 
                              ", Confirm: " + confirmPassword.length());

                    // Validate each field
                    if (currentPassword.isEmpty()) {
                        Toast.makeText(requireContext(), "Current password is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPassword.isEmpty()) {
                        Toast.makeText(requireContext(), "New password is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (confirmPassword.isEmpty()) {
                        Toast.makeText(requireContext(), "Confirm password is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Check if passwords match
                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(requireContext(), "New passwords don't match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // If all validations pass, proceed with password change
                    changePassword(currentPassword, newPassword);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void changePassword(String currentPassword, String newPassword) {
        Log.d(TAG, "Changing password");
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", currentEmail);
            requestBody.put("current_password", currentPassword);
            requestBody.put("new_password", newPassword);
            Log.d(TAG, "Password change request body: " + requestBody.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error preparing password change request", e);
            Toast.makeText(requireContext(), "Error preparing request", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Constants.CHANGE_TEACHER_PASSWORD,
                requestBody,
                response -> {
                    Log.d(TAG, "Password change response: " + response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            Log.d(TAG, "Password changed successfully");
                            Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = response.getString("message");
                            Log.e(TAG, "Password change failed: " + message);
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing password change response", e);
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Network error during password change", error);
                    String errorMessage = error.getMessage();
                    if (errorMessage != null) {
                        Log.e(TAG, "Error message: " + errorMessage);
                        Toast.makeText(requireContext(), "Failed to change password: " + errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
        Log.d(TAG, "Password change request added to queue");
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        // Clear shared preferences
        prefsManager.clearUserData();
        
        // Navigate to login activity
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
} 