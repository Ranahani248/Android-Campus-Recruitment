package com.example.androidcampusrecruitmentsystem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Job_Details_student extends AppCompatActivity {

    static String jobid ,studentid,recruiterid;
    private FirebaseFirestore firestore;
    static boolean backRecent;

    static TextView jobtitle_studentdetails, jobCompany_studentdetails, joblocation_studentdetails,jobsalary_studentdetails, jobdescription_studentdetails;

Button savebtn, applybtn, pendingbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details_student);
        jobtitle_studentdetails = findViewById(R.id.job_details_student_title);
        jobCompany_studentdetails = findViewById(R.id.job_details_student_company);
        joblocation_studentdetails = findViewById(R.id.job_details_student_location);
        jobdescription_studentdetails = findViewById(R.id.job_details_student_description);
        jobsalary_studentdetails = findViewById(R.id.job_details_student_salary);
        savebtn = findViewById(R.id.job_details_student_save);
        applybtn = findViewById(R.id.job_details_student_apply);
        pendingbtn = findViewById(R.id.job_details_student_pending);
        firestore = FirebaseFirestore.getInstance();
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            Intent intent = new Intent(Job_Details_student.this, MainActivity.class);
            MainActivity.backRecent = backRecent;
            startActivity(intent);
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
        for(JobItem jobItem: JobAdapter.jobList){
            if(jobItem.getJobid().equals(jobid)){
                jobtitle_studentdetails.setText(jobItem.getJobTitle());
                jobdescription_studentdetails.setText(jobItem.getJobDescription());
                jobCompany_studentdetails.setText(jobItem.getJobCompany());
                joblocation_studentdetails.setText(jobItem.getJobLocation());
                jobsalary_studentdetails.setText(jobItem.getJobSalary());
                break;
            }
        }
        check("Applications");
        check("SavedJobs");


        applybtn.setOnClickListener(v -> {
            checkCVUploaded();
        });
        savebtn.setOnClickListener(v -> {
            if(savebtn.getText().toString().equals("Unsave")){
                delete("SavedJobs");
                savebtn.setText("Save job");
            }
            else {
                setData("SavedJobs");
                savebtn.setText("Unsave");
            }
        });

    }
    private void checkCVUploaded() {
        firestore.collection("Students")
                .document(studentid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String cvPath = documentSnapshot.getString("cvFile");

                        if (cvPath != null && !cvPath.isEmpty()) {
                            // Student has uploaded a CV, proceed with applying
                            setData("Applications");
                            applybtn.setText("Applied");
                            savebtn.setVisibility(View.GONE);
                            pendingbtn.setVisibility(View.VISIBLE);
                            applybtn.setEnabled(false);
                        } else {
                            // Student has not uploaded a CV, show a Toast message
                            Toast.makeText(Job_Details_student.this, "Please upload your CV first in Profile Management.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to retrieve student data
                    Log.e("Job_post", "Error checking CV upload status", e);
                });
    }
    public  void setData(String collection){
        String title = jobtitle_studentdetails.getText().toString();
        String company = jobCompany_studentdetails.getText().toString();
        String location = joblocation_studentdetails.getText().toString();
        String description = jobdescription_studentdetails.getText().toString();
        String salary = jobsalary_studentdetails.getText().toString();
        String job = jobid;
        String student = studentid;
        String recruiter = recruiterid;
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);

        Map<String, Object> Application_details = new HashMap<>();
        Application_details.put("Title", title);
        Application_details.put("Company", company);
        Application_details.put("location", location);
        Application_details.put("Salary", salary);
        Application_details.put("description", description);
        Application_details.put("recruiterId", recruiter);
        Application_details.put("studentId", student);
        Application_details.put("jobId", job);
        Application_details.put("applicationDate", formattedDate);
        Application_details.put("isShortListed", "false");


        firestore.collection(collection).add(Application_details)
                .addOnSuccessListener(documentReference -> {
                    if(collection.equals("Applications")){
                        deleteSavedJobsWithJobId(job);
                    }
                    Log.d("Job_post", "Job ID updated successfully");
                })
                .addOnFailureListener(e -> {
                });
    }
    private void deleteSavedJobsWithJobId(String jobId) {
        firestore.collection("SavedJobs")
                .whereEqualTo("jobId", jobId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Delete each document with the matching jobId
                        document.getReference().delete();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to query the collection
                });
    }
    public void check(String collection){
        firestore.collection(collection)
                .whereEqualTo("jobId", jobid)
                .whereEqualTo("studentId", studentid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size() > 0) {
                        if (collection.equals("Applications")) {
                            applybtn.setText("Applied");
                            savebtn.setVisibility(View.GONE);
                            pendingbtn.setVisibility(View.VISIBLE);
                            applybtn.setEnabled(false);
                        } else if (collection.equals("SavedJobs")) {
                            savebtn.setText("Unsave");

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to query the Applications collection
                });


    }
    public void delete(String collection) {
        firestore.collection(collection)
                .whereEqualTo("jobId", jobid)
                .whereEqualTo("studentId", studentid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String documentId = document.getId();
                        firestore.collection(collection).document(documentId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Job_post", "Document deleted successfully");
                                    // You can perform any additional actions after deletion if needed
                                    if (collection.equals("SavedJobs")) {
                                        savebtn.setText("Save Job");
                                    }
                                    for(JobItem jobItem: RecentFragment.savejobList){
                                        if(jobItem.getJobid().equals(jobid)){

                                            };
                                            break;

                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Job_post", "Error deleting document", e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to query the collection
                    Log.e("Job_post", "Error querying collection for deletion", e);
                });
    }

}