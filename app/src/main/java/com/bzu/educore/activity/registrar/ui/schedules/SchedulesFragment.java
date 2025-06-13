package com.bzu.educore.activity.registrar.ui.schedules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bzu.educore.R;

public class SchedulesFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedules, container, false);
        
        TextView textView = root.findViewById(R.id.text_schedules);
        textView.setText("Hello World");
        
        return root;
    }
} 