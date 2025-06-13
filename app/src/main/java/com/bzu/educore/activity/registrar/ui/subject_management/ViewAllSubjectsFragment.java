package com.bzu.educore.activity.registrar.ui.subject_management;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.educore.R;
import com.bzu.educore.activity.registrar.ui.teacher_management.ViewAllTeachersFragmentDirections;
import com.bzu.educore.adapter.registrar.SubjectAdapter;
import com.bzu.educore.databinding.FragmentViewAllSubjectsBinding;
import com.bzu.educore.listener.OnItemClickListener;
import com.bzu.educore.model.school.Subject;

public class ViewAllSubjectsFragment extends Fragment implements OnItemClickListener<Subject> {

    private FragmentViewAllSubjectsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SubjectManagementViewModel subjectManagementViewModel = new ViewModelProvider(this).get(SubjectManagementViewModel.class);
        binding = FragmentViewAllSubjectsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        subjectManagementViewModel.getSubjects().observe(getViewLifecycleOwner(), subjects -> {
            SubjectAdapter adapter = new SubjectAdapter(subjects, ViewAllSubjectsFragment.this);
            binding.rclrviewSubjects.setAdapter(adapter);
            binding.rclrviewSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        });
        subjectManagementViewModel.fetchAllSubjects();

        binding.fltbtnAddSubject.setOnClickListener(v -> {
            NavDirections action = ViewAllSubjectsFragmentDirections.actionViewAllSubjectsFragmentToModifySubjectFragment();
            Navigation.findNavController(requireView()).navigate(action);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Subject subject) {
        ViewAllSubjectsFragmentDirections.ActionViewAllSubjectsFragmentToModifySubjectFragment action =
                ViewAllSubjectsFragmentDirections.actionViewAllSubjectsFragmentToModifySubjectFragment();
        action.setSubject(subject);
        Navigation.findNavController(requireView()).navigate(action);
    }
}