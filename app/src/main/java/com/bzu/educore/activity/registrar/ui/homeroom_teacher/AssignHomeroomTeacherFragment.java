package com.bzu.educore.activity.registrar.ui.homeroom_teacher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bzu.educore.activity.registrar.ui.student_management.DummyClassroom;
import com.bzu.educore.activity.registrar.ui.student_management.StudentManagementViewModel;
import com.bzu.educore.activity.registrar.ui.teacher_management.DummyTeacher;
import com.bzu.educore.activity.registrar.ui.teacher_management.TeacherManagementViewModel;
import com.bzu.educore.databinding.FragmentAssignHomeroomTeacherBinding;
import com.bzu.educore.util.DialogUtils;

import java.util.List;
import java.util.Objects;

public class AssignHomeroomTeacherFragment extends Fragment {

    private FragmentAssignHomeroomTeacherBinding binding;
    private TeacherManagementViewModel teacherManagementViewModel;
    private StudentManagementViewModel studentManagementViewModel;
    private DummyClassroom classroom;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            classroom = (DummyClassroom) getArguments().getSerializable("classroom");
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
            DummyTeacher teacher = (DummyTeacher) binding.spnrTchrs.getSelectedItem();
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
            teachers.add(0, new DummyTeacher());
            ArrayAdapter<DummyTeacher> adapter = new ArrayAdapter<>(
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