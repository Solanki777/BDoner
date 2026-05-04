package com.example.bdoner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputEditText etPhone, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;

    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseDatabase.getInstance().getReference("users");

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Prefill phone (optional)
        String prefillPhone = getIntent().getStringExtra("prefillPhone");
        if (prefillPhone != null) {
            etPhone.setText(prefillPhone);
        }

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {

        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 🔍 Validation
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Enter phone number");
            return;
        }

        if (phone.length() != 10) {
            etPhone.setError("Enter valid 10-digit number");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            return;
        }

        btnLogin.setEnabled(false);

        db.child(phone).get().addOnSuccessListener(snapshot -> {

            btnLogin.setEnabled(true);

            if (!snapshot.exists()) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = snapshot.getValue(User.class);

            if (user == null || user.password == null) {
                Toast.makeText(this, "User data error", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user.password.equals(password)) {

                // ✅ SAVE USER SESSION
                SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
                prefs.edit().putString("user_id", phone).apply();

                // ✅ Go to Home
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("userId", phone);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(e -> {
            btnLogin.setEnabled(true);
            Log.e(TAG, "Firebase Error", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}