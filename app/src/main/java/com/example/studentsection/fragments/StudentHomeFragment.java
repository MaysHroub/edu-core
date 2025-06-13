package com.example.studentsection.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bzu.educore.R;
import com.google.android.material.card.MaterialCardView;

public class StudentHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup click listeners for dashboard cards
        MaterialCardView cardViewMarks = view.findViewById(R.id.cardViewMarks);
        MaterialCardView cardViewEvents = view.findViewById(R.id.cardViewEvents);
        MaterialCardView cardSubmitAssignment = view.findViewById(R.id.cardSubmitAssignment);
        MaterialCardView cardViewTimetable = view.findViewById(R.id.cardViewTimetable);

        cardViewMarks.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new ViewMarksFragment())
                    .addToBackStack(null)
                    .commit();
        });

        cardViewEvents.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new ViewEventsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        cardSubmitAssignment.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new SubmitAssignmentFragment())
                    .addToBackStack(null)
                    .commit();
        });

        cardViewTimetable.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new ViewTimetableFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
} 