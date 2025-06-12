package com.bzu.educore.activity.registrar.ui.student_management;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.educore.R;
import com.bzu.educore.activity.registrar.User;
import com.bzu.educore.adapter.registrar.UserAdapter;
import com.bzu.educore.databinding.FragmentViewAllStudentsBinding;
import com.bzu.educore.listener.OnItemClickListener;
import com.bzu.educore.model.user.Person;

import java.util.ArrayList;
import java.util.List;

public class ViewAllStudentsFragment extends Fragment implements OnItemClickListener {

    private FragmentViewAllStudentsBinding binding;
    private StudentManagementViewModel studentManagementViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        studentManagementViewModel =
                new ViewModelProvider(this).get(StudentManagementViewModel.class);
        binding = FragmentViewAllStudentsBinding.inflate(inflater, container, false);

        studentManagementViewModel.getStudents().observe(getViewLifecycleOwner(), students -> {
            List<User> users = new ArrayList<>(students);
            UserAdapter adapter = new UserAdapter(users, this);
            binding.layoutViewAllUsrs.rclrviewUsrs.setAdapter(adapter);
            binding.layoutViewAllUsrs.rclrviewUsrs.setLayoutManager(new LinearLayoutManager(requireContext()));
        });
        studentManagementViewModel.fetchAllStudents();

        binding.layoutViewAllUsrs.fltbtnAddUsr.setOnClickListener(v -> {
            NavDirections action = ViewAllStudentsFragmentDirections.actionViewAllStudentsFragmentToModifyStudentFragment();
            Navigation.findNavController(requireView()).navigate(action);
        });

        studentManagementViewModel.getDeletionSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success)
                binding.layoutViewAllUsrs.rclrviewUsrs.getAdapter().notifyDataSetChanged();
        });

        return binding.getRoot();
    }

    // TODO: replace position with generic type
    @Override
    public void onItemClick(int position) {
        DummyStudent student = studentManagementViewModel.getStudents().getValue().get(position);
        ViewAllStudentsFragmentDirections.ActionViewAllStudentsFragmentToModifyStudentFragment action =
                ViewAllStudentsFragmentDirections.actionViewAllStudentsFragmentToModifyStudentFragment();
        action.setStudent(student);
        Navigation.findNavController(requireView()).navigate(action);
    }
}