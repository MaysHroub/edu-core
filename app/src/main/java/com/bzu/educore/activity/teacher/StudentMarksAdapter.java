package com.bzu.educore.activity.teacher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;

import java.util.List;

public class StudentMarksAdapter extends RecyclerView.Adapter<StudentMarksAdapter.ViewHolder> {

    public static class Student {
        public String id;
        public String name;
        public double mark = 0.0;

        public Student(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private List<Student> students;

    public StudentMarksAdapter(List<Student> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_mark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.nameTextView.setText(student.name);
        holder.markEditText.setText(student.mark > 0 ? String.valueOf(student.mark) : "");

        // Update student mark when text changes
        holder.markEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    String text = holder.markEditText.getText().toString().trim();
                    student.mark = text.isEmpty() ? 0.0 : Double.parseDouble(text);
                } catch (NumberFormatException e) {
                    student.mark = 0.0;
                    holder.markEditText.setText("");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public List<Student> getStudents() {
        return students;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        EditText markEditText;

        ViewHolder(View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.imgStudentPfp);
            nameTextView = itemView.findViewById(R.id.tvStudentName);
            markEditText = itemView.findViewById(R.id.etStudentMark);
        }
    }
}