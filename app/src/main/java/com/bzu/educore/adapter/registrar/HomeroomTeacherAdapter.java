package com.bzu.educore.adapter.registrar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.activity.registrar.ui.homeroom_teacher.HomeroomTeacherAssigning;

import java.util.List;

public class HomeroomTeacherAdapter extends RecyclerView.Adapter<HomeroomTeacherAdapter.ViewHolder> {

    private final List<HomeroomTeacherAssigning> homeroomTeacherAssigning;

    public HomeroomTeacherAdapter(List<HomeroomTeacherAssigning> homeroomTeacherAssigning) {
        this.homeroomTeacherAssigning = homeroomTeacherAssigning;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homeroom_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeroomTeacherAssigning assigning = homeroomTeacherAssigning.get(position);
        holder.bind(assigning);
    }

    @Override
    public int getItemCount() {
        return homeroomTeacherAssigning.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTeacher, txtClassroom;

        public ViewHolder(View view) {
            super(view);
            txtTeacher = view.findViewById(R.id.txt_homeroom_tchr);
            txtClassroom = view.findViewById(R.id.txt_classroom);
        }

        public void bind(HomeroomTeacherAssigning assigning) {
            txtTeacher.setText(assigning.getTeacher().toString());
            txtClassroom.setText(assigning.getClassroom().toString());
        }
    }

}
