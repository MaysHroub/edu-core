package com.bzu.educore.activity.teacher;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.fragments.AssignmentInfoFragment;

public class AssignAssignmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_assignment_activity);

        // Load the first fragment
        if (savedInstanceState == null) {
            loadFragment(new AssignmentInfoFragment());
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
