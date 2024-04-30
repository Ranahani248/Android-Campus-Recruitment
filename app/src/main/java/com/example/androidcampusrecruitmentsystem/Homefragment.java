package com.example.androidcampusrecruitmentsystem;


import static com.example.androidcampusrecruitmentsystem.MainActivity.student;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class Homefragment extends Fragment implements JobAdapter.OnItemClickListener,JobSearchAdapter.OnItemClickListener {


    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private FirebaseFirestore firestore;

    EditText search;
    ImageView searchBtn;
    ProgressBar homeProgress;


    public Homefragment() {
        // Required empty public constructor
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
        search = view.findViewById(R.id.search_bar);
        searchBtn = view.findViewById(R.id.search_btn);
        homeProgress = view.findViewById(R.id.home_progress);
        homeProgress.setVisibility(View.VISIBLE);

        searchBtn.setOnClickListener(v -> {

            if(search.getText().toString().length() > 0){
                searchJobdetails(search.getText().toString());
            }
           else if(search.getText().toString().isEmpty()){
               search.setError("Search field cannot be empty");
                fetchJobList();
            }
        });
        search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Perform search when the "Enter" key is pressed
                if (search.getText().toString().length() > 0) {
                    searchJobdetails(search.getText().toString());
                } else {
                    fetchJobList();
                }
                return true;
            }
            return false;
        });



        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        RecyclerView recyclerView = view.findViewById(R.id.jobRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        if(student.getProfilePictureUri() == null){
            loadProfilePicture(homeImg);}
        else{
            Glide.with(this)
                    .load(student.getProfilePictureUri())
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(homeImg);
        }
        homeImg.setOnClickListener(v -> {
          Intent intent = new Intent(getContext(), Profile_Management.class);
          startActivity(intent);
      });
        fetchJobList();
        return view;
    }
    private void loadProfilePicture(ImageView homeImg) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && firestore != null && isAdded()) { // Check if currentUser, firestore, and fragment are not null and fragment is added

            String uid = currentUser.getUid();

            firestore.collection("Students").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(isAdded()) {
                            if (documentSnapshot.exists()) {
                                // Check if the 'profilePicture' field exists
                                if (documentSnapshot.contains("profilePicture")) {
                                    // Download the profile picture into the ImageView using Glide or Picasso
                                    Glide.with(this)
                                            .load(documentSnapshot.getString("profilePicture"))
                                            .placeholder(R.drawable.user)
                                            .error(R.drawable.user)
                                            .into(homeImg);
                                }

                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error loading profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    void fetchJobList() {
        if (!isAdded()) {
            return;
        }

        firestore.collection("Jobs").limit(15).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) {
                        // Fragment is not attached, do nothing
                        return;
                    }

                    List<JobItem> jobList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String jobId = documentSnapshot.getId();
                        String jobTitle = documentSnapshot.getString("jobTitle");
                        String companyName = documentSnapshot.getString("companyName");
                        String location = documentSnapshot.getString("location");
                        String salary = documentSnapshot.getString("Salary");
                        String description = documentSnapshot.getString("description");
                        String recruiterId = documentSnapshot.getString("recruiterId");

                        jobList.add(new JobItem(jobId, jobTitle, description, companyName,salary, location, recruiterId));
                    }

                    // Set up RecyclerView
                    RecyclerView recyclerView = requireView().findViewById(R.id.jobRecycler);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    JobAdapter jobAdapter = new JobAdapter(jobList, this);
                    recyclerView.setAdapter(jobAdapter);
                    homeProgress.setVisibility(View.GONE);

                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Check your Internet Connection\nand try Again", Toast.LENGTH_SHORT).show();
                        Log.e("Error fetching job list", String.valueOf(e));
                        homeProgress.setVisibility(View.GONE);

                    }
                });
    }
    private void searchJobdetails(String searchText) {
        firestore.collection("Jobs").limit(10).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<JobItem> jobList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.getString("jobTitle") != null && Objects.requireNonNull(documentSnapshot.getString("jobTitle")).toLowerCase().contains(searchText.toLowerCase())) {
                            String jobId = documentSnapshot.getId(); // Retrieve the job ID
                            String jobTitle = documentSnapshot.getString("jobTitle");
                            String companyName = documentSnapshot.getString("companyName");
                            String location = documentSnapshot.getString("location");
                            String salary = documentSnapshot.getString("Salary");
                            String description = documentSnapshot.getString("description");
                            String recruiterId = documentSnapshot.getString("recruiterId");

                            jobList.add(new JobItem(jobId, jobTitle, description, companyName,salary, location,recruiterId));
                        }
                    }
                    if(jobList.isEmpty()){
                        Toast.makeText(getContext(), "No Jobs Found", Toast.LENGTH_SHORT).show();
                    }
                    SearchBar.searched = searchText;
                    search.setText("");
                    SearchBar.jobList = jobList;
                    Intent intent = new Intent(getContext(), SearchBar.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.e("Error fetching job list", String.valueOf(e));
                });
    }
    @Override
        public void onItemClick(JobItem jobItem) {
            Job_Details_student.jobid = jobItem.getJobid();
            Job_Details_student.recruiterid = jobItem.getRecruiterid();
            Job_Details_student.studentid = currentUser.getUid();
            Intent intent = new Intent(getContext(), Job_Details_student.class);
            Job_Details_student.backRecent = false;
            startActivity(intent);
    }
}