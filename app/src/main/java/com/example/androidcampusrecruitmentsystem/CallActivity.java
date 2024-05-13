package com.example.androidcampusrecruitmentsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidcampusrecruitmentsystem.databinding.ActivityCallBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
    private WebView webView;

    boolean send = false;
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
        webView = findViewById(R.id.webView);


        incoming = getIntent().getStringExtra("incoming");
        createdBy = getIntent().getStringExtra("createdBy");
        roomId = getIntent().getStringExtra("roomId");
        Log.d("TAG", "onCreate: "+createdBy+ "  "+ incoming);
        Log.d("TAG", "onCreate: "+roomId);
        checkSend();
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



    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl("file:///android_asset/call.html");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer();
            }
        });
    }

    void initializePeer() {
        String peerId = UUID.randomUUID().toString();
        JSONObject data = new JSONObject();
        try {
            data.put("roomId", roomId);
            data.put("peerId", peerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webView.evaluateJavascript("startCall('" + data.toString() + "')", null);

        // Update Firestore with peer ID
        db.collection("rooms").document(roomId).update("peerId", peerId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("CallActivity", "Peer ID updated in Firestore");
                        } else {
                            Log.e("CallActivity", "Failed to update peer ID in Firestore", task.getException());
                        }
                    }
                });
    }



//    @SuppressLint("SetJavaScriptEnabled")
//    void setupWebView() {
//        binding.webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onPermissionRequest(PermissionRequest request) {
//                request.grant(request.getResources());
//            }
//        });
//        binding.webView.getSettings().setJavaScriptEnabled(true);
//        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
//        binding.webView.addJavascriptInterface(new InterfaceJava(this), "Android");
//        loadVideoCall();
//    }
//    public void loadVideoCall() {
//        String filePath = "file:///android_asset/call.html";
//        binding.webView.loadUrl(filePath);
//        binding.webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                initializePeer();
//            }
//        });
//    }
//
//
//    void initializePeer() {
//        if(send) {
//            String yourUniqueId = getUniqueId();
//            callJavaScriptFunction( "javascript:init('" + yourUniqueId + "')");
//
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            Map<String, Object> data = new HashMap<>();
//            data.put("connId", yourUniqueId);
//            db.collection("rooms").document(roomId).update(data);
//
//        }
//        if(!send) {
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            db.collection("rooms").document(roomId).get().addOnSuccessListener(documentSnapshot -> {
//                String incoming = documentSnapshot.getString("connId");
//                callJavaScriptFunction( "javascript:startCall('" + incoming + "')");
//            });
//
//
//        }
//
//
//    }
    public void onPeerConnected() {
        isPeerConnected = true;
    }

    void callJavaScriptFunction(String function) {
        binding.webView.post(() -> binding.webView.evaluateJavascript(function, null));
    }
    String getUniqueId() {
        return UUID.randomUUID().toString();
    }
    public void checkSend() {
        if(Objects.equals(createdBy, auth.getCurrentUser().getUid())){
            send = true;
        }
        else {
            send = false;
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit = true;
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        if (auth.getCurrentUser() != null) {
//            db.collection("Students").document(auth.getCurrentUser().getUid()).update("connId", null);
        }
        else {
//            db.collection("Recruiters").document(createdBy).update("connId", null);
        }
    }
}