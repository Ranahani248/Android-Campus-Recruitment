package com.example.androidcampusrecruitmentsystem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPassword extends AppCompatActivity {
    Button cancel;
    EditText oldPassword , code;
    static boolean change = false, recruiter = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        cancel = findViewById(R.id.cancelButton);
        oldPassword = findViewById(R.id.forgotPassword_OldPassword);
        code = findViewById(R.id.forgotPassword_code);
        if(change)
        {
            oldPassword.setVisibility(View.VISIBLE);
            code.setVisibility(View.GONE);
        }
        else {
            oldPassword.setVisibility(View.GONE);
            code.setVisibility(View.VISIBLE);
        }


        cancel.setOnClickListener(v ->
        {
            if(!change){
            Intent intent = new Intent(ForgotPassword.this, loginPage.class);
            startActivity(intent);}

            else if(!recruiter) {
                Intent intent = new Intent(ForgotPassword.this, Profile_Management.class);
                startActivity(intent);
                change = false;
            }
            else {
                Intent intent = new Intent(ForgotPassword.this, Profile_Management_recruiter.class);
                startActivity(intent);
                change = false;
                recruiter = false;
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(!change){
                    Intent intent = new Intent(ForgotPassword.this, loginPage.class);
                    startActivity(intent);}

                else if(!recruiter) {
                    Intent intent = new Intent(ForgotPassword.this, Profile_Management.class);
                    startActivity(intent);
                    change = false;
                }
                else {
                    Intent intent = new Intent(ForgotPassword.this, Profile_Management_recruiter.class);
                    startActivity(intent);
                    change = false;
                    recruiter = false;
                }
                          }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }
}