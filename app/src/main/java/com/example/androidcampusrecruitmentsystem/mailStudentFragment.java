package com.example.androidcampusrecruitmentsystem;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class mailStudentFragment extends Fragment {

    private String senderId;
    private ConversationAdapter1 adapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    public mailStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mail_student, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        senderId = mAuth.getCurrentUser().getUid(); // Assign senderId here

        RecyclerView recyclerView = view.findViewById(R.id.conversations_recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ConversationAdapter1(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        fetchMessages();
        return view;
    }

    private void fetchRecruiterDetails() {

    }

    private void fetchMessages() {
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("Messages")
                .document(senderId)
                .collection("list")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                            List<String> recruiterIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String studentId = document.getId();
                                recruiterIds.add(studentId);
                            }

                            // Pass the list of student IDs to the ConversationAdapter
                            adapter.updateData(recruiterIds);
                        }

    else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }
}