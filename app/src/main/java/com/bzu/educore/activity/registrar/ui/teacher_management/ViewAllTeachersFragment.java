package com.bzu.educore.activity.registrar.ui.teacher_management;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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
            NavDirections action = ViewAllTeachersFragmentDirections.actionViewAllTeachersFragmentToModifyTeacherFragment();
            Navigation.findNavController(requireView()).navigate(action);
        });

        teacherManagementViewModel.getDeletionSuccess().observe(getViewLifecycleOwner(), success -> {
          //  if (success)
//binding.layoutViewAllUsrs.rclrviewUsrs.getAdapter().notifyItemRemoved(teacherManagementViewModel.getCurrentIndex().getValue());

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
        ViewAllTeachersFragmentDirections.ActionViewAllTeachersFragmentToModifyTeacherFragment action =
                ViewAllTeachersFragmentDirections.actionViewAllTeachersFragmentToModifyTeacherFragment();
        DummyTeacher teacher = teacherManagementViewModel.getTeachers().getValue().get(position);
        action.setTeacher(teacher);
        Navigation.findNavController(requireView()).navigate(action);
    }

}