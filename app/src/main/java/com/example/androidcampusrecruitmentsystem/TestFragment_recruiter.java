package com.example.androidcampusrecruitmentsystem;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TestFragment_recruiter extends Fragment {

    private RecyclerView test_recyclerView,scheduledInterviews_recyclerView;
    private TestAttemptedAdapterRecruiter testAdapter;
    private List<TestListItemRecruiter> testItemList;
    private List<SheduledInterviewItem> interviewList;
    private SheduledInterviewAdapter scheduleInterviewAdapter;

    private FirebaseFirestore firestore;
    ConstraintLayout test_layoutRecruiter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        testItemList = new ArrayList<>();
        loadTests();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_recruiter, container, false);
        test_recyclerView = view.findViewById(R.id.testRecycle_recruiter);
        interviewList = new ArrayList<>();
        scheduledInterviews_recyclerView = view.findViewById(R.id.interviewRecycle);

        scheduleInterviewAdapter = new SheduledInterviewAdapter(interviewList, scheduledInterviews_recyclerView, null, this);
        scheduledInterviews_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        scheduledInterviews_recyclerView.setAdapter(scheduleInterviewAdapter);


        testAdapter = new TestAttemptedAdapterRecruiter(testItemList, test_recyclerView,this);
        test_layoutRecruiter = view.findViewById(R.id.test_layoutRecruiter);
        test_layoutRecruiter.setVisibility(View.GONE);
        test_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        test_recyclerView.setAdapter(testAdapter);
        loadScheduledInterviews();
        return view;
    }

    public void loadScheduledInterviews() {
        firestore.collection("Interviews").get().addOnSuccessListener(queryDocumentSnapshots -> {
            interviewList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if(documentSnapshot.getString("recruiterID").equals(getCurrentUserId())) {
                    String interviewId = documentSnapshot.getId();
                    String startDate = documentSnapshot.getString("scheduleDate");
                    String startTime = documentSnapshot.getString("scheduleTime");
                    String jobId = documentSnapshot.getString("jobID");
                    String recruiterID = documentSnapshot.getString("studentID");
                    SheduledInterviewItem scheduledInterviewItem = new SheduledInterviewItem(startDate, startTime, "Job Title", "Student Name", interviewId, recruiterID);
                    getScheduleInterviewJob(jobId, scheduledInterviewItem, recruiterID);
                }
            }
        });
    }
    public void getScheduleInterviewJob(String jobId, SheduledInterviewItem scheduledInterviewItem, String recruiterID) {
        firestore.collection("Jobs").document(jobId).get().addOnSuccessListener(documentSnapshot -> {
            String jobTitle = documentSnapshot.getString("jobTitle");
            scheduledInterviewItem.setJobtitle(jobTitle);
            getScheduleInterviewRecruiter(recruiterID, scheduledInterviewItem);
        }).addOnFailureListener(e -> {
            getScheduleInterviewRecruiter(recruiterID, scheduledInterviewItem);

        });

    }
    public void getScheduleInterviewRecruiter(String recruiterID, SheduledInterviewItem scheduledInterviewItem) {
        firestore.collection("Students").document(recruiterID).get().addOnSuccessListener(documentSnapshot -> {
            String recruiterName = documentSnapshot.getString("name");
            scheduledInterviewItem.setRecruiterName(recruiterName);
            interviewList.add(scheduledInterviewItem);
            scheduleInterviewAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            interviewList.add(scheduledInterviewItem);
            scheduleInterviewAdapter.notifyDataSetChanged();
        });
    }
    private void loadTests() {
        firestore.collection("AttemptedTest")
                .whereEqualTo("RecruiterId", getCurrentUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        test_layoutRecruiter.setVisibility(View.VISIBLE);
                        return;
                    }
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String attemptedTestId = documentSnapshot.getId();
                        String studentId = documentSnapshot.getString("studentID");

                        String jobId = documentSnapshot.getString("jobId");

                        TestListItemRecruiter testItem = new TestListItemRecruiter( "recruiterName", "jobTitle",attemptedTestId);


                        assert studentId != null;
                        firestore.collection("Students").document(studentId).get()
                                .addOnSuccessListener(documentSnapshot1 -> {
                                  String studentName = documentSnapshot1.getString("name");
                                  testItem.setStudentName(studentName);
                                    retrieveJobName(jobId, testItem);
                                }).addOnFailureListener(e -> {
                                            retrieveJobName(jobId, testItem);
                                        });

                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }


    public void addItem(TestListItemRecruiter testItem) {
        testItemList.add(testItem);
        testAdapter.notifyDataSetChanged();

    }



    private void retrieveJobName(String jobId, TestListItemRecruiter testItem) {
        Log.d("TestFragment_recruiter", "Job ID: " + jobId);
        firestore.collection("Jobs").document(jobId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String jobTitle = documentSnapshot.getString("jobTitle");
                    testItem.setJobTitle(jobTitle);
                    addItem(testItem);
                    Log.d("TestFragment_recruiter", "Job title: " + jobTitle);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    addItem(testItem);
                });


    }


    private String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
       else{ return null;}
    }
}