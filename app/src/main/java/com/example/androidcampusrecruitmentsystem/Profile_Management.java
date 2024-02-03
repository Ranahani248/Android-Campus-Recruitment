package com.example.androidcampusrecruitmentsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile_Management extends AppCompatActivity {

    private ImageView backbutton;
    private EditText nameEditText,  phoneNumberEditText;
    TextView dobEditText;
    Button updateButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_profile_management);

        // Initialize Firebase component
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        backbutton = findViewById(R.id.backbutton);
        nameEditText = findViewById(R.id.nameEditText);
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
        dobEditText.setOnClickListener(v -> {
            showDatePickerDialog();
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
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(Profile_Management.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void displayUserData(DocumentSnapshot documentSnapshot) {
        String userId = currentUser.getUid();
        String name = documentSnapshot.getString("name");
        String dob = documentSnapshot.getString("dob");
        String phoneNumber = documentSnapshot.getString("phoneNumber");
        if (currentUser != null && userId.equals(currentUser.getUid())) {
            nameEditText.setText(name);
            dobEditText.setText(dob);
            phoneNumberEditText.setText(phoneNumber);
        } else {
            Toast.makeText(Profile_Management.this, "Error: Unauthorized access", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateUserData() {
        String newName = nameEditText.getText().toString();
        String newDOB = dobEditText.getText().toString();
        String newPhoneNumber = phoneNumberEditText.getText().toString();

        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Determine the user type
        firestore.collection("Students").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User is a Student
                        updateCollection("Students", uid, newName , newDOB, newPhoneNumber);
                    } else {
                        // User may be a Recruiter
                        firestore.collection("Recruiters").document(uid).get()
                                .addOnSuccessListener(recruiterDocument -> {
                                    if (recruiterDocument.exists()) {
                                        // User is a Recruiter
                                        updateCollection("Recruiters", uid, newName,  newDOB, newPhoneNumber);
                                        Intent intent = new Intent(Profile_Management.this, Profile_Management.class);
                                        startActivity(intent);
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

    private void updateCollection(String collectionName, String uid, String newName, String newDOB, String newPhoneNumber) {
        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("name", newName);
        updatedUserData.put("dob", newDOB);
        updatedUserData.put("phoneNumber", newPhoneNumber);


        // Update data in the appropriate collection
        firestore.collection(collectionName).document(uid).update(updatedUserData)
                .addOnSuccessListener(aVoid -> {
                    // Update email in Firebase Authentication
                    Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile_Management.this, "Error updating user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Set the selected date to the EditText
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    dobEditText.setText(selectedDate);
                },
                year,
                month,
                day);

        // Show the date picker dialog
        datePickerDialog.show();
    }

}

