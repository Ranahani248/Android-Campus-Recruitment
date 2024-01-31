package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile_Management extends AppCompatActivity {

    private ImageView backbutton;
    private EditText nameEditText, emailEditText, dobEditText, phoneNumberEditText;
    Button updateButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        backbutton = findViewById(R.id.backbutton);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        dobEditText = findViewById(R.id.dobEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        updateButton = findViewById(R.id.Save_changes);


        // Back button click listener
        backbutton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Management.this, MainActivity.class);
            startActivity(intent);
        });
        updateButton.setOnClickListener(v -> {
            updateUserData();
            Log.d("Profile_Management", "Save Changes button clicked");

        });
        retrieveUserData();
    }


    private void retrieveUserData() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = firestore.collection("Students").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    displayUserData(documentSnapshot);
                } else {
                    userRef = firestore.collection("Recruiters").document(userId);
                    retrieveRecruiterUserData();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(Profile_Management.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        Log.d("Profile_Management", "retrieveUserData() called");
    }

    private void retrieveRecruiterUserData() {
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                displayUserData(documentSnapshot);
            } else {
                Toast.makeText(Profile_Management.this, "Error: User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(Profile_Management.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void displayUserData(DocumentSnapshot documentSnapshot) {
        String userId = currentUser.getUid();
        String name = documentSnapshot.getString("name");
        String email = documentSnapshot.getString("email");
        String dob = documentSnapshot.getString("dob");
        String phoneNumber = documentSnapshot.getString("phoneNumber");

        if (currentUser != null && userId.equals(currentUser.getUid())) {
            nameEditText.setText(name);
            emailEditText.setText(email);
            dobEditText.setText(dob);
            phoneNumberEditText.setText(phoneNumber);
        } else {
            Toast.makeText(Profile_Management.this, "Error: Unauthorized access", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateUserData() {
        String newName = nameEditText.getText().toString();
        String newEmail = emailEditText.getText().toString();
        String newDOB = dobEditText.getText().toString();
        String newPhoneNumber = phoneNumberEditText.getText().toString();

        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Determine the user type
        firestore.collection("Students").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User is a Student
                        updateCollection("Students", uid, newName, newEmail, newDOB, newPhoneNumber);
                    } else {
                        // User may be a Recruiter
                        firestore.collection("Recruiters").document(uid).get()
                                .addOnSuccessListener(recruiterDocument -> {
                                    if (recruiterDocument.exists()) {
                                        // User is a Recruiter
                                        updateCollection("Recruiters", uid, newName, newEmail, newDOB, newPhoneNumber);
                                    } else {
                                        Toast.makeText(Profile_Management.this, "Error: Unknown user type", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Profile_Management.this, "Error determining user type: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile_Management.this, "Error determining user type: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCollection(String collectionName, String uid, String newName, String newEmail, String newDOB, String newPhoneNumber) {
        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("name", newName);
        updatedUserData.put("email", newEmail);
        updatedUserData.put("dob", newDOB);
        updatedUserData.put("phoneNumber", newPhoneNumber);

        // Update data in the appropriate collection
        firestore.collection(collectionName).document(uid).update(updatedUserData)
                .addOnSuccessListener(aVoid -> {
                    // Update email in Firebase Authentication
                    Objects.requireNonNull(mAuth.getCurrentUser()).verifyBeforeUpdateEmail(newEmail)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(Profile_Management.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Profile_Management.this, "Error updating email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Profile_Management", "Error updating email: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile_Management.this, "Error updating user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



}

