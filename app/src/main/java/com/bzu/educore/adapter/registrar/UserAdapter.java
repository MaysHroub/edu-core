package com.bzu.educore.adapter.registrar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;
import com.bzu.educore.activity.registrar.User;
import com.bzu.educore.activity.registrar.ui.student_management.DummyStudent;
import com.bzu.educore.listener.OnItemClickListener;
import com.bzu.educore.model.user.Person;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<User> users;
    private final OnItemClickListener listener;

    public UserAdapter(List<User> users, OnItemClickListener listener) {
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
        holder.bind(user, position, listener);
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

        public void bind(User user, int position, OnItemClickListener listener) {
            txtId.setText(String.valueOf(user.getId()));
            txtName.setText(String.format("%s %s", user.getFname(), user.getLname()));
            itemView.setOnClickListener(v -> listener.onItemClick(position));
            if (user instanceof DummyStudent)
                imgUser.setImageResource(R.drawable.student_icon);
            else
                imgUser.setImageResource(R.drawable.teacher);
        }
    }

}
