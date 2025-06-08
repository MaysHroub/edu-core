package com.bzu.educore.activity.registrar.ui.homeroom_teacher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.educore.R;
import com.bzu.educore.activity.registrar.ui.teacher_management.TeacherManagementViewModel;
import com.bzu.educore.adapter.registrar.HomeroomTeacherAdapter;
import com.bzu.educore.adapter.registrar.UserAdapter;
import com.bzu.educore.databinding.FragmentAssignHomeroomTeacherBinding;
import com.bzu.educore.databinding.FragmentViewAllTeachersBinding;
import com.bzu.educore.model.user.Person;

import java.util.ArrayList;
import java.util.List;

public class AssignHomeroomTeacherFragment extends Fragment {

    private FragmentAssignHomeroomTeacherBinding binding;
    private TeacherManagementViewModel teacherManagementViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        teacherManagementViewModel = new ViewModelProvider(this).get(TeacherManagementViewModel.class);
        binding = FragmentAssignHomeroomTeacherBinding.inflate(inflater, container, false);

        teacherManagementViewModel.getHomeroomTeacherAssigns().observe(getViewLifecycleOwner(), assigns -> {
            HomeroomTeacherAdapter adapter = new HomeroomTeacherAdapter(assigns);
            binding.rclrviewHomeroomTchr.setAdapter(adapter);
        });
        teacherManagementViewModel.fetchTeacherClassroomAssigns();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}