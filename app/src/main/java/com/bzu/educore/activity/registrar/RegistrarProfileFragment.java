package com.bzu.educore.activity.registrar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrarProfileFragment extends Fragment {

    private static final String TAG = "RegistrarProfileFragment";
    private TextView tvName, tvEmail, tvEmailInfo;
    private MaterialButton btnLogout;
    private SharedPreferencesManager prefsManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsManager = new SharedPreferencesManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registrar_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email_header);
        tvEmailInfo = view.findViewById(R.id.tv_email_info);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Load registrar data
        loadRegistrarData();

        // Set up logout button
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void loadRegistrarData() {
        String email = prefsManager.getUserEmail();
        if (email == null || email.isEmpty()) {
            Toast.makeText(requireContext(), "Email not found", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating request body", e);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.POST,
            UrlManager.URL_GET_REGISTRAR_DATA,
            requestBody,
            this::handleRegistrarDataResponse,
            this::handleError
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void handleRegistrarDataResponse(JSONObject response) {
        try {
            if (response.getString("status").equals("success")) {
                JSONObject registrar = response.getJSONObject("registrar");
                
                // Display registrar data
                String name = registrar.getString("name");
                String email = registrar.getString("email");

                tvName.setText(name);
                tvEmail.setText(email);
                tvEmailInfo.setText(email);
            } else {
                String message = response.getString("message");
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing response", e);
            Toast.makeText(requireContext(), "Error processing server response", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(VolleyError error) {
        Log.e(TAG, "Error loading registrar data", error);
        Toast.makeText(requireContext(), "Error loading profile data", Toast.LENGTH_SHORT).show();
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> logout())
            .setNegativeButton("No", null)
            .show();
    }

    private void logout() {
        // Clear user data
        prefsManager.clearUserData();
        
        // Navigate to login screen
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
