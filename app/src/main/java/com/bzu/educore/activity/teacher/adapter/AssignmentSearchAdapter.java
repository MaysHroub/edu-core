package com.bzu.educore.activity.teacher.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bzu.educore.R;
import java.util.List;

public class AssignmentSearchAdapter extends RecyclerView.Adapter<AssignmentSearchAdapter.AssignmentViewHolder> {

    private List<AssignmentItem> assignmentList;
    private OnAssignmentClickListener listener;

    public AssignmentSearchAdapter(List<AssignmentItem> assignmentList, OnAssignmentClickListener listener) {
        this.assignmentList = assignmentList;
        this.listener = listener;
    }

    public void updateAssignments(List<AssignmentItem> newAssignments) {
        this.assignmentList = newAssignments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment_card, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentItem assignment = assignmentList.get(position);
        holder.tvCardAssignmentTitle.setText(assignment.title);
        holder.tvCardSubjectGrade.setText("Subject: " + assignment.subjectTitle + " | Grade: " + assignment.gradeNumber);
        holder.tvCardDueDate.setText("Due Date: " + (assignment.deadline != null ? assignment.deadline : "N/A"));
        holder.tvCardSubmissionCount.setText("Submissions: " + assignment.submittedCount + " / " + assignment.totalStudents);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAssignmentClick(assignment.id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardAssignmentTitle;
        TextView tvCardSubjectGrade;
        TextView tvCardDueDate;
        TextView tvCardSubmissionCount;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardAssignmentTitle = itemView.findViewById(R.id.tvCardAssignmentTitle);
            tvCardSubjectGrade = itemView.findViewById(R.id.tvCardSubjectGrade);
            tvCardDueDate = itemView.findViewById(R.id.tvCardDueDate);
            tvCardSubmissionCount = itemView.findViewById(R.id.tvCardSubmissionCount);
        }
    }

    public interface OnAssignmentClickListener {
        void onAssignmentClick(int assignmentId);
    }

    public static class AssignmentItem {
        public int id;
        public String title;
        public String subjectTitle;
        public int gradeNumber;
        public String deadline;
        public int submittedCount;
        public int totalStudents;

        public AssignmentItem(int id, String title, String subjectTitle, int gradeNumber, String deadline, int submittedCount, int totalStudents) {
            this.id = id;
            this.title = title;
            this.subjectTitle = subjectTitle;
            this.gradeNumber = gradeNumber;
            this.deadline = deadline;
            this.submittedCount = submittedCount;
            this.totalStudents = totalStudents;
        }
    }
}