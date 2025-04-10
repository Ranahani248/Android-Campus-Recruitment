package com.example.androidcampusrecruitmentsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    ConstraintLayout signup_layout;

    ProgressBar progressBar;
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
        progressBar = findViewById(R.id.signup_progress);
        signup_layout = findViewById(R.id.ConstraintSignup);


        signup_login_button.setOnClickListener(v -> {
            Intent intent = new Intent(signUp_page.this, loginPage.class);
            startActivity(intent);
        });


        mAuth = FirebaseAuth.getInstance();
        signup_student_button = findViewById(R.id.signup_student_button);
        signup_student_button.setOnClickListener(v -> {
            setProgressBar(true);
            createUser("Student");
        });
        signup_recruiter_button.setOnClickListener(v -> {
            setProgressBar(true);
            createUser("Recruiter");
        });




    }
    private void createUser(String userType) {
        String email = emailSignUp.getText().toString();
        String password = passwordSignUp.getText().toString();
        String name = nameSignUp.getText().toString();
        if(name.isEmpty()){
            nameSignUp.setError("Name is required");
            nameSignUp.requestFocus();
            setProgressBar(false);

        }
       else if (email.isEmpty()) {
            emailSignUp.setError("Email is required");
            emailSignUp.requestFocus();
            setProgressBar(false);

        } else if (password.isEmpty()) {
            passwordSignUp.setError("Password is required");
            passwordSignUp.requestFocus();
            setProgressBar(false);

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
                                    setProgressBar(false);
                                })
                                .addOnFailureListener(e -> {
                                    setProgressBar(false);
                                    Toast.makeText(signUp_page.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                    else if (userType.equals("Student")) {
                        FirebaseFirestore.getInstance().collection("Students").document(uid)
                                .set(userMap)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(signUp_page.this, "Signed Up as student", Toast.LENGTH_SHORT).show();
                                    setProgressBar(false);

                                })
                                .addOnFailureListener(e -> {
                                    setProgressBar(false);

                                    Toast.makeText(signUp_page.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                    Intent intent = new Intent(signUp_page.this, loginPage.class);
                    startActivity(intent);

                } else {
                    setProgressBar(false);
                    Toast.makeText(signUp_page.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void setProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            nameSignUp.setEnabled(false);
            emailSignUp.setEnabled(false);
            passwordSignUp.setEnabled(false);
            signup_student_button.setEnabled(false);
            signup_recruiter_button.setEnabled(false);
            signup_login_button.setEnabled(false);
            signup_layout.setAlpha(0.5f);

        } else {
            progressBar.setVisibility(View.GONE);
            nameSignUp.setEnabled(true);
            emailSignUp.setEnabled(true);
            passwordSignUp.setEnabled(true);
            signup_student_button.setEnabled(true);
            signup_recruiter_button.setEnabled(true);
            signup_login_button.setEnabled(true);
            signup_layout.setAlpha(1f);
        }

    }

}