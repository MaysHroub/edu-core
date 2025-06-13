package com.bzu.educore.activity.registrar;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bzu.educore.R;
import com.bzu.educore.repository.registrar.MainRepository;
import com.bzu.educore.util.SharedPreferencesManager;
import com.bzu.educore.util.UrlManager;
import com.bzu.educore.util.VolleySingleton;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.bzu.educore.databinding.ActivityRegistrarMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrarMainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainRepository.init(this);

        ActivityRegistrarMainBinding binding = ActivityRegistrarMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarRegistrarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_teachers, R.id.nav_students, R.id.nav_subjects, R.id.nav_classrooms, R.id.nav_schedules, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_registrar_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        loadRegistrarName();
    }

    private void loadRegistrarName() {
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
                UrlManager.URL_GET_REGISTRAR_DATA,
                requestBody,
                response -> {
                    TextView txtRegName = findViewById(R.id.txt_reg_name),
                            txtRegEmail = findViewById(R.id.txt_reg_email);

                    txtRegEmail.setText(email);

                    try {
                        JSONObject registrar = response.getJSONObject("registrar");

                        // Display registrar data
                        String name = registrar.getString("name");
                        txtRegName.setText(name);

                    } catch (JSONException e) {
                        Toast.makeText(this, "Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Couldn't load registrar's data", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_registrar_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}