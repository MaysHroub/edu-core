package com.bzu.educore.activity.teacher.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.AssignAssignmentActivity;

public class AssignmentInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment_info, container, false);

        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> ((AssignAssignmentActivity) requireActivity()).loadFragment(new AssignmentDetailsFragment()));

        return view;
    }
}

