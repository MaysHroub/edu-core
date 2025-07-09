package com.bzu.educore.activity.teacher;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.ui.navigation_management.TeacherDashboardFragment;
import com.bzu.educore.activity.teacher.ui.profile.TeacherProfileFragment;
import com.bzu.educore.util.SharedPreferencesManager;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;

@Getter
public class TeacherMainActivity extends AppCompatActivity {

    // Getter method to access teacher ID from fragments
    private int teacherId; // Store teacher ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        loadTeacherId();

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

    private void loadTeacherId() {
        SharedPreferencesManager prefsManager = new SharedPreferencesManager(this);
        String email = prefsManager.getUserEmail();
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (JSONException e) {
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                UrlManager.GET_TEACHER_PROFILE,
                requestBody,
                response -> {
                    try {
                        JSONObject teacher = response.getJSONObject("teacher");
                        teacherId = teacher.getInt("id");

                    } catch (JSONException e) {
                        Toast.makeText(this, "Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Couldn't load teacher's data", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
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
