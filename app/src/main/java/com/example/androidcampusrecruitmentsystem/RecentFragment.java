package com.example.androidcampusrecruitmentsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecentFragment extends Fragment {

    TextView savemore,appliedmore;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    List<JobItem> savejobList = new ArrayList<>();
    List<JobItem> appliedlist = new ArrayList<>();
    RecyclerView appliedRecycle,saveRecycle;
    SavedJobsAdapter savedJobsAdapter;

    AppliedAdapter appliedAdapter;
    String userId;
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
        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
         saveRecycle = view.findViewById(R.id.savedJobsRecycler);
         appliedRecycle = view.findViewById(R.id.applied_recycler);
        savemore.setVisibility(View.GONE);
        appliedmore.setVisibility(View.GONE);

        checkJobs("Applications",true);
        checkJobs("SavedJobs",true);


        appliedmore.setOnClickListener(v->{

            if(appliedmore.getText()!= "See Less") {
                checkJobs("Applications",false);
                appliedmore.setText("See Less");
            }
            else {
                less(appliedlist);
                appliedRecycle.setAdapter(appliedAdapter);
                appliedmore.setText("See more...");
            }

        });



        saveRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
         savedJobsAdapter = new SavedJobsAdapter(savejobList);
        saveRecycle.setAdapter(savedJobsAdapter);
        savemore.setOnClickListener(v->{


            if(savemore.getText()!= "See Less") {
              checkJobs("SavedJobs", false);
               savemore.setText("See Less");
           }
           else {
                less(savejobList);
                 saveRecycle.setAdapter(savedJobsAdapter);
               savemore.setText("See more...");
           }


        });


        return view;
    }

    private void checkJobs(String collection, boolean less) {
        firestore.collection(collection)
                .whereEqualTo("studentId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(collection.equals("Applications")) {
                        appliedlist.clear();
                    }
                    else if(collection.equals("SavedJobs")) {
                        savejobList.clear();
                    }
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String jobId = document.getString("jobId");
                            String jobTitle = document.getString("Title");
                            String companyName = document.getString("Company");
                            String location = document.getString("location");
                            String salary = document.getString("Salary");
                            String description = document.getString("description");
                            String recruiterId = document.getString("recruiterId");
                            if (collection.equals("SavedJobs")) {
                                savejobList.add(new JobItem(jobId, jobTitle, description, companyName, salary, location, recruiterId));
                               if(savejobList.size() > 2) {
                                   savemore.setVisibility(View.VISIBLE);
                               }
                                if(less) {
                                    less(savejobList);
                                }
                                saveRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
                                savedJobsAdapter = new SavedJobsAdapter(savejobList);
                                saveRecycle.setAdapter(savedJobsAdapter);
                            } else if (collection.equals("Applications")) {
                                appliedlist.add(new JobItem(jobId, jobTitle, description, companyName, salary, location, recruiterId));
                                if(appliedlist.size() > 2) {
                                    appliedmore.setVisibility(View.VISIBLE);
                                }
                                if(less) {
                                    less(appliedlist);
                                }
                                appliedRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
                                appliedAdapter = new AppliedAdapter(appliedlist);
                                appliedRecycle.setAdapter(appliedAdapter);
                            }

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to query the collection
                });
    }
    public void less(List<JobItem> less) {
        if(less.size() > 2) {
            for(int i = less.size()-1; i > 1; i--) {
                less.remove(i);
            }
        }

    }
}