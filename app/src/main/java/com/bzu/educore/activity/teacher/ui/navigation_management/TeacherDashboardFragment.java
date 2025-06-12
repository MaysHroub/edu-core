package com.bzu.educore.activity.teacher.ui.navigation_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.TeacherMainActivity;
import com.bzu.educore.activity.teacher.ui.student_management.AttendanceRecordingFragment;
import com.bzu.educore.activity.teacher.ui.task_management.TimetableSelectionFragment;
import com.bzu.educore.activity.teacher.ui.student_management.SearchTasksFragment;

public class TeacherDashboardFragment extends Fragment {

    private static final String ARG_TEACHER_ID = "teacher_id";
    private int teacherId;

    // Factory method to create fragment with teacher ID
    public static TeacherDashboardFragment newInstance(int teacherId) {
        TeacherDashboardFragment fragment = new TeacherDashboardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TEACHER_ID, teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teacherId = getArguments().getInt(ARG_TEACHER_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_dashboard, container, false);
        setupClickListeners(view);
        return view;
    }

    private void setupClickListeners(View view) {
        CardView cardCreateAssignment = view.findViewById(R.id.cardCreateAssignment);
        CardView cardAnnounceExam = view.findViewById(R.id.cardAnnounceExam);
        CardView cardSubmissions = view.findViewById(R.id.cardViewSubmissions);
        CardView cardAttendance = view.findViewById(R.id.cardViewAttendance);

        cardCreateAssignment.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", "assignment");
            bundle.putInt("teacher_id", teacherId); // Pass teacher ID

            TimetableSelectionFragment fragment = new TimetableSelectionFragment();
            fragment.setArguments(bundle);
            ((TeacherMainActivity) requireActivity()).loadFragment(fragment, true);
        });

        cardAnnounceExam.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", "exam");
            bundle.putInt("teacher_id", teacherId); // Pass teacher ID

            TimetableSelectionFragment fragment = new TimetableSelectionFragment();
            fragment.setArguments(bundle);
            ((TeacherMainActivity) requireActivity()).loadFragment(fragment, true);
        });

        cardSubmissions.setOnClickListener(v -> {
            // Pass teacher ID to SearchTasksFragment
            SearchTasksFragment fragment = SearchTasksFragment.newInstance(teacherId);
            ((TeacherMainActivity) requireActivity()).loadFragment(fragment, true);
        });

        cardAttendance.setOnClickListener(v -> {
            int dummyClassId = 1;
            AttendanceRecordingFragment frag = AttendanceRecordingFragment.newInstance(teacherId, dummyClassId);
            ((TeacherMainActivity) requireActivity()).loadFragment(frag, true);
        });
    }
}
