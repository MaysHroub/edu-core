package com.bzu.educore.adapter.registrar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.model.school.Subject;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private final List<Subject> subjects;

    public SubjectAdapter(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtSubjectTitle, txtSubjectGrade, txtSubjectSemester;

        public ViewHolder(View view) {
            super(view);
            txtSubjectTitle = view.findViewById(R.id.txt_subj_title);
            txtSubjectGrade = view.findViewById(R.id.txt_subj_grade);
            txtSubjectSemester = view.findViewById(R.id.txt_subj_semester);
        }

        public void bind(Subject subject) {
            txtSubjectTitle.setText(subject.getTitle());
            txtSubjectGrade.setText(subject.getGradeNumber()+"");
            txtSubjectSemester.setText(subject.getSemesterNumber() == 1 ? "First" : "Second");
        }
    }

}
