package com.bzu.educore.activity.registrar.ui.student_registration;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bzu.educore.databinding.FragmentStudentRegistrationBinding;
import com.bzu.educore.util.InputValidator;

import java.util.Calendar;

public class StudentRegistrationFragment extends Fragment {

    private FragmentStudentRegistrationBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StudentRegistrationViewModel studentRegistrationViewModel =
                new ViewModelProvider(this).get(StudentRegistrationViewModel.class);

        binding = FragmentStudentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        populateFields();

        binding.btnStdDob.setOnClickListener(v -> showDatePickerDialog());
        binding.btnStdRegister.setOnClickListener(v -> addStudentToDB(studentRegistrationViewModel));

        return root;
    }

    private void populateFields() {

    }

    private void addStudentToDB(StudentRegistrationViewModel studentRegistrationViewModel) {

    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Month is 0-based in Calendar
                    String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    binding.btnStdDob.setText(date);
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