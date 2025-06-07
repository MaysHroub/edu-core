package com.bzu.educore.activity.registrar.ui.subject_management;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bzu.educore.R;
import com.bzu.educore.databinding.FragmentSubjectModifyBinding;
import com.bzu.educore.databinding.FragmentSubjectViewBinding;
import com.bzu.educore.model.school.Subject;
import com.bzu.educore.util.DialogUtils;
import com.bzu.educore.util.InputValidator;

public class SubjectModifyFragment extends Fragment {

    private FragmentSubjectModifyBinding binding;
    private SubjectManagementViewModel subjectManagementViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSubjectModifyBinding.inflate(inflater, container, false);
        subjectManagementViewModel = new ViewModelProvider(this).get(SubjectManagementViewModel.class);

        fillSpnrSemester();
        fillSpnrGrades();
        fillViewWithSubjectData();

        binding.btnSubjSave.setOnClickListener(v -> {
            if (!InputValidator.validateEditTexts(binding.edtxtSubjTitle)) {
                Toast.makeText(requireContext(), "Title can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Subject subject = subjectManagementViewModel.getCurrentSubject().getValue();
            assert subject != null;
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
                        subjectManagementViewModel.deleteCurrentSubject();
                        subjectManagementViewModel.getDeletionSuccess().observe(getViewLifecycleOwner(), success -> {
                            if (!success) return;
                            requireActivity().getSupportFragmentManager().popBackStack();
                        });
                    }
            );
        });

        binding.imgBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return binding.getRoot();
    }

    private void fillViewWithSubjectData() {
        subjectManagementViewModel.getCurrentSubject().observe(getViewLifecycleOwner(), subject -> {
            binding.edtxtSubjId.setText(subject.getId());
            binding.edtxtSubjTitle.setText(subject.getTitle());
            binding.spnrSubjSemester.setSelection(subject.getSemesterNumber() == 1 ? 0 : 1, true);
            binding.spnrSubjGrade.setSelection(subject.getGradeNumber() - (Integer) binding.spnrSubjGrade.getAdapter().getItem(0), true);
        });
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













