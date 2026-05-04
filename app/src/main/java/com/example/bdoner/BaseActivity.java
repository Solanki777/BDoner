package com.example.bdoner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected SwitchCompat switchAvailability;
    protected TextView tvUserName, tvUserPhone;
    protected TextView btnEditProfile;

    protected String currentUserId;
    protected DatabaseReference db;

    protected void setupDrawer(int layoutResID) {
        super.setContentView(R.layout.base_drawer);

        FrameLayout frame = findViewById(R.id.frameContainer);
        LayoutInflater.from(this).inflate(layoutResID, frame, true);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // ✅ Get userId
        currentUserId = getIntent().getStringExtra("userId");

        // ✅ Safety check
        if (currentUserId == null || currentUserId.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Firebase
        try {
            db = FirebaseDatabase.getInstance().getReference("users");
        } catch (Exception e) {
            Toast.makeText(this, "Firebase Configuration Error", Toast.LENGTH_SHORT).show();
        }

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.app_name, R.string.app_name);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        // Header setup
        if (navigationView != null && navigationView.getHeaderCount() > 0) {
            View header = navigationView.getHeaderView(0);

            tvUserName = header.findViewById(R.id.tvUserName);
            tvUserPhone = header.findViewById(R.id.tvUserPhone);
            switchAvailability = header.findViewById(R.id.switchAvailability);
            btnEditProfile = header.findViewById(R.id.btnEditProfile);

            loadUserInfo();
            setupSwitch();

            if (btnEditProfile != null) {
                btnEditProfile.setOnClickListener(v -> {
                    startActivity(new Intent(this, ProfileActivity.class)
                            .putExtra("userId", currentUserId));
                });
            }
        }

        // Navigation menu click handling
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                open(HomeActivity.class);

            } else if (id == R.id.nav_ai) {
                open(AIActivity.class);

            } else if (id == R.id.nav_new_chat) {

                // ✅ FIXED: clear chat for current user only
                ChatStorage.clearChat(this, currentUserId);

                if (this instanceof AIActivity) {
                    recreate();
                } else {
                    open(AIActivity.class);
                }

                Toast.makeText(this, "New chat started", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_search) {
                open(SearchActivity.class);

            } else if (id == R.id.nav_donor) {
                open(DonorFormActivity.class);

            } else if (id == R.id.nav_profile) {
                open(ProfileActivity.class);

            } else if (id == R.id.nav_logout) {
                logout();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void loadUserInfo() {
        if (db == null || currentUserId == null) return;

        db.child(currentUserId).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) return;

            String name = snapshot.child("name").getValue(String.class);
            String phone = snapshot.child("phone").getValue(String.class);

            if (name != null && tvUserName != null) tvUserName.setText(name);
            if (phone != null && tvUserPhone != null) tvUserPhone.setText(phone);
        });
    }

    private void setupSwitch() {
        if (db == null || currentUserId == null || switchAvailability == null) return;

        db.child(currentUserId).get().addOnSuccessListener(snapshot -> {
            Boolean isEligible = snapshot.child("isEligible").getValue(Boolean.class);
            Boolean isAvailable = snapshot.child("isAvailable").getValue(Boolean.class);

            if (isAvailable != null) {
                switchAvailability.setChecked(isAvailable);
            }

            switchAvailability.setEnabled(isEligible != null && isEligible);
        });

        switchAvailability.setOnCheckedChangeListener((buttonView, isChecked) -> {
            db.child(currentUserId).child("isAvailable").setValue(isChecked);

            Toast.makeText(this,
                    isChecked ? "You are now available" : "You are now offline",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void open(Class<?> cls) {
        if (this.getClass() == cls) {
            drawerLayout.closeDrawers();
            return;
        }

        Intent intent = new Intent(this, cls);
        intent.putExtra("userId", currentUserId);
        startActivity(intent);
    }

    private void logout() {

        // Optional: clear session if you stored it
        getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}