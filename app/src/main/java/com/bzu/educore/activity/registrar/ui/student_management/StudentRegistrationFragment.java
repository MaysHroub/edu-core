package com.bzu.educore.activity.registrar.ui.student_management;

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

import com.bzu.educore.databinding.FragmentStudentRegistrationBinding;
import com.bzu.educore.util.InputValidator;
import com.bzu.educore.util.CredentialsGenerator;

import java.time.LocalDate;
import java.util.Calendar;

public class StudentRegistrationFragment extends Fragment {

    private FragmentStudentRegistrationBinding binding;
    private StudentManagementViewModel studentManagementViewModel;
    private int generatedId;
    private String generatedEmail;
    private LocalDate dob;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        studentManagementViewModel =
                new ViewModelProvider(this).get(StudentManagementViewModel.class);

        binding = FragmentStudentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        studentManagementViewModel.getNumOfStudentsForCurrentYear().observe(getViewLifecycleOwner(), numOfStds -> {
            generatedId = CredentialsGenerator.generateStudentId(numOfStds);
            generatedEmail = CredentialsGenerator.generateStudentEmail(generatedId);
            binding.edttxtStdId.setText(generatedId+"");
            binding.edttxtStdEmail.setText(generatedEmail);
        });

        studentManagementViewModel.getClassrooms().observe(getViewLifecycleOwner(), classrooms -> {
            ArrayAdapter<DummyClassroom> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    classrooms
            );
            binding.spnrStdClassroom.setAdapter(adapter);
        });

        studentManagementViewModel.fetchNumOfStudentsForCurrentYear();
        studentManagementViewModel.fetchAllClassrooms();

        binding.btnStdDob.setOnClickListener(v -> showDatePickerDialog());
        binding.btnStdRegister.setOnClickListener(v -> addStudentToDB());

        return root;
    }

    private void addStudentToDB() {
        if (!InputValidator.validateEditTexts(binding.edttxtStdFname, binding.edttxtStdLname) ||
                !InputValidator.validateSpinners(binding.spnrStdClassroom) ||
                dob == null) {
            Toast.makeText(requireContext(), "Please Fill Empty Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String fname = binding.edttxtStdFname.getText().toString(),
                lname = binding.edttxtStdLname.getText().toString();
        DummyClassroom classroom = (DummyClassroom) binding.spnrStdClassroom.getSelectedItem();
        // TODO: replace dummy-student with actual student class
        DummyStudent student = new DummyStudent(generatedId, fname, lname, generatedEmail, classroom, dob);
        studentManagementViewModel.registerStudent(student);
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
                    binding.btnStdDob.setText(date);
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