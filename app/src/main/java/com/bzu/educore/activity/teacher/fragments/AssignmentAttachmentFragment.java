package com.bzu.educore.activity.teacher.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bzu.educore.R;

public class AssignmentAttachmentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assignment_attachment, container, false);

        // TODO: Add file upload logic here

        Button publishButton = view.findViewById(R.id.publishAssignmentButton);
        publishButton.setOnClickListener(v -> {
            // TODO: Submit assignment logic
        });

        return view;
    }
}
