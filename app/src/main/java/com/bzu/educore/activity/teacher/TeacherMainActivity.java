package com.bzu.educore.activity.teacher;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.ui.navigation_management.TeacherDashboardFragment;

import lombok.Getter;

@Getter
public class TeacherMainActivity extends AppCompatActivity {

    // Getter method to access teacher ID from fragments
    private int teacherId; // Store teacher ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        // Get teacher ID from login intent
        teacherId = getIntent().getIntExtra("TEACHER_ID", -1);

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            // Pass teacher ID to dashboard fragment
            TeacherDashboardFragment dashboardFragment = TeacherDashboardFragment.newInstance(teacherId);
            ft.replace(R.id.fragment_container, dashboardFragment);
            ft.commit();
        }
    }

    public void loadFragment(Fragment fragment, boolean addToStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);

        if (addToStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
