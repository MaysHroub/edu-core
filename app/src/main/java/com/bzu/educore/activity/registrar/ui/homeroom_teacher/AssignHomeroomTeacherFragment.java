package com.bzu.educore.activity.registrar.ui.homeroom_teacher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bzu.educore.model.school.Classroom;
import com.bzu.educore.activity.registrar.ui.student_management.StudentManagementViewModel;
import com.bzu.educore.model.user.Teacher;
import com.bzu.educore.activity.registrar.ui.teacher_management.TeacherManagementViewModel;
import com.bzu.educore.databinding.FragmentAssignHomeroomTeacherBinding;

import java.util.Objects;

public class AssignHomeroomTeacherFragment extends Fragment {

    private FragmentAssignHomeroomTeacherBinding binding;
    private TeacherManagementViewModel teacherManagementViewModel;
    private StudentManagementViewModel studentManagementViewModel;
    private Classroom classroom;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            classroom = (Classroom) getArguments().getSerializable("classroom");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        teacherManagementViewModel = new ViewModelProvider(this).get(TeacherManagementViewModel.class);
        studentManagementViewModel = new ViewModelProvider(this).get(StudentManagementViewModel.class);
        binding = FragmentAssignHomeroomTeacherBinding.inflate(inflater, container, false);

        fillTeacherSpinner();
        setAssignedTeacherPos();

        binding.txtClassroom.setText(classroom.toString());

        binding.btnAssign.setOnClickListener(v -> {
            Teacher teacher = (Teacher) binding.spnrTchrs.getSelectedItem();
            classroom.setHomeroomTeacherId(teacher.getId());
            studentManagementViewModel.updateClassroom(classroom);
        });

        return binding.getRoot();
    }

    private void setAssignedTeacherPos() {
        teacherManagementViewModel.getTeachers().observe(getViewLifecycleOwner(), teachers -> {
            int pos = -1;
            for (int i = 0; i < teachers.size(); i++)
                if (Objects.equals(teachers.get(i).getId(), classroom.getHomeroomTeacherId())) {
                    pos = i;
                    break;
                }
            binding.spnrTchrs.setSelection(pos);
        });
    }

    private void fillTeacherSpinner() {
        teacherManagementViewModel.getTeachers().observe(getViewLifecycleOwner(), teachers -> {
            teachers.add(0, new Teacher());
            ArrayAdapter<Teacher> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    teachers
            );
            binding.spnrTchrs.setAdapter(adapter);
        });
        teacherManagementViewModel.fetchAllTeachers();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}