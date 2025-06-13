package com.bzu.educore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.activity.registrar.RegistrarMainActivity;
import com.bzu.educore.activity.teacher.TeacherMainActivity;
import com.bzu.educore.util.SharedPreferencesManager;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.bzu.educore.activity.student.StudentMainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText emailInput, passwordInput;
    private MaterialButton loginButton;
    private View progressBar;
    private SharedPreferencesManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefsManager = new SharedPreferencesManager(this);
        
        // Check if user is already logged in
        if (prefsManager.isLoggedIn()) {
            String userType = prefsManager.getUserType();
            Log.d(TAG, "User is logged in with type: " + userType);
            if (userType != null && !userType.isEmpty()) {
                navigateToDashboard(userType);
                finish();
                return;
            }
        }
        
        setContentView(R.layout.activity_login);
        initializeViews();
        setupLoginButton();
        
        // Log the login URL for debugging
        Log.d(TAG, "Login URL: " + UrlManager.URL_LOGIN);
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                performLogin();
            }
        });
    }

    private boolean validateInputs() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return false;
        }

        return true;
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        setLoading(true);

        // Create JSON request body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            Log.d(TAG, "Request body: " + jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            setLoading(false);
            return;
        }

        // Create JSON request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                UrlManager.URL_LOGIN,
                jsonBody,
                this::handleLoginResponse,
                this::handleError
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add request to queue
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void handleError(VolleyError error) {
        setLoading(false);
        String errorMessage = "Connection failed. ";
        
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            String responseData = new String(error.networkResponse.data);
            Log.e(TAG, "Error Status Code: " + statusCode);
            Log.e(TAG, "Error Response: " + responseData);
            errorMessage += "Server returned status code: " + statusCode;
        } else if (error.getCause() != null) {
            Log.e(TAG, "Error Cause: " + error.getCause().getMessage());
            errorMessage += error.getCause().getMessage();
        } else {
            Log.e(TAG, "Error: " + error.toString());
            errorMessage += "Please check your internet connection.";
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void handleLoginResponse(JSONObject response) {
        setLoading(false);
        Log.d(TAG, "Response: " + response.toString());
        
        try {
            boolean success = response.getBoolean("success");
            String message = response.getString("message");
            
            if (success) {
                String userType = response.getString("user_type");
                String email = emailInput.getText().toString().trim();
                
                // Save user data using SharedPreferencesManager
                prefsManager.saveUserEmail(email);
                prefsManager.saveUserType(userType);
                prefsManager.setLoggedIn(true);
                
                Log.d(TAG, "Saved user type: " + userType);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                navigateToDashboard(userType);
                finish();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error: " + e.getMessage());
            Toast.makeText(this, "Error processing server response", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard(String userType) {
        Intent intent;
        Log.d(TAG, "Navigating to dashboard for user type: " + userType);
        
        switch (userType.toLowerCase()) {
            case "teacher":
                intent = new Intent(this, TeacherMainActivity.class);
                break;
            case "registrar":
                intent = new Intent(this, RegistrarMainActivity.class);
                break;
            case "student":
                intent = new Intent(this, StudentMainActivity.class);
                break;
            default:
                Log.e(TAG, "Unknown user type: " + userType);
                Toast.makeText(this, "Unknown user type", Toast.LENGTH_SHORT).show();
                return;
        }
        
        // Clear any existing tasks and start fresh
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
        emailInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
    }
}
