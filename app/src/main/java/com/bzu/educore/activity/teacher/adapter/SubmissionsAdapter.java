package com.bzu.educore.activity.teacher.adapter;

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
import java.util.List;

public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.SubmissionViewHolder> {

    private List<StudentSubmission> submissions;
    private OnViewSubmissionClickListener viewSubmissionClickListener;
    private OnMarkChangedListener markChangedListener;

    public SubmissionsAdapter(List<StudentSubmission> submissions,
                              OnViewSubmissionClickListener viewSubmissionClickListener,
                              OnMarkChangedListener markChangedListener) {
        this.submissions = submissions;
        this.viewSubmissionClickListener = viewSubmissionClickListener;
        this.markChangedListener = markChangedListener;
    }

    @NonNull
    @Override
    public SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_submission, parent, false);
        return new SubmissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionViewHolder holder, int position) {
        StudentSubmission submission = submissions.get(position);
        holder.tvStudentName.setText(submission.studentName);
        holder.tvSubmissionStatus.setText(submission.status);

        if (submission.submissionDate != null && !submission.submissionDate.isEmpty() && !"null".equals(submission.submissionDate)) {
            holder.tvSubmissionDate.setText("Submitted: " + submission.submissionDate);
            holder.tvSubmissionDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvSubmissionDate.setVisibility(View.GONE);
        }

        if (holder.markTextWatcher != null) {
            holder.etStudentMarkInput.removeTextChangedListener(holder.markTextWatcher);
        }

        if (submission.mark != null) {
            holder.etStudentMarkInput.setText(String.valueOf(submission.mark));
        } else {
            holder.etStudentMarkInput.setText("");
            holder.etStudentMarkInput.setHint("Mark");
        }

        holder.markTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Double newMark = null;
                    if (s != null && s.length() > 0) {
                        newMark = Double.parseDouble(s.toString());
                    }
                    submission.mark = newMark;
                    if (markChangedListener != null) {
                        markChangedListener.onMarkChanged(submission.studentId, newMark);
                    }
                } catch (NumberFormatException e) {
                    submission.mark = null;
                    if (markChangedListener != null) {
                        markChangedListener.onMarkChanged(submission.studentId, null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        holder.etStudentMarkInput.addTextChangedListener(holder.markTextWatcher);


        holder.btnViewSubmission.setOnClickListener(v -> {
            if (viewSubmissionClickListener != null) {
                viewSubmissionClickListener.onViewSubmissionClick(submission);
            }
        });
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

    public interface OnViewSubmissionClickListener {
        void onViewSubmissionClick(StudentSubmission submission);
    }

    public interface OnMarkChangedListener {
        void onMarkChanged(String studentId, Double mark);
    }
}