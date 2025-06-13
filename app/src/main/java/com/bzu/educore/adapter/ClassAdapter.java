package com.bzu.educore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.model.Class;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<Class> classes;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(Class classItem);
    }

    public ClassAdapter(List<Class> classes, OnClassClickListener listener) {
        this.classes = classes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_button, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Class classItem = classes.get(position);
        holder.bind(classItem);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public void updateClasses(List<Class> newClasses) {
        this.classes = newClasses;
        notifyDataSetChanged();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView textGradeSection;
        private TextView textTeacher;

        ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            textGradeSection = itemView.findViewById(R.id.text_grade_section);
            textTeacher = itemView.findViewById(R.id.text_teacher);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClassClick(classes.get(position));
                }
            });
        }

        void bind(Class classItem) {
            textGradeSection.setText(classItem.getGradeSection());
            textTeacher.setText(classItem.getTeacherName());
        }
    }
} 