package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecentFragment extends Fragment implements SavedJobsAdapter.OnItemClickListener, AppliedAdapter.OnItemClickListener {

    TextView savemore,appliedmore;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    static List<JobItem> savejobList = new ArrayList<>();
    static List<JobItem> appliedlist = new ArrayList<>();
    RecyclerView appliedRecycle,saveRecycle;
    TextView noSaved, noApplication, textView3;
    ProgressBar appliedBar, saveBar;
    ConstraintLayout applylayout,saveLayout;
    static SavedJobsAdapter savedJobsAdapter;

    AppliedAdapter appliedAdapter = new AppliedAdapter(appliedlist);
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
        appliedBar = view.findViewById(R.id.progressBarApplication);
        saveBar = view.findViewById(R.id.progressBarSaved);
        applylayout = view.findViewById(R.id.applylayout);
        saveLayout = view.findViewById(R.id.saveLayout);
        noSaved = view.findViewById(R.id.noSave);
        textView3 = view.findViewById(R.id.textView3);
        noApplication = view.findViewById(R.id.noApplication);
        savemore.setVisibility(View.GONE);
        appliedmore.setVisibility(View.GONE);
        savejobList.clear();
        appliedlist.clear();
        noApplication.setVisibility(View.GONE);
        noSaved.setVisibility(View.GONE);
        load(saveBar, true);
        load(appliedBar, true);

        saveRecycle.setAdapter(savedJobsAdapter);
        appliedRecycle.setAdapter(appliedAdapter);
        checkJobs("Applications",true);
        checkJobs("SavedJobs",true);



        appliedRecycle.setLayoutManager(new LinearLayoutManager(getContext()));

        appliedAdapter.setOnItemClickListener(savedJob -> {
            Job_Details_student.jobid = savedJob.getJobid();
            Job_Details_student.recruiterid = savedJob.getRecruiterid();
            Job_Details_student.studentid = currentUser.getUid();
            Job_Details_student.backRecent = true;
            Intent intent = new Intent(getContext(), Job_Details_student.class);
            startActivity(intent);
        });
        appliedRecycle.setAdapter(appliedAdapter);


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
        savedJobsAdapter.setOnItemClickListener(savedJob -> {
            Job_Details_student.jobid = savedJob.getJobid();
            Job_Details_student.recruiterid = savedJob.getRecruiterid();
            Job_Details_student.studentid = currentUser.getUid();
            Job_Details_student.backRecent = true;
            Intent intent = new Intent(getContext(), Job_Details_student.class);
            startActivity(intent);
        });
        saveRecycle.setAdapter(savedJobsAdapter);

        savemore.setOnClickListener(v->{


            if(savemore.getText()!= "See Less") {
                checkJobs("SavedJobs", false);
               savemore.setText("See Less");
           }
           else {
                less(savejobList);
                 saveRecycle.setAdapter(savedJobsAdapter);
                savedJobsAdapter.notifyDataSetChanged();
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
                                savedJobsAdapter.notifyDataSetChanged();

                            } else if (collection.equals("Applications")) {
                                appliedlist.add(new JobItem(jobId, jobTitle, description, companyName, salary, location, recruiterId));
                                if(appliedlist.size() > 2) {
                                    appliedmore.setVisibility(View.VISIBLE);
                                }
                                if(less) {
                                    less(appliedlist);
                                }
                                appliedAdapter.notifyDataSetChanged();
                            }

                        }



                    }
                    load(appliedBar, false);
                    if(appliedlist.isEmpty()){
                        noApplication.setVisibility(View.VISIBLE);
                    }
                    else {
                        noApplication.setVisibility(View.GONE);
                        applylayout.setVisibility(View.GONE);
                    }
                    load(saveBar, false);
                    if(savejobList.isEmpty()){
                        noSaved.setVisibility(View.VISIBLE);

                    }
                    else {
                        noSaved.setVisibility(View.GONE);
                        saveLayout.setVisibility(View.GONE);
                    }
                    savedJobsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    if(appliedlist.isEmpty()){
                        noApplication.setVisibility(View.VISIBLE);
                    }
                    else {
                        noApplication.setVisibility(View.GONE);
                    }
                    if(savejobList.isEmpty()){
                        noSaved.setVisibility(View.VISIBLE);
                    }
                    else {
                        noSaved.setVisibility(View.GONE);
                    }
                });
    }
    public void less(List<JobItem> less) {
        if(less.size() > 2) {
            for(int i = less.size()-1; i > 1; i--) {
                less.remove(i);
            }
        }


    }

    @Override
    public void onItemClick(JobItem jobItem) {

    }
    public void load(ProgressBar progressBar, boolean load) {
        if (load) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

    }

}