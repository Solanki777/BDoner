package com.example.bdoner;

public class User {

    // 🔹 Basic Info
    public String name;
    public String phone;
    public String bloodGroup;
    public String password;

    // 🔹 Address Info
    public String state;
    public String district; // used as city
    public String pincode;
    public String address;

    // 🔹 GPS Location
    public double latitude;
    public double longitude;

    // 🔹 Medical Info
    public int age;
    public double weight;
    public String lastDonationDate;

    // 🔹 Conditions
    public boolean hasDisease;
    public boolean recentSurgery;
    public boolean recentTattoo;
    public boolean hasFever;
    public boolean alcoholRecent;
    public boolean onMedication;
    public boolean pregnant;

    // 🔹 Status
    public boolean isEligible;
    public boolean isAvailable;

    // 🔹 Required for Firebase
    public User() {}

    // 🔹 Constructor
    public User(String name, String phone, String bloodGroup,
                String state, String district, String pincode,
                String address, String password) {

        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;

        this.state = state;
        this.district = district;
        this.pincode = pincode;
        this.address = address;

        this.password = password;

        this.isEligible = false;
        this.isAvailable = false;

        this.latitude = 0;
        this.longitude = 0;
    }
}