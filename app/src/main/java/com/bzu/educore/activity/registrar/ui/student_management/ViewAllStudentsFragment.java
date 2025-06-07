package com.bzu.educore.activity.registrar.ui.student_management;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.educore.R;
import com.bzu.educore.databinding.FragmentStudentRegistrationBinding;
import com.bzu.educore.databinding.FragmentViewAllStudentsBinding;

public class ViewAllStudentsFragment extends Fragment {

    private FragmentViewAllStudentsBinding binding;
    private StudentManagementViewModel studentManagementViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        studentManagementViewModel =
                new ViewModelProvider(this).get(StudentManagementViewModel.class);

        binding = FragmentViewAllStudentsBinding.inflate(inflater, container, false);

        binding.layoutViewAllUsrs.fltbtnAddStd.setOnClickListener(v -> {
            StudentRegistrationFragment fragment = new StudentRegistrationFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_content_registrar_main, fragment);
            transaction.addToBackStack(null); // so the user can navigate back
            transaction.commit();
        });

        return binding.getRoot();
    }

}