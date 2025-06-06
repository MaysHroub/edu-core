package com.bzu.educore.activity.registrar.ui.teacher_registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bzu.educore.databinding.FragmentTeacherRegistrationBinding;
import com.bzu.educore.util.CredentialsGenerator;

import java.time.LocalDate;

public class TeacherRegistrationFragment extends Fragment {

    private FragmentTeacherRegistrationBinding binding;
    private TeacherRegistrationViewModel teacherRegistrationViewModel;
    private int generatedId;
    private String generatedEmail;
    private LocalDate dob;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        teacherRegistrationViewModel =
                new ViewModelProvider(this).get(TeacherRegistrationViewModel.class);

        binding = FragmentTeacherRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        teacherRegistrationViewModel.getNumOfTeachers().observe(getViewLifecycleOwner(), numOfTeachers -> {
            generatedId = CredentialsGenerator.generateTeacherId(numOfTeachers);
            generatedEmail = CredentialsGenerator.generateTeacherEmail(generatedId);
            binding.edttxtTchrId.setText(generatedId+"");
            binding.edttxtTchrEmail.setText(generatedEmail);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}