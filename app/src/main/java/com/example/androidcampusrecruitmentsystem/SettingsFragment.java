package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView profileButton = view.findViewById(R.id.profile_settings);
        TextView logOutButton = view.findViewById(R.id.Log_Out);


        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Profile_Management.class);
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