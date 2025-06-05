package com.bzu.educore.activity.teacher;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.bzu.educore.R;
import com.bzu.educore.activity.teacher.fragment.TeacherDashboardFragment;

public class TeacherMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        // Load dashboard WITHOUT adding it to back stack
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, new TeacherDashboardFragment());
            // do NOT call ft.addToBackStack(...) here
            ft.commit();
        }
    }

    /**
     * “addToStack” flag controls whether we push this new fragment
     * onto the back stack. Dashboard itself was loaded with addToStack=false.
     */
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

        // If there is more than zero entries, pop one. Otherwise, finish.
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            // No fragments left to pop (we’re at dashboard), so finish the activity:
            super.onBackPressed();
        }
    }
}
