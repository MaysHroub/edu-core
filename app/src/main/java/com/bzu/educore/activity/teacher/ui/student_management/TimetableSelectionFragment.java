package com.bzu.educore.activity.teacher.ui.student_management;

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
import com.bzu.educore.activity.teacher.ui.task_management.AnnounceExamFragment;
import com.bzu.educore.activity.teacher.ui.task_management.AssignAssignmentFragment;
import com.bzu.educore.adapter.teacher.TimetableSelectionAdapter;
import com.bzu.educore.util.VolleySingleton;
import com.bzu.educore.util.teacher.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimetableSelectionFragment extends Fragment {

    private RecyclerView recyclerView;
    private int teacherId = 14; // Replace with actual logged-in teacher ID
    private List<JSONObject> timetableList = new ArrayList<>();
    private String mode;

    public TimetableSelectionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable_selection, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewTimetable);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (getArguments() != null) {
            mode = getArguments().getString("mode", "");
        }

        fetchTimetable();
        return view;
    }

    private void fetchTimetable() {
        String url = Constants.BASE_URL+ "get_timetable_by_teacher.php";

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

        // Now using loadFragment(fragment, true) to match updated TeacherMainActivity
        ((TeacherMainActivity) requireActivity()).loadFragment(targetFragment, true);
    }
}
