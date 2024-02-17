package com.example.androidcampusrecruitmentsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Profile_Management extends AppCompatActivity {

    private ImageView backbutton;
    private EditText nameEditText,  phoneNumberEditText;
    TextView dobEditText;
    Button updateButton, uploadCv;
    Uri selectedImageUri, selectedCvUri;

    ConstraintLayout constraintLayout;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    ProgressBar progressBar;
    ImageView profilePic;
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
        profilePic = findViewById(R.id.imageView_profile);
        uploadCv = findViewById(R.id.Upload_CV);
        // Initialize UI components
        backbutton = findViewById(R.id.backbutton);
        nameEditText = findViewById(R.id.nameEditText);
        constraintLayout = findViewById(R.id.constraintLayout_profile_student);
        progressBar = findViewById(R.id.progressBar_studentProfile);
        dobEditText = findViewById(R.id.dobEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        updateButton = findViewById(R.id.Save_changes);

        setProgressBar();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::resetProgressBar, 3000);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press if needed
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Back button click listener
        backbutton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Management.this, MainActivity.class);
            startActivity(intent);
        });

        updateButton.setOnClickListener(v -> {
            updateUserData();
            if (selectedImageUri != null) {
                uploadProfilePicture(selectedImageUri);
            }

            Log.d("Profile_Management", "Save Changes button clicked");
        });

        dobEditText.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        profilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        uploadCv.setOnClickListener(v -> {
            openFilePicker();

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
                    loadProfilePicture();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(Profile_Management.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        Log.d("Profile_Management", "retrieveUserData() called");
    }

    private void loadProfilePicture() {
        String uid = currentUser.getUid();

        firestore.collection("Students").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("profilePicture")) {
                            Glide.with(this)
                                    .load(documentSnapshot.getString("profilePicture"))
                                    .placeholder(R.drawable.user)
                                    .error(R.drawable.user)
                                    .into(profilePic);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile_Management.this, "Error loading profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        firestore.collection("Students").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        updateCollection("Students", uid, newName, newDOB, newPhoneNumber);
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

        firestore.collection(collectionName).document(uid).update(updatedUserData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile_Management.this, "Error updating user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    dobEditText.setText(selectedDate);
                },
                year,
                month,
                day);

        datePickerDialog.show();
    }

    private void uploadProfilePicture(Uri imageUri) {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        StorageReference profilePictureRef = FirebaseStorage.getInstance().getReference().child("profilePictures/" + uid + ".jpg");

        profilePictureRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profilePictureRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                updateProfilePicture(uid, uri.toString());
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile_Management.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfilePicture(String uid, String imageUrl) {
        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("profilePicture", imageUrl);

        firestore.collection("Students").document(uid).update(updatedUserData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Profile_Management.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile_Management.this, "Error updating profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Select CV File"), 2);
    }


    private void uploadCvFile(Uri cvUri) {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

            StorageReference cvRef = FirebaseStorage.getInstance().getReference().child("cvFiles/" + uid + ".pdf");

            cvRef.putFile(cvUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        cvRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    resetProgressBar();
                                    updateCvFile(uid, uri.toString());
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Profile_Management.this, "Failed to upload CV file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        resetProgressBar();
                    });


    }

    private void updateCvFile(String uid, String cvUrl) {
        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("cvFile", cvUrl);

        firestore.collection("Students").document(uid).update(updatedUserData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Profile_Management.this, "CV file updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Profile_Management.this, "Error updating CV file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(profilePic);
        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedCvUri = data.getData();
            setProgressBar();
            uploadCvFile(selectedCvUri);

            Toast.makeText(this, "Uploading file please wait", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "none", Toast.LENGTH_SHORT).show();
        }
    }
    public void setProgressBar() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        updateButton.setEnabled(false);
        profilePic.setEnabled(false);
        dobEditText.setEnabled(false);
        nameEditText.setEnabled(false);
        uploadCv.setEnabled(false);
        backbutton.setEnabled(false);
        phoneNumberEditText.setEnabled(false);
        constraintLayout.setAlpha(0.5f);

    }

    public void resetProgressBar() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        updateButton.setEnabled(true);
        profilePic.setEnabled(true);
        dobEditText.setEnabled(true);
        nameEditText.setEnabled(true);
        uploadCv.setEnabled(true);
        backbutton.setEnabled(true);
        phoneNumberEditText.setEnabled(true);
        constraintLayout.setAlpha(1f);
    }
}
