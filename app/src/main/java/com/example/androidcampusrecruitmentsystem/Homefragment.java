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


public class Homefragment extends Fragment {






    public Homefragment() {
        // Required empty public constructor
    }


    public static Homefragment newInstance() {
        Homefragment fragment = new Homefragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_homefragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<JobItem> jobList = new ArrayList<>();
        jobList.add(new JobItem("Job 1"));
        jobList.add(new JobItem("Job 2"));
        jobList.add(new JobItem("Job 3"));
        jobList.add(new JobItem("Job 4"));
        jobList.add(new JobItem("Job 5"));
        jobList.add(new JobItem("Job 6"));
        jobList.add(new JobItem("Job 7"));
        jobList.add(new JobItem("Job 8"));
        jobList.add(new JobItem("Job 9"));
        jobList.add(new JobItem("Job 10"));

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.jobRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        JobAdapter jobAdapter = new JobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);
    }
}