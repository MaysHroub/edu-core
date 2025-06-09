package com.bzu.educore.activity.registrar.ui.subject_management;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bzu.educore.databinding.FragmentModifySubjectBinding;
import com.bzu.educore.model.school.Subject;
import com.bzu.educore.util.DialogUtils;
import com.bzu.educore.util.InputValidator;

public class ModifySubjectFragment extends Fragment {

    private FragmentModifySubjectBinding binding;
    private SubjectManagementViewModel subjectManagementViewModel;
    private Subject subject;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentModifySubjectBinding.inflate(inflater, container, false);
        subjectManagementViewModel = new ViewModelProvider(this).get(SubjectManagementViewModel.class);

        subject = ModifySubjectFragmentArgs.fromBundle(getArguments()).getSubject();

        fillSpnrSemester();
        fillSpnrGrades();
        fillViewWithSubjectData();

        binding.btnSubjSave.setOnClickListener(v -> {
            if (!InputValidator.validateEditTexts(binding.edtxtSubjTitle)) {
                Toast.makeText(requireContext(), "Title can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            subject.setTitle(binding.edtxtSubjTitle.getText().toString());
            subject.setGradeNumber((Integer) binding.spnrSubjGrade.getSelectedItem());
            subject.setSemesterNumber((Integer) binding.spnrSubjSemester.getSelectedItem());
            subjectManagementViewModel.updateSubject(subject);
        });

        binding.btnSubjDelete.setOnClickListener(v -> {
            DialogUtils.showConfirmationDialog(
                    requireContext(),
                    "Delete Subject",
                    "Are you sure you want to delete it?",
                    () -> {
                        subjectManagementViewModel.deleteSubjectById(subject.getId());
                        subjectManagementViewModel.getDeletionSuccess().observe(getViewLifecycleOwner(), success -> {
                            if (!success) return;
                            navigateBack();
                        });
                    }
            );
        });

        binding.imgBack.setOnClickListener(v -> {
            navigateBack();
        });

        return binding.getRoot();
    }

    private void navigateBack() {
        NavDirections action = ModifySubjectFragmentDirections.actionModifySubjectFragmentToViewAllSubjects();
        Navigation.findNavController(requireView()).navigate(action);
    }

    private void fillViewWithSubjectData() {
        binding.edtxtSubjId.setText(subject.getId());
        binding.edtxtSubjTitle.setText(subject.getTitle());
        binding.spnrSubjSemester.setSelection(subject.getSemesterNumber() == 1 ? 0 : 1, true);
        binding.spnrSubjGrade.setSelection(subject.getGradeNumber() - (Integer) binding.spnrSubjGrade.getAdapter().getItem(0), true);
    }

    private void fillSpnrGrades() {
        subjectManagementViewModel.getGrades().observe(getViewLifecycleOwner(), grades -> {
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    grades
            );
            binding.spnrSubjGrade.setAdapter(adapter);
        });
        subjectManagementViewModel.fetchAllGrades();
    }

    private void fillSpnrSemester() {
        ArrayAdapter<Integer> semesterAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                new Integer[] {1, 2}
        );
        binding.spnrSubjSemester.setAdapter(semesterAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}













