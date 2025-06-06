package com.bzu.educore.activity.registrar.ui.student_registration;

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

import com.bzu.educore.R;
import com.bzu.educore.databinding.FragmentStudentRegistrationBinding;
import com.bzu.educore.model.user.Student;
import com.bzu.educore.util.InputValidator;
import com.bzu.educore.util.StudentCredentialsGenerator;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.Calendar;

public class StudentRegistrationFragment extends Fragment {

    private FragmentStudentRegistrationBinding binding;
    private StudentRegistrationViewModel studentRegistrationViewModel;
    private int generatedId;
    private String generatedEmail;
    private LocalDate dob;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        studentRegistrationViewModel =
                new ViewModelProvider(this).get(StudentRegistrationViewModel.class);

        binding = FragmentStudentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        studentRegistrationViewModel.getNumOfStudentsForCurrentYear().observe(getViewLifecycleOwner(), numOfStds -> {
            generatedId = StudentCredentialsGenerator.generateId(numOfStds);
            generatedEmail = StudentCredentialsGenerator.generateEmail(generatedId);
            binding.edttxtStdId.setText(generatedId+"");
            binding.edttxtStdEmail.setText(generatedEmail);
        });

        studentRegistrationViewModel.getClassrooms().observe(getViewLifecycleOwner(), classrooms -> {
            ArrayAdapter<DummyClassroom> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    classrooms
            );
            binding.spnrStdClassroom.setAdapter(adapter);
        });

        studentRegistrationViewModel.fetchNumOfStudentsForCurrentYear();
        studentRegistrationViewModel.fetchAllClassrooms();

        binding.btnStdDob.setOnClickListener(v -> showDatePickerDialog());
        binding.btnStdRegister.setOnClickListener(v -> addStudentToDB());

        return root;
    }

    private void addStudentToDB() {
        if (!InputValidator.validateEditTexts(binding.edttxtStdFname, binding.edttxtStdLname) ||
                !InputValidator.validateSpinners(binding.spnrStdClassroom) ||
                dob == null) {
            Toast.makeText(getContext(), "Please Fill Empty Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String fname = binding.edttxtStdFname.getText().toString(),
                lname = binding.edttxtStdLname.getText().toString();
        DummyClassroom classroom = (DummyClassroom) binding.spnrStdClassroom.getSelectedItem();
        // TODO: replace dummy-student with actual student class
        DummyStudent student = new DummyStudent(generatedId, fname, lname, generatedEmail, classroom, dob);
        studentRegistrationViewModel.registerStudent(student);
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