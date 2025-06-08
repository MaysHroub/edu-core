package com.bzu.educore.activity.registrar.ui.homeroom_teacher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bzu.educore.R;
import com.bzu.educore.activity.registrar.ui.student_management.DummyClassroom;
import com.bzu.educore.activity.registrar.ui.student_management.ModifyStudentFragment;
import com.bzu.educore.activity.registrar.ui.student_management.StudentManagementViewModel;
import com.bzu.educore.activity.registrar.ui.teacher_management.TeacherManagementViewModel;
import com.bzu.educore.databinding.FragmentAssignHomeroomTeacherBinding;
import com.bzu.educore.databinding.FragmentViewAllClassroomsBinding;

public class ViewAllClassroomsFragment extends Fragment {

    private StudentManagementViewModel studentManagementViewModel;
    FragmentViewAllClassroomsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        studentManagementViewModel = new ViewModelProvider(this).get(StudentManagementViewModel.class);
        binding = FragmentViewAllClassroomsBinding.inflate(inflater, container, false);

        fillClassroomListView();

        binding.lstClassrooms.setOnItemClickListener((parent, view, position, id) -> {
            DummyClassroom classroom = (DummyClassroom) parent.getItemAtPosition(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("classroom", classroom);
            AssignHomeroomTeacherFragment fragment = new AssignHomeroomTeacherFragment();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_content_registrar_main, fragment);
            transaction.addToBackStack(null); // so the user can navigate back
            transaction.commit();
        });

        return binding.getRoot();
    }

    private void fillClassroomListView() {
        studentManagementViewModel.getClassrooms().observe(getViewLifecycleOwner(), classrooms -> {
            ArrayAdapter<DummyClassroom> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    classrooms
            );
            binding.lstClassrooms.setAdapter(adapter);
        });
        studentManagementViewModel.fetchAllClassrooms();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}