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

public class ChatActivity extends AppCompatActivity {
    private RecyclerView userMessagesList;
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private final List<Messages> messagesList = new ArrayList<>();
    private Toolbar chatToolbar;
    private TextView chatToolbarTitle;
    private EditText messageEditText1;
    private ImageView sendMessageButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ArrayList<String> sentStudentIds = new ArrayList<>();
    private String saveCurrentTime, saveCurrentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String recruiterId = mAuth.getCurrentUser().getUid();

        // Initialize views
        chatToolbar = findViewById(R.id.chat_toolbar);
        chatToolbarTitle = findViewById(R.id.chat_toolbar_title);
        messageEditText1 = findViewById(R.id.input_message1);
        sendMessageButton = findViewById(R.id.send_message_btn);

        Intent intent = getIntent();
        String studentName = intent.getStringExtra("studentName");
        String studentContact = intent.getStringExtra("studentEmail");
        String studentId = intent.getStringExtra("studentId");

        Log.d("TAG", "onCreate: "+studentId+" "+studentName+" "+studentContact);
        if (studentId != null) {
            startMessageListener(studentId);
        }
        else {
            Toast.makeText(this, "Chat not found", Toast.LENGTH_SHORT).show();
        }
        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.private_messages_list_of_users1);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        Calendar calendar = Calendar.getInstance();


        setSupportActionBar(chatToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            chatToolbarTitle.setText(studentName);
        }
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        sendMessageButton.setOnClickListener(v -> {
            String message = messageEditText1.getText().toString().trim();
            SendMessage(message, studentId);

            sentStudentIds.add(studentId);
            SentStudentsSingleton.getInstance().addSentStudentIds(sentStudentIds);
        });

    }


    private void SendMessage(String messageText, String studentId) {
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "First write your message...", Toast.LENGTH_SHORT).show();
        } else {
            // Get current time and date
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            String saveCurrentTime = currentTime.format(calendar.getTime());
            String saveCurrentDate = currentDate.format(calendar.getTime());
            long currentTimeMillis = System.currentTimeMillis();
            String saveCurrentTime2 = String.valueOf(currentTimeMillis);
            // Continue with sending the message
            if (firestore != null) {
                String senderId = mAuth.getCurrentUser().getUid();
                String messageSenderRef = "Messages/" + studentId;
                String messageReceiverRef = "Messages/" + senderId;

                CollectionReference messagesRef = firestore.collection("Messages");
                String messagePushID = messagesRef.document().getId();
                Map<String, Object> newData = new HashMap<>();
                newData.put("newField", "value"); // Add new field with value

                DocumentReference docRef = firestore.collection("Messages").document(studentId);
                docRef.set(newData)
                        .addOnSuccessListener(aVoid ->{
                            Log.d("TAG", "DocumentSnapshot successfully updated!");
                        })
                        .addOnFailureListener(e -> Log.e("TAG", "Error updating document", e));



                DocumentReference docRef1 =  messagesRef.document(studentId)
                        .collection("list")
                        .document(senderId);
                docRef1.set(newData)
                        .addOnSuccessListener(aVoid ->{
                            Log.d("TAG", "DocumentSnapshot successfully updated!");
                        })
                        .addOnFailureListener(e -> Log.e("TAG", "Error updating document", e));


                // Student message data
                Map<String, Object> messageTextBody = new HashMap<>();
                messageTextBody.put("message", messageText);
                messageTextBody.put("type", "text");
                messageTextBody.put("from", senderId);  // Sender is the student
                messageTextBody.put("to", studentId); // Receiver is the recruiter
                messageTextBody.put("messageID", messagePushID);
                messageTextBody.put("time", saveCurrentTime);
                messageTextBody.put("time2", saveCurrentTime2);
                messageTextBody.put("date", saveCurrentDate);

                messagesRef.document(studentId)
                        .collection("list")
                        .document(senderId).
                        collection("msgs")
                        .document(messagePushID)
                        .set(messageTextBody)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                    fetchMessagesFromFirestore(studentId);
                                    messageAdapter.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                                messageEditText1.setText("");
                            }
                        });
            } else {
                Log.e("ChatActivity", "Firestore object is null");
            }
        }
    }
    private void fetchMessagesFromFirestore(String studentId) {
        String currentUserId = mAuth.getCurrentUser().getUid();

        Log.d("Firestore", "Fetching messages for user: " + currentUserId+ studentId);

        // Combine both sender and receiver collections in a single query
        firestore.collection("Messages")
                .document(studentId)
                .collection("list")
                .document(currentUserId)
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
                                .document(studentId)
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
    private void startMessageListener(String studentId) {
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Reference to the collection containing messages
        CollectionReference messagesRef = firestore.collection("Messages")
                .document(studentId)
                .collection("list")
                .document(currentUserId)
                .collection("msgs");

        // Attach a SnapshotListener to listen for real-time updates
        messagesRef.orderBy("time2", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Error fetching messages", error);
                        return;
                    }

                    if (value != null) {
                        messagesList.clear();
                        fetchMessagesFromFirestore(studentId);
                    }
                });
    }

}