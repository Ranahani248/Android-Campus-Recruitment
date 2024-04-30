package com.example.androidcampusrecruitmentsystem;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MailFragment extends Fragment {
    private String senderId;
    private ConversationAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public MailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mail, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        senderId = mAuth.getCurrentUser().getUid(); // Assign senderId here

        RecyclerView recyclerView = view.findViewById(R.id.conversations_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ConversationAdapter(new ArrayList<String>());
        recyclerView.setAdapter(adapter);

        fetchMessages();

        return view;
    }

    private void fetchMessages() {
        db.collectionGroup("list")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        if (document.getId().equals(senderId)) {
                            // Get the reference to the parent of parent document
                            DocumentReference parentOfParentRef = document.getReference().getParent().getParent();
                            if (parentOfParentRef != null) {
                                parentOfParentRef.get()
                                        .addOnSuccessListener(parentOfParentDoc -> {
                                            if (parentOfParentDoc.exists()) {
                                                Log.d("TAG", "Parent of Parent Document ID: " + parentOfParentDoc.getId());
                                                List<String> studentids = new ArrayList<>();
                                                String studentId = parentOfParentDoc.getId();
                                                studentids.add(studentId);

                                                // Pass the list of student IDs to the ConversationAdapter
                                                adapter.updateData(studentids);

                                            } else {
                                                Log.d("TAG", "Parent of Parent document does not exist");
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("TAG", "Error fetching parent of parent document: ", e);
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Error fetching documents: ", e);
                });
    }
}