package com.bzu.educore.adapter.teacher;

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
import com.bzu.educore.util.Constants;
import com.bzu.educore.util.MarkValidator;
import com.bzu.educore.util.StatusUtils;
import java.util.List;

public class UnifiedStudentAdapter extends RecyclerView.Adapter<UnifiedStudentAdapter.StudentViewHolder> {

    public enum DisplayMode {
        ASSIGNMENT_MODE,  // Shows status, date, view button
        EXAM_MODE        // Shows only name and mark input
    }

    private final List<StudentSubmission> students;
    private final DisplayMode displayMode;
    private final OnViewSubmissionClickListener viewSubmissionClickListener;
    private final OnMarkChangedListener markChangedListener;
    private final double maxMark;

    public UnifiedStudentAdapter(
            List<StudentSubmission> students,
            DisplayMode displayMode,
            OnViewSubmissionClickListener viewSubmissionClickListener,
            OnMarkChangedListener markChangedListener,
            double maxMark) {
        this.students = students;
        this.displayMode = displayMode;
        this.viewSubmissionClickListener = viewSubmissionClickListener;
        this.markChangedListener = markChangedListener;
        this.maxMark = maxMark;
    }

    // Convenience constructor for exam mode
    public UnifiedStudentAdapter(
            List<StudentSubmission> students,
            OnMarkChangedListener markChangedListener,
            double maxMark) {
        this(students, DisplayMode.EXAM_MODE, null, markChangedListener, maxMark);
    }

    // Convenience constructor for assignment mode with default max mark
    public UnifiedStudentAdapter(
            List<StudentSubmission> students,
            OnViewSubmissionClickListener viewSubmissionClickListener,
            OnMarkChangedListener markChangedListener) {
        this(students, DisplayMode.ASSIGNMENT_MODE, viewSubmissionClickListener, markChangedListener, Constants.MAX_MARK);
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = displayMode == DisplayMode.EXAM_MODE ?
                R.layout.item_student_exam : R.layout.item_student_submission;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new StudentViewHolder(view, displayMode);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentSubmission student = students.get(position);
        holder.bind(student, maxMark, viewSubmissionClickListener, markChangedListener);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    @Override
    public void onViewRecycled(@NonNull StudentViewHolder holder) {
        holder.cleanup();
        super.onViewRecycled(holder);
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgStudentPfp;
        private final TextView tvStudentName;
        private final TextView tvSubmissionStatus;
        private final TextView tvSubmissionDate;
        private final EditText etStudentMarkInput;
        private final Button btnViewSubmission;
        private final DisplayMode displayMode;

        private TextWatcher markTextWatcher;

        public StudentViewHolder(@NonNull View itemView, DisplayMode displayMode) {
            super(itemView);
            this.displayMode = displayMode;

            imgStudentPfp = itemView.findViewById(R.id.imgStudentPfp);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            etStudentMarkInput = itemView.findViewById(R.id.etStudentMarkInput);

            // These views only exist in assignment mode
            if (displayMode == DisplayMode.ASSIGNMENT_MODE) {
                tvSubmissionStatus = itemView.findViewById(R.id.tvSubmissionStatus);
                tvSubmissionDate = itemView.findViewById(R.id.tvSubmissionDate);
                btnViewSubmission = itemView.findViewById(R.id.btnViewSubmission);
            } else {
                tvSubmissionStatus = null;
                tvSubmissionDate = null;
                btnViewSubmission = null;
            }
        }

        public void bind(StudentSubmission student, double maxMark,
                         OnViewSubmissionClickListener viewClickListener,
                         OnMarkChangedListener markChangedListener) {

            // Set student name
            tvStudentName.setText(student.getStudentName());

            // Handle assignment-specific views
            if (displayMode == DisplayMode.ASSIGNMENT_MODE && student.getStatus() != null) {
                bindAssignmentViews(student, viewClickListener);
            }

            // Handle mark input
            bindMarkInput(student, maxMark, markChangedListener);
        }

        private void bindAssignmentViews(StudentSubmission student, OnViewSubmissionClickListener viewClickListener) {
            // Status
            tvSubmissionStatus.setText(StatusUtils.formatStatus(student.getStatus()));
            tvSubmissionStatus.setTextColor(StatusUtils.getStatusColor(student.getStatus()));

            // Submission date
            String submissionDate = student.getSubmissionDate();
            if (submissionDate != null && !submissionDate.isEmpty() && !"null".equals(submissionDate)) {
                tvSubmissionDate.setText("Submitted: " + submissionDate);
                tvSubmissionDate.setVisibility(View.VISIBLE);
            } else {
                tvSubmissionDate.setVisibility(View.GONE);
            }

            // View button
            boolean canView = StatusUtils.canViewSubmission(student.getStatus());
            btnViewSubmission.setEnabled(canView);
            btnViewSubmission.setAlpha(canView ? 1.0f : 0.5f);
            btnViewSubmission.setOnClickListener(v -> {
                if (viewClickListener != null && canView) {
                    viewClickListener.onViewSubmissionClick(student);
                }
            });
        }

        private void bindMarkInput(StudentSubmission student, double maxMark, OnMarkChangedListener markChangedListener) {
            // Remove previous watcher
            cleanup();

            // Set mark input state
            boolean canMark = displayMode == DisplayMode.EXAM_MODE ||
                    StatusUtils.canMarkSubmission(student.getStatus());
            etStudentMarkInput.setEnabled(canMark);
            etStudentMarkInput.setAlpha(canMark ? 1.0f : 0.5f);

            // Set current value
            if (student.getMark() != null) {
                etStudentMarkInput.setText(String.format("%.2f", student.getMark()));
            } else {
                etStudentMarkInput.setText("");
            }
            etStudentMarkInput.setHint(canMark ? "Mark" : "N/A");

            // Add new watcher
            markTextWatcher = new MarkTextWatcher(student, maxMark, markChangedListener);
            etStudentMarkInput.addTextChangedListener(markTextWatcher);
        }

        public void cleanup() {
            if (markTextWatcher != null) {
                etStudentMarkInput.removeTextChangedListener(markTextWatcher);
                markTextWatcher = null;
            }
        }
    }

    private static class MarkTextWatcher implements TextWatcher {
        private final StudentSubmission student;
        private final double maxMark;
        private final OnMarkChangedListener listener;

        public MarkTextWatcher(StudentSubmission student, double maxMark, OnMarkChangedListener listener) {
            this.student = student;
            this.maxMark = maxMark;
            this.listener = listener;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            MarkValidator.ValidationResult result = MarkValidator.validateMark(s.toString(), maxMark);
            student.setMark(result.value);
            if (listener != null) {
                listener.onMarkChanged(student.getStudentId(), result.value);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    public interface OnViewSubmissionClickListener {
        void onViewSubmissionClick(StudentSubmission submission);
    }

    public interface OnMarkChangedListener {
        void onMarkChanged(String studentId, Double mark);
    }
}
