package com.bzu.educore.activity.teacher.ui.task_management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.TeacherMainActivity;
import com.bzu.educore.adapter.teacher.TimetableSelectionAdapter;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class TimetableSelectionFragment extends Fragment {

    // Add constants for argument keys
    private static final String ARG_MODE = "mode";
    private static final String ARG_TEACHER_ID = "teacher_id";

    private RecyclerView recyclerView;
    @Getter
    private int teacherId = -1; // Initialize with -1 to indicate no teacher ID
    private List<JSONObject> timetableList = new ArrayList<>();
    private String mode;

    public TimetableSelectionFragment() {}

    // Factory method to create fragment with parameters (optional, for consistency)
    public static TimetableSelectionFragment newInstance(String mode, int teacherId) {
        TimetableSelectionFragment fragment = new TimetableSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, mode);
        args.putInt(ARG_TEACHER_ID, teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString(ARG_MODE, "");
            teacherId = getArguments().getInt(ARG_TEACHER_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable_selection, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewTimetable);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Validate teacher ID before proceeding
        if (teacherId == -1) {
            Toast.makeText(requireContext(), "Teacher ID not provided", Toast.LENGTH_SHORT).show();
            return view;
        }

        fetchTimetable();
        return view;
    }

    private void fetchTimetable() {
        String url = UrlManager.URL_GET_TIMETABLE_BY_TEACHER;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("teacher_id", teacherId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try {
                        JSONArray timetable = response.getJSONArray("timetable");
                        timetableList.clear();
                        for (int i = 0; i < timetable.length(); i++) {
                            timetableList.add(timetable.getJSONObject(i));
                        }

                        TimetableSelectionAdapter adapter = new TimetableSelectionAdapter(
                                timetableList,
                                item -> {
                                    if ("assignment".equals(mode)) {
                                        openFragment(new AssignAssignmentFragment(), item);
                                    } else if ("exam".equals(mode)) {
                                        openFragment(new AnnounceExamFragment(), item);
                                    } else {
                                        Toast.makeText(requireContext(), "Unknown mode", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );

                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Failed to load timetable", Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void openFragment(Fragment targetFragment, JSONObject item) {
        Bundle bundle = new Bundle();
        try {
            bundle.putInt("subject_id", item.getInt("subject_id"));
            bundle.putInt("class_grade_id", item.getInt("class_grade_id"));
            bundle.putInt("teacher_id", teacherId);
            bundle.putString("subject_name", item.getString("subject_name"));
            bundle.putString("class_grade_name", item.getString("class_grade_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        targetFragment.setArguments(bundle);

        ((TeacherMainActivity) requireActivity()).loadFragment(targetFragment, true);
    }
}