package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ApplicationDetails extends AppCompatActivity {

    TextView studentNameTextView, studentContactTextView;
    Button downloadCvButton;
    static String studentId;
    ImageView studentImage;
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
        retrieveStudentDetails();

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
                        Glide.with(ApplicationDetails.this).load(imageUrl).into(studentImage);
                    }
                    else {
                        Toast.makeText(ApplicationDetails.this, "Student not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                });
    }
}
