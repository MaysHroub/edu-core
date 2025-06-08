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

import lombok.Getter;

@Getter
public class HomeroomTeacherAdapter extends RecyclerView.Adapter<HomeroomTeacherAdapter.ViewHolder> {

    private final List<HomeroomTeacherAssigning> assigns;

    public HomeroomTeacherAdapter(List<HomeroomTeacherAssigning> assigns) {
        this.assigns = assigns;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homeroom_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeroomTeacherAssigning assigning = assigns.get(position);
        holder.bind(assigning);
    }

    @Override
    public int getItemCount() {
        return assigns.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTeacher, txtClassroom;

        public ViewHolder(View view) {
            super(view);
            txtTeacher = view.findViewById(R.id.txt_homeroom_tchr);
            txtClassroom = view.findViewById(R.id.txt_classroom);
        }

        public void bind(HomeroomTeacherAssigning assigning) {
            txtTeacher.setText(String.format("%s - %d", assigning.getTeacherName(), assigning.getTeacherId()));
            txtClassroom.setText(String.format("%d - %s", assigning.getClassroomGrade(), assigning.getClassroomSection()));
        }
    }

}
