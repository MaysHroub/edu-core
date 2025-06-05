package com.bzu.educore.activity.teacher.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TimetableSelectionAdapter extends RecyclerView.Adapter<TimetableSelectionAdapter.ViewHolder> {

    private List<JSONObject> timetableList;
    private OnItemClickListener listener;

    public TimetableSelectionAdapter(List<JSONObject> timetableList, OnItemClickListener listener) {
        this.timetableList = timetableList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectText, classText;

        public ViewHolder(View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.textSubject);
            classText = itemView.findViewById(R.id.textClassGrade);
        }

        public void bind(JSONObject item, OnItemClickListener listener) {
            try {
                subjectText.setText(item.getString("subject_name"));
                classText.setText(item.getString("class_grade_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(v -> listener.onItemClicked(item));
        }
    }

    @Override
    public TimetableSelectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selection_timetable, parent, false); // Simplified layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(timetableList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return timetableList.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(JSONObject item);
    }
}
