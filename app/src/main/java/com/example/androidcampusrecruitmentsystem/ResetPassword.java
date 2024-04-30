package com.example.androidcampusrecruitmentsystem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPassword extends AppCompatActivity {
    Button cancel, save;
    EditText oldPassword, newPasswordEditText, confirmPasswordEditText;
    static String emailAdress;
    String email;
    private boolean codeSent = false;
    static boolean recruiter = false;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        cancel = findViewById(R.id.cancelButton);
        oldPassword = findViewById(R.id.forgotPassword_OldPassword);
        newPasswordEditText = findViewById(R.id.newPassword);
        confirmPasswordEditText = findViewById(R.id.confirmNewPassword);
        save = findViewById(R.id.saveButton);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
        email = currentUser.getEmail();
        save.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            if (newPassword.isEmpty() ) {
                newPasswordEditText.setError("Field cannot be empty");
                newPasswordEditText.requestFocus();
                return;
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.setError("Field cannot be empty");
                confirmPasswordEditText.requestFocus();
                return;
            }
            if (oldPassword.getText().toString().isEmpty()) {
                oldPassword.setError("Field cannot be empty");
                oldPassword.requestFocus();
                return;
            }
            if (newPassword.equals(confirmPassword)) {
                authenticateUser(email, oldPassword.getText().toString(), newPassword);

            } else {
                Toast.makeText(ResetPassword.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(v -> navigateToProfileManagement());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToProfileManagement();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void navigateToProfileManagement() {
        Intent intent;
        if (!recruiter) {
            intent = new Intent(ResetPassword.this, Profile_Management.class);
        } else {
            intent = new Intent(ResetPassword.this, Profile_Management_recruiter.class);
            recruiter = false;
        }
        startActivity(intent);
    }
    private void authenticateUser(String email, String password, String newPassword) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Authentication successful, proceed to password reset
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        resetPassword(currentUser, newPassword);
                    } else {
                        // Authentication failed, display error message
                        Toast.makeText(ResetPassword.this, "Wrong Old Password",
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "resetPassword: " + task.getException().getMessage());

                    }
                });
    }
    private void resetPassword(FirebaseUser user, String newPassword) {
        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        navigateToProfileManagement();
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Log.d("TAG", "resetPassword: " + errorMessage);
                        Toast.makeText(ResetPassword.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
