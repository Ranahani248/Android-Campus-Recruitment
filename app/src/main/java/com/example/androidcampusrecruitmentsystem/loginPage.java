package com.example.androidcampusrecruitmentsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginPage extends AppCompatActivity {
FirebaseAuth mAuth;
EditText emailLogin, passwordLogin;

Button login_button,login_signup_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        emailLogin = findViewById(R.id.login_mail);
        passwordLogin = findViewById(R.id.login_password);
         mAuth = FirebaseAuth.getInstance();
            login_button = findViewById(R.id.login_button);
            login_signup_button = findViewById(R.id.login_signup_button);

        login_signup_button.setOnClickListener(v -> {
            Intent intent = new Intent(loginPage.this, signUp_page.class);
            startActivity(intent);
        });

        login_button.setOnClickListener(v -> {
            loginUser();
        });
    }
    private void loginUser() {
        String email = emailLogin.getText().toString();
        String password = passwordLogin.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(loginPage.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(loginPage.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(loginPage.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }
}