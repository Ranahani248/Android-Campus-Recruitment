package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsFragment extends Fragment implements ApplicationsAdapter_recruiter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ApplicationsAdapter_recruiter applicationsAdapter;
    private List<Application> applicationList;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    TextView noapp;
    ProgressBar progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applications, container, false);
        noapp = view.findViewById(R.id.noApplications);
        noapp.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.Applications); // Replace with your RecyclerView ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        applicationList = new ArrayList<>();
        applicationsAdapter = new ApplicationsAdapter_recruiter(applicationList,this);
        recyclerView.setAdapter(applicationsAdapter);
        progressBar = view.findViewById(R.id.progressBar_application);
        progressBar.setVisibility(View.VISIBLE);
        fetchApplicationsData();

        return view;
    }

    private void fetchApplicationsData() {
        if (currentUser != null) {
            String recruiterId = currentUser.getUid();

            firestore.collection("Applications")
                    .whereEqualTo("recruiterId", recruiterId) // Modify this query based on your data structure
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            applicationList.clear(); // Clear existing data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String jobId = document.getString("jobId");
                                String jobTitle = document.getString("Title");
                                String studentId = document.getString("studentId");

                                fetchStudentName(studentId, jobTitle, jobId);
                            }


                            applicationsAdapter.notifyDataSetChanged(); // Notify adapter about data changes
                            progressBar.setVisibility(View.GONE);
                            if(applicationList.isEmpty()){
                                noapp.setVisibility(View.VISIBLE);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            noapp.setVisibility(View.VISIBLE);


                        }
                    });
        }

    }
    private void fetchStudentName(String studentId, String jobTitle, String jobid) {
        firestore.collection("Students").document(studentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("name");
                        String profilePictureUrl = documentSnapshot.getString("profilePicture");
                        noapp.setVisibility(View.GONE);
                        applicationList.add(new Application(jobTitle, studentName, profilePictureUrl,studentId,jobid));
                        applicationsAdapter.notifyDataSetChanged();
                    }
                    if(applicationList.isEmpty()){
                        noapp.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                });
    }

    @Override
    public void onItemClicked(Application application) {
        ApplicationDetails.studentId = application.getStudentId();
        Intent intent = new Intent(getContext(), ApplicationDetails.class);
        intent.putExtra("jobId", application.getJobid());
        startActivity(intent);
    }
}
