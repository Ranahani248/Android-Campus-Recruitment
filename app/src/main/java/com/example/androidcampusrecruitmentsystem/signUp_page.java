package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;

public class signUp_page extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button signup_student_button, signup_recruiter_button;
    TextView signup_login_button;


    EditText emailSignUp, passwordSignUp ,nameSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        emailSignUp = findViewById(R.id.signup_mail);
        passwordSignUp = findViewById(R.id.signup_password);
        nameSignUp = findViewById(R.id.signup_name);
        signup_login_button = findViewById(R.id.signup_login_button);
        signup_recruiter_button = findViewById(R.id.signup_recruiter_button);
        signup_login_button.setOnClickListener(v -> {
            Intent intent = new Intent(signUp_page.this, loginPage.class);
            startActivity(intent);
        });

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("Students").document("user");
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("first", "Ada");
//        data.put("last", "Lovelace");
//        data.put("born", 1818);

        mAuth = FirebaseAuth.getInstance();
        signup_student_button = findViewById(R.id.signup_student_button);
        signup_student_button.setOnClickListener(v -> {
            createUser("Student");
        });
        signup_recruiter_button.setOnClickListener(v -> {
            createUser("Recruiter");
        });

//            docRef.set(data).addOnSuccessListener(aVoid -> {
//                Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show();
//            }).addOnFailureListener(e -> {
//                Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
//            });








    }
    private void createUser(String userType) {
        String email = emailSignUp.getText().toString();
        String password = passwordSignUp.getText().toString();
        String name = nameSignUp.getText().toString();
        if(name.isEmpty()){
            nameSignUp.setError("Name is required");
            nameSignUp.requestFocus();
        }
       else if (email.isEmpty()) {
            emailSignUp.setError("Email is required");
            emailSignUp.requestFocus();
        } else if (password.isEmpty()) {
            passwordSignUp.setError("Password is required");
            passwordSignUp.requestFocus();
        }  else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();

                    // Create a user map with the necessary data
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("name", name);
                    userMap.put("email", email);
                    userMap.put("userType", userType);
                    userMap.put("uid", uid);

                    // Store user data in the respective collection based on user type
                    if (userType.equals("Recruiter")) {
                        FirebaseFirestore.getInstance().collection("Recruiters").document(uid)
                                .set(userMap)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(signUp_page.this, "Signed up as recruiter", Toast.LENGTH_SHORT).show();
                                    // Redirect to the RecruiterActivity
                                    startActivity(new Intent(signUp_page.this, Profile_Management_recruiter.class));
                                    finish(); // Close the current activity
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(signUp_page.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                    else if (userType.equals("Student")) {
                        FirebaseFirestore.getInstance().collection("Students").document(uid)
                                .set(userMap)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(signUp_page.this, "Signed Up as student", Toast.LENGTH_SHORT).show();
                                    // Redirect to the StudentActivity
                                    startActivity(new Intent(signUp_page.this, Profile_Management.class));
                                    finish(); // Close the current activity
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(signUp_page.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }

                } else {
                    Toast.makeText(signUp_page.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}