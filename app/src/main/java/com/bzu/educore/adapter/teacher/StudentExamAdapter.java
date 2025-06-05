package com.bzu.educore.adapter.teacher;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.model.task.StudentSubmission;

import java.util.List;

/**
 * Adapter for displaying students in exam marking mode.
 * Shows student name and mark input field only (no submission status, date, or view button).
 */
public class StudentExamAdapter extends RecyclerView.Adapter<StudentExamAdapter.ExamViewHolder> {

    private final List<StudentSubmission> students;
    private final OnMarkChangedListener markChangedListener;

    public StudentExamAdapter(
            List<StudentSubmission> students,
            OnMarkChangedListener markChangedListener) {
        this.students = students;
        this.markChangedListener = markChangedListener;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_exam, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        StudentSubmission student = students.get(position);

        // Set student name
        holder.tvStudentName.setText(student.getStudentName());

        // Remove previous TextWatcher if any
        if (holder.markTextWatcher != null) {
            holder.etStudentMarkInput.removeTextChangedListener(holder.markTextWatcher);
        }

        // Set current mark
        if (student.getMark() != null) {
            holder.etStudentMarkInput.setText(String.format("%.2f", student.getMark()));
        } else {
            holder.etStudentMarkInput.setText("");
        }

        holder.etStudentMarkInput.setHint("Enter mark");

        // TextWatcher for mark changes
        holder.markTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Double newMark = null;
                    if (s != null && s.length() > 0) {
                        double value = Double.parseDouble(s.toString());
                        if (value >= 0 && value <= 100) {
                            newMark = value;
                        }
                    }
                    student.setMark(newMark);
                    if (markChangedListener != null) {
                        markChangedListener.onMarkChanged(student.getStudentId(), newMark);
                    }
                } catch (NumberFormatException e) {
                    student.setMark(null);
                    if (markChangedListener != null) {
                        markChangedListener.onMarkChanged(student.getStudentId(), null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        holder.etStudentMarkInput.addTextChangedListener(holder.markTextWatcher);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStudentPfp;
        TextView tvStudentName;
        EditText etStudentMarkInput;
        TextWatcher markTextWatcher;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStudentPfp = itemView.findViewById(R.id.imgStudentPfp);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            etStudentMarkInput = itemView.findViewById(R.id.etStudentMarkInput);
        }
    }

    public interface OnMarkChangedListener {
        void onMarkChanged(String studentId, Double mark);
    }
}