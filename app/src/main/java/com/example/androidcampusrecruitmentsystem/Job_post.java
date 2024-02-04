package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Job_post extends AppCompatActivity {
    private DocumentReference userRef;
    FirebaseUser currentUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post);

        Button postButton = findViewById(R.id.post_button);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        firestore = FirebaseFirestore.getInstance();

        userRef = firestore.collection("Recruiters").document(userId);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get job details from EditText fields
                String jobTitle = ((EditText) findViewById(R.id.job_title)).getText().toString();
                String companyName = ((EditText) findViewById(R.id.companyName)).getText().toString();
                String location = ((EditText) findViewById(R.id.location)).getText().toString();
                String description = ((EditText) findViewById(R.id.description)).getText().toString();

                // Assuming you have some kind of user authentication in place, get the recruiter ID
                String recruiterId = currentUser.getUid(); // Use the current user's UID as the recruiter ID


                Map<String, Object> job_details = new HashMap<>();
                job_details.put("jobTitle", jobTitle);
                job_details.put("companyName", companyName);
                job_details.put("location", location);
                job_details.put("description", description);
                job_details.put("recruiterId", recruiterId);

                firestore.collection("Jobs").add(job_details)
                        .addOnSuccessListener(aVoid -> {
                            // Log success
                            Log.d("Job_post", "Job added successfully");
                            // You can also show a success message to the user if needed
                        })
                        .addOnFailureListener(e -> {
                            // Log error
                            Log.e("Job_post", "Error adding job", e);
                            // You can also show an error message to the user if needed
                        });
            }
        });

    }
}
