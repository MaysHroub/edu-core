package com.bzu.educore.activity.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bzu.educore.R;
import com.bzu.educore.activity.LoginActivity;
import com.bzu.educore.activity.teacher.ui.navigation_management.TeacherDashboardFragment;
import com.bzu.educore.activity.teacher.ui.profile.TeacherProfileFragment;
import com.bzu.educore.util.SharedPreferencesManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import lombok.Getter;

@Getter
public class TeacherMainActivity extends AppCompatActivity {

    // Getter method to access teacher ID from fragments
    private int teacherId; // Store teacher ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        // Get teacher ID from SharedPreferences (much faster than network call)
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        teacherId = prefsManager.getUserId();


        // Setup bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = TeacherDashboardFragment.newInstance(teacherId);
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = TeacherProfileFragment.newInstance(teacherId);
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, false);
                return true;
            }
            return false;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(TeacherDashboardFragment.newInstance(teacherId), false);
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