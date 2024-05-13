package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Job_Details_recruiter extends AppCompatActivity {

    private FirebaseFirestore firestore;
    ProgressBar progressBar;

    ConstraintLayout constraintLayout;
    TextView jobtitle_recruiter, jobCompany_recruiter, joblocation_recruiter, jobsalary_recruiter, jobdescription_recruiter;
    static String jobid, recruiterid;
    Button deletebtn,job_details_recruiter_shortlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details_recruiter);

        jobtitle_recruiter = findViewById(R.id.job_details_recruiter_title);
        jobCompany_recruiter = findViewById(R.id.job_details_recruiter_company);
        joblocation_recruiter = findViewById(R.id.job_details_recruiter_location);
        jobdescription_recruiter = findViewById(R.id.job_details_recruiter_description);
        jobsalary_recruiter = findViewById(R.id.job_details_recruiter_salary);
        deletebtn = findViewById(R.id.job_details_recruiter_delete);
        progressBar = findViewById(R.id.recruiterDetailsProgressBar);
        constraintLayout = findViewById(R.id.constraintDetails);
        job_details_recruiter_shortlist = findViewById(R.id.job_details_recruiter_shortlist);
        firestore = FirebaseFirestore.getInstance();

        job_details_recruiter_shortlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Job_Details_recruiter.this, ShortListed.class);
                intent.putExtra("jobId", jobid);
                startActivity(intent);
            }
        });

        for (JobItem jobItem : Home_recruiter.joblist_recruiter) {
            if (jobItem.getJobid().equals(Job_Details_recruiter.jobid)) {
                jobtitle_recruiter.setText(jobItem.getJobTitle());
                jobdescription_recruiter.setText(jobItem.getJobDescription());
                jobCompany_recruiter.setText(jobItem.getJobCompany());
                joblocation_recruiter.setText(jobItem.getJobLocation());
                jobsalary_recruiter.setText(jobItem.getJobSalary());
                break;
            }
        }

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                constraintLayout.setAlpha(0.5f);
                deletebtn.setEnabled(false);
                deleteJob();
            }
        });
    }

    private void deleteJob() {
        firestore.collection("Jobs")
                .document(Job_Details_recruiter.jobid)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    deleteApplications();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    constraintLayout.setAlpha(1f);
                    deletebtn.setEnabled(true);
                    Toast.makeText(this, "Error Deleting Job", Toast.LENGTH_SHORT).show();
                });
    }
    private void deleteApplications() {
        // Delete all items from the "Applications" collection where job ID matches
        firestore.collection("Applications")
                .whereEqualTo("jobId", Job_Details_recruiter.jobid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    deleteSavedJobs();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    constraintLayout.setAlpha(1f);
                    deletebtn.setEnabled(true);

                    Toast.makeText(this, "Error Deleting Applications", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteSavedJobs() {
        // Delete all items from the "SavedJobs" collection where job ID matches
        firestore.collection("SavedJobs")
                .whereEqualTo("jobId", Job_Details_recruiter.jobid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                    progressBar.setVisibility(View.GONE);
                    constraintLayout.setAlpha(1f);
                    deletebtn.setEnabled(true);

                    Intent intent = new Intent(Job_Details_recruiter.this, MainActivityRecruiter.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    constraintLayout.setAlpha(1f);
                    deletebtn.setEnabled(true);
                    Toast.makeText(this, "Error Deleting Saved list", Toast.LENGTH_SHORT).show();
                });
    }
}
