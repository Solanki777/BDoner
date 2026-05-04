package com.example.bdoner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.*;
import com.google.firebase.database.*;

import java.util.*;

public class SearchActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private DonorAdapter adapter;
    private List<DonorWrapper> donorList = new ArrayList<>();

    private DatabaseReference db;
    private FusedLocationProviderClient locationClient;

    private double myLat = 0, myLng = 0;
    private String currentUserId;

    private String selectedBlood = null;
    private final List<TextView> bloodButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer(R.layout.activity_search);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DonorAdapter(this, donorList);
        recyclerView.setAdapter(adapter);

        db = FirebaseDatabase.getInstance().getReference("users");
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        currentUserId = getIntent().getStringExtra("userId");

        initBloodButtons();
        getLocation();
        loadDonors();

        findViewById(R.id.btnSearch).setOnClickListener(v -> search());
    }

    // 🔴 Blood selection
    private void initBloodButtons() {
        int[] ids = {
                R.id.bgAPlus, R.id.bgAMinus,
                R.id.bgBPlus, R.id.bgBMinus,
                R.id.bgOPlus, R.id.bgOMinus,
                R.id.bgABPlus, R.id.bgABMinus
        };

        String[] values = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

        for (int i = 0; i < ids.length; i++) {

            TextView btn = findViewById(ids[i]);
            if (btn == null) continue;

            String value = values[i];
            bloodButtons.add(btn);

            btn.setOnClickListener(v -> {
                selectedBlood = value;

                for (TextView b : bloodButtons) {
                    b.setBackgroundResource(R.drawable.bg_blood_unselected);
                    b.setTextColor(getColor(android.R.color.black));
                }

                btn.setBackgroundResource(R.drawable.bg_blood_selected);
                btn.setTextColor(getColor(android.R.color.white));
            });
        }
    }

    // 📍 Location
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                myLat = location.getLatitude();
                myLng = location.getLongitude();
            }
        });
    }

    private void loadDonors() {
        fetchDonors(null);
    }

    private void search() {
        if (selectedBlood == null) {
            Toast.makeText(this, "Select blood group", Toast.LENGTH_SHORT).show();
            return;
        }
        fetchDonors(selectedBlood);
    }

    // 🔥 MAIN LOGIC
    private void fetchDonors(String bloodFilter) {

        db.get().addOnSuccessListener(snapshot -> {

            donorList.clear();
            List<DonorWrapper> temp = new ArrayList<>();

            for (DataSnapshot data : snapshot.getChildren()) {

                if (data.getKey().equals(currentUserId)) continue;

                User u = data.getValue(User.class);
                if (u == null) continue;

                if (!u.isAvailable || !u.isEligible) continue;

                if (bloodFilter != null &&
                        (u.bloodGroup == null || !u.bloodGroup.equals(bloodFilter)))
                    continue;

                double distance = DistanceUtil.calculate(
                        myLat, myLng,
                        u.latitude, u.longitude
                );

                temp.add(new DonorWrapper(u, distance));
            }

            // 🔥 AI SORTING
            Collections.sort(temp, (a, b) -> {
                double scoreA = calculateScore(a);
                double scoreB = calculateScore(b);
                return Double.compare(scoreA, scoreB);
            });

            donorList.addAll(temp);
            adapter.notifyDataSetChanged();

            if (donorList.isEmpty()) {
                Toast.makeText(this, "No donors found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🧠 AI SCORE
    private double calculateScore(DonorWrapper d) {

        User u = d.user;
        double score = 0;

        if (d.distance == -1) score += 50;
        else score += d.distance;

        if (u.isAvailable) score -= 10;
        if (u.isEligible) score -= 10;

        if (u.lastDonationDate != null && !u.lastDonationDate.isEmpty()) {
            score += 5;
        }

        return score;
    }

    public static class DonorWrapper {
        public User user;
        public double distance;

        public DonorWrapper(User u, double d) {
            user = u;
            distance = d;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getLocation();
        }
    }
}