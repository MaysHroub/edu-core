package com.bzu.educore.adapter.teacher;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.ui.student_management.StudentSubmissionsActivity;
import com.bzu.educore.model.task.StudentSubmission;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.bzu.educore.util.teacher.MarkValidator;
import com.bzu.educore.util.teacher.StatusUtils;
import org.json.JSONException;
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

    private void viewSubmission(StudentSubmission student) {
        // Get taskId from the activity
        int taskId = -1;
        if (context instanceof StudentSubmissionsActivity) {
            taskId = ((StudentSubmissionsActivity) context).getTaskId();
        }

        if (taskId < 0) {
            Toast.makeText(context, "Invalid task ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // First, get file information from the server
        String infoUrl = UrlManager.URL_VIEW_SUBMISSION +
                "?task_id=" + taskId +
                "&student_id=" + student.getStudentId();

        JsonObjectRequest infoRequest = new JsonObjectRequest(
                Request.Method.GET, infoUrl, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            String fileName = response.getString("file_name");
                            String fileType = response.getString("file_type");
                            String viewUrl = response.getString("view_url");
                            String downloadUrl = response.getString("download_url");

                            // Show dialog with options
                            showSubmissionDialog(student.getStudentName(), fileName, fileType, viewUrl, downloadUrl);
                        } else {
                            Toast.makeText(context, "No submission file found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error reading submission info", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String message = "Failed to load submission";
                    if (error.networkResponse != null) {
                        switch (error.networkResponse.statusCode) {
                            case 404:
                                message = "No submission found";
                                break;
                            case 403:
                                message = "Access denied";
                                break;
                            case 400:
                                message = "Invalid request";
                                break;
                        }
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(infoRequest);
    }

    private void showSubmissionDialog(String studentName, String fileName, String fileType, String viewUrl, String downloadUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Submission: " + studentName)
                .setMessage("File: " + fileName + "\nType: " + fileType)
                .setPositiveButton("View", (dialog, which) -> {
                    // Open file for viewing (works for PDFs, images)
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(viewUrl));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    } else {
                        // Fallback to download if no app can view the file
                        downloadFile(downloadUrl, fileName);
                    }
                })
                .setNeutralButton("Download", (dialog, which) -> {
                    downloadFile(downloadUrl, fileName);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void downloadFile(String downloadUrl, String fileName) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle("Downloading " + fileName);
            request.setDescription("Downloading student submission");

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
                Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Download manager not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                if (d != null && d.length() > 0 && !"null".equals(d)) {
                    tvDate.setText("Submitted: " + d);
                    tvDate.setVisibility(View.VISIBLE);
                } else {
                    tvDate.setVisibility(View.GONE);
                }

                boolean canView = StatusUtils.canViewSubmission(s.getStatus());
                btnView.setEnabled(canView);
                btnView.setAlpha(canView ? 1f : 0.5f);

                // Updated click listener to use the new viewSubmission method
                btnView.setOnClickListener(v -> {
                    if (StatusUtils.canViewSubmission(s.getStatus())) {
                        viewSubmission(s);
                    } else {
                        Toast.makeText(context, "No submission available to view", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // MARK
            if (markWatcher != null) etMark.removeTextChangedListener(markWatcher);
            boolean canMark = displayMode == DisplayMode.EXAM_MODE
                    || StatusUtils.canMarkSubmission(s.getStatus());
            etMark.setEnabled(canMark);
            etMark.setAlpha(canMark ? 1f : 0.5f);
            etMark.setHint(canMark ? "Mark" : "N/A");
            etMark.setText(s.getMark() != null
                    ? String.format("%.2f", s.getMark())
                    : "");
            markWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence c, int a, int b, int d) {}
                @Override public void afterTextChanged(Editable e) {}
                @Override public void onTextChanged(CharSequence c, int a, int b, int d) {
                    MarkValidator.ValidationResult r =
                            MarkValidator.validateMark(c.toString(), maxMark);
                    s.setMark(r.value);
                }
            };
            etMark.addTextChangedListener(markWatcher);

            // Show/hide feedback toggle based on marking capability
            if (imgToggle != null) {
                imgToggle.setVisibility(canMark ? View.VISIBLE : View.GONE);
            }

            // FEEDBACK
            if (fbWatcher != null) etFeedback.removeTextChangedListener(fbWatcher);
            String fb = s.getFeedback();
            boolean hasFb = fb != null && fb.trim().length() > 0 && !"null".equalsIgnoreCase(fb);
            etFeedback.setText(hasFb ? fb : "");
            if (hasFb && !feedbackOpen) toggleFeedback();

            if (imgToggle != null) {
                imgToggle.setOnClickListener(v -> toggleFeedback());
                imgToggle.setAlpha(hasFb ? 1f : 0.6f);
            }

            fbWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence c, int a, int b, int d) {}
                @Override public void afterTextChanged(Editable e) {}
                @Override public void onTextChanged(CharSequence c, int a, int b, int d) {
                    String t = c.toString().trim();
                    s.setFeedback(t.isEmpty() ? null : t);
                }
            };
            etFeedback.addTextChangedListener(fbWatcher);
        }

        void toggleFeedback() {
            feedbackOpen = !feedbackOpen;
            if (llFeedback != null) {
                llFeedback.setVisibility(feedbackOpen
                        ? View.VISIBLE : View.GONE);
            }
            if (imgToggle != null) {
                imgToggle.animate()
                        .rotation(feedbackOpen ? 180f : 0f)
                        .setDuration(200).start();
            }
            if (feedbackOpen && etFeedback != null) {
                etFeedback.requestFocus();
            }
        }

        void cleanup() {
            if (markWatcher != null)
                etMark.removeTextChangedListener(markWatcher);
            if (fbWatcher != null)
                etFeedback.removeTextChangedListener(fbWatcher);
        }
    }
}