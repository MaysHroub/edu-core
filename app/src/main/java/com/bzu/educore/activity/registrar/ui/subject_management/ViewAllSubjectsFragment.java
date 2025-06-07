package com.bzu.educore.activity.registrar.ui.subject_management;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.educore.R;
import com.bzu.educore.adapter.registrar.SubjectAdapter;
import com.bzu.educore.databinding.FragmentSubjectViewBinding;
import com.bzu.educore.model.school.Subject;

public class ViewAllSubjectsFragment extends Fragment implements SubjectAdapter.OnItemClickListener {

    private FragmentSubjectViewBinding binding;
    private SubjectManagementViewModel subjectManagementViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        subjectManagementViewModel = new ViewModelProvider(this).get(SubjectManagementViewModel.class);
        binding = FragmentSubjectViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        subjectManagementViewModel.getSubjects().observe(getViewLifecycleOwner(), subjects -> {
            SubjectAdapter adapter = new SubjectAdapter(subjects, ViewAllSubjectsFragment.this);
            binding.rclrviewSubjects.setAdapter(adapter);
        });

        subjectManagementViewModel.fetchAllSubjects();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Subject subject) {
        subjectManagementViewModel.setCurrentSubject(subject);
        ModifySubjectFragment fragment = new ModifySubjectFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_registrar_main, fragment);
        transaction.addToBackStack(null); // so the user can navigate back
        transaction.commit();
    }
}