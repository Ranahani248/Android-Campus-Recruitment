package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Job_post extends AppCompatActivity {
    private DocumentReference userRef;
    FirebaseUser currentUser;
    ConstraintLayout constraintLayout;
    EditText jobTitle1, companyName1, location1, description1, salary1;
    ProgressBar progressBar;
    Button postButton;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post);

        postButton = findViewById(R.id.post_button);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        firestore = FirebaseFirestore.getInstance();
        constraintLayout = findViewById(R.id.constraint_postJob);
        progressBar = findViewById(R.id.progressBar_postJob);
        jobTitle1 = findViewById(R.id.job_title);
        companyName1 = findViewById(R.id.companyName);
        location1 = findViewById(R.id.location);
        description1 = findViewById(R.id.description);
        salary1 = findViewById(R.id.salary);

        userRef = firestore.collection("Recruiters").document(userId);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProgressBar();
                // Get job details from EditText fields
                String jobTitle = jobTitle1.getText().toString();
                String companyName = companyName1.getText().toString();
                String location = location1.getText().toString();
                String description = description1.getText().toString();
                String salary = salary1.getText().toString();

                // Check if any of the fields is null or empty
                if (jobTitle.isEmpty()) {
                    ((EditText) findViewById(R.id.job_title)).setError("Job title is required");
                    resetProgressBar();

                }

                else if (companyName.isEmpty()) {
                    ((EditText) findViewById(R.id.companyName)).setError("Company name is required");
                    resetProgressBar();

                }

               else if (location.isEmpty()) {
                    ((EditText) findViewById(R.id.location)).setError("Location is required");
                    resetProgressBar();

                }
               else if (salary.isEmpty()) {
                    ((EditText) findViewById(R.id.salary)).setError("Salary is required");
                    resetProgressBar();

                }

               else if (description.isEmpty()) {
                    ((EditText) findViewById(R.id.description)).setError("Description is required");

                    resetProgressBar();
                } else {
                    // Assuming you have some kind of user authentication in place, get the recruiter ID
                    String recruiterId = currentUser.getUid(); // Use the current user's UID as the recruiter ID

                    Map<String, Object> job_details = new HashMap<>();
                    job_details.put("jobTitle", jobTitle);
                    job_details.put("companyName", companyName);
                    job_details.put("location", location);
                    job_details.put("Salary", salary);
                    job_details.put("description", description);
                    job_details.put("recruiterId", recruiterId);


                    firestore.collection("Jobs").add(job_details)
                            .addOnSuccessListener(documentReference -> {
                                String jobId = documentReference.getId();
                                documentReference.update("jobId", jobId)
                                        .addOnSuccessListener(aVoid -> {

                                            Log.d("Job_post", "Job ID updated successfully");
                                        })
                                        .addOnFailureListener(e -> {
                                        });
                                Toast.makeText(Job_post.this, "Job posted successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Job_post.this, MainActivityRecruiter.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                resetProgressBar();
                                Toast.makeText(Job_post.this, "Error, Please Retry Later", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

    }
    public void setProgressBar(){
        constraintLayout.setAlpha(0.5f);
        progressBar.setVisibility(View.VISIBLE);
        jobTitle1.setEnabled(false);
        companyName1.setEnabled(false);
        location1.setEnabled(false);
        description1.setEnabled(false);
        postButton.setEnabled(false);

    }
    public void resetProgressBar(){
        constraintLayout.setAlpha(1f);
        progressBar.setVisibility(View.GONE);
        jobTitle1.setEnabled(true);
        companyName1.setEnabled(true);
        location1.setEnabled(true);
        description1.setEnabled(true);
        postButton.setEnabled(true);
    }
}
