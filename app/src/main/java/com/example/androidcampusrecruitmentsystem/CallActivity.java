package com.example.androidcampusrecruitmentsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidcampusrecruitmentsystem.databinding.ActivityCallBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallActivity extends AppCompatActivity {
    ActivityCallBinding binding;
    String uniqueId = "";
    FirebaseAuth auth;
    String incoming,roomId;
    boolean isPeerConnected = false;
    FirebaseFirestore db;
    boolean isAudio = true;
    boolean isVideo = true;
    String createdBy;
    boolean pageExit = false;
    ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        binding.webView.getSettings().setJavaScriptEnabled(true);
        incoming = getIntent().getStringExtra("studentId");
        createdBy = getIntent().getStringExtra("createdBy");
        roomId = getIntent().getStringExtra("roomId");

        Log.d("TAG", "onCreate: "+roomId);

        setupWebView();

        binding.micBtn.setOnClickListener(v -> {
            isAudio = !isAudio;
            callJavaScriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
            binding.micBtn.setImageResource(isAudio ? R.drawable.btn_unmute_normal : R.drawable.btn_mute_normal);
        });

        binding.videoBtn.setOnClickListener(v -> {
            isVideo = !isVideo;
            callJavaScriptFunction("javascript:toggleVideo(\"" + isVideo + "\")");
            binding.videoBtn.setImageResource(isVideo ? R.drawable.btn_video_normal : R.drawable.btn_video_muted);
        });

        binding.endCall.setOnClickListener(v -> finish());
    }
    @SuppressLint("SetJavaScriptEnabled")
    void setupWebView() {
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webView.addJavascriptInterface(new InterfaceJava(this), "Android");
        loadVideoCall();
    }
    public void loadVideoCall() {
        String filePath = "file:///android_asset/call.html";
        binding.webView.loadUrl(filePath);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer();
            }
        });
    }
    void initializePeer() {
        uniqueId = getUniqueId();
        callJavaScriptFunction("javascript:init(\"" + uniqueId + "\")");
        if (auth.getCurrentUser() != null) {
            if (auth.getCurrentUser().getUid().equals(createdBy)) {

                Map<String, Object> updateData = new HashMap<>();
                updateData.put("createdBy", createdBy);
                updateData.put("connId", uniqueId);
                updateData.put("isAvailable", true);
                Log.d("TAG", "initializePeer: "+updateData);

                db.collection("rooms")
                        .document(roomId)
                        .update(updateData)
                        .addOnSuccessListener(aVoid -> {
                            // Update UI
                            binding.loadingGroup.setVisibility(View.GONE);
                            binding.controls.setVisibility(View.VISIBLE);

                        })
                        .addOnFailureListener(e -> Log.e("CallActivity", "Error updating Firestore document: " + e.getMessage()));
            } else if (incoming != null && !incoming.isEmpty()) {
                db.collection("rooms")
                        .document(roomId)
                        .update("incoming", incoming)
                        .addOnSuccessListener(aVoid -> {

                            binding.loadingGroup.setVisibility(View.GONE);
                            binding.controls.setVisibility(View.VISIBLE);                        })
                        .addOnFailureListener(e -> Log.e("CallActivity", "Error updating Firestore document: " + e.getMessage()));
            } else {
                Log.e("CallActivity", "Error: incoming is null");
            }
        }

        // For students joining the call
        db.collection("rooms").document(roomId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String connId = documentSnapshot.getString("connId");
                        if (connId != null) {
                            // Update UI
                            sendCallRequest();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("CallActivity", "Error checking call initiator availability: " + e.getMessage()));
    }
    public void onPeerConnected() {
        isPeerConnected = true;
    }
    void sendCallRequest() {
        listenConnId();
    }
    void listenConnId() {
        listenerRegistration = db.collection("rooms").document(roomId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null || documentSnapshot == null) {
                        return;
                    }
                    else {
                        Log.d("TAG", "listenConnId1: "+documentSnapshot);
                    }
                    String connId = documentSnapshot.getString("connId");
                    Log.d("TAG", "listenConnId2: "+connId);
                    if (connId != null) {
                        binding.loadingGroup.setVisibility(View.GONE);
                        binding.controls.setVisibility(View.VISIBLE);
                        callJavaScriptFunction("javascript:startCall(\"" + connId + "\")");
                        Log.d("TAG", "listenConnId3: " + connId);
                    }
                    else {
                        Log.d("TAG", "listenConnId4: " + null);
                    }

                });
    }
    void callJavaScriptFunction(String function) {
        binding.webView.post(() -> binding.webView.evaluateJavascript(function, null));
    }
    String getUniqueId() {
        return UUID.randomUUID().toString();
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.collection("rooms").document(roomId).delete();
        pageExit = true;
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        if (auth.getCurrentUser() != null) {
            db.collection("Students").document(auth.getCurrentUser().getUid()).update("connId", null);
        }
        else {
            db.collection("Recruiters").document(createdBy).update("connId", null);
        }
    }
}
