package com.example.androidcampusrecruitmentsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class Recruiter_Settings_fragment extends Fragment {

    public Recruiter_Settings_fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_recruiter__settings_fragment, container, false);
        TextView profileButton = view.findViewById(R.id.profile_settings_recruiter);
        TextView logOutButton = view.findViewById(R.id.Log_Out_recruiter);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Profile_Management_recruiter.class);
            startActivity(intent);
        });
        logOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), loginPage.class);
            startActivity(intent);

        });
        return view;
    }
}