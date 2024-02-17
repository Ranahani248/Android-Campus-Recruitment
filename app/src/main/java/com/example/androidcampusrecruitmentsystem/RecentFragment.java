package com.example.androidcampusrecruitmentsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecentFragment extends Fragment {

    TextView savemore,appliedmore;
    public RecentFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        appliedmore = view.findViewById(R.id.applied_more);
        savemore = view.findViewById(R.id.saved_more);
        RecyclerView saveRecycle = view.findViewById(R.id.savedJobsRecycler);
        RecyclerView appliedRecycle = view.findViewById(R.id.applied_recycler);

        List<JobItem> savejobList = new ArrayList<>();
        List<JobItem> appliedlist = new ArrayList<>();




        appliedRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        AppliedAdapter appliedAdapter = new AppliedAdapter(appliedlist);
        appliedRecycle.setAdapter(appliedAdapter);
        appliedmore.setOnClickListener(v->{


            if(appliedmore.getText()!= "See Less") {
                appliedRecycle.setNestedScrollingEnabled(true);
                appliedlist.add(new JobItem("Applied 1","Applied 1","Applied 1","Applied 1","Applied 1"));
                appliedlist.add(new JobItem("Applied 2","Applied 1","Applied 1","Applied 1","Applied 1"));
                appliedlist.add(new JobItem("Applied 1","Applied 1","Applied 1","Applied 1","Applied 1"));
                appliedlist.add(new JobItem("Applied 2","Applied 1","Applied 1","Applied 1","Applied 1"));
                appliedRecycle.setAdapter(appliedAdapter);
                appliedmore.setText("See Less");
            }
            else {
                saveRecycle.scrollToPosition(0);
                saveRecycle.setNestedScrollingEnabled(false);
                appliedlist.clear();
                appliedlist.add(new JobItem("Applied 1","Applied 1","Applied 1","Applied 1","Applied 1"));
                appliedlist.add(new JobItem("Applied 2","Applied 1","Applied 1","Applied 1","Applied 1"));
                appliedRecycle.setAdapter(appliedAdapter);
                appliedmore.setText("See more...");
            }


        });







        savejobList.add(new JobItem("Job 1","Applied 1","Applied 1","Applied 1","Applied 1"));
        savejobList.add(new JobItem("Job 2","Applied 1","Applied 1","Applied 1","Applied 1"));


        saveRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        SavedJobsAdapter savedJobsAdapter = new SavedJobsAdapter(savejobList);
        saveRecycle.setAdapter(savedJobsAdapter);
        savemore.setOnClickListener(v->{


            if(savemore.getText()!= "See Less") {
               saveRecycle.setNestedScrollingEnabled(true);
                savejobList.add(new JobItem("Job 1","Applied 1","Applied 1","Applied 1","Applied 1"));
                savejobList.add(new JobItem("Job 2","Applied 1","Applied 1","Applied 1","Applied 1"));
                savejobList.add(new JobItem("Job 1","Applied 1","Applied 1","Applied 1","Applied 1"));
                savejobList.add(new JobItem("Job 2","Applied 1","Applied 1","Applied 1","Applied 1"));
                saveRecycle.setAdapter(savedJobsAdapter);
               savemore.setText("See Less");
           }
           else {
               saveRecycle.scrollToPosition(0);
               saveRecycle.setNestedScrollingEnabled(false);
               savejobList.clear();
                savejobList.add(new JobItem("Job 1","Applied 1","Applied 1","Applied 1","Applied 1"));
                savejobList.add(new JobItem("Job 2","Applied 1","Applied 1","Applied 1","Applied 1"));
                saveRecycle.setAdapter(savedJobsAdapter);
               savemore.setText("See more...");
           }


        });


        return view;
    }
}