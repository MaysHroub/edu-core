package com.bzu.educore.activity.teacher.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.TeacherMainActivity;

public class TeacherDashboardFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable  ViewGroup container,
            @Nullable  Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.teacher_dashboard, container, false);
        setupClickListeners(view);
        return view;
    }

    private void setupClickListeners(View view) {
        CardView cardCreateAssignment = view.findViewById(R.id.cardCreateAssignment);
        CardView cardAnnounceExam    = view.findViewById(R.id.cardAnnounceExam);
        CardView cardSubmissions     = view.findViewById(R.id.cardViewSubmissions);

        cardCreateAssignment.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", "assignment");

            TimetableSelectionFragment fragment = new TimetableSelectionFragment();
            fragment.setArguments(bundle);
            // Use two-argument loadFragment: (fragment, addToBackStack)
            ((TeacherMainActivity) requireActivity()).loadFragment(fragment, true);
        });

        cardAnnounceExam.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", "exam");

            TimetableSelectionFragment fragment = new TimetableSelectionFragment();
            fragment.setArguments(bundle);
            ((TeacherMainActivity) requireActivity()).loadFragment(fragment, true);
        });

        cardSubmissions.setOnClickListener(v ->
                ((TeacherMainActivity) requireActivity()).loadFragment(
                        new SearchAssignmentsFragment(),
                        true
                )
        );
    }
}
