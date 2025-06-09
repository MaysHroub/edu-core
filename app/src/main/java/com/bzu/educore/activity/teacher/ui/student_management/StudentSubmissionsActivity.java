package com.bzu.educore.activity.teacher.ui.student_management;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.adapter.teacher.StudentSubmissionAdapter;
import com.bzu.educore.model.task.StudentSubmission;
import com.bzu.educore.util.VolleySingleton;
import com.bzu.educore.util.teacher.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

public class StudentSubmissionsActivity extends AppCompatActivity {

    private RecyclerView rvStudents;
    private Button btnPublish;
    private EditText etSearch;
    private StudentSubmissionAdapter adapter;
    private final List<StudentSubmission> studentList = new ArrayList<>();
    private final List<StudentSubmission> filtered = new ArrayList<>();
    private int taskId;
    private String type;
    private double maxMark;

    @Override
    protected void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_student_list);
        rvStudents = findViewById(R.id.recycler_view_students);
        btnPublish = findViewById(R.id.btn_publish_marks);
        etSearch   = findViewById(R.id.et_search_student);

        taskId = getIntent().getIntExtra("taskId", -1);
        type   = getIntent().getStringExtra("type");
        maxMark= getIntent().getDoubleExtra("maxMark", Constants.MAX_MARK);
        if (taskId<0 || type==null) {
            Toast.makeText(this, "Invalid task data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        StudentSubmissionAdapter.DisplayMode mode =
                "assignment".equalsIgnoreCase(type)
                        ? StudentSubmissionAdapter.DisplayMode.ASSIGNMENT_MODE
                        : StudentSubmissionAdapter.DisplayMode.EXAM_MODE;

        adapter = new StudentSubmissionAdapter(
                this, filtered, mode, maxMark
        );

        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);

        btnPublish.setOnClickListener(v -> publishMarks());
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence c,int a,int b,int d){}
            @Override public void afterTextChanged(Editable e){}
            @Override
            public void onTextChanged(CharSequence s,int a,int b,int d) {
                filter(s.toString());
            }
        });

        fetchStudentList();
    }

    private void filter(String txt) {
        filtered.clear();
        String q = txt.trim().toLowerCase();
        if (q.isEmpty()) filtered.addAll(studentList);
        else for (StudentSubmission s: studentList)
            if (s.getStudentName().toLowerCase().contains(q))
                filtered.add(s);
        adapter.notifyDataSetChanged();
        boolean ok = !filtered.isEmpty();
        btnPublish.setEnabled(ok);
        btnPublish.setAlpha(ok?1f:0.5f);
    }

    private void publishMarks() {
        JSONArray arr = new JSONArray();
        boolean any = false;
        for (StudentSubmission s: studentList) {
            if (s.getMark()!=null) {
                any = true;
                JSONObject o = new JSONObject();
                try {
                    o.put("task_id",   taskId);
                    o.put("student_id",s.getStudentId());
                    o.put("mark",      s.getMark());
                    o.put("feedback",  s.getFeedback()==null?"":s.getFeedback());
                    arr.put(o);
                } catch (JSONException ignored) {}
            }
        }
        if (!any) {
            Toast.makeText(this, "No marks to publish.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject body = new JSONObject();
        try { body.put("marks", arr); }
        catch (JSONException e) {
            Toast.makeText(this, "Error prepping data",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        btnPublish.setEnabled(false);
        btnPublish.setText("Publishing...");
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.POST,
                Constants.PUBLISH_MARKS_URL,
                body,
                resp -> {
                    btnPublish.setEnabled(true);
                    btnPublish.setText("Publish Marks");
                    try {
                        boolean success = resp.getBoolean("success");
                        Toast.makeText(this,
                                resp.getString("message"),
                                Toast.LENGTH_LONG).show();
                        if (success) fetchStudentList();
                    } catch (JSONException e) {
                        Toast.makeText(this,
                                "Response error: "+e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                err -> {
                    btnPublish.setEnabled(true);
                    btnPublish.setText("Publish Marks");
                    Toast.makeText(this,
                            "Failed to publish marks",
                            Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(this)
                .addToRequestQueue(req);
    }

    private void fetchStudentList() {
        String url = type.equalsIgnoreCase("assignment")
                ? Constants.BASE_URL + "get_assignment_students.php?taskId="+taskId
                : Constants.BASE_URL + "get_exam_students.php?taskId="+taskId;

        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET, url, null,
                arr -> {
                    studentList.clear();
                    try {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            studentList.add(new StudentSubmission(
                                    o.getString("student_id"),
                                    o.getString("student_name"),
                                    o.optString("submission_date",""),
                                    o.optString("submission_file_url",""),
                                    o.has("mark") && !o.isNull("mark")
                                            ? o.getDouble("mark")
                                            : null,
                                    o.optString("feedback",""),
                                    o.optString("status","")
                            ));
                        }
                        filtered.clear();
                        filtered.addAll(studentList);
                        adapter.notifyDataSetChanged();
                        btnPublish.setEnabled(!filtered.isEmpty());
                        btnPublish.setAlpha(filtered.isEmpty()?0.5f:1f);
                    } catch (JSONException e) {
                        Toast.makeText(this,
                                "Parsing error: "+e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                },
                err -> Toast.makeText(this,
                        "Failed to load students",
                        Toast.LENGTH_SHORT).show()
        );
        VolleySingleton.getInstance(this)
                .addToRequestQueue(req);
    }
}
