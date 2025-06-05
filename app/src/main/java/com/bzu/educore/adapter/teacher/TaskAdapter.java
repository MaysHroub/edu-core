package com.bzu.educore.adapter.teacher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.StudentListActivity;
import com.bzu.educore.model.task.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Task> tasks;
    private final Context context;

    private static final int TYPE_ASSIGNMENT = 0;
    private static final int TYPE_EXAM = 1;

    public TaskAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return tasks.get(position).getType().equalsIgnoreCase("assignment") ? TYPE_ASSIGNMENT : TYPE_EXAM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ASSIGNMENT) {
            View view = inflater.inflate(R.layout.item_assignment, parent, false);
            return new AssignmentViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_exam, parent, false);
            return new ExamViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder, int position) {
        Task task = tasks.get(position);
        if (getItemViewType(position) == TYPE_ASSIGNMENT) {
            ((AssignmentViewHolder) holder).bind(task);
        } else {
            ((ExamViewHolder) holder).bind(task);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class AssignmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSubjectTitle, tvGrade, tvTeacherName, tvMaxScore, tvDate, tvDeadline;
        private final Button btnViewSubmissions;

        AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectTitle = itemView.findViewById(R.id.tv_subject_title);
            tvGrade = itemView.findViewById(R.id.tv_grade);
            tvTeacherName = itemView.findViewById(R.id.tv_teacher_name);
            tvMaxScore = itemView.findViewById(R.id.tv_max_score);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDeadline = itemView.findViewById(R.id.tv_deadline);
            btnViewSubmissions = itemView.findViewById(R.id.btnViewSubmission);
        }

        void bind(final Task task) {
            tvSubjectTitle.setText(task.getSubjectTitle());
            tvGrade.setText("Grade " + task.getGradeNumber());
            tvTeacherName.setText("Teacher: " + task.getTeacherName());
            tvMaxScore.setText("Max: " + task.getMaxScore());
            tvDate.setText("Created: " + task.getDate());
            tvDeadline.setText("Due: " + task.getDeadline());

            btnViewSubmissions.setOnClickListener(v -> {
                Intent intent = new Intent(context, StudentListActivity.class);
                intent.putExtra("taskId", task.getId());
                intent.putExtra("type", "assignment");
                context.startActivity(intent);
            });
        }
    }

    class ExamViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSubjectTitle, tvGrade, tvTeacherName, tvMaxScore, tvExamDate;
        private final Button btnViewSubmissions;  // Add button reference

        ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectTitle = itemView.findViewById(R.id.tv_subject_title);
            tvGrade = itemView.findViewById(R.id.tv_grade);
            tvTeacherName = itemView.findViewById(R.id.tv_teacher_name);
            tvMaxScore = itemView.findViewById(R.id.tv_max_score);
            tvExamDate = itemView.findViewById(R.id.tv_exam_date);
            btnViewSubmissions = itemView.findViewById(R.id.btn_view_submissions);  // Use exact ID from your XML
        }

        void bind(final Task task) {
            tvSubjectTitle.setText(task.getSubjectTitle());
            tvGrade.setText("Grade " + task.getGradeNumber());
            tvTeacherName.setText("Teacher: " + task.getTeacherName());
            tvMaxScore.setText("Max: " + task.getMaxScore());
            tvExamDate.setText("Exam Date: " + task.getDate());

            btnViewSubmissions.setOnClickListener(v -> {
                Intent intent = new Intent(context, StudentListActivity.class);
                intent.putExtra("taskId", task.getId());
                intent.putExtra("type", "exam");
                context.startActivity(intent);
            });
        }
    }
}
