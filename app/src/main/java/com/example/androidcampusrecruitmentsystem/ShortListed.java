package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShortListed extends AppCompatActivity implements ApplicationsAdapter_recruiter.OnItemClickListener{
    FirebaseFirestore firestore;
    String jobId;

ApplicationsAdapter_recruiter applicationsAdapter;
FirebaseUser currentUser;
List<Application> applicationList = new ArrayList<>();
    TextView title;
    RecyclerView shortListed_recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_listed);
        jobId = getIntent().getStringExtra("jobId");
         title = findViewById(R.id.shortListed_textView);
        shortListed_recyclerView = findViewById(R.id.shortListed_recyclerView);
        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        applicationsAdapter = new ApplicationsAdapter_recruiter(applicationList,this);
        shortListed_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        shortListed_recyclerView.setAdapter(applicationsAdapter);

        Jobname();
        fetchApplicationsData();
    }
    private void Jobname() {
        firestore.collection("Jobs").document(jobId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        title.setText(documentSnapshot.getString("jobTitle"));
                    }
                });

    }
    private void fetchApplicationsData() {
        if (currentUser != null) {
            String recruiterId = currentUser.getUid();

            firestore.collection("Applications")
                    .whereEqualTo("recruiterId", recruiterId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String applicationId = document.getId();
                                String jobId = document.getString("jobId");
                                String jobTitle = document.getString("Title");
                                String studentId = document.getString("studentId");
                                String isShortListed = document.getString("isShortListed");
                                if(isShortListed.equals("true")) {
                                    Log.d("TAG", "fetchApplicationsData: ");
                                    fetchStudentName(studentId, jobTitle, jobId, applicationId);
                                }
                            }


                            applicationsAdapter.notifyDataSetChanged(); // Notify adapter about data changes
                        }
                    });
        }

    }
    private void fetchStudentName(String studentId, String jobTitle, String jobid, String applicationid) {
        firestore.collection("Students").document(studentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("name");
                        String profilePictureUrl = documentSnapshot.getString("profilePicture");
                        applicationList.add(new Application(jobTitle, studentName, profilePictureUrl,studentId,jobid,applicationid));
                        applicationsAdapter.notifyDataSetChanged();
                    }

                })
                .addOnFailureListener(e -> {
                });
    }

    @Override
    public void onItemClicked(Application application) {
        ApplicationDetails.studentId = application.getStudentId();
        Intent intent = new Intent(ShortListed.this, ApplicationDetails.class);
        intent.putExtra("jobId", application.getJobid());
        intent.putExtra("applicationId", application.getApplicationId());
        startActivity(intent);
    }
}