package com.bzu.educore.activity.registrar.ui.subject_management;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bzu.educore.R;
import com.bzu.educore.databinding.FragmentSubjectModifyBinding;
import com.bzu.educore.databinding.FragmentSubjectViewBinding;
import com.bzu.educore.model.school.Subject;
import com.bzu.educore.util.InputValidator;

public class SubjectModifyFragment extends Fragment {

    private FragmentSubjectModifyBinding binding;
    private SubjectManagementViewModel subjectManagementViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSubjectModifyBinding.inflate(inflater, container, false);
        subjectManagementViewModel = new ViewModelProvider(this).get(SubjectManagementViewModel.class);

        subjectManagementViewModel.getCurrentSubject().observe(getViewLifecycleOwner(), subject -> {
            binding.edtxtSubjId.setText(subject.getId());
            binding.edtxtSubjTitle.setText(subject.getTitle());
            binding.spnrSubjSemester.setSelection(subject.getSemesterNumber() == 1 ? 0 : 1, true);
            binding.spnrSubjGrade.setSelection(subject.getGradeNumber() - (Integer) binding.spnrSubjGrade.getAdapter().getItem(0), true);
        });
        
        binding.btnSubjSave.setOnClickListener(v -> {
            if (!InputValidator.validateEditTexts(binding.edtxtSubjTitle)) {
                Toast.makeText(requireContext(), "Title can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Subject modifiedSubject = subjectManagementViewModel.getCurrentSubject().getValue();
            assert modifiedSubject != null;
            modifiedSubject.setTitle(binding.edtxtSubjTitle.getText().toString());
            modifiedSubject.setGradeNumber((Integer) binding.spnrSubjGrade.getSelectedItem());
            modifiedSubject.setSemesterNumber((Integer) binding.spnrSubjSemester.getSelectedItem());
            subjectManagementViewModel.updateSubject(modifiedSubject);
        });

        return binding.getRoot();
    }

}













