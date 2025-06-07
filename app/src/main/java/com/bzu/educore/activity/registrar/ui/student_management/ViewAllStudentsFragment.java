package com.bzu.educore.activity.registrar.ui.student_management;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.educore.R;
import com.bzu.educore.adapter.registrar.UserAdapter;
import com.bzu.educore.databinding.FragmentStudentRegistrationBinding;
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
            List<Person> users = new ArrayList<>(students);
            UserAdapter adapter = new UserAdapter(users, this);
            binding.layoutViewAllUsrs.rclrviewStds.setAdapter(adapter);
        });
        studentManagementViewModel.fetchAllStudents();

        binding.layoutViewAllUsrs.fltbtnAddStd.setOnClickListener(v -> {
            StudentRegistrationFragment fragment = new StudentRegistrationFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_content_registrar_main, fragment);
            transaction.addToBackStack(null); // so the user can navigate back
            transaction.commit();
        });

        return binding.getRoot();
    }

    @Override
    public void onItemClick(int position) {

    }
}