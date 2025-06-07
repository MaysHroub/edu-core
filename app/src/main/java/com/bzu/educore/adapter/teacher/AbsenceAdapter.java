package com.bzu.educore.adapter.teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bzu.educore.R;
import com.bzu.educore.model.user.Student;
import com.bzu.educore.model.user.Absence;

import java.util.List;
import java.util.Map;

public class AbsenceAdapter
        extends RecyclerView.Adapter<AbsenceAdapter.VH> {

    private final List<Student> students;
    private final Map<Integer, Absence> absenceMap;
    // key: student.getId(), value: current Absence record

    public AbsenceAdapter(List<Student> students,
                          Map<Integer, Absence> absenceMap) {
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

        // Name & placeholder image
        h.tvName.setText(s.getName());
        h.ivProfile.setImageResource(R.drawable.student_icon);

        // Does map have an Absence for this student?
        boolean isAbsent = absenceMap.containsKey(sid);
        h.cbAbsent.setOnCheckedChangeListener(null);
        h.cbAbsent.setChecked(isAbsent);
        h.rgStatus.setVisibility(isAbsent ? View.VISIBLE : View.GONE);

        // Initialize radios if absent
        if (isAbsent) {
            Absence a = absenceMap.get(sid);
            boolean exc = "excused".equalsIgnoreCase(a.getStatus());
            h.rbExcused.setChecked(exc);
            h.rbUnexcused.setChecked(!exc);
        }

        // Handle checkbox toggle
        h.cbAbsent.setOnCheckedChangeListener((cb, checked) -> {
            if (checked) {
                // create default unexcused Absence; date set later in Fragment
                Absence a = new Absence();
                a.setStudentId(Integer.valueOf(sid));
                a.setStatus("unexcused");
                absenceMap.put(Integer.valueOf(sid), a);
                h.rgStatus.setVisibility(View.VISIBLE);
                h.rbUnexcused.setChecked(true);
            } else {
                absenceMap.remove(sid);
                h.rgStatus.setVisibility(View.GONE);
            }
        });

        // Handle radio‐group changes
        h.rgStatus.setOnCheckedChangeListener((grp, checkedId) -> {
            Absence a = absenceMap.get(sid);
            if (a == null) return;
            if (checkedId == R.id.rbExcused) {
                a.setStatus("excused");
            } else {
                a.setStatus("unexcused");
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

        VH(@NonNull View itemView) {
            super(itemView);
            ivProfile   = itemView.findViewById(R.id.ivProfile);
            tvName      = itemView.findViewById(R.id.tvStudentName);
            cbAbsent    = itemView.findViewById(R.id.cbAbsent);
            rgStatus    = itemView.findViewById(R.id.rgStatus);
            rbUnexcused = itemView.findViewById(R.id.rbUnexcused);
            rbExcused   = itemView.findViewById(R.id.rbExcused);
        }
    }
}
