package com.bzu.educore.activity.registrar.ui.subject_management;

import static android.view.View.VISIBLE;

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

        binding.btnSubjSave.setOnClickListener(v -> saveSubject());
        binding.btnSubjDelete.setOnClickListener(v -> deleteSubject());

        fillSpnrSemester();
        fillSpnrGrades();

        if (subject != null) {
            fillViewWithSubjectData();
            binding.edtxtSubjId.setVisibility(VISIBLE);
            binding.txtSubjId.setVisibility(VISIBLE);
            binding.btnSubjDelete.setVisibility(VISIBLE);
        }

        return binding.getRoot();
    }

    private void deleteSubject() {
        DialogUtils.showConfirmationDialog(
                requireContext(),
                "Delete Subject",
                "Are you sure you want to delete it?",
                () -> {
                    subjectManagementViewModel.deleteSubjectById(subject.getId());
                    subjectManagementViewModel.getDeletionSuccess().observe(getViewLifecycleOwner(), success -> {
                        if (success)
                            navigateBack();
                    });
                }
        );
    }

    private void saveSubject() {
        if (!InputValidator.validateEditTexts(binding.edtxtSubjTitle, binding.edtxtSubjDesc)) {
            Toast.makeText(requireContext(), "Please fill empty fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = binding.edtxtSubjTitle.getText().toString(),
                description = binding.edtxtSubjDesc.getText().toString();
        Integer gradeNum = (Integer) binding.spnrSubjGrade.getSelectedItem(),
                semesterNum = (Integer) binding.spnrSubjSemester.getSelectedItem();

        if (subject == null) {
            Subject subjectTemp = new Subject(title, description, gradeNum, semesterNum);
            subjectManagementViewModel.addNewSubject(subjectTemp);
            subjectManagementViewModel.getAdditionSuccess().observe(getViewLifecycleOwner(), success -> {
                if (!success) return;
                binding.edtxtSubjTitle.setText("");
                binding.edtxtSubjDesc.setText("");
                binding.spnrSubjGrade.setSelection(0);
                binding.spnrSubjSemester.setSelection(0);
            });
        } else {
            subject.setTitle(title);
            subject.setDescription(description);
            subject.setGradeNumber(gradeNum);
            subject.setSemesterNumber(semesterNum);
            subjectManagementViewModel.updateSubject(subject);
        }
    }

    private void navigateBack() {
        NavDirections action = ModifySubjectFragmentDirections.actionModifySubjectFragmentToViewAllSubjects();
        Navigation.findNavController(requireView()).navigate(action);
    }

    private void fillViewWithSubjectData() {
        binding.edtxtSubjId.setText(subject.getId()+"");
        binding.edtxtSubjTitle.setText(subject.getTitle());
        binding.edtxtSubjDesc.setText(subject.getDescription());
        binding.spnrSubjSemester.setSelection(subject.getSemesterNumber() == 1 ? 0 : 1, true);
        subjectManagementViewModel.getGrades().observe(getViewLifecycleOwner(), grades -> {
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













