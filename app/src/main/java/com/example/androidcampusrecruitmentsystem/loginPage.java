package com.example.androidcampusrecruitmentsystem;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class loginPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailLogin, passwordLogin;
    private Button login_button, login_signup_button;

    ProgressBar progressBar;
    ConstraintLayout login_layout;
    TextView forgot;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mAuth = FirebaseAuth.getInstance();
        emailLogin = findViewById(R.id.login_mail);
        passwordLogin = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        login_layout = findViewById(R.id.constraintLayoutLogin);
        progressBar = findViewById(R.id.progressBarLogin);
        login_signup_button = findViewById(R.id.login_signup_button);
        forgot = findViewById(R.id.forgot);

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        setProgressBar();
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(this::resetProgressBar, 3000);
//

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity();
        }
        else {
            resetProgressBar();
        }

        login_signup_button.setOnClickListener(v -> {
            Intent intent = new Intent(loginPage.this, signUp_page.class);
            startActivity(intent);
        });
        forgot.setOnClickListener(v -> {
            Intent intent = new Intent(loginPage.this, ForgotPassword.class);
            startActivity(intent);
        });
        login_button.setOnClickListener(v -> {
            loginUser();
        });
    }

    private void loginUser() {
        String email = emailLogin.getText().toString();
        String password = passwordLogin.getText().toString();

        if (email.isEmpty()) {
            emailLogin.setError("Email is required");
            emailLogin.requestFocus();
        } else if (password.isEmpty()) {
            passwordLogin.setError("Password is required");
            passwordLogin.requestFocus();
        } else {
            setProgressBar();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkUserRole();
                    } else {
                        resetProgressBar();
                        Toast.makeText(loginPage.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkUserRole() {
        String userId = mAuth.getCurrentUser().getUid();

        // Check in the "Students" collection
        FirebaseFirestore.getInstance().collection("Students").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Set the user as logged in
                        setLoggedIn(true);
                        // Redirect to student activity
                        navigateToMainActivity();
                    } else {
                        FirebaseFirestore.getInstance().collection("Recruiters").document(userId)
                                .get()
                                .addOnCompleteListener(taskRecruiter -> {
                                    if (taskRecruiter.isSuccessful() && taskRecruiter.getResult().exists()) {
                                        // Set the user as logged in
                                        setLoggedIn(true);
                                        // Redirect to recruiter activity
                                        navigateToMainActivity();
                                    } else {
                                        // Handle cases where the user is not found in either collection
                                        Toast.makeText(loginPage.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    private void setLoggedIn(boolean loggedIn) {
        // Save the login state in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", loggedIn);
        editor.apply();
    }

    private void navigateToMainActivity() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            FirebaseFirestore.getInstance().collection("Students").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            // User is a student

                            Intent intent = new Intent(loginPage.this, MainActivity.class);
                            startActivityForResult(intent, MainActivity.REQUEST_CODE);
                        } else {
                            // User is not a student, check if they are a recruiter
                            FirebaseFirestore.getInstance().collection("Recruiters").document(userId)
                                    .get()
                                    .addOnCompleteListener(taskRecruiter -> {
                                        if (taskRecruiter.isSuccessful() && taskRecruiter.getResult().exists()) {
                                            // User is a recruiter
                                            Intent intent = new Intent(loginPage.this, MainActivityRecruiter.class);
                                            startActivityForResult(intent, MainActivityRecruiter.REQUEST_CODE);
                                        } else {
                                            resetProgressBar();

                                            // Handle cases where the user is not found in either collection
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Handle the result if needed
        }
    }
 public  void  setProgressBar(){
        progressBar.setVisibility(ProgressBar.VISIBLE);
        login_signup_button.setEnabled(false);
        login_button.setEnabled(false);
        emailLogin.setEnabled(false);
        passwordLogin.setEnabled(false);
     login_layout.setAlpha(0.5f);

 }
 public  void  resetProgressBar(){
     progressBar.setVisibility(ProgressBar.GONE);
     login_signup_button.setEnabled(true);
     login_button.setEnabled(true);
     emailLogin.setEnabled(true);
     passwordLogin.setEnabled(true);
     login_layout.setAlpha(1f);

 }

}
