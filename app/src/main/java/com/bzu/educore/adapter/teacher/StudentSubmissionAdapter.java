package com.bzu.educore.adapter.teacher;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.model.task.StudentSubmission;

import java.util.List;

public class StudentSubmissionAdapter extends RecyclerView.Adapter<StudentSubmissionAdapter.SubmissionViewHolder> {

    private final List<StudentSubmission> submissions;
    private final OnViewSubmissionClickListener viewSubmissionClickListener;
    private final OnMarkChangedListener markChangedListener;

    public StudentSubmissionAdapter(
            List<StudentSubmission> submissions,
            OnViewSubmissionClickListener viewSubmissionClickListener,
            OnMarkChangedListener markChangedListener) {
        this.submissions = submissions;
        this.viewSubmissionClickListener = viewSubmissionClickListener;
        this.markChangedListener = markChangedListener;
    }

    @NonNull
    @Override
    public SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_submission, parent, false);
        return new SubmissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionViewHolder holder, int position) {
        StudentSubmission submission = submissions.get(position);

        // Set student name
        holder.tvStudentName.setText(submission.getStudentName());

        // Set status with color coding
        holder.tvSubmissionStatus.setText(formatStatus(submission.getStatus()));
        setStatusColor(holder.tvSubmissionStatus, submission.getStatus());

        // Submission date
        if (submission.getSubmissionDate() != null &&
                !submission.getSubmissionDate().isEmpty() &&
                !"null".equals(submission.getSubmissionDate())) {
            holder.tvSubmissionDate.setText("Submitted: " + submission.getSubmissionDate());
            holder.tvSubmissionDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvSubmissionDate.setVisibility(View.GONE);
        }

        // Remove previous TextWatcher if any
        if (holder.markTextWatcher != null) {
            holder.etStudentMarkInput.removeTextChangedListener(holder.markTextWatcher);
        }

        // Show/hide mark input based on status
        boolean canMark = "submitted".equalsIgnoreCase(submission.getStatus());
        holder.etStudentMarkInput.setEnabled(canMark);
        holder.etStudentMarkInput.setAlpha(canMark ? 1.0f : 0.5f);

        if (submission.getMark() != null) {
            holder.etStudentMarkInput.setText(String.format("%.2f", submission.getMark()));
        } else {
            holder.etStudentMarkInput.setText("");
            holder.etStudentMarkInput.setHint(canMark ? "Mark" : "N/A");
        }

        // TextWatcher for mark changes
        holder.markTextWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
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
                    submission.setMark(newMark);
                    if (markChangedListener != null) {
                        markChangedListener.onMarkChanged(submission.getStudentId(), newMark);
                    }
                } catch (NumberFormatException e) {
                    submission.setMark(null);
                    if (markChangedListener != null) {
                        markChangedListener.onMarkChanged(submission.getStudentId(), null);
                    }
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        holder.etStudentMarkInput.addTextChangedListener(holder.markTextWatcher);

        // View button enabled only if status="submitted"
        holder.btnViewSubmission.setEnabled("submitted".equalsIgnoreCase(submission.getStatus()));
        holder.btnViewSubmission.setAlpha("submitted".equalsIgnoreCase(submission.getStatus()) ? 1.0f : 0.5f);
        holder.btnViewSubmission.setOnClickListener(v -> {
            if (viewSubmissionClickListener != null &&
                    "submitted".equalsIgnoreCase(submission.getStatus())) {
                viewSubmissionClickListener.onViewSubmissionClick(submission);
            }
        });
    }

    private String formatStatus(String status) {
        if (status == null) return "Unknown";
        switch (status.toLowerCase()) {
            case "submitted":
                return "Submitted ✓";
            case "overdue":
                return "Overdue ⚠";
            case "pending":
                return "Pending ⏳";
            default:
                return status;
        }
    }

    private void setStatusColor(TextView textView, String status) {
        if (status == null) {
            textView.setTextColor(Color.GRAY);
            return;
        }
        switch (status.toLowerCase()) {
            case "submitted":
                textView.setTextColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "overdue":
                textView.setTextColor(Color.parseColor("#F44336")); // Red
                break;
            case "pending":
                textView.setTextColor(Color.parseColor("#FFA000")); // Orange
                break;
            default:
                textView.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }

    static class SubmissionViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStudentPfp;
        TextView tvStudentName;
        TextView tvSubmissionStatus;
        TextView tvSubmissionDate;
        EditText etStudentMarkInput;
        Button btnViewSubmission;
        TextWatcher markTextWatcher;

        public SubmissionViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStudentPfp = itemView.findViewById(R.id.imgStudentPfp);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvSubmissionStatus = itemView.findViewById(R.id.tvSubmissionStatus);
            tvSubmissionDate = itemView.findViewById(R.id.tvSubmissionDate);
            etStudentMarkInput = itemView.findViewById(R.id.etStudentMarkInput);
            btnViewSubmission = itemView.findViewById(R.id.btnViewSubmission);
        }
    }

    public interface OnViewSubmissionClickListener {
        void onViewSubmissionClick(StudentSubmission submission);
    }

    public interface OnMarkChangedListener {
        void onMarkChanged(String studentId, Double mark);
    }
}
