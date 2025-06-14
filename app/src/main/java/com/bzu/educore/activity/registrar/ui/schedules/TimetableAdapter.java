package com.bzu.educore.activity.registrar.ui.schedules;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bzu.educore.R;

import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private List<TimetableCell> mData;
    private int mColumnCount;

    // Constructor
    public TimetableAdapter(List<TimetableCell> data, int columnCount) {
        this.mData = data;
        this.mColumnCount = columnCount;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getCellType();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TimetableCell.TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_timetable_cell_header, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_timetable_cell_standard, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimetableCell cell = mData.get(position);
        holder.cellText.setText(cell.getText());
        holder.cellText.setTextColor(cell.getTextColor());

        // Set background color directly on the item view
        holder.itemView.setBackgroundColor(cell.getBackgroundColor());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cellText;

        ViewHolder(View itemView) {
            super(itemView);
            cellText = itemView.findViewById(R.id.cell_text);
        }
    }

    public void updateData(List<TimetableCell> newData) {
        mData = newData;
        notifyDataSetChanged();
    }
} 