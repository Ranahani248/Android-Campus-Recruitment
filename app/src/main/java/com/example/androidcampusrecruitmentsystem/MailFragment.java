package com.example.androidcampusrecruitmentsystem;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class MailFragment extends Fragment {

    public MailFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mail, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<ConversationItem> conversationItemList = new ArrayList<>();
        conversationItemList.add(new ConversationItem("Recruiter 1"));
        conversationItemList.add(new ConversationItem("Recruiter 2"));
        conversationItemList.add(new ConversationItem("Recruiter 3"));
        conversationItemList.add(new ConversationItem("Recruiter 4"));
        conversationItemList.add(new ConversationItem("Recruiter 5"));
        conversationItemList.add(new ConversationItem("Recruiter 6"));
        conversationItemList.add(new ConversationItem("Recruiter 7"));
        conversationItemList.add(new ConversationItem("Recruiter 8"));
        conversationItemList.add(new ConversationItem("Recruiter 9"));
        conversationItemList.add(new ConversationItem("Recruiter 10"));
        conversationItemList.add(new ConversationItem("Recruiter 11"));

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.conversations_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ConversationAdapter conversationAdapter = new ConversationAdapter(conversationItemList);
        recyclerView.setAdapter(conversationAdapter);
    }
}