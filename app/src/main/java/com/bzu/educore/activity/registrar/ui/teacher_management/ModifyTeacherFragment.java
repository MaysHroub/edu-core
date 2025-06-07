package com.bzu.educore.activity.registrar.ui.teacher_management;

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
import com.bzu.educore.util.CredentialsGenerator;
import com.bzu.educore.util.InputValidator;

import java.time.LocalDate;
import java.util.Calendar;

public class ModifyTeacherFragment extends Fragment {

    private FragmentModifyTeacherBinding binding;
    private TeacherManagementViewModel teacherManagementViewModel;
    private LocalDate dob;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        teacherManagementViewModel =
                new ViewModelProvider(this).get(TeacherManagementViewModel.class);

        binding = FragmentModifyTeacherBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnTchrDob.setOnClickListener(v -> showDatePickerDialog());
        binding.btnTchrRegister.setOnClickListener(v -> registerTeacher());

        teacherManagementViewModel.getSubjects().observe(getViewLifecycleOwner(), subjects -> {
            ArrayAdapter<Subject> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    subjects
            );
            binding.spnrTchrSubject.setAdapter(adapter);
        });

        teacherManagementViewModel.fetchAllSubjects();

        return root;
    }

    private void registerTeacher() {
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
        // TODO: replace dummy-teacher with actual teacher class
        DummyTeacher teacher = new DummyTeacher(generatedId, fname, lname, generatedEmail, phoneNumber, subject, dob);
        teacherManagementViewModel.registerTeacher(teacher);
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