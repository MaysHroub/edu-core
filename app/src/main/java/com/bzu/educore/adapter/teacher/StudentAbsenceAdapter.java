package com.bzu.educore.adapter.teacher;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bzu.educore.R;
import com.bzu.educore.model.user.*;

import java.util.*;

public class StudentAbsenceAdapter extends RecyclerView.Adapter<StudentAbsenceAdapter.VH> {

    private final List<Student> students;
    private final Map<String, Absence> absenceMap;

    public StudentAbsenceAdapter(List<Student> students, Map<String, Absence> absenceMap) {
        this.students = students;
        this.absenceMap = absenceMap;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_absence, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Student s = students.get(pos);
        String sid = s.getId();

        h.tvName.setText(s.getName());
        h.ivProfile.setImageResource(R.drawable.student_icon);

        boolean isAbsent = absenceMap.containsKey(sid);
        h.cbAbsent.setOnCheckedChangeListener(null);
        h.cbAbsent.setChecked(isAbsent);
        h.rgStatus.setVisibility(isAbsent ? View.VISIBLE : View.GONE);

        if (isAbsent) {
            boolean exc = "excused".equalsIgnoreCase(absenceMap.get(sid).getStatus());
            h.rbExcused.setChecked(exc);
            h.rbUnexcused.setChecked(!exc);
        }

        h.cbAbsent.setOnCheckedChangeListener((cb, checked) -> {
            if (checked) {
                Absence a = new Absence();
                a.setStudentId(sid);
                a.setStatus("unexcused");
                absenceMap.put(sid, a);
                h.rgStatus.setVisibility(View.VISIBLE);
                h.rbUnexcused.setChecked(true);
            } else {
                absenceMap.remove(sid);
                h.rgStatus.setVisibility(View.GONE);
            }
        });

        h.rgStatus.setOnCheckedChangeListener((grp, checkedId) -> {
            Absence a = absenceMap.get(sid);
            if (a != null) {
                a.setStatus(checkedId == R.id.rbExcused ? "excused" : "unexcused");
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName;
        CheckBox cbAbsent;
        RadioGroup rgStatus;
        RadioButton rbUnexcused, rbExcused;

        VH(@NonNull View v) {
            super(v);
            ivProfile = v.findViewById(R.id.ivProfile);
            tvName = v.findViewById(R.id.tvStudentName);
            cbAbsent = v.findViewById(R.id.cbAbsent);
            rgStatus = v.findViewById(R.id.rgStatus);
            rbUnexcused = v.findViewById(R.id.rbUnexcused);
            rbExcused = v.findViewById(R.id.rbExcused);
        }
    }
}
