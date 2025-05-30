package com.bzu.educore.activity.teacher;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;

import java.util.List;

public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.ViewHolder> {

    private List<StudentSubmission> submissions;
    private OnSubmissionClickListener clickListener;

    public interface OnSubmissionClickListener {
        void onSubmissionClick(StudentSubmission submission);
    }

    public SubmissionsAdapter(List<StudentSubmission> submissions, OnSubmissionClickListener clickListener) {
        this.submissions = submissions;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_submission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentSubmission submission = submissions.get(position);

        holder.tvStudentName.setText(submission.studentName);

        // Set status and color
        holder.tvSubmissionStatus.setText(getStatusText(submission.status));
        holder.tvSubmissionStatus.setTextColor(getStatusColor(holder, submission.status));

        // Set submission date
        if (submission.submissionDate != null) {
            holder.tvSubmissionDate.setText("Submitted: " + submission.submissionDate);
            holder.tvSubmissionDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvSubmissionDate.setVisibility(View.GONE);
        }

        // Set mark if available
        if (submission.mark != null) {
            holder.tvMark.setText(String.format("%.1f", submission.mark));
            holder.tvMark.setVisibility(View.VISIBLE);
        } else {
            holder.tvMark.setVisibility(View.INVISIBLE);
        }

        // Set button state and click listener
        if (submission.submissionFileUrl != null && !submission.submissionFileUrl.isEmpty()) {
            holder.btnViewSubmission.setEnabled(true);
            holder.btnViewSubmission.setText("View Submission");
            holder.btnViewSubmission.setOnClickListener(v -> clickListener.onSubmissionClick(submission));
        } else {
            holder.btnViewSubmission.setEnabled(false);
            holder.btnViewSubmission.setText("No Submission");
            holder.btnViewSubmission.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }

    private String getStatusText(String status) {
        switch (status) {
            case "submitted":
                return "Submitted";
            case "overdue":
                return "Overdue";
            case "pending":
                return "Pending";
            default:
                return "Unknown";
        }
    }

    private int getStatusColor(ViewHolder holder, String status) {
        switch (status) {
            case "submitted":
                return ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark);
            case "overdue":
                return ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark);
            case "pending":
                return ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_orange_dark);
            default:
                return Color.GRAY;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStudentPfp;
        TextView tvStudentName;
        TextView tvSubmissionStatus;
        TextView tvSubmissionDate;
        TextView tvMark;
        Button btnViewSubmission;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStudentPfp = itemView.findViewById(R.id.imgStudentPfp);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvSubmissionStatus = itemView.findViewById(R.id.tvSubmissionStatus);
            tvSubmissionDate = itemView.findViewById(R.id.tvSubmissionDate);
            tvMark = itemView.findViewById(R.id.tvMark);
            btnViewSubmission = itemView.findViewById(R.id.btnViewSubmission);
        }
    }

    public static class StudentSubmission {
        public String studentId;
        public String studentName;
        public String submissionDate;
        public String submissionFileUrl;
        public Double mark;
        public String feedback;
        public String status;

        public StudentSubmission(String studentId, String studentName, String submissionDate,
                                 String submissionFileUrl, Double mark, String feedback, String status) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.submissionDate = submissionDate;
            this.submissionFileUrl = submissionFileUrl;
            this.mark = mark;
            this.feedback = feedback;
            this.status = status;
        }
    }
}