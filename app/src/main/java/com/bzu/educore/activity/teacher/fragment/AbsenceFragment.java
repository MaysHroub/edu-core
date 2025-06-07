package com.bzu.educore.activity.teacher.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.adapter.teacher.AbsenceAdapter;
import com.bzu.educore.model.user.Student;
import com.bzu.educore.model.user.Absence;
import com.bzu.educore.util.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDate;
import java.util.*;

public class AbsenceFragment extends Fragment {

    private RecyclerView rvStudents;
    private Button btnDate, btnSubmit;
    private final List<Student> studentList = new ArrayList<>();
    private final Map<Integer, Absence> absenceMap = new HashMap<>();
    private AbsenceAdapter adapter;

    private int classId, teacherId;
    private int year, month, day;

    public static AbsenceFragment newInstance(int teacherId, int classId) {
        AbsenceFragment f = new AbsenceFragment();
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
            classId   = getArguments().getInt("classId");
        }
        LocalDate now = LocalDate.now();
        year  = now.getYear();
        month = now.getMonthValue() - 1;
        day   = now.getDayOfMonth();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup cont, Bundle s
    ) {
        return inflater.inflate(R.layout.fragment_absence, cont, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        rvStudents = v.findViewById(R.id.rvStudentsAbsence);
        btnDate    = v.findViewById(R.id.btnDatePicker);
        btnSubmit  = v.findViewById(R.id.btnSubmitAbsence);

        btnDate.setText(fmtDate());
        btnDate.setOnClickListener(_btn -> new DatePickerDialog(
                requireContext(),
                (DatePicker dp, int y, int m, int d) -> {
                    year=y; month=m; day=d;
                    btnDate.setText(fmtDate());
                },
                year, month, day
        ).show());

        adapter = new AbsenceAdapter(studentList, absenceMap);
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStudents.setAdapter(adapter);

        btnSubmit.setOnClickListener(_b -> submitAttendance());

        fetchStudents();
    }

    private String fmtDate() {
        return String.format(Locale.getDefault(),
                "%04d-%02d-%02d", year, month+1, day);
    }

    private void fetchStudents() {
        String url = "http://yourserver.com/android/get_class_students.php"
                + "?classId=" + classId;
        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, url, null,
                resp -> {
                    studentList.clear();
                    for (int i = 0; i < resp.length(); i++) {
                        try {
                            JSONObject o = resp.getJSONObject(i);
                            Student s = new Student(
                                    o.getInt("id"),
                                    null, // age
                                    o.getString("name"),
                                    o.getString("email"),
                                    null, // dateOfBirth
                                    o.getString("grade_number"),
                                    o.getString("class_id")
                            );
                            studentList.add(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                err -> Toast.makeText(getContext(),
                        "Failed to load students",
                        Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(requireContext())
                .addToRequestQueue(req);
    }

    private void submitAttendance() {
        JSONArray arr = new JSONArray();
        LocalDate sel = LocalDate.of(year, month+1, day);

        for (Absence a : absenceMap.values()) {
            a.setDate(sel); // set chosen date
            JSONObject o = new JSONObject();
            try {
                o.put("student_id", a.getStudentId());
                o.put("date",       sel.toString());
                o.put("status",     a.getStatus());
                arr.put(o);
            } catch (JSONException ignored) {}
        }

        if (arr.length() == 0) {
            Toast.makeText(getContext(),
                    "No absences marked",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("teacher_id", teacherId);
            body.put("class_id",   classId);
            body.put("absences",   arr);
        } catch (JSONException e) {
            Toast.makeText(getContext(),
                    "Error preparing data",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/android/submit_absence.php";
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST, url, body,
                resp -> Toast.makeText(getContext(),
                        resp.optString("message","Saved"),
                        Toast.LENGTH_LONG).show(),
                err -> Toast.makeText(getContext(),
                        "Submit failed",
                        Toast.LENGTH_LONG).show()
        );
        VolleySingleton.getInstance(requireContext())
                .addToRequestQueue(req);
    }
}
