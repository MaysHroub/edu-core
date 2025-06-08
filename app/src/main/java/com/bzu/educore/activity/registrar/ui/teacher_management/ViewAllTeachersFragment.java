package com.bzu.educore.activity.registrar.ui.teacher_management;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bzu.educore.R;
import com.bzu.educore.activity.registrar.ui.student_management.ModifyStudentFragment;
import com.bzu.educore.adapter.registrar.UserAdapter;
import com.bzu.educore.databinding.FragmentModifyTeacherBinding;
import com.bzu.educore.databinding.FragmentViewAllTeachersBinding;
import com.bzu.educore.listener.OnItemClickListener;
import com.bzu.educore.model.user.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViewAllTeachersFragment extends Fragment implements OnItemClickListener {

    private FragmentViewAllTeachersBinding binding;
    private TeacherManagementViewModel teacherManagementViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        teacherManagementViewModel =
                new ViewModelProvider(this).get(TeacherManagementViewModel.class);
        binding = FragmentViewAllTeachersBinding.inflate(inflater, container, false);

        teacherManagementViewModel.getTeachers().observe(getViewLifecycleOwner(), teachers -> {
            List<Person> users = new ArrayList<>(teachers);
            UserAdapter adapter = new UserAdapter(users, this);
            binding.layoutViewAllUsrs.rclrviewUsrs.setAdapter(adapter);
        });
        teacherManagementViewModel.fetchAllTeachers();

        binding.layoutViewAllUsrs.fltbtnAddUsr.setOnClickListener(v -> {
            ModifyTeacherFragment fragment = new ModifyTeacherFragment();
            teacherManagementViewModel.setCurrentIndex(-1);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_content_registrar_main, fragment);
            transaction.addToBackStack(null); // so the user can navigate back
            transaction.commit();
        });

        teacherManagementViewModel.getDeletionSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success)
                binding.layoutViewAllUsrs.rclrviewUsrs.getAdapter().notifyItemRemoved(teacherManagementViewModel.getCurrentIndex().getValue());
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        ModifyTeacherFragment fragment = new ModifyTeacherFragment();
        teacherManagementViewModel.setCurrentIndex(position);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_registrar_main, fragment);
        transaction.addToBackStack(null); // so the user can navigate back
        transaction.commit();
    }

}