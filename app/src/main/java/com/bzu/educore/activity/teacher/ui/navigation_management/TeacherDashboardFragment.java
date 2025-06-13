package com.bzu.educore.activity.teacher.ui.navigation_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.TeacherMainActivity;
import com.bzu.educore.activity.teacher.ui.student_management.AttendanceRecordingFragment;
import com.bzu.educore.activity.teacher.ui.task_management.TimetableSelectionFragment;
import com.bzu.educore.activity.teacher.ui.student_management.SearchTasksFragment;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import org.json.JSONException;

public class TeacherDashboardFragment extends Fragment {

    private static final String ARG_TEACHER_ID = "teacher_id";
    private int teacherId;
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
            // Fetch homeroom class ID asnd then navigate to attendance
            fetchHomeroomClassAndNavigate();
        });
    }

    private void fetchHomeroomClassAndNavigate() {
        String url = UrlManager.URL_GET_HOMEROOM_CLASS + "?teacher_id=" + teacherId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("class_id") && !response.isNull("class_id")) {
                            int classId = response.getInt("class_id");
                            AttendanceRecordingFragment fragment = AttendanceRecordingFragment.newInstance(teacherId, classId);
                            ((TeacherMainActivity) requireActivity()).loadFragment(fragment, true);
                        } else {
                            showToast("You are not assigned as a homeroom teacher to any class.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Error parsing server response.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    showToast("Network error: could not fetch homeroom class.");
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}