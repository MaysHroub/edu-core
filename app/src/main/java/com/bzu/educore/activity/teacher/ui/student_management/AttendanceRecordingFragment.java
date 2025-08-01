package com.bzu.educore.activity.teacher.ui.student_management;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.android.volley.Request;
import com.android.volley.toolbox.*;
import com.bzu.educore.R;
import com.bzu.educore.adapter.teacher.StudentAbsenceAdapter;
import com.bzu.educore.model.user.*;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;

import org.json.*;
import java.time.LocalDate;
import java.util.*;

public class AttendanceRecordingFragment extends Fragment {

    private RecyclerView rvStudents;
    private Button btnDate, btnSubmit;
    private final List<Student> studentList = new ArrayList<>();
    private final Map<Integer, Absence> absenceMap = new HashMap<>();
    private StudentAbsenceAdapter adapter;

    private int classId, teacherId, year, month, day;

    public static AttendanceRecordingFragment newInstance(int teacherId, int classId) {
        AttendanceRecordingFragment f = new AttendanceRecordingFragment();
        Bundle args = new Bundle();
        args.putInt("teacherId", teacherId);
        args.putInt("classId", classId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        if (getArguments() != null) {
            teacherId = getArguments().getInt("teacherId");
            classId = getArguments().getInt("classId");
        }

        LocalDate now = LocalDate.now();
        year = now.getYear();
        month = now.getMonthValue() - 1;
        day = now.getDayOfMonth();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup cont, Bundle s) {
        return inf.inflate(R.layout.fragment_absence, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        rvStudents = v.findViewById(R.id.rvStudentsAbsence);
        btnDate = v.findViewById(R.id.btnDatePicker);
        btnSubmit = v.findViewById(R.id.btnSubmitAbsence);

        btnDate.setText(fmtDate());
        btnDate.setOnClickListener(_btn -> pickDate());

        adapter = new StudentAbsenceAdapter(studentList, absenceMap);
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStudents.setAdapter(adapter);

        btnSubmit.setOnClickListener(_b -> submitAttendance());
        fetchStudents();
    }

    private void pickDate() {
        new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
            year = y; month = m; day = d;
            btnDate.setText(fmtDate());
        }, year, month, day).show();
    }

    private String fmtDate() {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }

    private void fetchStudents() {
        String url = UrlManager.URL_GET_CLASS_STUDENTS + "?classId=" + classId;

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
                resp -> {
                    studentList.clear();
                    for (int i = 0; i < resp.length(); i++) {
                        try {
                            JSONObject o = resp.getJSONObject(i);
                            studentList.add(new Student(
                                    o.getInt("id"),
                                    o.getString("fname"),
                                    o.getString("lname"),
                                    o.getString("email"),
                                    null,
                                    null,
                                    o.getInt("class_id")
                            ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("Error parsing student: " + e.getMessage());
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                err -> {
                    String msg = "Failed to load students";
                    if (err.networkResponse != null) msg += " (HTTP " + err.networkResponse.statusCode + ")";
                    if (err.getMessage() != null) msg += ": " + err.getMessage();
                    showToast(msg);
                    err.printStackTrace();
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }
    private void submitAttendance() {
        JSONArray arr = new JSONArray();
        LocalDate sel = LocalDate.of(year, month + 1, day);

        for (Absence a : absenceMap.values()) {
            a.setDate(sel);
            JSONObject o = new JSONObject();
            try {
                o.put("student_id", a.getStudentId());
                o.put("date", sel.toString());
                o.put("status", a.getStatus());
                arr.put(o);
            } catch (JSONException ignored) {}
        }

        if (arr.length() == 0) {
            showToast("No absences marked");
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("teacher_id", teacherId);
            body.put("class_id", classId);
            body.put("absences", arr);
        } catch (JSONException e) {
            showToast("Error preparing data");
            return;
        }

        String url = UrlManager.URL_SUBMIT_ABSENCE;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, body,
                resp -> showToast(resp.optString("message", "Saved")),
                err -> showToast("Submit failed")
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
