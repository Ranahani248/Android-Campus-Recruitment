package com.example.androidcampusrecruitmentsystem;


import static com.example.androidcampusrecruitmentsystem.MainActivity.student;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class Homefragment extends Fragment {


    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private FirebaseFirestore firestore;



    public Homefragment() {
        // Required empty public constructor
    }


    public static Homefragment newInstance() {
        Homefragment fragment = new Homefragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view= inflater.inflate(R.layout.fragment_homefragment, container, false);
        CircleImageView homeImg = view.findViewById(R.id.imageView_home);
        TextView userName = view.findViewById(R.id.userName);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        if(student.getName() == null){

          userRef = firestore.collection("Students").document(userId);

        if(currentUser != null) {
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    student.setName(name);
                    userName.setText(student.getName());
                }});}}
        else{
            userName.setText(student.getName());
       }

        homeImg.setOnClickListener(v -> {
          Intent intent = new Intent(getContext(), Profile_Management.class);
          startActivity(intent);
      });
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        List<JobItem> jobList = new ArrayList<>();
        jobList.add(new JobItem("Job 1"));
        jobList.add(new JobItem("Job 2"));
        jobList.add(new JobItem("Job 3"));
        jobList.add(new JobItem("Job 4"));
        jobList.add(new JobItem("Job 5"));
        jobList.add(new JobItem("Job 6"));
        jobList.add(new JobItem("Job 7"));
        jobList.add(new JobItem("Job 8"));
        jobList.add(new JobItem("Job 9"));
        jobList.add(new JobItem("Job 10"));

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.jobRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        JobAdapter jobAdapter = new JobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);
    }
}