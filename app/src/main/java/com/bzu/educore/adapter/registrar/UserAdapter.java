package com.bzu.educore.adapter.registrar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.model.user.User;
import com.bzu.educore.model.user.Student;
import com.bzu.educore.listener.OnItemClickListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<User> users;
    private final OnItemClickListener<User> listener;

    public UserAdapter(List<User> users, OnItemClickListener<User> listener) {
        this.users = users;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtId, txtName;
        private final ImageView imgUser;

        public ViewHolder(View view) {
            super(view);
            txtId = view.findViewById(R.id.txt_usr_id);
            txtName = view.findViewById(R.id.txt_usr_name);
            imgUser = view.findViewById(R.id.img_user);
        }

        public void bind(User user, OnItemClickListener<User> listener) {
            txtId.setText(String.valueOf(user.getId()));
            txtName.setText(String.format("%s %s", user.getFname(), user.getLname()));
            itemView.setOnClickListener(v -> listener.onItemClick(user));
            if (user instanceof Student)
                imgUser.setImageResource(R.drawable.student_icon);
            else
                imgUser.setImageResource(R.drawable.teacher);
        }
    }

}
