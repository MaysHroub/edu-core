package com.bzu.educore.activity.registrar.ui.student_management;

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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bzu.educore.databinding.FragmentModifyStudentBinding;
import com.bzu.educore.model.school.Classroom;
import com.bzu.educore.model.user.Student;
import com.bzu.educore.util.DialogUtils;
import com.bzu.educore.util.InputValidator;
import com.bzu.educore.util.PasswordGenerator;

import java.time.LocalDate;
import java.util.Calendar;

public class ModifyStudentFragment extends Fragment {

    private FragmentModifyStudentBinding binding;
    private StudentManagementViewModel studentManagementViewModel;
    private LocalDate dob;
    private Student student;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        studentManagementViewModel =
                new ViewModelProvider(this).get(StudentManagementViewModel.class);
        binding = FragmentModifyStudentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        student = ModifyStudentFragmentArgs.fromBundle(getArguments()).getStudent();

        fillClassroomSpinner();
        binding.btnSaveStd.setOnClickListener(v -> saveStudent());
        binding.btnDeleteStd.setOnClickListener(v -> deleteStudent());
        binding.btnStdDob.setOnClickListener(v -> showDatePickerDialog());

        if (student == null)
            generateCredentials();
        else {
            fillViewWithData();
            binding.btnDeleteStd.setVisibility(VISIBLE);
        }

        return root;
    }

    private void fillViewWithData() {
        binding.edttxtStdId.setText(student.getId()+"");
        binding.edttxtStdEmail.setText(student.getEmail());
        binding.edttxtStdPass.setText(student.getPassword());
        binding.edttxtStdFname.setText(student.getFname());
        binding.edttxtStdLname.setText(student.getLname());
        String date = student.getDateOfBirth().getYear() + "-" + student.getDateOfBirth().getMonthValue() + "-" + student.getDateOfBirth().getDayOfMonth();
        binding.btnStdDob.setText(date);
        studentManagementViewModel.getClassrooms().observe(getViewLifecycleOwner(), classrooms -> {
            int pos = studentManagementViewModel.getClassrooms().getValue().indexOf(new Classroom(student.getClassId()));
            binding.spnrStdClassroom.setSelection(pos);
        });
        dob = student.getDateOfBirth();
    }

    private void generateCredentials() {
        studentManagementViewModel.getNextStudentId().observe(getViewLifecycleOwner(), studentId -> {
            String generatedEmail = String.format("%d@student.educore.edu", studentId);
            binding.edttxtStdId.setText(studentId+"");
            binding.edttxtStdEmail.setText(generatedEmail);
            binding.edttxtStdPass.setText(PasswordGenerator.generatePassword());
        });
        studentManagementViewModel.generateStudentId();
    }

    private void fillClassroomSpinner() {
        studentManagementViewModel.getClassrooms().observe(getViewLifecycleOwner(), classrooms -> {
            ArrayAdapter<Classroom> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    classrooms
            );
            binding.spnrStdClassroom.setAdapter(adapter);
        });
        studentManagementViewModel.fetchAllClassrooms();
    }

    private void saveStudent() {
        if (!InputValidator.validateEditTexts(binding.edttxtStdFname, binding.edttxtStdLname) ||
                !InputValidator.validateSpinners(binding.spnrStdClassroom) ||
                dob == null) {
            Toast.makeText(requireContext(), "Please Fill Empty Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String fname = binding.edttxtStdFname.getText().toString(),
                lname = binding.edttxtStdLname.getText().toString();
        Classroom classroom = (Classroom) binding.spnrStdClassroom.getSelectedItem();
        String stdEmail = binding.edttxtStdEmail.getText().toString(),
                stdPass = binding.edttxtStdPass.getText().toString();

        // TODO: replace dummy-student with actual student class
        if (student == null) {
            Student studentTemp = new Student(fname, lname, stdEmail, stdPass, dob, classroom.getId());
            studentManagementViewModel.registerStudent(studentTemp);
            studentManagementViewModel.getAdditionSuccess().observe(getViewLifecycleOwner(), success -> {
                if (!success) return;
                generateCredentials();
                binding.edttxtStdFname.setText("");
                binding.edttxtStdLname.setText("");
                binding.spnrStdClassroom.setSelection(0);
            });
        } else {
            student.setFname(fname);
            student.setLname(lname);
            student.setClassId(classroom.getId());
            studentManagementViewModel.updateStudent(student);
        }
    }

    private void deleteStudent() {
        DialogUtils.showConfirmationDialog(
                requireContext(),
                "Delete Student",
                "Are you sure you want to delete this student?",
                () -> {
                    int studentId = Integer.parseInt(binding.edttxtStdId.getText().toString());
                    studentManagementViewModel.deleteStudentById(studentId);
                    studentManagementViewModel.getDeletionSuccess().observe(getViewLifecycleOwner(), success -> {
                        if (success)
                            navigateBack();
                    });
                }
        );
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

    private void navigateBack() {
        NavDirections action = ModifyStudentFragmentDirections.actionModifyStudentFragmentToViewAllStudents();
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}