package com.bzu.educore.activity.registrar.ui.teacher_management;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.educore.model.user.Teacher;
import com.bzu.educore.model.user.User;
import com.bzu.educore.adapter.registrar.UserAdapter;
import com.bzu.educore.databinding.FragmentViewAllTeachersBinding;
import com.bzu.educore.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ViewAllTeachersFragment extends Fragment implements OnItemClickListener<User> {

    private FragmentViewAllTeachersBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TeacherManagementViewModel teacherManagementViewModel = new ViewModelProvider(this).get(TeacherManagementViewModel.class);
        binding = FragmentViewAllTeachersBinding.inflate(inflater, container, false);

        teacherManagementViewModel.getTeachers().observe(getViewLifecycleOwner(), teachers -> {
            List<User> users = new ArrayList<>(teachers);
            UserAdapter adapter = new UserAdapter(users, this);
            binding.layoutViewAllUsrs.rclrviewUsrs.setAdapter(adapter);
            binding.layoutViewAllUsrs.rclrviewUsrs.setLayoutManager(new LinearLayoutManager(requireContext()));
        });
        teacherManagementViewModel.fetchAllTeachers();

        binding.layoutViewAllUsrs.fltbtnAddUsr.setOnClickListener(v -> {
            NavDirections action = ViewAllTeachersFragmentDirections.actionViewAllTeachersFragmentToModifyTeacherFragment();
            Navigation.findNavController(requireView()).navigate(action);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(User user) {
        ViewAllTeachersFragmentDirections.ActionViewAllTeachersFragmentToModifyTeacherFragment action =
                ViewAllTeachersFragmentDirections.actionViewAllTeachersFragmentToModifyTeacherFragment();
        action.setTeacher((Teacher) user);
        Navigation.findNavController(requireView()).navigate(action);
    }

}