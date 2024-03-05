package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    Button downloadCvButton, sendMessageButton, scheduleTestButton,scheduleInterviewButton;
    static String studentId;
    ImageView studentImage, imageOpen;
    ProgressBar appDetailsProgress;
    boolean imagePresent = false;
    ConstraintLayout constraintLayout;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_details);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        studentNameTextView = findViewById(R.id.Student_name_application_details);
        studentContactTextView = findViewById(R.id.Student_contact_application_details);
        studentImage = findViewById(R.id.StudentImage);
        downloadCvButton = findViewById(R.id.download_Cv);
        appDetailsProgress = findViewById(R.id.appDetailsProgress);
        constraintLayout = findViewById(R.id.constraintAppliDetails);
        sendMessageButton = findViewById(R.id.sendMessage);
        scheduleTestButton = findViewById(R.id.testSchedule);
        scheduleInterviewButton = findViewById(R.id.interviewSchedule);
        imageOpen = findViewById(R.id.imageOpen);
        load(true);
        retrieveStudentDetails();
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
}
