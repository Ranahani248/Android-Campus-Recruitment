package com.example.androidcampusrecruitmentsystem;

public class User {
    private String name;
    private String email;
    private String dob;
    private String phoneNumber;

    public User() {}

    public User(String name, String email, String dob, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.dob = dob;
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
