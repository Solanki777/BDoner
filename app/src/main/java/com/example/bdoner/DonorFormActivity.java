package com.example.bdoner;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DonorFormActivity extends AppCompatActivity {

    EditText etAge, etWeight, etLastDonation;
    CheckBox cbDisease, cbSurgery, cbTattoo;
    CheckBox cbFever, cbAlcohol, cbMedication, cbPregnant;
    Button btnSubmit;

    DatabaseReference db;
    String userId;

    FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_form);

        userId = getIntent().getStringExtra("userId");

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db = FirebaseDatabase.getInstance().getReference("users");
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        // Bind UI
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etLastDonation = findViewById(R.id.etLastDonation);

        cbDisease = findViewById(R.id.cbDisease);
        cbSurgery = findViewById(R.id.cbSurgery);
        cbTattoo = findViewById(R.id.cbTattoo);

        cbFever = findViewById(R.id.cbFever);
        cbAlcohol = findViewById(R.id.cbAlcohol);
        cbMedication = findViewById(R.id.cbMedication);
        cbPregnant = findViewById(R.id.cbPregnant);

        btnSubmit = findViewById(R.id.btnSubmit);

        etLastDonation.setOnClickListener(v -> showDatePicker());

        loadPreviousData();

        btnSubmit.setOnClickListener(v -> saveDonorDetails());
    }

    // 📅 Date Picker
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    String date = year + "-" +
                            String.format(Locale.getDefault(), "%02d", (month + 1)) + "-" +
                            String.format(Locale.getDefault(), "%02d", day);

                    etLastDonation.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    // 🔄 Load existing data safely
    private void loadPreviousData() {
        db.child(userId).get().addOnSuccessListener(snapshot -> {

            if (!snapshot.exists()) return;

            Object ageObj = snapshot.child("age").getValue();
            if (ageObj != null) etAge.setText(ageObj.toString());

            Object weightObj = snapshot.child("weight").getValue();
            if (weightObj != null) etWeight.setText(weightObj.toString());

            Object dateObj = snapshot.child("lastDonationDate").getValue();
            if (dateObj != null) etLastDonation.setText(dateObj.toString());
        });
    }

    // 💾 Save donor details
    private void saveDonorDetails() {

        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String lastDate = etLastDonation.getText().toString().trim();

        if (TextUtils.isEmpty(ageStr)) {
            etAge.setError("Enter age");
            return;
        }

        if (TextUtils.isEmpty(weightStr)) {
            etWeight.setError("Enter weight");
            return;
        }

        int age;
        double weight;

        try {
            age = Integer.parseInt(ageStr);
            weight = Double.parseDouble(weightStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        if (age < 18 || age > 65) {
            etAge.setError("Age must be 18–65");
            return;
        }

        if (weight < 50) {
            etWeight.setError("Minimum 50kg required");
            return;
        }

        // Date validation
        if (!lastDate.isEmpty()) {

            if (!isValidDate(lastDate)) {
                etLastDonation.setError("Invalid date");
                return;
            }

            if (!isDonationGapValid(lastDate)) {
                etLastDonation.setError("Minimum 90 days required");
                return;
            }
        }

        // Medical conditions
        boolean hasDisease = cbDisease.isChecked();
        boolean recentSurgery = cbSurgery.isChecked();
        boolean recentTattoo = cbTattoo.isChecked();
        boolean hasFever = cbFever.isChecked();
        boolean alcoholRecent = cbAlcohol.isChecked();
        boolean onMedication = cbMedication.isChecked();
        boolean pregnant = cbPregnant.isChecked();

        // Cleaner eligibility logic
        boolean isEligible = !(hasDisease || recentSurgery || recentTattoo ||
                hasFever || alcoholRecent || onMedication || pregnant);

        // Save data
        db.child(userId).child("age").setValue(age);
        db.child(userId).child("weight").setValue(weight);
        db.child(userId).child("lastDonationDate").setValue(lastDate);

        db.child(userId).child("hasDisease").setValue(hasDisease);
        db.child(userId).child("recentSurgery").setValue(recentSurgery);
        db.child(userId).child("recentTattoo").setValue(recentTattoo);

        db.child(userId).child("hasFever").setValue(hasFever);
        db.child(userId).child("alcoholRecent").setValue(alcoholRecent);
        db.child(userId).child("onMedication").setValue(onMedication);
        db.child(userId).child("pregnant").setValue(pregnant);

        db.child(userId).child("isEligible").setValue(isEligible);
        db.child(userId).child("isAvailable").setValue(isEligible);

        saveLocation();

        Toast.makeText(this,
                isEligible ? "Eligible to donate!" : "Not eligible currently",
                Toast.LENGTH_SHORT).show();

        finish();
    }

    // 📍 Save GPS location safely
    private void saveLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                db.child(userId).child("latitude").setValue(location.getLatitude());
                db.child(userId).child("longitude").setValue(location.getLongitude());
            }
        });
    }

    private boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDonationGapValid(String lastDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date last = sdf.parse(lastDate);

            if (last == null) return true;

            Date today = new Date();
            long diff = today.getTime() - last.getTime();
            long days = diff / (1000 * 60 * 60 * 24);

            return days >= 90;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            saveLocation();
        }
    }
}