package com.bzu.educore.activity.registrar.ui.homeroom_teacher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bzu.educore.model.school.Classroom;
import com.bzu.educore.activity.registrar.ui.student_management.StudentManagementViewModel;
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
            Classroom classroom = (Classroom) parent.getItemAtPosition(position);
            NavDirections action = ViewAllClassroomsFragmentDirections.actionViewAllClassroomsFragmentToAssignHomeroomTeacherFragment(classroom);
            Navigation.findNavController(requireView()).navigate(action);
        });

        return binding.getRoot();
    }

    private void fillClassroomListView() {
        studentManagementViewModel.getClassrooms().observe(getViewLifecycleOwner(), classrooms -> {
            ArrayAdapter<Classroom> adapter = new ArrayAdapter<>(
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