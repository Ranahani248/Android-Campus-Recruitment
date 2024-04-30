package com.example.androidcampusrecruitmentsystem;

import static com.example.androidcampusrecruitmentsystem.MainActivityRecruiter.recruiter;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile_Management_recruiter extends AppCompatActivity {
    private ImageView backbutton;
    private EditText nameEditText, phoneNumberEditText;
    Button changePassword;
    TextView dobEditText;
    Button updateButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    ProgressBar progressBar;
    ConstraintLayout constraintLayout;
    private DocumentReference userRef;
    private ImageView profilePic;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management_recruiter);

        // Initialize Firebase component
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


        constraintLayout = findViewById(R.id.constraintLayout_profile_management_recruiter);
        progressBar = findViewById(R.id.progressBar_recruiterProfile);

        // Initialize UI components
        backbutton = findViewById(R.id.backbutton_recruiter);
        nameEditText = findViewById(R.id.nameEditText_recruiter);
        dobEditText = findViewById(R.id.dobEditText_recruiter);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText_recruiter);
        updateButton = findViewById(R.id.Save_changes_recruiter);
        changePassword = findViewById(R.id.Reset_password_recruiter);
        profilePic = findViewById(R.id.imageView_profile_recruiter);
        setProgressBar();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::resetProgressBar, 3000);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Profile_Management_recruiter.this, MainActivityRecruiter.class);
                startActivity(intent);
            }
        };
        changePassword.setOnClickListener(v -> {
            ResetPassword.recruiter = true;
            Intent intent = new Intent(Profile_Management_recruiter.this, ResetPassword.class);
            startActivity(intent);
        });
        getOnBackPressedDispatcher().addCallback(this, callback);
        // Back button click listener
        backbutton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile_Management_recruiter.this, MainActivityRecruiter.class);
            startActivity(intent);
        });

        updateButton.setOnClickListener(v -> {
            updateUserData();
            Log.d("Profile_Management", "Save Changes button clicked");
        });

        dobEditText.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        profilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        retrieveUserData();
    }

    private void retrieveUserData() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = firestore.collection("Recruiters").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    displayUserData(documentSnapshot);
                    loadProfilePicture();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(Profile_Management_recruiter.this, "Error retrieving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        Log.d("Profile_Management", "retrieveUserData() called");
    }

    private void loadProfilePicture() {

            String uid = currentUser.getUid();

            firestore.collection("Recruiters").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Check if the 'profilePicture' field exists
                            if (documentSnapshot.contains("profilePicture")) {
                                // Download the profile picture into the ImageView using Glide or Picasso
                                Glide.with(this)
                                        .load(documentSnapshot.getString("profilePicture"))
                                        .placeholder(R.drawable.user)
                                        .error(R.drawable.user)
                                        .into(profilePic);
                            }

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Profile_Management_recruiter.this, "Error loading profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Profile_Management_recruiter.this, "Error: Unauthorized access", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserData() {
        String newName = nameEditText.getText().toString();
        String newDOB = dobEditText.getText().toString();
        String newPhoneNumber = phoneNumberEditText.getText().toString();

        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Update collection based on user type (Recruiter)
        updateCollection("Recruiters", uid, newName, newDOB, newPhoneNumber);
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
                    Toast.makeText(Profile_Management_recruiter.this, "Error updating user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Upload profile picture if selected
        if (selectedImageUri != null) {
            uploadProfilePicture(selectedImageUri);
            recruiter.setProfilePictureUri(selectedImageUri);
        }
    }

    private void showDatePickerDialog() {
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

    private void uploadProfilePicture(Uri imageUri) {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Create a reference to 'profilePictures/[uid].jpg'
        StorageReference profilePictureRef = FirebaseStorage.getInstance().getReference().child("profilePictures/" + uid + ".jpg");

        // Upload file to Firebase Storage
        profilePictureRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // Get the download URL and update the user's profile picture in Firestore
                    profilePictureRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                updateProfilePicture(uid, uri.toString());
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(Profile_Management_recruiter.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfilePicture(String uid, String imageUrl) {
        // Update the 'profilePicture' field in the user's document
        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("profilePicture", imageUrl);

        firestore.collection("Recruiters").document(uid).update(updatedUserData)

                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(Profile_Management_recruiter.this, "Error updating profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(profilePic);
        }
    }

    public void setProgressBar() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        updateButton.setEnabled(false);
        profilePic.setEnabled(false);
        dobEditText.setEnabled(false);
        nameEditText.setEnabled(false);
        phoneNumberEditText.setEnabled(false);
        constraintLayout.setAlpha(0.5f);
        changePassword.setEnabled(false);

    }

    public void resetProgressBar() {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        updateButton.setEnabled(true);
        profilePic.setEnabled(true);
        dobEditText.setEnabled(true);
        nameEditText.setEnabled(true);
        phoneNumberEditText.setEnabled(true);
        changePassword.setEnabled(true);

        constraintLayout.setAlpha(1f);
    }



}
