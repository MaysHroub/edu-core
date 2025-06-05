package com.bzu.educore.activity.teacher.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.fragment.AssignmentSubmissionsFragment;
import com.bzu.educore.model.Assignment;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {
    private List<Assignment> assignments;
    private FragmentActivity activity;

    public AssignmentAdapter(List<Assignment> assignments, FragmentActivity activity) {
        this.assignments = assignments;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubjectTitle;
        private TextView tvGrade;
        private TextView tvTeacherName;
        private TextView tvMaxScore;
        private TextView tvDate;
        private TextView tvDeadline;
        private Button btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeViews();
        }

        private void initializeViews() {
            tvSubjectTitle = itemView.findViewById(R.id.tv_subject_title);
            tvGrade = itemView.findViewById(R.id.tv_grade);
            tvTeacherName = itemView.findViewById(R.id.tv_teacher_name);
            tvMaxScore = itemView.findViewById(R.id.tv_max_score);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDeadline = itemView.findViewById(R.id.tv_deadline);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }

        public void bind(Assignment assignment) {
            // Set assignment data to views
            tvSubjectTitle.setText(assignment.getSubjectTitle());
            tvGrade.setText("Grade " + assignment.getGradeNumber());
            tvTeacherName.setText("Teacher: " + assignment.getTeacherName());
            tvMaxScore.setText("Max: " + assignment.getMaxScore());
            tvDate.setText("Created: " + assignment.getDate());
            tvDeadline.setText("Due: " + assignment.getDeadline());

            // Set click listener for view submissions button
            btnViewDetails.setOnClickListener(v -> {
                if (activity != null) {
                    // Create and navigate to AssignmentSubmissionsFragment
                    AssignmentSubmissionsFragment submissionsFragment =
                            AssignmentSubmissionsFragment.newInstance(assignment.getId());

                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, submissionsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }
}