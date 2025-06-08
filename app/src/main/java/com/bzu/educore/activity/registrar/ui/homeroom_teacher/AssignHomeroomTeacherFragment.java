package com.bzu.educore.activity.registrar.ui.homeroom_teacher;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

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
import com.bzu.educore.util.DialogUtils;

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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // no drag-and-drop needed
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                DialogUtils.showConfirmationDialog(
                        requireContext(),
                        "Delete Assigning",
                        "Are you sure you want to delete this assigning?",
                        () -> {
                            teacherManagementViewModel.getHomeroomTeacherAssigns().getValue().remove(position);
                        }
                );
                binding.rclrviewHomeroomTchr.getAdapter().notifyItemChanged(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.rclrviewHomeroomTchr);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}