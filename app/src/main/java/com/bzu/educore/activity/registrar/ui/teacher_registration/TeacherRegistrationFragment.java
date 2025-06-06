package com.bzu.educore.activity.registrar.ui.teacher_registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bzu.educore.databinding.FragmentTeacherRegistrationBinding;

public class TeacherRegistrationFragment extends Fragment {

    private FragmentTeacherRegistrationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TeacherRegistrationViewModel teacherRegistrationViewModel =
                new ViewModelProvider(this).get(TeacherRegistrationViewModel.class);

        binding = FragmentTeacherRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        teacherRegistrationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}