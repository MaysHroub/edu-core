package com.bzu.educore.adapter.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.model.Mark;

import java.util.List;

public class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.MarkViewHolder> {
    private List<Mark> marksList;

    public MarksAdapter(List<Mark> marksList) {
        this.marksList = marksList;
    }

    @NonNull
    @Override
    public MarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mark, parent, false);
        return new MarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkViewHolder holder, int position) {
        Mark mark = marksList.get(position);
        holder.subjectTextView.setText(mark.getSubject());
        holder.gradeTextView.setText(mark.getGrade());
        holder.semesterTextView.setText(mark.getSemester());
    }

    @Override
    public int getItemCount() {
        return marksList.size();
    }

    static class MarkViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;
        TextView gradeTextView;
        TextView semesterTextView;

        MarkViewHolder(View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subjectTextView);
            gradeTextView = itemView.findViewById(R.id.gradeTextView);
            semesterTextView = itemView.findViewById(R.id.semesterTextView);
        }
    }
} 