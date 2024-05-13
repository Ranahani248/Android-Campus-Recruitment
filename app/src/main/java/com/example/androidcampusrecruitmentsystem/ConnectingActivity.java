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
    String studentId;
    private String incoming1;

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
                // Permissions not granted, handle accordingly (e.g., show a message or exit)
                Log.e("ConnectingActivity", "Required permissions not granted");
                finish();
            }
        }
    }

    // Your logic after permissions are granted
    private void performLogic() {
        // Retrieve intent extras
        username = getIntent().getStringExtra("recruiterId");
        incoming1 = getIntent().getStringExtra("studentId");
        studentId = getIntent().getStringExtra("studentID");

        // Perform Firestore operation
        firestore.collection("rooms")
                .whereEqualTo("isAvailable", true)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Room Available
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String roomId = documentSnapshot.getId();
                        // Update room with user details
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("incoming", incoming1);
                        updateData.put("status", 1);

                        firestore.collection("rooms").document(roomId)
                                .update(updateData)
                                .addOnSuccessListener(aVoid -> {
                                    // Start call activity
                                    Intent intent = new Intent(ConnectingActivity.this, CallActivity.class);
                                    intent.putExtra("createdBy", username);
                                    intent.putExtra("studentId", incoming1);
                                    intent.putExtra("roomId", roomId);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Log.e("ConnectingActivity", "Error updating room: " + e.getMessage());
                                });
                    } else {
                        // Room Not Available
                        Map<String, Object> room = new HashMap<>();
                        room.put("incoming", incoming1);
                        room.put("createdBy", username);
                        room.put("isAvailable", true);
                        room.put("studentID",studentId);
                        room.put("status", 0);

                        firestore.collection("rooms")
                                .add(room)
                                .addOnSuccessListener(documentReference -> {
                                    documentReference.addSnapshotListener((snapshot, error) -> {
                                        if (snapshot != null && snapshot.exists()) {
                                            if (snapshot.contains("status") && snapshot.getLong("status") == 1) {
                                                if (isOkay) return;

                                                isOkay = true;
                                                // Start call activity
                                                Intent intent = new Intent(ConnectingActivity.this, CallActivity.class);
                                                intent.putExtra("createdBy", username);
                                                intent.putExtra("studentId", incoming1);
                                                intent.putExtra("roomId", documentReference.getId());
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Log.e("ConnectingActivity", "Error creating room: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("ConnectingActivity", "Error fetching rooms: " + e.getMessage());
                });
    }
}
