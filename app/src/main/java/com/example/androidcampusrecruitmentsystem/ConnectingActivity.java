package com.example.androidcampusrecruitmentsystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.androidcampusrecruitmentsystem.databinding.ActivityConnectingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ConnectingActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private ActivityConnectingBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private boolean isOkay = false;
    private String username;
    private String incoming1, outgoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Check permissions before proceeding
        if (checkPermissions()) {
            // Permissions are granted, proceed with your logic
            performLogic();
        } else {
            // Request permissions
            requestPermissions();
        }
    }

    // Check if required permissions are granted
    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int modifyAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS);
        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                audioPermission == PackageManager.PERMISSION_GRANTED &&
                modifyAudioPermission == PackageManager.PERMISSION_GRANTED;
    }

    // Request permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                },
                PERMISSION_REQUEST_CODE);
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                // Permissions granted, proceed with your logic
                performLogic();
            } else {
                requestPermissions();
                Log.e("ConnectingActivity", "Required permissions not granted");
                finish();
            }
        }
    }

    // Your logic after permissions are granted
    private void performLogic() {
        // Retrieve intent extras
        username = getIntent().getStringExtra("createdBy");
        incoming1 = getIntent().getStringExtra("incoming");
        outgoing = getIntent().getStringExtra("outgoing");


        // Perform Firestore operation
        firestore.collection("rooms")
                .get().addOnSuccessListener((queryDocumentSnapshots )-> {

                    assert queryDocumentSnapshots != null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                           if(documentSnapshot.getString("outgoing").equals(incoming1) && documentSnapshot.getString("incoming").equals(outgoing)) {
                               Log.d("ConnectingActivity", "Room found: "+ incoming1 + " " + outgoing);
                               String roomId = documentSnapshot.getId();
                               Map<String, Object> updateData = new HashMap<>();
                               updateData.put("status", 1);

                               firestore.collection("rooms").document(roomId)
                                       .update(updateData)
                                       .addOnSuccessListener(aVoid -> {
                                           // Start call activity
                                           Intent intent = new Intent(ConnectingActivity.this, CallActivity.class);
                                           intent.putExtra("createdBy", outgoing);
                                           intent.putExtra("incoming", incoming1);
                                           intent.putExtra("roomId", roomId);
                                           startActivity(intent);
                                           finish();
                                       })
                                       .addOnFailureListener(e -> {
                                           // Handle failure
                                           Log.e("ConnectingActivity", "Error updating room: " + e.getMessage());
                                       });
                               return;
                           }
                        }



                    } else {
                        // Room Not Available
                        Map<String, Object> room = new HashMap<>();
                        room.put("incoming", incoming1);
                        room.put("createdBy", username);
                        room.put("outgoing", outgoing);
                        room.put("isAvailable", true);
                        room.put("status", 0);

                        firestore.collection("rooms")
                                .add(room)
                                .addOnSuccessListener(documentReference -> {
                                    documentReference.addSnapshotListener((snapshot, error1) -> {
                                        if (snapshot != null && snapshot.exists()) {
                                            if (snapshot.contains("status")) {
                                                Long status = snapshot.getLong("status");
                                                if (status != null) {
                                                    if (status == 1) {
                                                        if (!isOkay) {
                                                            isOkay = true;
                                                            Intent intent = new Intent(ConnectingActivity.this, CallActivity.class);
                                                            intent.putExtra("createdBy", outgoing);
                                                            intent.putExtra("studentId", incoming1);
                                                            intent.putExtra("roomId", documentReference.getId());
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    } else {
                                                        // Other user hasn't accepted yet, do nothing or handle appropriately
                                                    }
                                                }
                                            }
                                        }
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Log.e("ConnectingActivity", "Error creating room: " + e.getMessage());
                                });
                    }
                });

    }
}