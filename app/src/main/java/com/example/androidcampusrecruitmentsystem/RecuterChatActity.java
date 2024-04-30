package com.example.androidcampusrecruitmentsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import android.text.TextUtils;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RecuterChatActity extends AppCompatActivity {
    private RecyclerView userMessagesList;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private final List<Messages> messagesList = new ArrayList<>();
    private Toolbar chatToolbar;
    private TextView chatToolbarTitle;
    private EditText messageEditText;
    private ImageView sendMessageButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ArrayList<String> sentStudentIds = new ArrayList<>();
    private String saveCurrentTime, saveCurrentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuter_chat_actity);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        chatToolbar = findViewById(R.id.chat_toolbar);
        chatToolbarTitle = findViewById(R.id.chat_toolbar_title);
        messageEditText = findViewById(R.id.input_message);
        sendMessageButton = findViewById(R.id.send_message_btn);


        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        Intent intent = getIntent();
        String studentName = intent.getStringExtra("recruiterName");
        String studentContact = intent.getStringExtra("recruiterEmail");
        String recruiterId = intent.getStringExtra("recruiterId");
        startMessageListener(recruiterId);

        // Set student name as toolbar title
        setSupportActionBar(chatToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide default title
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            chatToolbarTitle.setText(studentName);
        }
        // Retrieve recruiter details (current user details) from Firebase
        firestore.collection("Recruiters").document(recruiterId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String recruiterName = documentSnapshot.getString("name");

                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
        sendMessageButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            SendMessage(message, recruiterId);

            sentStudentIds.add(recruiterId);
            SentStudentsSingleton.getInstance().addSentStudentIds(sentStudentIds);

        });
    }


    private void SendMessage(String messageText, String recruiterId) {
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "First write your message...", Toast.LENGTH_SHORT).show();
        } else {
            String senderId = mAuth.getCurrentUser().getUid();
            String messageSenderRef = "Messages/" + senderId + "/massage"; // Update path
            String messageReceiverRef = "Messages/" + recruiterId + "/massage"; // Update path

            // Get the Firestore instance
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            CollectionReference messagesRef = firestore.collection("Messages");
            // Get current time and date
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            String saveCurrentTime = currentTime.format(calendar.getTime());            long currentTimeMillis = System.currentTimeMillis();
            String saveCurrentTime2 = String.valueOf(currentTimeMillis);
            String saveCurrentDate = currentDate.format(calendar.getTime());

            // Create a new message document ID
            String messagePushID = messagesRef.document().getId();

// Recruiter message data
            Map<String, Object> messageTextBody = new HashMap<>();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("to", recruiterId);// Sender is the recruiter
            messageTextBody.put("from", senderId);    // Receiver is the student
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("time2", saveCurrentTime2);
            messageTextBody.put("date", saveCurrentDate);

            Map<String, Object> newData = new HashMap<>();
            newData.put("newField", "value"); // Add new field with value

            DocumentReference docRef = firestore.collection("Messages").document(senderId);
            docRef.set(newData)
                    .addOnSuccessListener(aVoid ->{
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                            })
                    .addOnFailureListener(e -> Log.e("TAG", "Error updating document", e));



            DocumentReference docRef1 =  messagesRef.document(senderId)
                    .collection("list")
                    .document(recruiterId);
            docRef1.set(newData)
                    .addOnSuccessListener(aVoid ->{
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    })
                    .addOnFailureListener(e -> Log.e("TAG", "Error updating document", e));



            messagesRef.document(senderId)
                    .collection("list")
                    .document(recruiterId)
                    .collection("msgs")

                    .document(messagePushID) // New document for the message
                    .set(messageTextBody) // Set the message details
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RecuterChatActity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                fetchMessagesFromFirestore(recruiterId);
                                messageAdapter.notifyDataSetChanged();
                             } else {
                                Toast.makeText(RecuterChatActity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                            messageEditText.setText("");
                        }
                    });
        }
    }
    private void fetchMessagesFromFirestore(String recruiterId) {
        String currentUserId = mAuth.getCurrentUser().getUid();

        Log.d("Firestore", "Fetching messages for user: " + currentUserId);

        // Combine both sender and receiver collections in a single query
        firestore.collection("Messages")
                .document(currentUserId)
                .collection("list")
                .document(recruiterId)
                .collection("msgs")
                .orderBy("time2", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messagesList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Messages message = document.toObject(Messages.class);
                            Log.d("Firestore", "Document data: " + document.getData());
                            messagesList.add(message);
                        }
                        // Fetch messages from receiver's collection
                        firestore.collection("Messages")
                                .document(recruiterId)
                                .collection(currentUserId)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                            Messages message = document.toObject(Messages.class);
                                            Log.d("Firestore", "Document data: " + document.getData());
                                            Collections.sort(messagesList, (m1, m2) -> m1.getTime().compareTo(m2.getTime()));
                                            messagesList.add(message);
                                        }
                                        // Notify adapter after both sender and receiver messages are fetched
                                        messageAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.e("Firestore", "Error fetching receiver messages", task2.getException());
                                    }
                                });
                    } else {
                        Log.e("Firestore", "Error fetching sender messages", task.getException());
                    }
                });
    }
    private void startMessageListener(String recruiterId) {
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Reference to the collection containing messages
        CollectionReference messagesRef = firestore.collection("Messages")
                .document(currentUserId)
                .collection("list")
                .document(recruiterId)
                .collection("msgs");

        // Attach a SnapshotListener to listen for real-time updates
        messagesRef.orderBy("time2", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Error fetching messages", error);
                        return;
                    }

                    if (value != null) {
                        messagesList.clear();
                        fetchMessagesFromFirestore(recruiterId);
                    }
                });
    }
}