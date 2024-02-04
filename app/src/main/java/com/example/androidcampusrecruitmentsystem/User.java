package com.example.androidcampusrecruitmentsystem;

import android.net.Uri;

public class User {
    private String name;
    private String email;

    public Uri getProfilePictureUri() {
        return profilePictureUri;
    }

    public void setProfilePictureUri(Uri profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    private String dob;
    private String phoneNumber;
    private Uri profilePictureUri;

    public User() {}

    public User(String name, String email, String dob, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.phoneNumber = phoneNumber;
    }

    public  void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
