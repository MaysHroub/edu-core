package com.bzu.educore.activity.registrar.ui.schedules;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bzu.educore.R;
import com.bzu.educore.model.Class;

public class ClassDetailsFragment extends Fragment {
    private static final String ARG_CLASS = "classData";
    private TextView textClassDetails;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textClassDetails = view.findViewById(R.id.text_class_details);

        // Get class data from arguments
        Bundle args = getArguments();
        if (args != null) {
            Class classData = (Class) args.getSerializable(ARG_CLASS);
            if (classData != null) {
                textClassDetails.setText(String.format("Hello Grade %s Section %s", 
                    classData.getGradeNumber(), classData.getSection()));
            }
        }
    }
} 