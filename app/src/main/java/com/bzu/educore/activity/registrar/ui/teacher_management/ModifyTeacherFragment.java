package com.bzu.educore.activity.registrar.ui.teacher_management;

import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bzu.educore.databinding.FragmentModifyTeacherBinding;
import com.bzu.educore.model.school.Subject;
import com.bzu.educore.util.DialogUtils;
import com.bzu.educore.util.InputValidator;

import java.time.LocalDate;
import java.util.Calendar;

public class ModifyTeacherFragment extends Fragment {

    private FragmentModifyTeacherBinding binding;
    private TeacherManagementViewModel teacherManagementViewModel;
    private LocalDate dob;
    private int index;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        teacherManagementViewModel =
                new ViewModelProvider(this).get(TeacherManagementViewModel.class);
        binding = FragmentModifyTeacherBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fillSubjectsSpinner();
        binding.btnTchrSave.setOnClickListener(v -> saveTeacher());
        binding.btnTchrDelete.setOnClickListener(v -> deleteTeacher());
        binding.btnTchrDob.setOnClickListener(v -> showDatePickerDialog());

        index = teacherManagementViewModel.getCurrentIndex().getValue();

        if (index == -1)
            generateCredentials();
        else {
            fillViewWithData();
            binding.btnTchrDelete.setVisibility(VISIBLE);
        }

        return root;
    }

    private void deleteTeacher() {
        DialogUtils.showConfirmationDialog(
                requireContext(),
                "Delete Teacher",
                "Are you sure you want to delete this teacher?",
                () -> {
                    int teacherId = Integer.parseInt(binding.edttxtTchrId.getText().toString());
                    teacherManagementViewModel.deleteTeacherById(teacherId);
                    teacherManagementViewModel.getDeletionSuccess().observe(getViewLifecycleOwner(), success -> {
                        if (!success) return;
                        requireActivity().getSupportFragmentManager().popBackStack();
                    });
                }
        );
    }

    private void fillViewWithData() {
        DummyTeacher teacher = teacherManagementViewModel.getTeachers().getValue().get(index);
        binding.edttxtTchrId.setText(teacher.getId()+"");
        binding.edttxtTchrEmail.setText(teacher.getEmail());
        binding.edttxtTchrFname.setText(teacher.getFname());
        binding.edttxtTchrLname.setText(teacher.getLname());
        binding.edttxtTchrPhone.setText(teacher.getPhoneNumber());
        String date = teacher.getDateOfBirth().getYear() + "-" + teacher.getDateOfBirth().getMonthValue() + "-" + teacher.getDateOfBirth().getDayOfMonth();
        binding.btnTchrDob.setText(date);
        int pos = teacherManagementViewModel.getSubjects().getValue().indexOf(teacher.getSubjectTaught());
        binding.spnrTchrSubject.setSelection(pos);
    }

    private void generateCredentials() {
        teacherManagementViewModel.getTeacherId().observe(getViewLifecycleOwner(), teacherId -> {
            String generatedEmail = String.format("%d@teacher.educore.edu", teacherId);
            binding.edttxtTchrId.setText(teacherId+"");
            binding.edttxtTchrEmail.setText(generatedEmail);
        });
        teacherManagementViewModel.generateTeacherId();
    }

    private void fillSubjectsSpinner() {
        teacherManagementViewModel.getSubjects().observe(getViewLifecycleOwner(), subjects -> {
            ArrayAdapter<Subject> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    subjects
            );
            binding.spnrTchrSubject.setAdapter(adapter);
        });
        teacherManagementViewModel.fetchAllSubjects();
    }

    private void saveTeacher() {
        if (!InputValidator.validateEditTexts(binding.edttxtTchrFname, binding.edttxtTchrLname, binding.edttxtTchrPhone) ||
                !InputValidator.validateSpinners(binding.spnrTchrSubject) ||
                dob == null) {
            Toast.makeText(requireContext(), "Please Fill Empty Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String fname = binding.edttxtTchrFname.getText().toString(),
                lname = binding.edttxtTchrLname.getText().toString(),
                phoneNumber = binding.edttxtTchrPhone.getText().toString();
        Subject subject = (Subject) binding.spnrTchrSubject.getSelectedItem();
        int generatedId = Integer.parseInt(binding.edttxtTchrId.getText().toString());
        String generatedEmail = binding.edttxtTchrEmail.getText().toString();
        // TODO: replace dummy-teacher with actual teacher class
        DummyTeacher teacher = new DummyTeacher(generatedId, fname, lname, generatedEmail, phoneNumber, subject, dob);
        if (index == -1)
            teacherManagementViewModel.registerTeacher(teacher);
        else
            teacherManagementViewModel.updateTeacher(teacher);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog (
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Month is 0-based in Calendar
                    String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    binding.btnTchrDob.setText(date);
                    dob = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}