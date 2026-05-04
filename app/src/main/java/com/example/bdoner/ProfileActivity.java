package com.example.bdoner;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends BaseActivity {

    EditText etName, etLocation, etPassword;
    TextView tvPhone;
    Button btnUpdate;

    DatabaseReference db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 Load layout inside drawer
        setupDrawer(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etLocation = findViewById(R.id.etLocation);
        etPassword = findViewById(R.id.etPassword);
        tvPhone = findViewById(R.id.tvPhone);
        btnUpdate = findViewById(R.id.btnUpdate);

        userId = getIntent().getStringExtra("userId");

        db = FirebaseDatabase.getInstance().getReference("users");

        loadUserData();

        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    // 🔹 Load existing data
    private void loadUserData() {
        db.child(userId).get().addOnSuccessListener(snapshot -> {

            if (!snapshot.exists()) return;

            String name = snapshot.child("name").getValue(String.class);
            String location = snapshot.child("location").getValue(String.class);
            String phone = snapshot.child("phone").getValue(String.class);

            if (name != null) etName.setText(name);
            if (location != null) etLocation.setText(location);
            if (phone != null) tvPhone.setText(phone); // 🔒 read-only
        });
    }

    // 🔐 Update with password check
    private void updateProfile() {

        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(location) ||
                TextUtils.isEmpty(password)) {

            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        db.child(userId).get().addOnSuccessListener(snapshot -> {

            if (!snapshot.exists()) return;

            String realPass = snapshot.child("password").getValue(String.class);

            if (realPass != null && realPass.equals(password)) {

                db.child(userId).child("name").setValue(name);
                db.child(userId).child("location").setValue(location);

                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}