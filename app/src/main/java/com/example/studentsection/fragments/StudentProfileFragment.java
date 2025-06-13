package com.example.studentsection.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bzu.educore.R;
import com.bzu.educore.activity.LoginActivity;
import com.bzu.educore.util.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;

public class StudentProfileFragment extends Fragment {
    private SharedPreferencesManager prefsManager;
    private TextView tvStudentName, tvStudentEmail;
    private MaterialButton btnEditProfile, btnChangePassword, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsManager = new SharedPreferencesManager(requireContext());
        initializeViews(view);
        setupClickListeners();
        loadUserData();
    }

    private void initializeViews(View view) {
        tvStudentName = view.findViewById(R.id.tvStudentName);
        tvStudentEmail = view.findViewById(R.id.tvStudentEmail);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            // TODO: Implement edit profile
            Toast.makeText(requireContext(), "Edit profile coming soon", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            // TODO: Implement change password
            Toast.makeText(requireContext(), "Change password coming soon", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserData() {
        String email = prefsManager.getUserEmail();
        tvStudentEmail.setText(email);
        // TODO: Load student name from API
    }

    private void logout() {
        prefsManager.clearUserData();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
} 