package com.example.androidcampusrecruitmentsystem;

import static com.example.androidcampusrecruitmentsystem.MainActivity.student;
import static com.example.androidcampusrecruitmentsystem.MainActivityRecruiter.recruiter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Home_recruiter extends Fragment {


    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private FirebaseFirestore firestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home_recruiter, container, false);
        CircleImageView homeImg_recruiter = view.findViewById(R.id.imageView_home_recruiter);
        if(recruiter.getProfilePictureUri() == null){
            loadProfilePicture(homeImg_recruiter);}
        else{
            Glide.with(this)
                    .load(recruiter.getProfilePictureUri())
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(homeImg_recruiter);
        }

        Button postjob_recruiter = view.findViewById(R.id.Post_button);
        TextView userName_recruiter = view.findViewById(R.id.userName_recruiter);
        String userId = currentUser.getUid();
        if(recruiter.getName() == null){

            userRef = firestore.collection("Recruiters").document(userId);

            if(currentUser != null) {
                userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        recruiter.setName(name);
                        userName_recruiter.setText(recruiter.getName());
                    }});}}
        else{
            userName_recruiter.setText(recruiter.getName());
        }


        homeImg_recruiter.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Profile_Management_recruiter.class);
            startActivity(intent);
        });
        postjob_recruiter.setOnClickListener(v ->{

            Intent intent = new Intent(getContext(), Job_post.class);
            startActivity(intent);
        });
    return view;
    }
    private void loadProfilePicture(ImageView homeImg) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && firestore != null && isAdded()) { // Check if currentUser, firestore, and fragment are not null and fragment is added
            String uid = currentUser.getUid();

            firestore.collection("Recruiters").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Check if the 'profilePicture' field exists
                            if (documentSnapshot.contains("profilePicture")) {
                                // Download the profile picture into the ImageView using Glide or Picasso
                                Glide.with(requireContext()) // Use requireContext() for safety
                                        .load(documentSnapshot.getString("profilePicture"))
                                        .placeholder(R.drawable.user)
                                        .error(R.drawable.user)
                                        .into(homeImg);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error loading profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        List<JobItem> joblist_recruiter = new ArrayList<>();
        joblist_recruiter.add(new JobItem("Job 1"));
        joblist_recruiter.add(new JobItem("Job 2"));
        joblist_recruiter.add(new JobItem("Job 3"));
        joblist_recruiter.add(new JobItem("Job 4"));
        joblist_recruiter.add(new JobItem("Job 5"));
        joblist_recruiter.add(new JobItem("Job 6"));
        joblist_recruiter.add(new JobItem("Job 7"));
        joblist_recruiter.add(new JobItem("Job 8"));
        joblist_recruiter.add(new JobItem("Job 9"));
        joblist_recruiter.add(new JobItem("Job 10"));

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.jobRecycler_recruiter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        JobAdapter_recruiter jobAdapter_recruiter = new JobAdapter_recruiter(joblist_recruiter);
        recyclerView.setAdapter(jobAdapter_recruiter);
    }
}