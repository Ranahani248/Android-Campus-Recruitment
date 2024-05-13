package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ApplicationDetails extends AppCompatActivity {

    TextView studentNameTextView, studentContactTextView;
    Button downloadCvButton, sendMessageButton, scheduleTestButton,scheduleInterviewButton, shortListButton;
    static String studentId, applicationId;
    ImageView studentImage, imageOpen;
    ProgressBar appDetailsProgress;
    boolean imagePresent = false;

    String jobId;
    ConstraintLayout constraintLayout;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_details);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        jobId = intent.getStringExtra("jobId");
        applicationId = intent.getStringExtra("applicationId");
        checkIfTestScheduled();
        // Initialize views
        studentNameTextView = findViewById(R.id.Student_name_application_details);
        studentContactTextView = findViewById(R.id.Student_contact_application_details);
        studentImage = findViewById(R.id.StudentImage);
        downloadCvButton = findViewById(R.id.download_Cv);
        appDetailsProgress = findViewById(R.id.appDetailsProgress);
        constraintLayout = findViewById(R.id.constraintAppliDetails);
        sendMessageButton = findViewById(R.id.sendMessage);
        scheduleTestButton = findViewById(R.id.testSchedule);
        scheduleTestButton.setEnabled(false);
        scheduleInterviewButton = findViewById(R.id.interviewSchedule);
        imageOpen = findViewById(R.id.imageOpen);
        shortListButton = findViewById(R.id.shortList);
        shortListButton.setEnabled(true);
        load(true);
        retrieveStudentDetails();


        shortListButton.setOnClickListener(v -> {
            if(shortListButton.getText().toString().equals("Short Listed")){
                firestore.collection("Applications").document(applicationId).update("isShortListed", "false");
                shortListButton.setText("Short List");
            }
            else {
                firestore.collection("Applications").document(applicationId).update("isShortListed", "true");
                shortListButton.setText("Short Listed");
            }
        });
        scheduleInterviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationDetails.this, ScheduleInterview.class);
                intent.putExtra("studentId", studentId);
                intent.putExtra("jobId", jobId);


                startActivity(intent);            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (imageOpen.getVisibility() == View.VISIBLE) {
                    imageOpen.setVisibility(View.GONE);
                    constraintLayout.setAlpha(1f);
                    downloadCvButton.setEnabled(true);
                    studentImage.setEnabled(true);
                    scheduleTestButton.setEnabled(true);
                    scheduleInterviewButton.setEnabled(true);
                    sendMessageButton.setEnabled(true);
                } else {
                    Intent intent = new Intent(ApplicationDetails.this, MainActivityRecruiter.class);
                    MainActivityRecruiter.application = true;
                    startActivity(intent);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        studentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imagePresent) {


                    imageOpen.setVisibility(View.VISIBLE);
                    constraintLayout.setAlpha(0.1f);
                    downloadCvButton.setEnabled(false);
                    studentImage.setEnabled(false);
                    scheduleTestButton.setEnabled(false);
                    scheduleInterviewButton.setEnabled(false);
                    sendMessageButton.setEnabled(false);
                }
            }
        });
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve current user details
                firestore.collection("Students").document(studentId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String studentName = documentSnapshot.getString("name");
                                String studentContact = documentSnapshot.getString("email");
                                String studentId = documentSnapshot.getId();

                                // Pass user details to the message activity
                                Intent messageIntent = new Intent(ApplicationDetails.this, ChatActivity.class);
                                messageIntent.putExtra("studentName", studentName);
                                messageIntent.putExtra("studentId", studentId);
                                messageIntent.putExtra("studentContact", studentContact);


                                // Start the message activity
                                startActivity(messageIntent);
                            } else {
                                Toast.makeText(ApplicationDetails.this, "Student not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ApplicationDetails.this, "Error", Toast.LENGTH_SHORT).show();
                        });
            }
        });
        imageOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageOpen.setVisibility(View.GONE);
                constraintLayout.setAlpha(1f);
                downloadCvButton.setEnabled(true);
                studentImage.setEnabled(true);
                scheduleTestButton.setEnabled(true);
                scheduleInterviewButton.setEnabled(true);
                sendMessageButton.setEnabled(true);
            }
        });
        scheduleTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplicationDetails.this, ScheduleTestRecruiter.class);
                intent.putExtra("studentId", studentId);
                intent.putExtra("jobId", jobId);


                startActivity(intent);
            }
        });
        downloadCvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Students").document(studentId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if(documentSnapshot.exists()) {
                                String cvUrl = documentSnapshot.getString("cvFile");
                                if(cvUrl != null && !cvUrl.isEmpty()) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(cvUrl));
                                    startActivity(browserIntent);
                                }
                                else {
                                    Toast.makeText(ApplicationDetails.this, "CV not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(ApplicationDetails.this, "Student not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ApplicationDetails.this, "Error", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void retrieveStudentDetails() {
        firestore.collection("Applications").document(applicationId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String isShortListed = documentSnapshot.getString("isShortListed");
                        if (isShortListed != null && isShortListed.equals("true")) {
                            shortListButton.setText("Short Listed");
                        }
                    }
                });
        firestore.collection("Students").document(studentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("name");
                        String studentContact = documentSnapshot.getString("email");
                        String imageUrl = documentSnapshot.getString("profilePicture");
                        studentNameTextView.setText(studentName);
                        studentContactTextView.setText(studentContact);
                        if(imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(ApplicationDetails.this).load(imageUrl).into(studentImage);
                            Glide.with(ApplicationDetails.this).load(imageUrl).into(imageOpen);
                            imagePresent = true;
                        }

                        load(false);

                    }
                    else {
                        load(false);
                        Toast.makeText(ApplicationDetails.this, "Student not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    load(false);
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                });
    }
    public  void load(Boolean load) {
        if (load) {
            appDetailsProgress.setVisibility(View.VISIBLE);
            constraintLayout.setAlpha(0.5f);
            downloadCvButton.setEnabled(false);
            studentImage.setEnabled(false);
            scheduleTestButton.setEnabled(false);
            scheduleInterviewButton.setEnabled(false);
            sendMessageButton.setEnabled(false);
        }
        else {
            appDetailsProgress.setVisibility(View.GONE);
            constraintLayout.setAlpha(1f);
            downloadCvButton.setEnabled(true);
            studentImage.setEnabled(true);
            scheduleTestButton.setEnabled(true);
            scheduleInterviewButton.setEnabled(true);
            sendMessageButton.setEnabled(true);
        }

    }
    private void checkIfTestScheduled() {
        firestore.collection("test")
                .whereEqualTo("jobId", jobId)
                .whereEqualTo("studentID", studentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // If a test document exists, disable the test schedule button
                    if (!queryDocumentSnapshots.isEmpty()) {
                        scheduleTestButton.setEnabled(false);
                        scheduleTestButton.setText("Test Scheduled");
                    }
                    else {
                        scheduleTestButton.setEnabled(true);
                        scheduleTestButton.setText("Schedule Test");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure if any
                    Log.e("Firestore", "Error checking for existing test documents: " + e.getMessage());
                });
    }
}
