package com.example.bdoner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etPhone, etPincode, etAddress, etPassword;
    Spinner spBloodGroup, spState, spDistrict;
    Button btnRegister;
    TextView tvLogin;

    DatabaseReference db;

    String[] bloodGroups = {"Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
    String[] states = {"Select State", "Gujarat", "Maharashtra"};

    String[][] districts = {
            {},
            {"Ahmedabad", "Surat", "Rajkot", "Bhavnagar"},
            {"Mumbai", "Pune", "Nagpur"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseDatabase.getInstance().getReference("users");

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etPincode = findViewById(R.id.etPincode);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);

        spBloodGroup = findViewById(R.id.spBloodGroup);
        spState = findViewById(R.id.spState);
        spDistrict = findViewById(R.id.spDistrict);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // 🩸 Blood group spinner
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                bloodGroups
        );
        bloodAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spBloodGroup.setAdapter(bloodAdapter);

        // 🌍 State spinner
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                states
        );
        stateAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spState.setAdapter(stateAdapter);

        // 🔁 District spinner (FIXED)
        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {

                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(
                        RegisterActivity.this,
                        R.layout.spinner_item,   // ✅ FIXED
                        districts[position]
                );

                districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // ✅ FIXED

                spDistrict.setAdapter(districtAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnRegister.setOnClickListener(v -> registerUser());

        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            });
        }
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        String state = spState.getSelectedItem().toString();
        String district = spDistrict.getSelectedItem().toString();
        String pincode = etPincode.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String blood = spBloodGroup.getSelectedItem().toString();

        // 🔒 Validations (same as your code)
        if (name.isEmpty()) {
            etName.setError("Enter name");
            return;
        }

        if (phone.length() != 10) {
            etPhone.setError("Enter valid 10-digit phone");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Min 6 characters");
            return;
        }

        if (blood.equals("Select Blood Group")) {
            Toast.makeText(this, "Select blood group", Toast.LENGTH_SHORT).show();
            return;
        }

        if (state.equals("Select State")) {
            Toast.makeText(this, "Select state", Toast.LENGTH_SHORT).show();
            return;
        }

        if (district == null || district.isEmpty()) {
            Toast.makeText(this, "Select district", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pincode.length() != 6) {
            etPincode.setError("Enter valid pincode");
            return;
        }

        if (address.isEmpty()) {
            etAddress.setError("Enter address");
            return;
        }

        String userId = phone;

        btnRegister.setEnabled(false);

        // 🔍 CHECK IF USER ALREADY EXISTS
        db.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    // ❌ User already registered
                    btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this,
                            "User already registered! Please login.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // ✅ New user → Register
                    User user = new User(name, phone, blood, state, district, pincode, address, password);

                    db.child(userId).setValue(user)
                            .addOnSuccessListener(a -> {
                                Toast.makeText(RegisterActivity.this,
                                        "Registration Successful!",
                                        Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra("prefillPhone", phone);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                btnRegister.setEnabled(true);
                                Toast.makeText(RegisterActivity.this,
                                        "Registration Failed",
                                        Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this,
                        "Database error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}