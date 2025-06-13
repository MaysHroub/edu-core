package com.example.studentsection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studentsection.model.EventData;
import java.util.List;


// this is a custom recyclerview.adapter that creates visual cards (using item_event.xml)
// binds each event data to a card and displays it in the recyclerview inside vieweventsactivity

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<EventData> eventList;

    public EventAdapter(List<EventData> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder holder, int position) {
        EventData event = eventList.get(position);
        holder.txtSubject.setText(event.getSubject_title());
        holder.txtType.setText(capitalize(event.getType()));
        holder.txtDate.setText(event.getDate());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtSubject, txtType, txtDate;

        public ViewHolder(View itemView) {
            super(itemView);
            txtSubject = itemView.findViewById(R.id.txtSubjectTitle);
            txtType = itemView.findViewById(R.id.txtType);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
