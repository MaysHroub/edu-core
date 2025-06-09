package com.bzu.educore.adapter.teacher;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bzu.educore.R;
import com.bzu.educore.model.task.StudentSubmission;
import com.bzu.educore.util.teacher.MarkValidator;
import com.bzu.educore.util.teacher.StatusUtils;
import java.util.List;

public class StudentSubmissionAdapter extends RecyclerView.Adapter<StudentSubmissionAdapter.StudentViewHolder> {

    public enum DisplayMode { ASSIGNMENT_MODE, EXAM_MODE }

    private final Context context;
    private final List<StudentSubmission> students;
    private final DisplayMode displayMode;
    private final double maxMark;

    public StudentSubmissionAdapter(
            Context context,
            List<StudentSubmission> students,
            DisplayMode displayMode,
            double maxMark
    ) {
        this.context = context;
        this.students = students;
        this.displayMode = displayMode;
        this.maxMark = maxMark;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = displayMode == DisplayMode.EXAM_MODE
                ? R.layout.item_student_exam
                : R.layout.item_student_submission;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new StudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder h, int pos) {
        h.bind(students.get(pos));
    }

    @Override
    public int getItemCount() { return students.size(); }

    @Override
    public void onViewRecycled(@NonNull StudentViewHolder h) {
        h.cleanup();
        super.onViewRecycled(h);
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPfp, imgToggle;
        TextView tvName, tvStatus, tvDate;
        Button btnView;
        EditText etMark, etFeedback;
        LinearLayout llFeedback;
        TextWatcher markWatcher, fbWatcher;
        boolean feedbackOpen = false;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPfp      = itemView.findViewById(R.id.imgStudentPfp);
            imgToggle   = itemView.findViewById(R.id.imgFeedbackToggle);
            tvName      = itemView.findViewById(R.id.tvStudentName);
            etMark      = itemView.findViewById(R.id.etStudentMarkInput);
            llFeedback  = itemView.findViewById(R.id.llFeedbackSection);
            etFeedback  = itemView.findViewById(R.id.etFeedbackInput);

            if (displayMode == DisplayMode.ASSIGNMENT_MODE) {
                tvStatus = itemView.findViewById(R.id.tvSubmissionStatus);
                tvDate   = itemView.findViewById(R.id.tvSubmissionDate);
                btnView  = itemView.findViewById(R.id.btnViewSubmission);
            }
        }

        void bind(StudentSubmission s) {
            tvName.setText(s.getStudentName());

            if (displayMode == DisplayMode.ASSIGNMENT_MODE) {
                // status + date
                int color = StatusUtils.getStatusColor(s.getStatus());
                tvStatus.setText(StatusUtils.formatStatus(s.getStatus()));
                tvStatus.setTextColor(color);

                String d = s.getSubmissionDate();
                if (d!=null && d.length()>0 && !"null".equals(d)) {
                    tvDate.setText("Submitted: "+d);
                    tvDate.setVisibility(View.VISIBLE);
                } else tvDate.setVisibility(View.GONE);

                boolean canView = StatusUtils.canViewSubmission(s.getStatus());
                btnView.setEnabled(canView);
                btnView.setAlpha(canView?1f:0.5f);
                btnView.setOnClickListener(v -> {
                    String url = s.getSubmissionFileUrl();
                    if (url!=null && !url.isEmpty()) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        if (i.resolveActivity(context.getPackageManager())!=null)
                            context.startActivity(i);
                    }
                });
            }

            // MARK
            if (markWatcher!=null) etMark.removeTextChangedListener(markWatcher);
            boolean canMark = displayMode==DisplayMode.EXAM_MODE
                    || StatusUtils.canMarkSubmission(s.getStatus());
            etMark.setEnabled(canMark);
            etMark.setAlpha(canMark?1f:0.5f);
            etMark.setHint(canMark?"Mark":"N/A");
            etMark.setText(s.getMark()!=null
                    ? String.format("%.2f", s.getMark())
                    : "");
            markWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence c,int a,int b,int d){}
                @Override public void afterTextChanged(Editable e){}
                @Override public void onTextChanged(CharSequence c,int a,int b,int d) {
                    MarkValidator.ValidationResult r =
                            MarkValidator.validateMark(c.toString(), maxMark);
                    s.setMark(r.value);
                }
            };
            etMark.addTextChangedListener(markWatcher);
            imgToggle.setVisibility(canMark ? View.VISIBLE : View.GONE);

            // FEEDBACK
            if (fbWatcher!=null) etFeedback.removeTextChangedListener(fbWatcher);
            String fb = s.getFeedback();
            boolean hasFb = fb!=null && fb.trim().length()>0 && !"null".equalsIgnoreCase(fb);
            etFeedback.setText(hasFb ? fb : "");
            if (hasFb && !feedbackOpen) toggleFeedback();
            imgToggle.setOnClickListener(v -> toggleFeedback());
            fbWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence c,int a,int b,int d){}
                @Override public void afterTextChanged(Editable e){}
                @Override public void onTextChanged(CharSequence c,int a,int b,int d) {
                    String t = c.toString().trim();
                    s.setFeedback(t.isEmpty() ? null : t);
                }
            };
            etFeedback.addTextChangedListener(fbWatcher);
            imgToggle.setAlpha(hasFb ? 1f : 0.6f);
        }

        void toggleFeedback() {
            feedbackOpen = !feedbackOpen;
            llFeedback.setVisibility(feedbackOpen
                    ? View.VISIBLE : View.GONE);
            imgToggle.animate()
                    .rotation(feedbackOpen?180f:0f)
                    .setDuration(200).start();
            if (feedbackOpen) etFeedback.requestFocus();
        }

        void cleanup() {
            if (markWatcher!=null)
                etMark.removeTextChangedListener(markWatcher);
            if (fbWatcher!=null)
                etFeedback.removeTextChangedListener(fbWatcher);
        }
    }
}
